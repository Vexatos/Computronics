package audio.gme;

// Nintendo NSF music file emulator
// http://www.slack.net/~ant/

/* Copyright (C) 2003-2007 Shay Green. This module is free software; you
can redistribute it and/or modify it under the terms of the GNU Lesser
General Public License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version. This
module is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
details. You should have received a copy of the GNU Lesser General Public
License along with this module; if not, write to the Free Software Foundation,
Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA */

public final class NsfEmu extends NesCpu
{
	// header offsets
	static final int trackCountOff = 0x06;
	static final int loadAddrOff   = 0x08;
	static final int initAddrOff   = 0x0A;
	static final int playAddrOff   = 0x0C;
	static final int ntscSpeedOff  = 0x6E;
	static final int banksOff      = 0x70;
	static final int palSpeedOff   = 0x78;
	static final int speedFlagsOff = 0x7A;
	static final int chipFlagsOff  = 0x7B;
	
	// memory addresses
	static final int sramAddr       = 0x6000;
	static final int bankSelectAddr = 0x5FF8;
	static final int idleAddr       = bankSelectAddr;
	static final int romStart       = 0x8000;
	
	static final int sramOffset = 0x800; // offset in ram []
	static final int sramSize   = 0x2000;
	static final int unmapped4000Offset = sramAddr + sramSize;
	static final int ramSize    = unmapped4000Offset + 0x100;
	
	static final int bankSize   = 0x1000;
	static final int bankCount  = 8;
	
	byte [] ram;
	final MemPager	rom				= new MemPager( bankSize, ramSize );
	final byte []	header			= new byte [0x80];
	final int []	initialBanks	= new int [8];
	final NesApu	apu				= new NesApu();
	
	int palOnly;
	int endTime;
	int playPeriod;
	int nextPlay;
	
	protected int loadFile_( byte [] in )
	{
		if ( !isHeader( in, "NESM" ) )
			error( "Not an NSF file" );
		
		// Load ROM data
		final int loadAddr = getLE16( in, loadAddrOff );
		ram = rom.load( in, header, loadAddr % bankSize, 0xF2 );
		
		if ( header [chipFlagsOff] != 0 )
			error( "Extra sound chips not supported" );
		
		// Copy initial banks
		int nonZero = 0;
		for ( int i = 0; i < bankCount; i++ )
		{
			int bank = header [banksOff + i] & 0xFF;
			initialBanks [i] = bank;
			nonZero |= bank;
		}
		
		// Use default banks if initial banks were all zero
		if ( nonZero == 0 )
		{
			int totalBanks = rom.size() / bankSize;
			int firstBank = (loadAddr - romStart) / bankSize;
			for ( int i = 0; i < bankCount; i++ )
			{
				int bank = i - firstBank;
				if ( bank < 0 || totalBanks <= bank )
					bank = 0;
				initialBanks [i] = bank;
			}
		}
		
		// NTSC rate
		int playbackRate = getLE16( header, ntscSpeedOff );
		double clockRate = 1789772.727273;
		int standardRate = 0x411A;
		playPeriod       = 29781;
		palOnly          = 0;
		
		if ( (header [speedFlagsOff] & 3) == 1 )
		{
			// PAL rate
			playbackRate = getLE16( header, palSpeedOff );
			clockRate    = 1662607.125;
			standardRate = 0x4E20;
			playPeriod   = 33247;
			palOnly      = 1;
		}
		
		// Custom rate
		if ( playbackRate != standardRate && playbackRate != 0 )
			playPeriod = (int) (playbackRate * clockRate * (1.0 / 1000000.0) + 0.5);
		
		setClockRate( (int) (clockRate + 0.5) );
		
		apu.setOutput( buf.center() );
		
		return header [trackCountOff] & 0xFF;
	}
	
	private void cpuCall( int addr )
	{
		pc = addr;
		p |= 0x04;
		ram [ s         | 0x100] = (byte) ((idleAddr - 1) >> 8);
		ram [(s + 0xFF) | 0x100] = (byte) (idleAddr - 1);
		s = (s - 2) & 0xFF;
	}
	
	public void startTrack( int track )
	{
		super.startTrack( track );
		
		// APU
		apu.reset( this, (palOnly != 0) );
		apu.write( 0, 0x4015, 0x0F );
		
		// Memory
		java.util.Arrays.fill( ram, 0, ramSize, (byte) 0 );
		reset( ram, rom.unmapped() );
		mapMemory( 0,        sramOffset, 0 );
		mapMemory( sramAddr, sramSize,   sramOffset );
		// some NSF rips expect to read back 0 from 0x4016 and 0x4017 (Maniac Mansion)
		mapMemory( 0x4000,   pageSize,   unmapped4000Offset );
		for ( int i = 0; i < bankCount; ++i )
			cpuWrite( bankSelectAddr + i, initialBanks [i] );
		
		nextPlay = playPeriod;
		
		// CPU
		a  = track;
		x  = palOnly;
		y  = 0;
		p  = 0;
		s  = 0xFF;
		pc = idleAddr;
		cpuCall( getLE16( header, initAddrOff ) );
	}
	
	protected int runClocks( int clockCount )
	{
		endTime = clockCount;
		time = -endTime;
		
		while ( true )
		{
			runCpu();
			if ( time >= 0 )
				break;
			
			if ( pc != idleAddr )
			{
				logError();
				return endTime;
			}
			
			// Next play call
			int next = nextPlay - endTime;
			if ( time < next )
			{
				time = 0;
				if ( next > 0 )
					break;
				time = next;
			}
			
			nextPlay += playPeriod;
			cpuCall( getLE16( header, playAddrOff ) );
		}
		
		// End time frame
		endTime += time;
		nextPlay -= endTime;
		if ( nextPlay < 0 ) // could go negative if routine is taking too long to return
			nextPlay = 0;
		apu.endFrame( endTime );
		
		return endTime;
	}
	
	protected final int cpuRead( int addr )
	{
		if ( addr <= 0x7FF ) // 90%
			return ram [addr] & 0xFF;
		
		// APU
		if ( addr == 0x4015 )
			return apu.read( time + endTime );
		
		// TODO: return addr >> 8 for unmapped areas?
		
		if ( addr < 0x10000 )
			return ram [mapAddr( addr )] & 0xFF;
		
		// address wrapped around
		return ram [addr - 0x10000] & 0xFF;
	}
	
	protected final void cpuWrite( int addr, int data )
	{
		if ( debug ) assert 0 <= data && data < 0x100;
		if ( debug ) assert 0 <= addr && addr < 0x10100;
		
		// SRAM
		int offset;
		if ( (offset = addr ^ sramAddr) < sramSize )
		{
			ram [sramOffset + offset] = (byte) data;
			return;
		}
		
		// APU
		if ( (addr ^ 0x4000) <= 0x17 )
		{
			apu.write( time + endTime, addr, data );
			return;
		}
		
		// Bank
		int bank = addr - bankSelectAddr;
		if ( 0 <= bank && bank < bankCount )
		{
			mapMemory( bank * bankSize + romStart, bankSize, rom.mapAddr( data * bankSize ) );
			return;
		}
		
		// RAM
		if ( (addr & 0xF800) == 0 ) // addr <= 0x7FF || addr >= 0x10000
		{
			ram [addr & 0x7FF] = (byte) data;
			return;
		}
	}
}

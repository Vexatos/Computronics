package audio.gme;

// Nintendo Game Boy GBS music file emulator
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

public final class GbsEmu extends GbCpu
{
	// header offsets
	static final int trackCountOff  = 0x04;
	static final int loadAddrOff    = 0x06;
	static final int initAddrOff    = 0x08;
	static final int playAddrOff    = 0x0A;
	static final int stackPtrOff    = 0x0C;
	static final int timerModuloOff = 0x0E;
	static final int timerModeOff   = 0x0F;
	
	// memory addresses
	static final int idleAddr = 0xF00D;
	static final int ramAddr  = 0xA000;
	static final int hiPage   = 0xFF00 - ramAddr;
	
	static final int ramSize  = 0x4000 + 0x2000;
	static final int bankSize = 0x4000;
	
	final MemPager rom = new MemPager( bankSize, ramSize );
	final byte [] header = new byte [0x70];
	byte [] ram;
	
	int endTime;
	int playPeriod;
	int nextPlay;

	GbApu apu = new GbApu();
	
	protected int loadFile_( byte in [] )
	{
		if ( !isHeader( in, "GBS\u0001" ) )
			error( "Not a GBS file" );
		
		rstBase = getLE16( in, loadAddrOff );
		ram = rom.load( in, header, rstBase, 0xFF );
		
		setClockRate( 4194304 );
		apu.setOutput( buf.center(), buf.left(), buf.right() );
		
		return header [trackCountOff] & 0xFF;
	}
	
	final void setBank( int n )
	{
		int addr = rom.maskAddr( n * bankSize );
		if ( addr == 0 && rom.size() > bankSize )
			n = 1;
		mapMemory( bankSize, bankSize, rom.mapAddr( addr ) );
	}
	
	static final byte [] rates = { 10, 4, 6, 8 };
	
	void updateTimer()
	{
		playPeriod = 70224; // 59.73 Hz
		if ( (header [timerModeOff] & 0x04) != 0 )
		{
			int shift = rates [ram [hiPage + 7] & 3] - (header [timerModeOff] >> 7 & 1);
			playPeriod = (256 - (ram [hiPage + 6] & 0xFF)) << shift;
		}
	}
	
	static final int [] sound_data = {
		0x80, 0xBF, 0x00, 0x00, 0xBF, // square 1
		0x00, 0x3F, 0x00, 0x00, 0xBF, // square 2
		0x7F, 0xFF, 0x9F, 0x00, 0xBF, // wave
		0x00, 0xFF, 0x00, 0x00, 0xBF, // noise
		0x77, 0xFF, 0x80, // vin/volume, status, power mode
		0, 0, 0, 0, 0, 0, 0, 0, 0, // unused
		0xAC, 0xDD, 0xDA, 0x48,	0x36, 0x02, 0xCF, 0x16,	// waveform data
		0x2C, 0x04, 0xE5, 0x2C,	0xAC, 0xDD, 0xDA, 0x48
	};
	
	void cpuCall( int addr )
	{
		assert sp == getLE16( header, stackPtrOff );
		pc = addr;
		cpuWrite( --sp, idleAddr >> 8 );
		cpuWrite( --sp, idleAddr&0xFF );
	}
	
	public void startTrack( int track )
	{
		super.startTrack( track );
		
		apu.reset();
		apu.write( 0, 0xFF26, 0x80 ); // power on
		for ( int i = 0; i < sound_data.length; i++ )
			apu.write( 0, i + apu.startAddr, sound_data [i] );
		
		reset( ram, rom.unmapped() );
		mapMemory( ramAddr, 0x10000 - ramAddr, 0 );
		mapMemory( 0, bankSize, rom.mapAddr( 0 ) );
		setBank( 1 );
		
		java.util.Arrays.fill( ram,      0, 0x4000,  (byte)    0 );
		java.util.Arrays.fill( ram, 0x4000, 0x5F80,  (byte) 0xFF );
		java.util.Arrays.fill( ram, 0x5F80, ramSize, (byte)    0 );
		
		ram [hiPage] = 0; // joypad reads back as 0
		ram [mapAddr( idleAddr )] = (byte) 0xED; // illegal instruction
		
		ram [hiPage + 6] = header [timerModuloOff];
		ram [hiPage + 7] = header [timerModeOff];
		updateTimer();
		nextPlay = playPeriod;
		
		a  = track;
		pc = idleAddr;
		sp = getLE16( header, stackPtrOff );
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
				// TODO: PC overflow handling
				pc = (pc + 1) & 0xFFFF;
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
	
	protected int cpuRead( int addr )
	{
		if ( debug ) assert 0 <= addr && addr < 0x10000;
		
		if ( apu.startAddr <= addr && addr <= apu.endAddr )
			return apu.read( time + endTime, addr );
		
		return ram [mapAddr( addr )] & 0xFF;
	}
	
	protected void cpuWrite( int addr, int data )
	{
		if ( debug ) assert 0 <= data && data < 0x100;
		if ( debug ) assert 0 <= addr && addr < 0x10000;
		
		int offset = addr - ramAddr;
		if ( offset >= 0 )
		{
			ram [offset] = (byte) data;
			if ( addr < 0xFF80 && addr >= 0xFF00 )
			{
				if ( apu.startAddr <= addr && addr <= apu.endAddr )
				{
					apu.write( time + endTime, addr, data );
				}
				else if ( (addr ^ 0xFF06) < 2 )
				{
					updateTimer();
				}
				else if ( addr == 0xFF00 )
				{
					ram [offset] = 0; // keep joypad return value 0
				}
				else
				{
					ram [offset] = (byte) 0xFF;
				}
			}
		}
		else if ( (addr ^ 0x2000) <= 0x2000 - 1 )
		{
			setBank( data );
		}
	}
}

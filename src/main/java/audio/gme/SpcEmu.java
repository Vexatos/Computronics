package audio.gme;

// Nintendo SPC music file player
// http://www.slack.net/~ant/

/* Copyright (C) 2007 Shay Green. This module is free software; you
can redistribute it and/or modify it under the terms of the GNU Lesser
General Public License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version. This
module is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
details. You should have received a copy of the GNU Lesser General Public
License along with this module; if not, write to the Free Software Foundation,
Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA */

public final class SpcEmu extends SpcCpu
{
	private static final class Timer
	{
		int time; // time of next event
		int prescaler;
		int period;
		int divider;
		int enabled;
		int counter;
	}
	
	static final int ramSize		= 0x10000;
	static final int ramPadSize		= 0x100;
	
	// header offsets
	static final int cpuStateOff	= 0x25;
	static final int ramOff			= 0x100;
	static final int dspStateOff	= 0x10100;
	
	static final int romAddr		= 0xFFC0;
	
	// SMP registers
	static final int testReg		= 0x0;
	static final int controlReg		= 0x1;
	static final int dspaddrReg		= 0x2;
	static final int dspdataReg		= 0x3;
	static final int cpuio0Reg		= 0x4;
	static final int cpuio1Reg		= 0x5;
	static final int cpuio2Reg		= 0x6;
	static final int cpuio3Reg		= 0x7;
	static final int f8Reg			= 0x8;
	static final int f9Reg			= 0x9;
	static final int t0targetReg	= 0xA;
	static final int t1targetReg	= 0xB;
	static final int t2targetReg	= 0xC;
	static final int t0outReg		= 0xD;
	static final int t1outReg		= 0xE;
	static final int t2outReg		= 0xF;
	
	static final int romSize = 0x40;
	static final int timerCount = 3;
	static final int regCount = 0x10;

	int				dspTime;
	int				romEnabled;
	byte []			spc;
	final byte []	rom    = new byte [romSize];
	final byte []	hiRam  = new byte [romSize];
	final byte []	ram    = new byte [ramSize + ramPadSize];
	final int []	regs   = new int [regCount];
	final int []	regsIn = new int [regCount];
	final SpcDsp	dsp    = new SpcDsp();
	final Timer []	timers = new Timer [timerCount];

	protected int setSampleRate_( int rate ) { return 32000; }
	
	protected int loadFile_( byte [] in )
	{
		if ( !isHeader( in, "SNES-SPC700 Sound File Data" ) )
			error( "Not an SPC file" );
		
		spc = in;
		
		// almost no SPC music rely on more than last two bytes of boot ROM
		java.util.Arrays.fill( rom, 0, romSize, (byte) 0 );
		rom [0x3E] = (byte) 0xFF;
		rom [0x3F] = (byte) 0xC0;
		
		// TODO: use SPC file's copy of ROM, if present?
		
		return 1;
	}
	
	// Runs timer to present. Time must be >= t.time.
	static void runTimer_( Timer t, int time )
	{
		int elapsed = ((time - t.time) >> t.prescaler) + 1;
		t.time += elapsed << t.prescaler;
		
		if ( t.enabled != 0 )
		{
			int remain = ((t.period - t.divider - 1) & 0xFF) + 1;
			int divider = t.divider + elapsed;
			int over;
			if ( (over = elapsed - remain) >= 0 )
			{
				int n = over / t.period;
				t.counter = (t.counter + 1 + n) & 0x0F;
				divider = over - n * t.period;
			}
			t.divider = divider & 0xFF;
		}
	}
	
	// Runs timer to present if it's not already
	static void runTimer( Timer t, int time )
	{
		if ( time >= t.time )
			runTimer_( t, time );
	}
	
	// Enables/disables boot ROM by swapping it out of RAM
	private void enableRom( int enable )
	{
		if ( romEnabled != enable )
		{
			romEnabled = enable;
			if ( enable != 0 )
			{
				System.arraycopy( ram, romAddr, hiRam, 0, romSize );
				System.arraycopy( rom, 0, ram, romAddr, romSize );
			}
			else
			{
				System.arraycopy( hiRam, 0, ram, romAddr, romSize );
			}
			// TODO: ROM can still get overwritten when DSP writes to echo buffer
		}
	}
	
	public void startTrack( int track )
	{
		super.startTrack( track );
		
		time     = 0;
		dspTime = 32;
		
		// RAM
		java.util.Arrays.fill( ram, ramSize, ram.length, (byte) 0xFF );
		System.arraycopy( spc, ramOff, ram, 0, ramSize );
		
		dsp.init( ram, spc, dspStateOff );
		
		// CPU
		reset( ram );
		pc = (spc [cpuStateOff + 1] & 0xFF) << 8 | (spc [cpuStateOff] & 0xFF);
		a  = spc [cpuStateOff + 2] & 0xFF;
		x  = spc [cpuStateOff + 3] & 0xFF;
		y  = spc [cpuStateOff + 4] & 0xFF;
		sp = spc [cpuStateOff + 6] & 0xFF;
		setPsw( spc [cpuStateOff + 5] & 0xFF );
		
		// SMP registers
		for ( int i = 0; i < regCount; i++ )
			regsIn [i] = regs [i] = ram [0xF0 + i] & 0xFF;
		
		regsIn [testReg    ] = 0; // these always read back as 0
		regsIn [controlReg ] = 0;
		regsIn [t0targetReg] = 0;
		regsIn [t1targetReg] = 0;
		regsIn [t2targetReg] = 0;
		
		// ROM
		romEnabled = 0;
		enableRom( regs [controlReg] & 0x80 );
		
		// Timers
		for ( int i = 0; i < timerCount; i++ )
		{
			Timer t = timers [i] = new Timer();
			t.time = 1;
			t.divider = 0;
			t.period  = ((regs [t0targetReg + i] - 1) & 0xFF) + 1;
			t.enabled = regs [controlReg] >> i & 1;
			t.counter = regsIn [t0outReg + i] & 0x0F;
		}
		
		timers [2].prescaler = 4;
		timers [1].prescaler = 4 + 3;
		timers [0].prescaler = 4 + 3;
		
		// Clear echo
		if ( (dsp.regs [dsp.r_flg] & 0x20) == 0 )
		{
			int addr = (dsp.regs [dsp.r_esa] & 0xFF) << 8;
			int end  = addr + ((dsp.regs [dsp.r_edl] & 0x0F) << 11);
			if ( end > ramSize )
				end = ramSize;
			java.util.Arrays.fill( ram, addr, end, (byte) 0xFF );
		}
	}
	
	protected int play_( byte out [], int count )
	{
		dsp.setOutput( out );
		
		// Run for count/2*32 clocks + extra to get DSP time half-way between samples,
		// since CPU might run for slightly less than requested
		int clockCount = count * (32 / 2) + 16 - ((time - dspTime) & 31);
		time            -= clockCount;
		dspTime         -= clockCount;
		timers [0].time -= clockCount;
		timers [1].time -= clockCount;
		timers [2].time -= clockCount;
		runCpu();
		
		if ( time < 0 ) // emulation error
		{
			logError();
			return 0;
		}
		
		// Catch up to CPU
		runTimer( timers [0], time );
		runTimer( timers [1], time );
		runTimer( timers [2], time );
		
		// Run DSP to present
		int delta;
		if ( (delta = time - dspTime) >= 0 )
		{
			delta = (delta >> 5) + 1;
			dspTime += delta << 5;
			dsp.run( delta );
		}
		
		assert dsp.sampleCount() == count;
		return dsp.sampleCount();
	}
	
	// Writes to SMP register
	private void writeReg( int addr, int data )
	{
		switch ( addr )
		{
		case t0targetReg:
		case t1targetReg:
		case t2targetReg: {
			Timer t = timers [addr - t0targetReg];
			int period = ((data - 1) & 0xFF) + 1;
			if ( t.period != period )
			{
				runTimer( t, time );
				t.period = period;
			}
			break;
		}
		
		case t0outReg:
		case t1outReg:
		case t2outReg:
			// TODO
			//if ( data < no_read_before_write / 2 )
			//	run_timer( &m.timers [addr - t0outReg], time - 1 )->counter = 0;
			break;
		
		// Registers that act like RAM
		case 0x8:
		case 0x9:
			regsIn [addr] = data;
			break;
		
		case testReg:
			//if ( (uint8_t) data != 0x0A )
			//	dprintf( "SPC wrote to test register\n" );
			break;
		
		case controlReg:
			// port clears
			if ( (data & 0x10) != 0 )
			{
				regsIn [cpuio0Reg] = 0;
				regsIn [cpuio1Reg] = 0;
			}
			if ( (data & 0x20) != 0 )
			{
				regsIn [cpuio2Reg] = 0;
				regsIn [cpuio3Reg] = 0;
			}
			
			// timers
			for ( int i = 0; i < timerCount; i++ )
			{
				Timer t = timers [i];
				int enabled = data >> i & 1;
				if ( t.enabled != enabled )
				{
					runTimer( t, time );
					t.enabled = enabled;
					if ( enabled != 0 )
					{
						t.divider = 0;
						t.counter = 0;
					}
				}
			}
			enableRom( data & 0x80 );
			break;
		}
	}
	
	public final void cpuWrite( int addr, int data )
	{
		// RAM
		ram [addr] = (byte) data;
		if ( (addr -= 0xF0) >= 0 ) // 64%
		{
			// $F0-$FF
			if ( addr < regCount ) // 87%
			{
				regs [addr] = (data &= 0xFF);
				
				// Ports
				
				// Registers other than $F2 and $F4-$F7
				//if ( addr != 2 && addr != 4 && addr != 5 && addr != 6 && addr != 7 )
				if ( (~0x2F000000 << addr) < 0 ) // 36%
				{
					if ( addr == dspdataReg ) // 99%
					{
						// Run DSP to present
						int delta;
						if ( (delta = time - dspTime) >= 0 ) // 95%
						{
							delta = (delta >> 5) + 1;
							dspTime += delta << 5;
							dsp.run( delta );
						}
						
						int dspaddr;
						if ( (dspaddr = regs [dspaddrReg]) <= 0x7F )
							dsp.write( dspaddr, data );
					}
					else
					{
						writeReg( addr, data );
					}
				}
			}
			// IPL ROM area or address wrapped around
			else if ( (addr -= romAddr - 0xF0) >= 0 ) // 1% in IPL ROM area or address wrapped around
			{
				if ( addr < romSize )
				{
					hiRam [addr] = (byte) data;
					if ( romEnabled != 0 )
						ram [addr + romAddr] = rom [addr]; // restore overwritten ROM
				}
				else
				{
					if ( debug ) assert ram [addr + romAddr] == (byte) data;
					ram [addr + romAddr] = (byte) 0xFF; // restore overwritten padding
					cpuWrite( data, addr - (ramSize - romAddr) );
				}
			}
		}
	}
	
	public final int cpuRead( int addr )
	{
		// Low RAM
		if ( addr < 0xF0 ) // 60%
			return ram [addr] & 0xFF;
		
		// Timers
		if ( (addr ^= 0xFF) < timerCount ) // 68%
		{
			Timer t = timers [2 - addr]; // TODO: reorder timers to eliminate 2-
			if ( time >= t.time )
				runTimer_( t, time );
			int result = t.counter;
			t.counter = 0;
			return result;
		}
		
		// Other registers
		if ( (addr ^= 0xFF) <= 0xFF ) // 9%
		{
			if ( addr == dspaddrReg + 0xF0 )
				return regs [dspaddrReg];
			
			if ( addr == dspdataReg + 0xF0 )
			{
				// DSP
				
				// Run to present
				int delta;
				if ( (delta = time - dspTime) >= 0 ) // 1%
				{
					delta = (delta >> 5) + 1;
					dspTime += delta << 5;
					dsp.run( delta );
				}
				
				return dsp.regs [regs [dspaddrReg] & 0x7F] & 0xFF;
			}
			
			return regsIn [addr - 0xF0];
		}
		
		// RAM
		if ( addr <= 0xFFFF ) // 99%
			return ram [addr] & 0xFF;
		
		// Address wrapped around
		return cpuRead( addr - 0x10000 );
	}
}

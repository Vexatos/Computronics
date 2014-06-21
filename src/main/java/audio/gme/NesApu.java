package audio.gme;

// Nintendo NES sound chip emulator
// http://www.slack.net/~ant/

/* Copyright (C) 2003-2010 Shay Green. This module is free software; you
can redistribute it and/or modify it under the terms of the GNU Lesser
General Public License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version. This
module is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
details. You should have received a copy of the GNU Lesser General Public
License along with this module; if not, write to the Free Software Foundation,
Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA */

class NesOsc
{
	static final int squareUnit   = (int) (0.125 / 15  * 65535);
	static final int triangleUnit = (int) (0.150 / 15  * 65535);
	static final int noiseUnit    = (int) (0.095 / 15  * 65535);
	static final int dmcUnit      = (int) (0.450 / 127 * 65535);
	
	final int [] regs = new int [4];
	final boolean [] regWritten = new boolean [4];
	int lengthCounter;// length counter (0 if unused by oscillator)
	int delay;        // delay until next (potential) transition
	int lastAmp;     // last amplitude oscillator was outputting
	
	void clockLength( int halt_mask )
	{
		if ( lengthCounter != 0 && (regs [0] & halt_mask) == 0 )
			lengthCounter--;
	}
	
	int period() { return (regs [3] & 7) * 0x100 + (regs [2] & 0xFF); }
	
	void reset()
	{
		delay = 0;
		lastAmp = 0;
	}
	
	int updateAmp( int amp )
	{
		int delta = amp - lastAmp;
		lastAmp = amp;
		return delta;
	}
}

class NesEnvelope extends NesOsc
{
	int envVolume;
	int envDelay;
	
	void clockEnvelope()
	{
		int period = regs [0] & 15;
		if ( regWritten [3] )
		{
			regWritten [3] = false;
			envDelay = period;
			envVolume = 15;
		}
		else if ( --envDelay < 0 )
		{
			envDelay = period;
			if ( (envVolume | (regs [0] & 0x20)) != 0 )
				envVolume = (envVolume - 1) & 15;
		}
	}
	
	int volume()
	{
		if ( lengthCounter == 0 )
			return 0;
		
		if ( (regs [0] & 0x10) != 0 )
			return regs [0] & 0x0F;
		
		return envVolume;
	}
	
	void reset()
	{
		envVolume = 0;
		envDelay  = 0;
		super.reset();
	}
}

final class NesSquare extends NesEnvelope
{
	static final int negateMask = 0x08;
	static final int shiftMask  = 0x07;
	static final int phaseRange = 8;
	int phase;
	int sweepDelay;
	
	void reset()
	{
		sweepDelay = 0;
		super.reset();
	}
	
	void clockSweep( int negative_adjust )
	{
		int sweep = regs [1];
		
		if ( --sweepDelay < 0 )
		{
			regWritten [1] = true;
			
			int period = this.period();
			int shift = sweep & shiftMask;
			if ( shift != 0 && (sweep & 0x80) != 0 && period >= 8 )
			{
				int offset = period >> shift;
				
				if ( (sweep & negateMask) != 0 )
					offset = negative_adjust - offset;
				
				if ( period + offset < 0x800 )
				{
					period += offset;
					// rewrite period
					regs [2] = period & 0xFF;
					regs [3] = (regs [3] & ~7) | ((period >> 8) & 7);
				}
			}
		}
		
		if ( regWritten [1] )
		{
			regWritten [1] = false;
			sweepDelay = (sweep >> 4) & 7;
		}
	}
	
	void run( BlipBuffer output, int time, int endTime )
	{
		final int period = this.period();
		final int timer_period = (period + 1) * 2;
		
		int offset = period >> (regs [1] & shiftMask);
		if ( (regs [1] & negateMask) != 0 )
			offset = 0;
		
		final int volume = this.volume();
		if ( volume == 0 || period < 8 || (period + offset) > 0x7FF )
		{
			if ( lastAmp != 0 )
			{
				output.addDelta( time, lastAmp * -squareUnit );
				lastAmp = 0;
			}
			
			time += delay;
			
			int remain = endTime - time;
			if ( remain > 0 )
			{
				int count = (remain + timer_period - 1) / timer_period;
				phase = (phase + count) & (phaseRange - 1);
				time += count * timer_period;
			}
		}
		else
		{
			// handle duty select
			int duty_select = (regs [0] >> 6) & 3;
			int duty = 1 << duty_select; // 1, 2, 4, 2
			int amp = 0;
			if ( duty_select == 3 )
			{
				duty = 2; // negated 25%
				amp = volume;
			}
			if ( phase < duty )
				amp ^= volume;
			
			{
				int delta = updateAmp( amp );
				if ( delta != 0 )
					output.addDelta( time, delta * squareUnit );
			}
			
			time += delay;
			if ( time < endTime )
			{
				int phase = this.phase; // cache
				int delta = (amp * 2 - volume) * squareUnit;
				
				do
				{
					if ( (phase = (phase + 1) & (phaseRange - 1)) == 0 ||
							phase == duty )
						output.addDelta( time, delta = -delta );
				}
				while ( (time += timer_period) < endTime );
				
				this.phase = phase;
				lastAmp = (delta < 0 ? 0 : volume);
			}
		}
		
		delay = time - endTime;
	}
}

final class NesTriangle extends NesOsc
{
	static final int phaseRange = 16;
	int phase;
	int linearCounter;
	
	void reset()
	{
		linearCounter = 0;
		phase         = phaseRange;
		super.reset();
	}
	
	void clockLinearCounter()
	{
		if ( regWritten [3] )
			linearCounter = regs [0] & 0x7F;
		else if ( linearCounter != 0 )
			linearCounter--;
		
		if ( (regs [0] & 0x80) == 0 )
			regWritten [3] = false;
	}
	
	int calc_amp()
	{
		int amp = phaseRange - phase;
		if ( amp < 0 )
			amp = phase - (phaseRange + 1);
		return amp;
	}
	
	void run( BlipBuffer output, int time, int endTime )
	{
		final int timer_period = period() + 1;
		
		// to do: track phase when period < 3
		// to do: Output 7.5 on dac when period < 2? More accurate, but results in more clicks.
		
		int delta = updateAmp( calc_amp() );
		if ( delta != 0 )
			output.addDelta( time, delta * triangleUnit );
		
		time += delay;
		if ( lengthCounter == 0 || linearCounter == 0 || timer_period < 3 )
		{
			time = endTime;
		}
		else if ( time < endTime )
		{
			int volume = triangleUnit;
			if ( phase > phaseRange )
			{
				phase -= phaseRange;
				volume = -volume;
			}
			
			do
			{
				if ( --phase != 0 )
				{
					output.addDelta( time, volume );
				}
				else
				{
					phase = phaseRange;
					volume = -volume;
				}
			}
			while ( (time += timer_period) < endTime );
			
			if ( volume < 0 )
				phase += phaseRange;
			lastAmp = calc_amp();
		}
		delay = time - endTime;
	}
}

final class NesNoise extends NesEnvelope
{
	int lfsr;
	
	static final int [] noisePeriods = {
		0x004, 0x008, 0x010, 0x020, 0x040, 0x060, 0x080, 0x0A0,
		0x0CA, 0x0FE, 0x17C, 0x1FC, 0x2FA, 0x3F8, 0x7F2, 0xFE4
	};

	void run( BlipBuffer output, int time, int endTime )
	{
		final int volume = this.volume();
		int amp = (lfsr & 1) != 0 ? volume : 0;
		{
			int delta = updateAmp( amp );
			if ( delta != 0 )
				output.addDelta( time, delta * noiseUnit );
		}
		
		time += delay;
		if ( time < endTime )
		{
			final int period = noisePeriods [regs [2] & 15];
			final int tap = (regs [2] & 0x80) != 0 ? 8 : 13;
			
			if ( volume == 0 )
			{
				// round to next multiple of period
				time += (endTime - time + period - 1) / period * period;
				
				// approximate noise cycling while muted, by shuffling up noise register
				int feedback = (lfsr << tap) ^ (lfsr << 14);
				lfsr = (feedback & 0x4000) | (lfsr >> 1);
			}
			else
			{
				int lfsr = this.lfsr; // cache
				int delta = (amp * 2 - volume) * noiseUnit;
				
				do
				{
					if ( ((lfsr + 1) & 2) != 0 )
						output.addDelta( time, delta = -delta );
					
					lfsr = ((lfsr << tap) ^ (lfsr << 14)) & 0x4000 | (lfsr >> 1);
				}
				while ( (time += period) < endTime );
				
				this.lfsr = lfsr;
				lastAmp = (delta < 0 ? 0 : volume);
			}
		}
		
		delay = time - endTime;
	}
	
	void reset()
	{
		lfsr = 1 << 14;
		super.reset();
	}
}

final class NesDmc extends NesOsc
{
	static final int loop_flag = 0x40;
	
	int address;    // address of next byte to read
	int period;
	//int length_counter; // bytes remaining to play (already defined in NesOsc)
	int buf;
	int bits_remain;
	int bits;
	boolean buf_full;
	boolean silence;
	int dac;
	int irqEnabled;
	int irqFlag;
	boolean palMode;
	
	// in DMC since it needs to clear it
	int oscEnables;
	NesCpu cpu;

	void reset()
	{
		address = 0;
		dac = 0;
		buf = 0;
		bits_remain = 1;
		bits = 0;
		buf_full = false;
		silence = true;
		
		irqFlag    = 0;
		irqEnabled = 0;
		
		super.reset();
		period = 0x1AC;
	}
	
	static final int [] dmc_period_table = {
		428, 380, 340, 320, 286, 254, 226, 214, // NTSC
		190, 160, 142, 128, 106,  84,  72,  54,
	
		398, 354, 316, 298, 276, 236, 210, 198, // PAL
		176, 148, 132, 118,  98,  78,  66,  50
	};
	
	void reload_sample()
	{
		address = 0x4000 + regs [2] * 0x40;
		lengthCounter = regs [3] * 0x10 + 1;
	}
	
	static final int [] dac_table =
	{
		 0, 1, 2, 3, 4, 5, 6, 7, 7, 8, 9,10,11,12,13,14,
		15,15,16,17,18,19,20,20,21,22,23,24,24,25,26,27,
		27,28,29,30,31,31,32,33,33,34,35,36,36,37,38,38,
		39,40,41,41,42,43,43,44,45,45,46,47,47,48,48,49,
		50,50,51,52,52,53,53,54,55,55,56,56,57,58,58,59,
		59,60,60,61,61,62,63,63,64,64,65,65,66,66,67,67,
		68,68,69,70,70,71,71,72,72,73,73,74,74,75,75,75,
		76,76,77,77,78,78,79,79,80,80,81,81,82,82,82,83,
	};
	
	void write_register( int addr, int data )
	{
		if ( addr == 0 )
		{
			period = dmc_period_table [(data & 15) + (palMode ? 16 : 0)];
			
			irqEnabled = 1;
			if ( (data & 0xC0) != 0x80 )
			{
				irqEnabled = 0;
				irqFlag    = 0;
			}
		}
		else if ( addr == 1 )
		{
			// adjust lastAmp so that "pop" amplitude will be properly non-linear
			// with respect to change in dac
			data &= 0x7F;
			lastAmp = data - dac_table [data] + dac_table [dac];
			dac = data;
		}
	}
	
	void start()
	{
		reload_sample();
		fill_buffer();
	}
	
	void fill_buffer()
	{
		if ( !buf_full && lengthCounter != 0 )
		{
			// Read byte via CPU
			buf = cpu.cpuRead( 0x8000 + address );
			address = (address + 1) & 0x7FFF;
			buf_full = true;
			
			if ( --lengthCounter == 0 ) // Reached end of sample
			{
				if ( (regs [0] & loop_flag) != 0 )
				{
					reload_sample();
				}
				else
				{
					oscEnables &= ~0x10;
					irqFlag = irqEnabled;
				}
			}
		}
	}
	
	void run( BlipBuffer output, int time, int endTime )
	{
		int delta = updateAmp( dac );
		if ( delta != 0 )
			output.addDelta( time, delta * dmcUnit );
		
		time += delay;
		if ( time < endTime )
		{
			if ( silence && !buf_full )
			{
				int count = (endTime - time + period - 1) / period;
				bits_remain = (bits_remain - 1 + 8 - (count % 8)) % 8 + 1;
				time += count * period;
			}
			else
			{
				do
				{
					if ( !silence )
					{
						int step;
						int newDac = dac + (step = (bits << 2 & 4) - 2);
						// if ( newDac >= 0 && newDac <= 0x7F )
						if ( (byte) newDac >= 0 )
						{
							dac = newDac;
							output.addDelta( time, step * dmcUnit );
						}
						bits >>= 1;
					}
					
					if ( --bits_remain == 0 )
					{
						bits_remain = 8;
						silence = true;
						if ( buf_full )
						{
							buf_full = false;
							silence  = false;
							bits     = buf;
							fill_buffer();
						}
					}
				}
				while ( (time += period) < endTime );
				
				lastAmp = dac;
			}
		}
		delay = time - endTime;
	}
}

public final class NesApu
{
	public NesApu()
	{
		oscs [0] = square1;
		oscs [1] = square2;
		oscs [2] = triangle;
		oscs [3] = noise;
		oscs [4] = dmc;
	}
	
	public void setOutput( BlipBuffer b ) { output = b; }
	
	// Resets oscillators and internal state
	public void reset( NesCpu cpu, boolean palMode )
	{
		dmc.cpu     = cpu;
		dmc.palMode = palMode;
		
		framePeriod    = palMode ? 8314 : 7458;
		frameTime      = framePeriod;
		lastTime       = 0;
		irqFlag        = 0;
		dmc.oscEnables = 0;
		
		square1 .reset();
		square2 .reset();
		triangle.reset();
		noise   .reset();
		dmc     .reset();
		
		write( 0, 0x4017, 0x00 );
		write( 0, 0x4015, 0x00 );
		
		for ( int addr = 0x4000; addr <= 0x4013; addr++ )
			write( 0, addr, (addr & 3) != 0 ? 0x00 : 0x10 );
		
		dmc.lastAmp = dmc.dac = 0; // prevents click
	}
	
	// Writes data to address at specified time
	public static final int startAddr = 0x4000;
	public static final int endAddr   = 0x4017;
	public void write( int time, int addr, int data )
	{
		assert 0 <= data && data < 0x100;
		assert startAddr <= addr && addr <= endAddr;
		
		runUntil( time );
		
		if ( addr < 0x4014 )
		{
			// Write to channel
			int index = (addr - startAddr) >> 2;
			NesOsc osc = oscs [index];
			
			int reg = addr & 3;
			osc.regs       [reg] = data;
			osc.regWritten [reg] = true;
			
			if ( index == 4 )
			{
				// handle DMC specially
				dmc.write_register( reg, data );
			}
			else if ( reg == 3 )
			{
				// load length counter
				if ( (dmc.oscEnables >> index & 1) != 0 )
					osc.lengthCounter = length_table [data >> 3 & 0x1F];
				
				// reset square phase
				if ( index < 2 )
					((NesSquare) osc).phase = NesSquare.phaseRange - 1;
			}
		}
		else if ( addr == 0x4015 )
		{
			// Channel enables
			for ( int i = oscCount; i-- > 0; )
				if ( (data >> i & 1) == 0 )
					oscs [i].lengthCounter = 0;
			
			dmc.irqFlag = 0;
			
			int justEnabled = data & ~dmc.oscEnables;
			dmc.oscEnables = data;
			
			if ( (justEnabled & 0x10) != 0 )
				dmc.start();
		}
		else if ( addr == 0x4017 )
		{
			// Frame mode
			frameMode = data;
			
			if ( (data & 0x40) != 0 )
				irqFlag = 0;
			
			// mode 1
			frameTime = time;
			framePhase = 0;
			
			if ( (data & 0x80) == 0 )
			{
				// mode 0
				framePhase = 1;
				frameTime += framePeriod;
			}
		}

	}
	
	// Reads from status register at specified time
	public int read( int time )
	{
		runUntil( time );
		
		int result = (dmc.irqFlag << 7) | (irqFlag << 6);
		irqFlag = 0;
		
		for ( int i = 0; i < oscCount; i++ )
			if ( oscs [i].lengthCounter != 0 )
				result |= 1 << i;
		
		return result;
	}
	
	// Runs all oscillators up to specified time, ends current time frame, then
	// starts a new frame at time 0
	public void endFrame( int endTime )
	{
		if ( endTime > lastTime )
			runUntil( endTime );
		
		assert frameTime >= endTime;
		frameTime -= endTime;
		
		assert lastTime >= endTime;
		lastTime -= endTime;
	}
	
	static final int [] length_table = {
		0x0A, 0xFE, 0x14, 0x02, 0x28, 0x04, 0x50, 0x06,
		0xA0, 0x08, 0x3C, 0x0A, 0x0E, 0x0C, 0x1A, 0x0E, 
		0x0C, 0x10, 0x18, 0x12, 0x30, 0x14, 0x60, 0x16,
		0xC0, 0x18, 0x48, 0x1A, 0x10, 0x1C, 0x20, 0x1E
	};
	
	static final int oscCount = 5;
	final NesOsc [] oscs = new NesOsc [oscCount];
	final NesSquare   square1  = new NesSquare();
	final NesSquare   square2  = new NesSquare();
	final NesTriangle triangle = new NesTriangle();
	final NesNoise    noise    = new NesNoise();
	final NesDmc      dmc      = new NesDmc();
	BlipBuffer output;
	int framePeriod;
	int frameTime;
	int framePhase;
	int lastTime;
	int frameMode;
	int irqFlag;
	
	void runUntil( int endTime )
	{
		assert endTime >= lastTime; // endTime must not be before previous time
		if ( endTime == lastTime )
			return;
		
		while ( true )
		{
			// run oscillators
			int time = endTime;
			if ( time > frameTime )
				time = frameTime;
			
			square1 .run( output, lastTime, time );
			square2 .run( output, lastTime, time );
			triangle.run( output, lastTime, time );
			noise   .run( output, lastTime, time );
			dmc     .run( output, lastTime, time );
			lastTime = time;
			
			if ( time == endTime )
				break;
			
			// run frame sequencer
			frameTime += framePeriod;
			switch ( framePhase++ )
			{
			case 0:
				if ( (frameMode & 0xC0) == 0 )
					irqFlag = 1;
			case 2:
				// 120 Hz
				square1 .clockLength( 0x20 );
				square2 .clockLength( 0x20 );
				triangle.clockLength( 0x80 ); // different bit for halt flag on triangle
				noise   .clockLength( 0x20 );
				
				square1.clockSweep( -1 );
				square2.clockSweep(  0 );
				break;
			
			case 3:
				// 60 Hz
				framePhase = 0;
				if ( (frameMode & 0x80) != 0 )
					frameTime += framePeriod; // frame 3 is almost twice as long in mode 1
				break;
			}
			
			// 240 Hz
			square1 .clockEnvelope();
			square2 .clockEnvelope();
			triangle.clockLinearCounter();
			noise   .clockEnvelope();
		}
	}
}

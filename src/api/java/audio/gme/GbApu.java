package audio.gme;

// Nintendo Game Boy sound emulator
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

class GbOsc
{
	static final boolean gbc_02 = false; // TODO: allow to be set?
	static final int trigger_mask   = 0x80;
	static final int length_enabled = 0x40;
	static final int dac_bias       = 7;
	
	BlipBuffer output;
	int output_select;
	final int [] regs = new int [5];
	
	int vol_unit;
	int delay;
	int last_amp;
	int length;
	int enabled;
	
	void reset()
	{
		output        = null;
		output_select = 0;
		delay         = 0;
		last_amp      = 0;
		length        = 64;
		enabled       = 0;
		
		for ( int i = 5; --i >= 0; )
			regs [i] = 0;
	}
	
	void clock_length()
	{
		if ( (regs [4] & length_enabled) != 0 && length != 0 )
		{
			if ( --length <= 0 )
				enabled = 0;
		}
	}

	int frequency() { return (regs [4] & 7) * 0x100 + regs [3]; }
	
	boolean write_register( int frame_phase, int reg, int old_data, int data ) { return false; }
	
	int write_trig( int frame_phase, int max_len, int old_data )
	{
		int data = regs [4];
		
		if ( gbc_02 && (frame_phase & 1) != 0 && (old_data & length_enabled) == 0 && length != 0 )
			length--;
		
		if ( (data & trigger_mask) != 0 )
		{
			enabled = 1;
			if ( length == 0 )
			{
				length = max_len;
				if ( gbc_02 && (frame_phase & 1) != 0 && (data & length_enabled) != 0 )
					length--;
			}
		}
		
		if ( gbc_02 && length == 0 )
			enabled = 0;
		
		return data & trigger_mask;
	}
}

class GbEnv extends GbOsc
{
	int env_delay;
	int volume;
	
	int dac_enabled() { return regs [2] & 0xF8; }
	
	void reset()
	{
		env_delay = 0;
		volume    = 0;
		super.reset();
	}
	
	int reload_env_timer()
	{
		int raw = regs [2] & 7;
		env_delay = (raw != 0 ? raw : 8);
		return raw;
	}
	
	void clock_envelope()
	{
		if ( --env_delay <= 0 && reload_env_timer() != 0 )
		{
			int v = volume + ((regs [2] & 0x08) != 0 ? +1 : -1);
			if ( 0 <= v && v <= 15 )
				volume = v;
		}
	}
	
	boolean write_register( int frame_phase, int reg, int old_data, int data )
	{
		final int max_len = 64;
		
		switch ( reg )
		{
		case 1:
			length = max_len - (data & (max_len - 1));
			break;
		
		case 2:
			if ( dac_enabled() == 0 )
				enabled = 0;
			
			// TODO: once zombie mode used, envelope not clocked?
			if ( ((old_data ^ data) & 8) != 0 )
			{
				int step = 0;
				if ( (old_data & 7) != 0 )
					step = +1;
				else if ( (data & 7) != 0 )
					step = -1;
				
				if ( (data & 8) != 0 )
					step = -step;
				
				volume = (15 + step - volume) & 15;
			}
			else
			{
				int step = ((old_data & 7) != 0 ? 2 : 0) | ((data & 7) != 0 ? 0 : 1);
				volume = (volume + step) & 15;
			}
			break;
		
		case 4:
			if ( write_trig( frame_phase, max_len, old_data ) != 0 )
			{
				volume = regs [2] >> 4;
				reload_env_timer();
				if ( frame_phase == 7 )
					env_delay++;
				if ( dac_enabled() == 0 )
					enabled = 0;
				return true;
			}
		}
		return false;
	}
}

class GbSquare extends GbEnv
{
	int phase;
	
	final int period() { return (2048 - frequency()) * 4; }
	
	void reset()
	{
		phase = 0;
		super.reset();
		delay = 0x40000000; // TODO: less hacky (never clocked until first trigger)
	}
	
	boolean write_register( int frame_phase, int reg, int old_data, int data )
	{
		boolean result = super.write_register( frame_phase, reg, old_data, data );
		if ( result )
			delay = period();
		return result;
	}
	
	static final byte [] duty_offsets = { 1, 1, 3, 7 };
	static final byte [] duties = { 1, 2, 4, 6 };
	
	void run( int time, int end_time )
	{
		final int duty_code = regs [1] >> 6;
		final int duty_offset = duty_offsets [duty_code];
		final int duty = duties [duty_code];
		int playing = 0;
		int amp = 0;
		int phase = (this.phase + duty_offset) & 7;
		
		if ( output != null )
		{
			if ( volume != 0 )
			{
				playing = -enabled;
			
				if ( phase < duty )
					amp = volume & playing;
				
				// Treat > 16 kHz as DC
				if ( frequency() > 2041 && delay < 32 )
				{
					amp = (volume * duty) >> 3 & playing;
					playing = 0;
				}
			}
			
			if ( dac_enabled() == 0 )
			{
				playing = 0;
				amp = 0;
			}
			else
			{
				amp -= dac_bias;
			}
		
			int delta = amp - last_amp;
			if ( delta != 0 )
			{
				last_amp = amp;
				output.addDelta( time, delta * vol_unit );
			}
		}
		
		time += delay;
		if ( time < end_time )
		{
			final int period = this.period();
			if ( playing == 0 )
			{
				// maintain phase
				int count = (end_time - time + period - 1) / period;
				phase = (phase + count) & 7;
				time += count * period;
			}
			else
			{
				final BlipBuffer output = this.output;
				// TODO: eliminate ugly +dac_bias -dac_bias adjustments
				int delta = ((amp + dac_bias) * 2 - volume) * vol_unit;
				do
				{
					if ( (phase = (phase + 1) & 7) == 0 || phase == duty )
						output.addDelta( time, delta = -delta );
				}
				while ( (time += period) < end_time );
				
				last_amp = (delta < 0 ? 0 : volume) - dac_bias;
			}
			this.phase = (phase - duty_offset) & 7;
		}
		delay = time - end_time;
	}
}

final class GbSweepSquare extends GbSquare
{
	static final int period_mask = 0x70;
	static final int shift_mask  = 0x07;
	
	int sweep_freq;
	int sweep_delay;
	int sweep_enabled;
	int sweep_neg;
	
	void reset()
	{
		sweep_freq    = 0;
		sweep_delay   = 0;
		sweep_enabled = 0;
		sweep_neg     = 0;
		super.reset();
	}
	
	void reload_sweep_timer()
	{
		sweep_delay = (regs [0] & period_mask) >> 4;
		if ( sweep_delay == 0 )
			sweep_delay = 8;
	}
	
	void calc_sweep( boolean update )
	{
		int freq  = sweep_freq;
		int shift = regs [0] & shift_mask;
		int delta = freq >> shift;
		sweep_neg = regs [0] & 0x08;
		if ( sweep_neg != 0 )
			delta = -delta;
		freq += delta;
		
		if ( freq > 0x7FF )
		{
			enabled = 0;
		}
		else if ( shift != 0 && update )
		{
			sweep_freq = freq;
			regs [3] = freq & 0xFF;
			regs [4] = (regs [4] & ~0x07) | (freq >> 8 & 0x07);
		}
	}
	
	void clock_sweep()
	{
		if ( --sweep_delay <= 0 )
		{
			reload_sweep_timer();
			if ( sweep_enabled != 0 && (regs [0] & period_mask) != 0 )
			{
				calc_sweep( true );
				calc_sweep( false );
			}
		}
	}
	
	boolean write_register( int frame_phase, int reg, int old_data, int data )
	{
		if ( reg == 0 && (sweep_neg & 0x08 & ~data) != 0 )
			enabled = 0;
		
		if ( super.write_register( frame_phase, reg, old_data, data ) )
		{
			sweep_freq = frequency();
			reload_sweep_timer();
			sweep_enabled = regs [0] & (period_mask | shift_mask);
			if ( (regs [0] & shift_mask) != 0 )
				calc_sweep( false );
		}
		
		return false;
	}
}

final class GbNoise extends GbEnv
{
	int bits;
	
	boolean write_register( int frame_phase, int reg, int old_data, int data )
	{
		if ( reg == 3 )
		{
			int p = period();
			if ( p != 0 )
				delay %= p; // TODO: not entirely correct
		}
		
		if ( super.write_register( frame_phase, reg, old_data, data ) )
			bits = 0x7FFF;
		
		return false;
	}
	
	static final byte [] noise_periods = { 8, 16, 32, 48, 64, 80, 96, 112 };
	
	int period()
	{
		int shift = regs [3] >> 4;
		int p = noise_periods [regs [3] & 7] << shift;
		if ( shift >= 0x0E )
			p = 0;
		return p;
	}
	
	void run( int time, int end_time )
	{
		int feedback = (1 << 14) >> (regs [3] & 8);
		int playing = 0;
		int amp = 0;
		
		if ( output != null )
		{
			if ( volume != 0 )
			{
				playing = -enabled;
			
				if ( (bits & 1) == 0 )
					amp = volume & playing;
			}
			
			if ( dac_enabled() != 0 )
			{
				amp -= dac_bias;
			}
			else
			{
				amp = 0;
				playing = 0;
			}
			
			int delta = amp - last_amp;
			if ( delta != 0 )
			{
				last_amp = amp;
				output.addDelta( time, delta * vol_unit );
			}
		}
		
		time += delay;
		if ( time < end_time )
		{
			final int period = this.period();
			if ( period == 0 )
			{
				time = end_time;
			}
			else
			{
				int bits = this.bits;
				if ( playing == 0 )
				{
					// maintain phase
					int count = (end_time - time + period - 1) / period;
					time += count * period;
					
					// TODO: be sure this doesn't drag performance too much
					bits ^= (feedback << 1) & -(bits & 1);
					feedback *= 3;
					do
					{
						bits = (bits >> 1) ^ (feedback & -(bits & 2));
					}
					while ( --count > 0 );
					bits &= ~(feedback << 1);
				}
				else
				{
					final BlipBuffer output = this.output;
					// TODO: eliminate ugly +dac_bias -dac_bias adjustments
					int delta = ((amp + dac_bias) * 2 - volume) * vol_unit;
					
					do
					{
						int changed = bits + 1;
						bits >>= 1;
						if ( (changed & 2) != 0 )
						{
							bits |= feedback;
							output.addDelta( time, delta = -delta );
						}
					}
					while ( (time += period) < end_time );
					
					last_amp = (delta < 0 ? 0 : volume) - dac_bias;
				}
				this.bits = bits;
			}
		}
		delay = time - end_time;
	}
}

final class GbWave extends GbOsc
{
	int wave_pos;
	int sample_buf_high;
	int sample_buf;
	static final int wave_size = 32;
	int [] wave = new int [wave_size];
	
	int period() { return (2048 - frequency()) * 2; }
	int dac_enabled() { return regs [0] & 0x80; }
	
	int access( int addr )
	{
		if ( enabled != 0 )
			addr = 0xFF30 + (wave_pos >> 1);
		return addr;
	}
	
	void reset()
	{
		wave_pos = 0;
		sample_buf_high = 0;
		sample_buf = 0;
		length = 256;
		super.reset();
	}
	
	boolean write_register( int frame_phase, int reg, int old_data, int data )
	{
		final int max_len = 256;
		
		switch ( reg )
		{
		case 1:
			length = max_len - data;
			break;
		
		case 4:
			if ( write_trig( frame_phase, max_len, old_data ) != 0 )
			{
				wave_pos = 0;
				delay    = period() + 6;
				sample_buf = sample_buf_high;
			}
			// fall through
		case 0:
			if ( dac_enabled() == 0 )
				enabled = 0;
		}
		
		return false;
	}
	
	void run( int time, int end_time )
	{
		int volume_shift = regs [2] >> 5 & 3;
		int playing = 0;
		
		if ( output != null )
		{
			playing = -enabled;
			if ( --volume_shift < 0 )
			{
				volume_shift = 7;
				playing = 0;
			}
			
			int amp = sample_buf & playing;
			
			if ( frequency() > 0x7FB && delay < 16 )
			{
				// 16 kHz and above, act as DC at mid-level
				// (really depends on average level of entire wave,
				// but this is good enough)
				amp = 8;
				playing = 0;
			}
			
			amp >>= volume_shift;
			
			if ( dac_enabled() == 0 )
			{
				playing = 0;
				amp = 0;
			}
			else
			{
				amp -= dac_bias;
			}
	
			int delta = amp - last_amp;
			if ( delta != 0 )
			{
				last_amp = amp;
				output.addDelta( time, delta * vol_unit );
			}
		}
		
		time += delay;
		if ( time < end_time )
		{
			int wave_pos = (this.wave_pos + 1) & (wave_size - 1);
			final int period = this.period();
			if ( playing == 0 )
			{
				// maintain phase
				int count = (end_time - time + period - 1) / period;
				wave_pos += count; // will be masked below
				time += count * period;
			}
			else
			{
				final BlipBuffer output = this.output;
				int last_amp = this.last_amp + dac_bias;
				do
				{
					int amp = wave [wave_pos] >> volume_shift;
					wave_pos = (wave_pos + 1) & (wave_size - 1);
					int delta;
					if ( (delta = amp - last_amp) != 0 )
					{
						last_amp = amp;
						output.addDelta( time, delta * vol_unit );
					}
				}
				while ( (time += period) < end_time );
				this.last_amp = last_amp - dac_bias;
			}
			wave_pos = (wave_pos - 1) & (wave_size - 1);
			this.wave_pos = wave_pos;
			if ( enabled != 0 )
			{
				sample_buf_high = wave [wave_pos & ~1];
				sample_buf      = wave [wave_pos];
			}
		}
		delay = time - end_time;
	}
}

final public class GbApu
{
	public GbApu()
	{
		oscs [0] = square1;
		oscs [1] = square2;
		oscs [2] = wave;
		oscs [3] = noise;
		
		reset();
	}
	
	// Resets oscillators and internal state
	public void setOutput( BlipBuffer center, BlipBuffer left, BlipBuffer right )
	{
		outputs [1] = right;
		outputs [2] = left;
		outputs [3] = center;
		
		for ( int i = osc_count; --i >= 0; )
			oscs [i].output = outputs [oscs [i].output_select];
	}
	
	private void update_volume()
	{
		final int unit = (int) (1.0 / osc_count / 15 / 8 * 65536);
		
		// TODO: doesn't handle left != right volume (not worth the complexity)
		int data = regs [vol_reg - startAddr];
		int left  = data >> 4 & 7;
		int right = data & 7;
		int vol_unit = (left > right ? left : right) * unit;
		for ( int i = osc_count; --i >= 0; )
			oscs [i].vol_unit = vol_unit;
	}
	
	private void reset_regs()
	{
		for ( int i = 0x20; --i >= 0; )
			regs [i] = 0;
		
		for ( int i = osc_count; --i >= 0; )
			oscs [i].reset();
		
		update_volume();
	}
	
	static final int initial_wave [] = {
		0x84,0x40,0x43,0xAA,0x2D,0x78,0x92,0x3C,
		0x60,0x59,0x59,0xB0,0x34,0xB8,0x2E,0xDA
	};
	
	public void reset()
	{
		frame_time  = 0;
		last_time   = 0;
		frame_phase = 0;
		
		reset_regs();
		
		for ( int i = 16; --i >= 0; )
			write( 0, i + wave_ram, initial_wave [i] );
	}
	
	private void run_until( int end_time )
	{
		assert end_time >= last_time; // end_time must not be before previous time
		if ( end_time == last_time )
			return;
		
		while ( true )
		{
			// run oscillators
			int time = end_time;
			if ( time > frame_time )
				time = frame_time;
			
			square1.run( last_time, time );
			square2.run( last_time, time );
			wave   .run( last_time, time );
			noise  .run( last_time, time );
			last_time = time;
			
			if ( time == end_time )
				break;
			
			// run frame sequencer
			frame_time += frame_period;
			switch ( frame_phase++ )
			{
			case 2:
			case 6:
				// 128 Hz
				square1.clock_sweep();
			case 0:
			case 4:
				// 256 Hz
				square1.clock_length();
				square2.clock_length();
				wave   .clock_length();
				noise  .clock_length();
				break;
			
			case 7:
				// 64 Hz
				frame_phase = 0;
				square1.clock_envelope();
				square2.clock_envelope();
				noise  .clock_envelope();
			}
		}
	}
	
	// Runs all oscillators up to specified time, ends current time frame, then
	// starts a new frame at time 0
	public void endFrame( int end_time )
	{
		if ( end_time > last_time )
			run_until( end_time );
		
		assert frame_time >= end_time;
		frame_time -= end_time;
		
		assert last_time >= end_time;
		last_time -= end_time;
	}
	
	static void silence_osc( int time, GbOsc osc )
	{
		int amp = osc.last_amp;
		if ( amp != 0 )
		{
			osc.last_amp = 0;
			if ( osc.output != null )
				osc.output.addDelta( time, -amp * osc.vol_unit );
		}
	}
	
	// Reads and writes at addr must satisfy start_addr <= addr <= end_addr
	public static final int startAddr = 0xFF10;
	public static final int endAddr   = 0xFF3F;
	
	public void write( int time, int addr, int data )
	{
		assert startAddr <= addr && addr <= endAddr;
		assert 0 <= data && data < 0x100;
		
		if ( addr < status_reg && (regs [status_reg - startAddr] & power_mask) == 0 )
			return;
		
		run_until( time );
		int reg = addr - startAddr;
		if ( addr < wave_ram )
		{
			int old_data = regs [reg];
			regs [reg] = data;
			
			if ( addr < vol_reg )
			{
				int index = reg / 5;
				GbOsc osc = oscs [index];
				int r = reg - index * 5;
				osc.regs [r] = data;
				osc.write_register( frame_phase, r, old_data, data );
			}
			else if ( addr == vol_reg && data != old_data )
			{
				for ( int i = osc_count; --i >= 0; )
					silence_osc( time, oscs [i] );
				
				update_volume();
			}
			else if ( addr == stereo_reg )
			{
				for ( int i = osc_count; --i >= 0; )
				{
					GbOsc osc = oscs [i];
					int bits = data >> i;
					osc.output_select = (bits >> 3 & 2) | (bits & 1);
					BlipBuffer output = outputs [osc.output_select];
					if ( osc.output != output )
					{
						silence_osc( time, osc );
						osc.output = output;
					}
				}
			}
			else if ( addr == status_reg && ((data ^ old_data) & power_mask) != 0 )
			{
				frame_phase = 0;
				if ( (data & power_mask) == 0 )
				{
					for ( int i = osc_count; --i >= 0; )
						silence_osc( time, oscs [i] );
				
					reset_regs();
				}
			}
		}
		else // wave data
		{
			addr = wave.access( addr );
			regs [addr - startAddr] = data;
			int index = (addr & 0x0F) * 2;
			wave.wave [index    ] = data >> 4;
			wave.wave [index + 1] = data & 0x0F;
		}
	}
	
	static final int masks [] = {
		0x80,0x3F,0x00,0xFF,0xBF,
		0xFF,0x3F,0x00,0xFF,0xBF,
		0x7F,0xFF,0x9F,0xFF,0xBF,
		0xFF,0xFF,0x00,0x00,0xBF,
		0x00,0x00,0x70,
		0xFF,0xFF,0xFF,0xFF,0xFF,0xFF,0xFF,0xFF,0xFF
	};
	
	// Reads from address at specified time
	public int read( int time, int addr )
	{
		assert startAddr <= addr && addr <= endAddr;
		
		run_until( time );
		
		if ( addr >= wave_ram )
			addr = wave.access( addr );
		
		int index = addr - startAddr;
		int data = regs [index];
		if ( index < masks.length )
			data |= masks [index];
		
		if ( addr == status_reg )
		{
			data &= 0xF0;
			if ( square1.enabled != 0 ) data |= 1;
			if ( square2.enabled != 0 ) data |= 2;
			if ( wave   .enabled != 0 ) data |= 4;
			if ( noise  .enabled != 0 ) data |= 8;
		}
		
		return data;
	}

	static final int vol_reg    = 0xFF24;
	static final int stereo_reg = 0xFF25;
	static final int status_reg = 0xFF26;
	static final int wave_ram   = 0xFF30;
	static final int frame_period = 4194304 / 512; // 512 Hz
	
	static final int power_mask = 0x80;
	
	static final int osc_count = 4;
	final GbOsc [] oscs = new GbOsc [osc_count];
	int frame_time;
	int last_time;
	int         frame_phase;
	final BlipBuffer [] outputs = new BlipBuffer [4];
	
	final GbSweepSquare  square1 = new GbSweepSquare();
	final GbSquare       square2 = new GbSquare();
	final GbWave         wave    = new GbWave();
	final GbNoise        noise   = new GbNoise();
	final int [] regs = new int [endAddr - startAddr + 1];
}

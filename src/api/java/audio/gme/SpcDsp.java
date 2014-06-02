package audio.gme;

// Nintendo SPC-700 DSP emulator
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

public final class SpcDsp
{
	// Initializes DSP with new RAM and 128 bytes of register state (beginning at regs [regs_offset]).
	// Keeps reference to ram_64k.
	public void init( byte [] ram_64k, byte [] regs, int regs_offset )
	{
		this.ram = ram_64k;
		for ( int i = register_count; --i >= 0; )
			this.regs [i] = regs [i + regs_offset];
		
		java.util.Arrays.fill( echo_hist, 0, echo_hist.length, 0 );
		
		echo_hist_pos      = 0;
		every_other_sample = 1;
		kon                = 0;
		lfsr               = 0x4000;
		echo_offset        = 0;
		echo_length        = 0;
		new_kon            = this.regs [r_kon];
		t_koff             = 0;
		
		// counters start out with this synchronization
		counter0.i =   1;
		counter1.i =   0;
		counter2.i = -32;
		counter3.i =  11;
		
		// Internal state
		for ( int i = voice_count; --i >= 0; )
		{
			Voice v = new Voice();
			voices [i] = v;
			v.brr_offset = 1;
		}
	}
	
	// Sets output volume, where 1.0 is normal and 2.0 is twice as loud
	public void setVolume( double v ) { volume = (int) (v * 0x8000); }
	
	// Sets buffer to write samples into
	public void setOutput( byte [] out )
	{
		this.out = out;
		out_pos  = 0;
	}
	
	// Number of samples written into buffer (stereo, so always a multiple of 2)
	public int sampleCount() { return out_pos >> 1; }
	
	// Writes to DSP register
	public void write( int addr, int data )
	{
		if ( addr == r_endx ) // always cleared, regardless of data written
			data = 0;
		
		regs [addr] = (byte) data;
		
		if ( addr == r_kon )
			new_kon = (byte) data;
	}
	
	// DSP registers
	static final int r_mvoll = 0x0C;
	static final int r_mvolr = 0x1C;
	static final int r_evoll = 0x2C;
	static final int r_evolr = 0x3C;
	static final int r_kon   = 0x4C;
	static final int r_koff  = 0x5C;
	static final int r_flg   = 0x6C;
	static final int r_endx  = 0x7C;
	static final int r_efb   = 0x0D;
	static final int r_pmon  = 0x2D;
	static final int r_non   = 0x3D;
	static final int r_eon   = 0x4D;
	static final int r_dir   = 0x5D;
	static final int r_esa   = 0x6D;
	static final int r_edl   = 0x7D;
	static final int r_fir   = 0x0F;
	
	// Voice registers
	static final int v_voll   = 0x00;
	static final int v_volr   = 0x01;
	static final int v_pitchl = 0x02;
	static final int v_pitchh = 0x03;
	static final int v_srcn   = 0x04;
	static final int v_adsr0  = 0x05;
	static final int v_adsr1  = 0x06;
	static final int v_gain   = 0x07;
	static final int v_envx   = 0x08;
	static final int v_outx   = 0x09;
	
	public static final int register_count = 128;
	public final byte [] regs = new byte [register_count];
	
	// Runs DSP for sampleCount/32000 of a second
	public void run( int sampleCount )
	{
		// locals are faster, and first three are more efficient to access
		final byte [] regs = this.regs;
		Voice v;
		
		final byte [] ram  = this.ram;
		final Rate [] rates = this.rates;
		final Voice [] voices = this.voices;
		final int flg = regs [r_flg];
		
		final int dir = (regs [r_dir] & 0xFF) << 8;
		final int slow_gaussian = ((regs [r_pmon] & 0xFF) >> 1) | regs [r_non];
		final Rate noise_rate = rates [flg & 0x1F];
		
		// Global volumes
		final int volume = (flg & 0x40) == 0 ? this.volume : 0;
		final int mvoll = (regs [r_mvoll] * volume) >> 15;
		final int mvolr = (regs [r_mvolr] * volume) >> 15;
		final int evoll = (regs [r_evoll] * volume) >> 15;
		final int evolr = (regs [r_evolr] * volume) >> 15;
		
		final byte [] out = this.out;
		int out_pos = this.out_pos;
		final int out_end = out_pos + (sampleCount << 2);
		
		do
		{
			// KON/KOFF reading
			if ( (every_other_sample ^= 1) != 0 )
			{
				kon    = (new_kon &= ~kon);
				t_koff = regs [r_koff]; 
			}
			
			// run counters
			{ int n = counter1.i; if ( (n & 7) == 0 ) n -= 6 - 1; counter1.i = n - 1; }
			{ int n = counter2.i; if ( (n & 7) == 0 ) n -= 6 - 2; counter2.i = n - 1; }
			{ int n = counter3.i; if ( (n & 7) == 0 ) n -= 6 - 3; counter3.i = n - 1; }
			
			// Noise
			if ( (noise_rate.c.i & noise_rate.m) == 0 )
				lfsr = (lfsr >> 1) ^ (-(lfsr & 2) & 0xC000);
			
			// Voices
			int pmon_input = 0;
			int main_out_l = 0;
			int main_out_r = 0;
			int echo_out_l = 0;
			int echo_out_r = 0;
			int voice = -1;
			do
			{
				v = voices [++voice];
				final int vbit   = 1 << voice;
				final int v_regs = voice << 4;
				
				// Pitch
				int pitch = (regs [v_regs + v_pitchh] & 0x3F) << 8 | (regs [v_regs + v_pitchl] & 0xFF);
				if ( (regs [r_pmon] & vbit) != 0 )
					pitch += ((pmon_input >> 5) * pitch) >> 10;
				
				int brr_header = ram [v.brr_addr];
				
				// KON phases
				if ( v.kon_delay > 0 )
				{
					final int kon_delay = --v.kon_delay;
					
					// Disable BRR decoding until last three samples
					v.interp_pos = (kon_delay & 3) != 0 ? 0x4000 : 0;
					
					// Get ready to start BRR decoding on next sample
					if ( kon_delay == 4 )
					{
						int addr = dir + ((regs [v_regs + v_srcn] & 0xFF) << 2);
						v.brr_addr   = (ram [addr + 1] & 0xFF) << 8 | (ram [addr] & 0xFF);
						v.brr_offset = 1;
						v.buf_pos    = 0;
						brr_header   = 0; // header is ignored on this sample
					}
					
					// Envelope is never run during KON
					v.env        = 0;
					v.hidden_env = 0;
					
					// Pitch is never added during KON
					pitch = 0;
				}
				
				int env;
				regs [v_regs + v_envx] = (byte) ((env = v.env) >> 4);
				
				// Gaussian interpolation
				{
					int output = 0;
					if ( env != 0 )
					{
						int whole = v.buf_pos + (v.interp_pos >> 12);
						int fract = v.interp_pos >> 3 & 0x1FE;
						if ( (slow_gaussian & vbit) == 0 ) // 99%
						{
							// Faster approximation when exact sample value isn't necessary for pitch mod
							output = (((gauss [fract    ] * v.buf [whole  ] +
							            gauss [fract+1  ] * v.buf [whole+1] +
							            gauss [511-fract] * v.buf [whole+2] +
							            gauss [510-fract] * v.buf [whole+3]) >> 11) * env) >> 11;
						}
						else
						{
							output = (short) (lfsr << 1);
							if ( (regs [r_non] & vbit) == 0 )
							{
								output  = (gauss [fract    ] * v.buf [whole  ]) >> 11;
								output += (gauss [fract+1  ] * v.buf [whole+1]) >> 11;
								output += (gauss [511-fract] * v.buf [whole+2]) >> 11;
								output = (short) output;
								output += (gauss [510-fract] * v.buf [whole+3]) >> 11;
								
								if ( (short) output != output ) output = (output >> 24) ^ 0x7FFF; // 16-bit clamp
								output &= ~1;
							}
							pmon_input = output = (output * env) >> 11 & ~1;
						}
						
						regs [v_regs + v_outx] = (byte) (output >> 8);
						
						// Output
						int l, r;
						main_out_l += (l = output * regs [v_regs + v_voll]);
						main_out_r += (r = output * regs [v_regs + v_volr]);
						
						if ( (regs [r_eon] & vbit) != 0 )
						{
							echo_out_l += l;
							echo_out_r += r;
						}
					}
				}
				
				// Soft reset or end of sample
				if ( flg < 0 || (brr_header & 3) == 1 )
				{
					v.env_mode = env_release;
					env         = 0;
				}
				
				if ( every_other_sample != 0 )
				{
					// KOFF
					if ( (t_koff & vbit) != 0 )
						v.env_mode = env_release;
					
					// KON
					if ( (kon & vbit) != 0 )
					{
						v.kon_delay = 5;
						v.env_mode  = env_attack;
						regs [r_endx] &= ~vbit;
					}
				}
				
				// Envelope
				if ( v.kon_delay == 0 )
				{
					if ( v.env_mode == env_release ) // 97%
					{
						if ( (v.env = (env -= 0x8)) <= 0 )
						{
							v.env = 0;
							continue; // no BRR decoding for you!
						}
					}
					else do // 3%
					{
						int rate;
						int env_data = regs [v_regs + v_adsr1] & 0xFF;
						int adsr0;
						if ( (adsr0 = regs [v_regs + v_adsr0]) < 0 ) // 97% ADSR
						{
							if ( v.env_mode > env_decay ) // 89%
							{
								// optimized handling
								v.hidden_env = (env -= (env >> 8) + 1);
								Rate r = rates [env_data & 0x1F];
								if ( (r.c.i & r.m) == 0 )
									v.env = env;
								break;
							}
							else if ( v.env_mode == env_decay )
							{
								env -= (env >> 8) + 1;
								rate = (adsr0 >> 3 & 0x0E) + 0x10;
							}
							else // env_attack
							{
								rate = ((adsr0 & 0x0F) << 1) + 1;
								env += rate < 31 ? 0x20 : 0x400;
							}
						}
						else // GAIN
						{
							int mode;
							env_data = regs [v_regs + v_gain] & 0xFF;
							mode = env_data >> 5;
							if ( mode < 4 ) // direct
							{
								env = env_data << 4;
								rate = 31;
							}
							else
							{
								rate = env_data & 0x1F;
								if ( mode == 4 ) // 4: linear decrease
								{
									env -= 0x20;
								}
								else if ( mode < 6 ) // 5: exponential decrease
								{
									env -= (env >> 8) + 1;
								}
								else // 6,7: linear increase
								{
									env += 0x20;
									if ( mode > 6 && (v.hidden_env < 0 || v.hidden_env >= 0x600) )
										env += 0x8 - 0x20; // 7: two-slope linear increase
								}
							}
						}
						
						// Sustain level
						if ( (env >> 8) == (env_data >> 5) && v.env_mode == env_decay )
							v.env_mode = env_sustain;
						
						v.hidden_env = env;
						
						if ( env < 0 || env > 0x7FF )
						{
							env = (env < 0 ? 0 : 0x7FF);
							if ( v.env_mode == env_attack )
								v.env_mode = env_decay;
						}
						
						Rate r = rates [rate];
						if ( (r.c.i & r.m) == 0 )
							v.env = env; // nothing else is controlled by the counter
					}
					while ( false );
				}
				
				// Apply pitch
				int old_pos;
				int interp_pos = ((old_pos = v.interp_pos) & 0x3FFF) + pitch;
				if ( interp_pos > 0x7FFF )
					interp_pos = 0x7FFF;
				v.interp_pos = interp_pos;
				
				// BRR decode if necessary
				if ( old_pos > 0x4000 - 1 )
				{
					// Arrange the four input nybbles in 0xABCD order for easy decoding
					int brr_addr = v.brr_addr;
					int brr_offset = v.brr_offset;
					int nybbles = ram [brr_addr + brr_offset] << 8 | (ram [brr_addr + brr_offset + 1] & 0xFF);
					
					// Advance read position
					final int brr_block_size = 9;
					if ( (brr_offset += 2) >= brr_block_size )
					{
						// Next BRR block
						brr_addr = (brr_addr + brr_block_size) & 0xFFFF;
						//assert brr_offset == brr_block_size;
						if ( (brr_header & 1) != 0 )
						{
							int addr = dir + ((regs [v_regs + v_srcn] & 0xFF) << 2);
							brr_addr = (ram [addr + 3] & 0xFF) << 8 | (ram [addr + 2] & 0xFF);
							if ( v.kon_delay == 0 )
								regs [r_endx] |= vbit;
						}
						v.brr_addr = brr_addr;
						brr_offset = 1;
					}
					v.brr_offset = brr_offset;
					
					// Decode
					
					final int scale = brr_header >> 4 & 0x0F;
					final int right_shift = brr_shifts [scale];
					final int left_shift  = brr_shifts [scale + 16];
					
					final int filter = brr_header & 0x0C;
					
					// Decode and write to next four samples in circular buffer
					int pos = v.buf_pos;
					int p1 = v.buf [pos + (brr_buf_size - 1)];
					int p2 = v.buf [pos + (brr_buf_size - 2)] >> 1;
					final int end = pos + 4;
					do
					{
						// Extract upper nybble and scale appropriately
						int s = ((short) nybbles >> right_shift) << left_shift;
						nybbles <<= 4;
						
						// Apply IIR filter (8 is the most commonly used)
						if ( filter >= 8 )
						{
							if ( filter == 8 ) // s += p1 * 0.953125 - p2 * 0.46875
								s += p1 - p2 + (p2 >> 4) + ((p1 * -3) >> 6);
							else // s += p1 * 0.8984375 - p2 * 0.40625
								s += p1 - p2 + ((p1 * -13) >> 7) + ((p2 * 3) >> 4);
						}
						else if ( filter != 0 ) // s += p1 * 0.46875
						{
							s += (p1 >> 1) + ((-p1) >> 5);
						}
						p2 = p1 >> 1;
						
						// Adjust and write sample
						if ( (short) s != s ) s = (s >> 24) ^ 0x7FFF; // 16-bit clamp
						v.buf [pos + brr_buf_size] = v.buf [pos] = p1 = (short) (s << 1);
						// second copy simplifies wrap-around
					}
					while ( ++pos < end );
					
					if ( pos >= brr_buf_size )
						pos = 0;
					v.buf_pos = pos;
				}
			}
			while ( voice < 7 );
			
			// Echo position
			int echo_offset;
			int echo_ptr = ((regs [r_esa] << 8) + (echo_offset = this.echo_offset)) & 0xFFFF;
			if ( echo_offset == 0 )
				echo_length = (regs [r_edl] & 0x0F) << 11;
			if ( (echo_offset += 4) >= echo_length )
				echo_offset = 0;
			this.echo_offset = echo_offset;
			
			// FIR
			int echo_hist_pos;
			this.echo_hist_pos = echo_hist_pos = (this.echo_hist_pos + 2) & (echo_hist_half - 1);
			
			int echo_in_l = ram [echo_ptr + 1] << 8 | (ram [echo_ptr    ] & 0xFF);
			echo_hist [echo_hist_pos    ] = echo_hist [echo_hist_pos + echo_hist_half] = echo_in_l;
			
			int echo_in_r = ram [echo_ptr + 3] << 8 | (ram [echo_ptr + 2] & 0xFF);
			echo_hist [echo_hist_pos + 1] = echo_hist [echo_hist_pos + echo_hist_half + 1] = echo_in_r;
			
			echo_in_l  = regs [r_fir + 0x70] * echo_in_l +
					     regs [r_fir       ] * echo_hist [echo_hist_pos +  2] +
					     regs [r_fir + 0x10] * echo_hist [echo_hist_pos +  4] +
					     regs [r_fir + 0x20] * echo_hist [echo_hist_pos +  6] +
					     regs [r_fir + 0x30] * echo_hist [echo_hist_pos +  8] +
					     regs [r_fir + 0x40] * echo_hist [echo_hist_pos + 10] +
					     regs [r_fir + 0x50] * echo_hist [echo_hist_pos + 12] +
					     regs [r_fir + 0x60] * echo_hist [echo_hist_pos + 14];
			
			echo_in_r  = regs [r_fir + 0x70] * echo_in_r +
					     regs [r_fir       ] * echo_hist [echo_hist_pos +  3] +
					     regs [r_fir + 0x10] * echo_hist [echo_hist_pos +  5] +
					     regs [r_fir + 0x20] * echo_hist [echo_hist_pos +  7] +
					     regs [r_fir + 0x30] * echo_hist [echo_hist_pos +  9] +
					     regs [r_fir + 0x40] * echo_hist [echo_hist_pos + 11] +
					     regs [r_fir + 0x50] * echo_hist [echo_hist_pos + 13] +
					     regs [r_fir + 0x60] * echo_hist [echo_hist_pos + 15];
			
			// Echo out
			if ( (flg & 0x20) == 0 )
			{
				final int efb = regs [r_efb];
				int l = (echo_out_l >> 7) + ((echo_in_l * efb) >> 14);
				if ( (short) l != l ) l = (l >> 24) ^ 0x7FFF; // 16-bit clamp
				ram [echo_ptr    ] = (byte) l;
				ram [echo_ptr + 1] = (byte) (l >> 8);
				
				int r = (echo_out_r >> 7) + ((echo_in_r * efb) >> 14);
				if ( (short) r != r ) r = (r >> 24) ^ 0x7FFF; // 16-bit clamp
				ram [echo_ptr + 2] = (byte) r;
				ram [echo_ptr + 3] = (byte) (r >> 8);
			}
			
			// Sound out
			int l = (main_out_l * mvoll + echo_in_l * evoll) >> 14;
			if ( (short) l != l ) l = (l >> 24) ^ 0x7FFF; // 16-bit clamp
			out [out_pos    ] = (byte) (l >> 8);
			out [out_pos + 1] = (byte) l;
			
			int r = (main_out_r * mvolr + echo_in_r * evolr) >> 14;
			if ( (short) r != r ) r = (r >> 24) ^ 0x7FFF; // 16-bit clamp
			out [out_pos + 2] = (byte) (r >> 8);
			out [out_pos + 3] = (byte) r;
		}
		while ( (out_pos += 4) < out_end );
		
		this.out_pos = out_pos;
	}
	
	public SpcDsp()
	{
		int mask = 4095;
		for ( int i = 0; i < 32 - 2; i += 3 )
		{
			rates [i  ] = new Rate( counter2, mask );
			rates [i+1] = new Rate( counter1, mask );
			rates [i+2] = new Rate( counter3, mask );
			mask >>= 1;
		}
		rates [ 0] = new Rate( counter0, 7 );
		rates [30] = new Rate( counter2, 1 );
		rates [31] = new Rate( counter2, 0 );
		
		setVolume( 2.0 ); // TODO: let line increase volume, not DSP
	}
	
	static final int env_release = 0;
	static final int env_attack  = 1;
	static final int env_decay   = 2;
	static final int env_sustain = 3;

	static final int voice_count = 8;
	static final int brr_buf_size = 12;
	static final int echo_hist_half = 16;
	
	private static final class Voice
	{
		final int [] buf = new int [12*2];// decoded samples (twice the size to simplify wrap 	handling)
		int buf_pos;			// place in buffer where next samples will be decoded
		int interp_pos;			// relative fractional position in sample (0x1000 = 1.0)
		int brr_addr;			// address of current BRR block
		int brr_offset;			// current decoding offset in BRR block
		int kon_delay;			// KON delay/current setup phase
		int env_mode;
		int env;				// current envelope level
		int hidden_env;			// used by GAIN mode 7, very obscure quirk
	}
	
	private static final class Counter { int i; }
	
	private static final class Rate
	{
		Counter c;
		int m;
		Rate( Counter c, int m )
		{
			this.c = c;
			this.m = m;
		}
	}
	
	final Counter	counter0	= new Counter();
	final Counter	counter1	= new Counter();
	final Counter	counter2	= new Counter();
	final Counter	counter3	= new Counter();
	final Rate []	rates		= new Rate [32];
	final Voice []	voices		= new Voice [voice_count];
	final int []	echo_hist	= new int [echo_hist_half * 2];
	
	int echo_hist_pos;
	int every_other_sample;	// toggles every sample
	int kon;				// KON value when last checked
	int lfsr;
	int echo_offset;		// offset from ESA in echo buffer
	int echo_length;		// number of bytes that echo_offset will stop at
	int new_kon;
	int t_koff;
	int volume;
	byte [] ram; // 64K shared RAM between DSP and SMP
	byte [] out; // sample output
	int out_pos;
	
	// 0: >>1  1: <<0  2: <<1 ... 12: <<11  13-15: >>4 <<11
	static final int [] brr_shifts = {
		13,12,12,12,12,12,12,12,12,12,12, 12, 12, 16, 16, 16,
		 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 11, 11, 11
	};
	
	static final int [] gauss =
	{
	 370,1305, 366,1305, 362,1304, 358,1304, 354,1304, 351,1304, 347,1304, 343,1303,
	 339,1303, 336,1303, 332,1302, 328,1302, 325,1301, 321,1300, 318,1300, 314,1299,
	 311,1298, 307,1297, 304,1297, 300,1296, 297,1295, 293,1294, 290,1293, 286,1292,
	 283,1291, 280,1290, 276,1288, 273,1287, 270,1286, 267,1284, 263,1283, 260,1282,
	 257,1280, 254,1279, 251,1277, 248,1275, 245,1274, 242,1272, 239,1270, 236,1269,
	 233,1267, 230,1265, 227,1263, 224,1261, 221,1259, 218,1257, 215,1255, 212,1253,
	 210,1251, 207,1248, 204,1246, 201,1244, 199,1241, 196,1239, 193,1237, 191,1234,
	 188,1232, 186,1229, 183,1227, 180,1224, 178,1221, 175,1219, 173,1216, 171,1213,
	 168,1210, 166,1207, 163,1205, 161,1202, 159,1199, 156,1196, 154,1193, 152,1190,
	 150,1186, 147,1183, 145,1180, 143,1177, 141,1174, 139,1170, 137,1167, 134,1164,
	 132,1160, 130,1157, 128,1153, 126,1150, 124,1146, 122,1143, 120,1139, 118,1136,
	 117,1132, 115,1128, 113,1125, 111,1121, 109,1117, 107,1113, 106,1109, 104,1106,
	 102,1102, 100,1098,  99,1094,  97,1090,  95,1086,  94,1082,  92,1078,  90,1074,
	  89,1070,  87,1066,  86,1061,  84,1057,  83,1053,  81,1049,  80,1045,  78,1040,
	  77,1036,  76,1032,  74,1027,  73,1023,  71,1019,  70,1014,  69,1010,  67,1005,
	  66,1001,  65, 997,  64, 992,  62, 988,  61, 983,  60, 978,  59, 974,  58, 969,
	  56, 965,  55, 960,  54, 955,  53, 951,  52, 946,  51, 941,  50, 937,  49, 932,
	  48, 927,  47, 923,  46, 918,  45, 913,  44, 908,  43, 904,  42, 899,  41, 894,
	  40, 889,  39, 884,  38, 880,  37, 875,  36, 870,  36, 865,  35, 860,  34, 855,
	  33, 851,  32, 846,  32, 841,  31, 836,  30, 831,  29, 826,  29, 821,  28, 816,
	  27, 811,  27, 806,  26, 802,  25, 797,  24, 792,  24, 787,  23, 782,  23, 777,
	  22, 772,  21, 767,  21, 762,  20, 757,  20, 752,  19, 747,  19, 742,  18, 737,
	  17, 732,  17, 728,  16, 723,  16, 718,  15, 713,  15, 708,  15, 703,  14, 698,
	  14, 693,  13, 688,  13, 683,  12, 678,  12, 674,  11, 669,  11, 664,  11, 659,
	  10, 654,  10, 649,  10, 644,   9, 640,   9, 635,   9, 630,   8, 625,   8, 620,
	   8, 615,   7, 611,   7, 606,   7, 601,   6, 596,   6, 592,   6, 587,   6, 582,
	   5, 577,   5, 573,   5, 568,   5, 563,   4, 559,   4, 554,   4, 550,   4, 545,
	   4, 540,   3, 536,   3, 531,   3, 527,   3, 522,   3, 517,   2, 513,   2, 508,
	   2, 504,   2, 499,   2, 495,   2, 491,   2, 486,   1, 482,   1, 477,   1, 473,
	   1, 469,   1, 464,   1, 460,   1, 456,   1, 451,   1, 447,   1, 443,   1, 439,
	   0, 434,   0, 430,   0, 426,   0, 422,   0, 418,   0, 414,   0, 410,   0, 405,
	   0, 401,   0, 397,   0, 393,   0, 389,   0, 385,   0, 381,   0, 378,   0, 374,
	};
}

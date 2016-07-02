/*
DFPWM1a implementation in Java
operates on 8-bit signed PCM data and little-endian DFPWM data

by Ben "GreaseMonkey" Russell, 2013, 2016 - Public Domain

NOTE, len is in bytes relative to DFPWM (len*8 PCM bytes)
also the main() function takes unsigned 8-bit data and converts it to suit
*/

package pl.asie.lib.audio;

public class DFPWM
{
	private final int RESP_INC = 1;
	private final int RESP_DEC = 1;
	private final int RESP_PREC = 10;
	private final int LPF_STRENGTH = 140;

	private int response = 0;
	private int level = 0;
	private boolean lastbit = false;

	private int flastlevel = 0;
	private int lpflevel = 0;

	public DFPWM() {}

	private void ctx_update(boolean curbit)
	{
		int target = (curbit ? 127 : -128);
		int nlevel = (level + ((response*(target - level)
			+ (1<<(RESP_PREC-1)))>>RESP_PREC));
		if(nlevel == level && level != target)
			nlevel += (curbit ? 1 : -1);

		int rtarget, rdelta;
		if(curbit == lastbit)
		{
			rtarget = (1<<RESP_PREC)-1;
			rdelta = RESP_INC;
		} else {
			rtarget = 0;
			rdelta = RESP_DEC;
		}

		int nresponse = response;
		if(response != rtarget)
			nresponse += (curbit == lastbit ? 1 : -1);

		if(RESP_PREC > 8)
		{
			if(nresponse < (2<<(RESP_PREC-8)))
				nresponse = (2<<(RESP_PREC-8));
		}

		response = nresponse;
		lastbit = curbit;
		level = nlevel;
	}

	/**
	 * Decompresses a set of DFPWM-encoded data.
	 * @param dest An 8-bit, signed integer output array.
	 * @param src A 1-bit DFPWM input array.
	 * @param destoffs The offset in the destination array.
	 * @param srcoffs The offset in the source array.
	 * @param len The length of the source array.
	 */
	public void decompress(byte[] dest, byte[] src, int destoffs, int srcoffs, int len)
	{
		for(int i = 0; i < len; i++)
		{
			byte d = src[srcoffs++];
			for(int j = 0; j < 8; j++)
			{
				// apply context
				boolean curbit = ((d&1) != 0);
				boolean lastbit = this.lastbit;
				ctx_update(curbit);
				d >>= 1;

				// apply noise shaping
				int blevel = (byte)(curbit == lastbit
					? level
					: ((flastlevel + level + 1)>>1));
				flastlevel = level;

				// apply low-pass filter
				lpflevel += ((LPF_STRENGTH * (blevel - lpflevel) + 0x80)>>8);
				dest[destoffs++] = (byte)(lpflevel);
			}
		}
	}

	/**
	 * Compresses a set of 8-bit signed integer audio data to 1-bit DFPWM data.
	 * @param dest A 1-bit DFPWM output array.
	 * @param src An 8-bit, signed integer input array.
	 * @param destoffs The offset in the destination array.
	 * @param srcoffs The offset in the source array.
	 * @param len The length of the destination array.
	 */
	public void compress(byte[] dest, byte[] src, int destoffs, int srcoffs, int len)
	{
		for(int i = 0; i < len; i++)
		{
			int d = 0;
			for(int j = 0; j < 8; j++)
			{
				int inlevel = src[srcoffs++];
				boolean curbit = (inlevel > level || (inlevel == level && level == 127));
				d = (curbit ? (d>>1)+128 : d>>1);
				ctx_update(curbit);
			}
			dest[destoffs++] = (byte)d;
		}
	}

	public static void main(String[] args) throws Exception // FUCK THE POLICE
	{
		int mode = 0;
		if(args.length >= 1)
		{
			if(args[0].equals("-e"))
				mode = 1;
			else if(args[0].equals("-d"))
				mode = 2;
		}
		byte[] pcmin = new byte[1024];
		byte[] pcmout = new byte[1024];
		byte[] cmpdata = new byte[128];

		DFPWM incodec = new DFPWM();
		DFPWM outcodec = new DFPWM();

		if(mode == 0)
		{
			while(true)
			{
				for(int ctr = 0; ctr < 1024;)
				{
					int amt = System.in.read(pcmin, ctr, 1024-ctr);
					if(amt == -1) return;
					ctr += amt;
				}
				for(int i = 0; i < 1024; i++)
					pcmin[i] ^= (byte)0x80;
				incodec.compress(cmpdata, pcmin, 0, 0, 128);
				outcodec.decompress(pcmout, cmpdata, 0, 0, 128);
				for(int i = 0; i < 1024; i++)
					pcmout[i] ^= (byte)0x80;
				System.out.write(pcmout, 0, 1024);
			}
		} else if(mode == 1) {
			while(true)
			{
				for(int ctr = 0; ctr < 1024;)
				{
					int amt = System.in.read(pcmin, ctr, 1024-ctr);
					if(amt == -1) return;
					ctr += amt;
				}
				for(int i = 0; i < 1024; i++)
					pcmin[i] ^= (byte)0x80;
				incodec.compress(cmpdata, pcmin, 0, 0, 128);
				System.out.write(cmpdata, 0, 128);
			}
		} else if(mode == 2) {
			while(true)
			{
				for(int ctr = 0; ctr < 128;)
				{
					int amt = System.in.read(cmpdata, ctr, 128-ctr);
					if(amt == -1) return;
					ctr += amt;
				}
				outcodec.decompress(pcmout, cmpdata, 0, 0, 128);
				for(int i = 0; i < 1024; i++)
					pcmout[i] ^= (byte)0x80;
				System.out.write(pcmout, 0, 1024);
			}
		}
	}
}


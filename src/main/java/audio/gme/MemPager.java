package audio.gme;

// Manages memory paging used by CPU emulators
// http://www.slack.net/~ant/

final class MemPager
{
	public MemPager( int pageSize, int ramSize )
	{
		this.pageSize  = pageSize;
		this.romOffset = ramSize + pageSize;
	}
	
	// Loads data and returns memory array
	public byte [] load( byte in [], byte [] header, int addr, int fill )
	{
		// allocate
		int romLength = in.length - header.length;
		int romSize = (romLength + addr + pageSize - 1) / pageSize * pageSize;
		data = new byte [romOffset + romSize + padding];
		
		// copy data
		java.util.Arrays.fill( data, 0, romOffset + addr, (byte) fill );
		java.util.Arrays.fill( data, data.length - pageSize - padding, data.length, (byte) fill );
		System.arraycopy( in, header.length, data, romOffset + addr, romLength );
		
		// addrMask
		int shift = 0;
		int max_addr = romSize - 1;
		while ( (max_addr >> shift) != 0 )
			shift++;
		addrMask = (1 << shift) - 1;
		
		// copy header
		System.arraycopy( in, 0, header, 0, header.length );
		return data;
	}
	
	// Size of ROM data, a multiple of pageSize
	public int size() { return data.length - padding - romOffset; }
	
	// Page of unmapped fill value
	public int unmapped() { return romOffset - pageSize; }
	
	// Masks address to nearest power of two greater than size()
	public int maskAddr( int addr ) { return addr & addrMask; }
	
	// Page starting at addr. Returns unmapped() if outside data.
	public int mapAddr( int addr )
	{
		int offset = maskAddr( addr );
		if ( offset < 0 || size() - pageSize < offset )
			offset = -pageSize;
		return offset + romOffset;
	}
	
// private
	
	static final int padding = 8; // extra at end for CPU emulators that read past end
	byte [] data;
	int pageSize;
	int romOffset;
	int addrMask;
}

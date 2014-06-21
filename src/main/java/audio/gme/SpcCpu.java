package audio.gme;

// Nintendo SNES SPC-700 CPU emulator
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

public class SpcCpu extends MusicEmu
{
	// Registers. NOT kept updated during runCpu()
	public int a, x, y, psw, sp, pc;
	
	// Current time
	public int time;
	
	// Memory read and write handlers
	protected int  cpuRead ( int addr ) { return 0; }
	protected void cpuWrite( int addr, int data ) { }
	
	// Resets registers and uses supplied physical memory
	public final void reset( byte [] mem )
	{
		this.mem = mem;
		a   = 0;
		x   = 0;
		y   = 0;
		sp  = 0xFF;
		pc  = 0;
		psw = 0x04;
		
		time = 0;
	}
	
	public final void setPsw( int psw ) { this.psw = psw; }
	
	private byte [] mem;
	
	static final int [] instrTimes =
	{// 0 1 2 3 4 5 6 7 8 9 A B C D E F
	    2,8,4,5,3,4,3,6,2,6,5,4,5,4,6,8, // 0
	    2,8,4,5,4,5,5,6,5,5,6,5,2,2,4,6, // 1
	    2,8,4,5,3,4,3,6,2,6,5,4,5,4,5,2, // 2
	    2,8,4,5,4,5,5,6,5,5,6,5,2,2,3,8, // 3
	    2,8,4,5,3,4,3,6,2,6,4,4,5,4,6,6, // 4
	    2,8,4,5,4,5,5,6,5,5,4,5,2,2,4,3, // 5
	    2,8,4,5,3,4,3,6,2,6,4,4,5,4,5,5, // 6
	    2,8,4,5,4,5,5,6,5,5,5,5,2,2,3,6, // 7
	    2,8,4,5,3,4,3,6,2,6,5,4,5,2,4,5, // 8
	    2,8,4,5,4,5,5,6,5,5,5,5,2,2,12,5,// 9
	    3,8,4,5,3,4,3,6,2,6,4,4,5,2,4,4, // A
	    2,8,4,5,4,5,5,6,5,5,5,5,2,2,3,4, // B
	    3,8,4,5,4,5,4,7,2,5,6,4,5,2,4,9, // C
	    2,8,4,5,5,6,6,7,4,5,5,5,2,2,6,3, // D
	    2,8,4,5,3,4,3,6,2,4,5,3,4,3,4,0, // E
	    2,8,4,5,4,5,5,6,3,4,5,4,2,2,4,0, // F
	};
	
	// Hex value in name to clarify code and bit shifting.
	// Flag stored in indicated variable during emulation
	static final int n80 = 0x80; // (nz & 0x880) != 0
	static final int v40 = 0x40; // psw
	static final int p20 = 0x20; // psw, dp == 0x100
	static final int b10 = 0x10; // psw
	static final int h08 = 0x08; // psw
	static final int i04 = 0x04; // psw
	static final int z02 = 0x02; // (byte) nz == 0
	static final int c01 = 0x01; // (c & 0x100) != 0

	// Runs until time >= 0
	public final void runCpu()
	{
		// locals are faster, and first three are more efficient to access
		final byte [] mem = this.mem;
		int nz;
		int pc   = this.pc;
		
		int a    = this.a;
		int x    = this.x;
		int y    = this.y;
		int psw  = this.psw;
		int sp   = (this.sp + 1) | 0x100;
		int time = this.time;
		final int [] instrTimes = this.instrTimes;
		
		// unpack psw
		int c, dp;
		c   = psw << 8;
		dp  = psw << 3 & 0x100;
		nz  = (psw << 4 & 0x800) | (~psw & z02);
	
		int data = 0;
		int addr = 0;
	
	loop:
		while ( time < 0 )
		{
			if ( debug )
			{
				assert 0 <= a && a < 0x100;
				assert 0 <= x && x < 0x100;
				assert 0 <= y && y < 0x100;
				assert 0 <= pc && pc < 0x10000;
				assert 0x100 <= sp && sp < 0x200;
				assert dp == 0 || dp == 0x100;
			}
			
			int opcode;
			this.time = (time += instrTimes [opcode = mem [pc] & 0xFF]);
			switch ( opcode )
			{
	
		//////// Often used
	
			case 0xE4: // MOV   A, d
				a = nz = cpuRead( mem [pc + 1] & 0xFF | dp );
				pc += 2;
				continue;
			
			case 0xF5: // MOV   A, !a+X
				a = nz = cpuRead( ((mem [pc + 2] & 0xFF) << 8 | (mem [pc + 1] & 0xFF)) + x );
				pc += 3;
				continue;
			
			case 0xF4: // MOV   A, d+X
				a = nz = cpuRead( (mem [pc + 1] + x) & 0xFF | dp );
				pc += 2;
				continue;

			case 0xEB: // MOV   Y, d
				y = nz = cpuRead( mem [pc + 1] & 0xFF | dp );
				pc += 2;
				continue;
			
			case 0x2F: // BRA   r
				pc += mem [pc + 1] + 2;
				continue;
			
			case 0x90: // BCC   r
				pc += 2;
				if ( (c & 0x100) == 0 )
				{
					pc += mem [pc - 1];
					time += 2;
				}
				continue;
			
			case 0xB0: // BCS   r
				pc += 2;
				if ( (c & 0x100) != 0 )
				{
					pc += mem [pc - 1];
					time += 2;
				}
				continue;
			
			case 0xF0: // BEQ   r
				pc += 2;
				if ( ((byte) nz) == 0 )
				{
					pc += mem [pc - 1];
					time += 2;
				}
				continue;
			
			case 0xD0: // BNE   r
				pc += 2;
				if ( ((byte) nz) != 0 )
				{
					pc += mem [pc - 1];
					time += 2;
				}
				continue;
			
			case 0x30: // BMI   r
				pc += 2;
				if ( (nz & 0x880) != 0 )
				{
					pc += mem [pc - 1];
					time += 2;
				}
				continue;
			
			case 0x10: // BPL   r
				pc += 2;
				if ( (nz & 0x880) == 0 )
				{
					pc += mem [pc - 1];
					time += 2;
				}
				continue;
			
			case 0x50: // BVC   r
				pc += 2;
				if ( (psw & v40) == 0 )
				{
					pc += mem [pc - 1];
					time += 2;
				}
				continue;
			
			case 0x70: // BVS   r
				pc += 2;
				if ( (psw & v40) != 0 )
				{
					pc += mem [pc - 1];
					time += 2;
				}
				continue;
			
		//////// Self-contained
	
			case 0xFE: // DBNZ  Y, r
				pc += 2;
				if ( (y = (y - 1) & 0xFF) != 0 )
					break;
				continue;
			
			case 0xEF: // SLEEP
			case 0xFF: // STOP
				break loop;
	
			case 0x9C: // DEC   A
				pc++;
				a = (nz = a - 1) & 0xFF;
				continue;
			
			case 0xBC: // INC   A
				pc++;
				a = (nz = a + 1) & 0xFF;
				continue;
			
			case 0x1D: // DEC   X
				pc++;
				x = (nz = x - 1) & 0xFF;
				continue;
			
			case 0x3D: // INC   X
				pc++;
				x = (nz = x + 1) & 0xFF;
				continue;
			
			case 0xDC: // DEC   Y
				pc++;
				y = (nz = y - 1) & 0xFF;
				continue;
			
			case 0xFC: // INC   Y
				pc++;
				y = (nz = y + 1) & 0xFF;
				continue;
			
			case 0x1C: // ASL   A
				c = 0;
			case 0x3C:{// ROL   A
				int t = c >> 8 & 1;
				c = a << 1;
				a = (nz = c | t) & 0xFF;
				pc++;
				continue;
			}
			
			case 0x5C: // LSR   A
				c = 0;
			case 0x7C: // ROR   A
				nz = ((c & 0x100) | a) >> 1;
				c = a << 8;
				a = nz;
				pc++;
				continue;
			
			case 0x9F: // XCN   A
				pc++;
				a = nz = a >> 4 | ((a & 0x0F) << 4);
				continue;
			
			case 0xDF: // DAA   A
				pc++;
				if ( a > 0x99 || (c & 0x100) != 0 )
				{
					a += 0x60;
					c = 0x100;
				}
				
				if ( (a & 0x0F) > 9 || (psw & h08) != 0 )
					a += 0x06;
				
				nz = (a &= 0xFF);
				continue;
			
			case 0xBE: // DAS   A
				pc++;
				if ( a > 0x99 || (c & 0x100) == 0 )
				{
					a -= 0x60;
					c = 0;
				}
				
				if ( (a & 0x0F) > 9 || (psw & h08) == 0 )
					a -= 0x06;
				
				nz = (a &= 0xFF);
				continue;
			
			case 0x9E:{// DIV   YA, X
				pc++;
				int ya = y << 8 | a;
				
				psw &= ~(h08 | v40);
				
				if ( y >= x )
					psw |= v40;
				
				if ( (y & 15) >= (x & 15) )
					psw |= h08;
				
				if ( y < (x << 1) )
				{
					a = ya / x;
					y = ya - a * x;
				}
				else
				{
					a = 255 - (ya - (x << 9)) / (256 - x);
					y = x   + (ya - (x << 9)) % (256 - x);
				}
				
				nz = (a &= 0xFF);
				continue;
			}
			
			case 0xCF:{// MUL   YA
				pc++;
				int t = y * a;
				a = t & 0xFF;
				nz = (t >> 1 | t) & 0x7F;
				nz |= (y = t >> 8);
				continue;
			}
			
			case 0x00: // NOP
				pc++;
				continue;
	
			case 0x60: // CLRC
				pc++;
				c = 0;
				continue;
			
			case 0x80: // SETC
				pc++;
				c = ~0;
				continue;
			
			case 0xED: // NOTC
				pc++;
				c ^= 0x100;
				continue;
			
			case 0x20: // CLRP
				pc++;
				dp = 0;
				continue;
			
			case 0x40: // SETP
				pc++;
				dp = 0x100;
				continue;
			
			case 0xE0: // CLRV
				pc++;
				psw &= ~(v40 | h08);
				continue;
			
			case 0xC0: // DI
				pc++;
				psw |= i04;
				continue;
			
			case 0xA0: // EI
				pc++;
				psw &= ~i04;
				continue;
			
			case 0x5D: // MOV   X, A
				pc++;
				x = nz = a;
				continue;
			
			case 0xFD: // MOV   Y, A
				pc++;
				y = nz = a;
				continue;
			
			case 0x7D: // MOV   A, X
				pc++;
				a = nz = x;
				continue;
			
			case 0xDD: // MOV   A, Y
				pc++;
				a = nz = y;
				continue;
			
			case 0x9D: // MOV   X, SP
				pc++;
				x = nz = (sp - 1) & 0xFF;
				continue;
			
			case 0xBD: // MOV   SP, X
				pc++;
				sp = (x + 1) | 0x100;
				continue;
			
			case 0xBF: // MOV   A, (X)+
				pc++;
				a = nz = cpuRead( x + dp );
				x = (x + 1) & 0xFF;
				continue;
			
			case 0xD9: // MOV   d+Y, X
				cpuWrite( (mem [pc + 1] + y) & 0xFF | dp, x );
				pc += 2;
				continue;
			
			case 0xD6: // MOV   !a+Y, A
				cpuWrite( ((mem [pc + 2] & 0xFF) << 8 | (mem [pc + 1] & 0xFF)) + y, a );
				pc += 3;
				continue;
			
			case 0xD5: // MOV   !a+X, A
				cpuWrite( ((mem [pc + 2] & 0xFF) << 8 | (mem [pc + 1] & 0xFF)) + x, a );
				pc += 3;
				continue;
			
			case 0xF9: // MOV   X, d+Y
				x = nz = cpuRead( (mem [pc + 1] + y) & 0xFF | dp );
				pc += 2;
				continue;
				
			case 0xD7:{// MOV   [d]+Y, A
				int t = mem [pc + 1];
				cpuWrite( ((mem [(t + 1) & 0xFF | dp] & 0xFF) << 8 | (mem [t & 0xFF | dp] & 0xFF)) + y, a );
				pc += 2;
				continue;
			}
			
			case 0xC7:{// MOV   [d+X], A
				int t = mem [pc + 1] + x;
				cpuWrite( (mem [(t + 1) & 0xFF | dp] & 0xFF) << 8 | (mem [t & 0xFF | dp] & 0xFF), a );
				pc += 2;
				continue;
			}
			
			case 0xC6: // MOV   (X), A
				pc++;
				cpuWrite( x + dp, a );
				continue;
			
			case 0xAF: // MOV   (X)+, A
				pc++;
				cpuWrite( x + dp, a );
				x = (x + 1) & 0xFF;
				continue;
	
			case 0x8F: // MOV   d, #i
				cpuWrite( mem [pc + 2] & 0xFF | dp, mem [pc + 1] );
				pc += 3;
				continue;
	
			case 0xFA: // MOV   dd, ds
				cpuWrite( mem [pc + 2] & 0xFF | dp, cpuRead( mem [pc + 1] & 0xFF | dp ) );
				pc += 3;
				continue;
				
			case 0xCA: // MOV1  m.b, C
				data = mem [pc + 2];
				addr = (data & 0x1F) << 8 | (mem [pc + 1] & 0xFF);
				data = data >> 5 & 7;
				cpuWrite( addr, cpuRead( addr ) & ~(1 << data) | ((c & 0x100) >> (8 - data)) );
				pc += 3;
				continue;
			
			case 0xEA: // NOT1  m.b
				data = mem [pc + 2];
				addr = (data & 0x1F) << 8 | (mem [pc + 1] & 0xFF);
				cpuWrite( addr, cpuRead( addr ) ^ (1 << (data >> 5 & 7)) );
				pc += 3;
				continue;
			
			case 0x4A: // AND1  C, m.b
			case 0xAA: // MOV1  C, m.b
			case 0x0A: // OR1   C, m.b
			case 0x6A: // AND1  C, /m.b
			case 0x2A: // OR1   C, /m.b
			case 0x8A: // EOR1  C, m.b
				data = mem [pc + 2];
				data = cpuRead( (data & 0x1F) << 8 | (mem [pc + 1] & 0xFF) ) << (8 - (data >> 5 & 7));
				pc += 3;
				switch ( opcode )
				{
				case 0x4A: // AND1  C, m.b
					c &= data;
					continue;
				
				case 0xAA: // MOV1  C, m.b
					c = data;
					continue;
				
				case 0x0A: // OR1   C, m.b
					c |= data;
					continue;
				
				case 0x6A: // AND1  C, /m.b
					c &= ~data;
					continue;
				
				case 0x2A: // OR1   C, /m.b
					c |= ~data;
					continue;
				
				default:
				//case 0x8A: // EOR1  C, m.b
					c ^= data;
					continue;
				}
			
			case 0x02: // SET1  d.0
			case 0x22: // SET1  d.1
			case 0x42: // SET1  d.2
			case 0x62: // SET1  d.3
			case 0x82: // SET1  d.4
			case 0xA2: // SET1  d.5
			case 0xC2: // SET1  d.6
			case 0xE2: // SET1  d.7
			case 0x12: // CLR1  d.0
			case 0x32: // CLR1  d.1
			case 0x52: // CLR1  d.2
			case 0x72: // CLR1  d.3
			case 0x92: // CLR1  d.4
			case 0xB2: // CLR1  d.5
			case 0xD2: // CLR1  d.6
			case 0xF2:{// CLR1  d.7
				data = cpuRead( addr = mem [pc + 1] & 0xFF | dp );
				int t = 1 << (opcode >> 5);
				data |= t;
				if ( (opcode & 0x10) != 0 )
					data ^= t;
				cpuWrite( addr, data );
				pc += 2;
				continue;
			}
			
			case 0x2D: // PUSH  A
				pc++;
				mem [sp = (sp - 1) | 0x100] = (byte) a;
				continue;
			
			case 0x4D: // PUSH  X
				pc++;
				mem [sp = (sp - 1) | 0x100] = (byte) x;
				continue;
			
			case 0x6D: // PUSH  Y
				pc++;
				mem [sp = (sp - 1) | 0x100] = (byte) y;
				continue;
			
			case 0x0D: // PUSH  PSW
			case 0x0F:{// BRK
				// calculate PSW
				int t = psw & ~(n80 | p20 | z02 | c01);
				t |= c  >> 8 & c01;
				t |= dp >> 3 & p20;
				t |= ((nz >> 4) | nz) & n80;
				if ( ((byte) nz) == 0 ) t |= z02;
				
				pc++;
				if ( opcode == 0x0F ) // BRK
				{
					mem [(sp - 1) | 0x100] = (byte) (pc >> 8);
					mem [sp = (sp - 2) | 0x100] = (byte) pc;
					pc = (mem [0xFFDF] & 0xFF) << 8 | (mem [0xFFDE] & 0xFF);
					psw = (psw | b10) & ~i04;
				}
				mem [sp = (sp - 1) | 0x100] = (byte) t;
				continue;
			}
			
			case 0xAE: // POP   A
				pc++;
				a = mem [sp] & 0xFF;
				sp = (sp + 1) | 0x100;
				continue;
	
			case 0xCE: // POP   X
				pc++;
				x = mem [sp] & 0xFF;
				sp = (sp + 1) | 0x100;
				continue;
	
			case 0xEE: // POP   Y
				pc++;
				y = mem [sp] & 0xFF;
				sp = (sp + 1) | 0x100;
				continue;
	
			case 0x8E: // POP   PSW
			case 0x7F: // RET1
				pc++;
				psw = mem [sp];
				sp = (sp - 0xFF) | 0x100;
				
				if ( opcode == 0x7F ) // RET1
				{
					pc = (mem [(sp - 0xFF) | 0x100] & 0xFF) << 8 | (mem [sp] & 0xFF);
					sp = (sp - 0xFE) | 0x100;
				}
				
				// unpack psw
				c   = psw << 8;
				dp  = psw << 3 & 0x100;
				nz  = (psw << 4 & 0x800) | (~psw & z02);
				continue;
			
			case 0x6F: // RET
				pc = (mem [(sp - 0xFF) | 0x100] & 0xFF) << 8 | (mem [sp] & 0xFF);
				sp = (sp - 0xFE) | 0x100;
				continue;
	
			case 0xDA:{// MOVW  d, YA
				int t = mem [pc + 1];
				cpuWrite(  t      & 0xFF | dp, a );
				cpuWrite( (t + 1) & 0xFF | dp, y );
				pc += 2;
				continue;
			}
			
			case 0x1A: // DECW  d
			case 0x3A: // INCW  d
			case 0x5A: // CMPW  YA, d
			case 0x7A: // ADDW  YA, d
			case 0x9A: // SUBW  YA, d
			case 0xBA:{// MOVW  YA, d
				addr = mem [pc + 1] & 0xFF | dp;
				data = (mem [addr + 1] & 0xFF) << 8 | (mem [addr] & 0xFF);
				
				// addr >= 0xEF || addr <= 0xFF
				if ( (addr ^ 0xFF) <= 0x11 ) // 1%
					data = cpuRead( addr + 1 ) << 8 | cpuRead( addr );
				
				pc += 2;
				switch ( opcode )
				{
				case 0x1A: // DECW  d
					data -= 2;
				case 0x3A: // INCW  d
					data++;
					nz = (data & 0x7F) | (data >> 1 & 0x7F) | (data >> 8);
					
					mem [addr    ] = (byte) data;
					mem [addr + 1] = (byte) (data >> 8);
					
					// addr >= 0xEF || addr <= 0xFF
					if ( (addr ^ 0xFF) <= 0x11 ) // 1%
					{
						cpuWrite( addr, data );
						cpuWrite( addr + 1, data >> 8 );
					}
					continue;
				
				case 0xBA: // MOVW  YA, d
					nz = 0x7F & (a = data & 0xFF);
					nz |= (a >> 1) | (y = data >> 8);
					continue;
					
				case 0x5A: // CMPW  YA, d
					data = (y << 8 | a) - data;
					nz = (data & 0x7F) | (data >> 1 & 0x7F);
					nz = (byte) (nz | (data >>= 8));
					c = ~data;
					continue;
				
				case 0x9A: // SUBW  YA, d
					data = -data & 0xFFFF;
				default: {
				//case 0x7A: // ADDW  YA, d
					int t = (data >> 8) ^ y;
					a = 0xFF & (data += y << 8 | a);
					t ^= (c = data >> 8);
					nz = (a & 0x7F) | (a >> 1) | (y = c & 0xFF);
					psw = (psw & ~(v40 | h08)) |
							(t >> 1 & h08) |
							((t + 0x80) >> 2 & v40);
					continue;
				}
				}
			}
			
		//////// Misc
	
			case 0x13: // BBC   d.0, r
			case 0x33: // BBC   d.1, r
			case 0x53: // BBC   d.2, r
			case 0x73: // BBC   d.3, r
			case 0x93: // BBC   d.4, r
			case 0xB3: // BBC   d.5, r
			case 0xD3: // BBC   d.6, r
			case 0xF3: // BBC   d.7, r
			case 0x03: // BBS   d.0, r
			case 0x23: // BBS   d.1, r
			case 0x43: // BBS   d.2, r
			case 0x63: // BBS   d.3, r
			case 0x83: // BBS   d.4, r
			case 0xA3: // BBS   d.5, r
			case 0xC3: // BBS   d.6, r
			case 0xE3: // BBS   d.7, r
			case 0x6E: // DBNZ  d, r
			case 0x2E: // CBNE  d, r
				data = cpuRead( (addr = mem [pc + 1] & 0xFF | dp) );
				pc += 3;
				break;
			
			case 0xDE: // CBNE  d+X, r
				data = cpuRead( (addr = (mem [pc + 1] + x) & 0xFF | dp) );
				pc += 3;
				break;
			
			case 0x1F: // JMP   [!a+X]
			case 0x3F: // CALL  !a
			case 0x5F: // JMP   !a
			case 0xC5: // MOV   !a, A
			case 0xC9: // MOV   !a, X
			case 0xCC: // MOV   !a, Y
				addr = (mem [pc + 2] & 0xFF) << 8 | (mem [pc + 1] & 0xFF);
				pc += 3;
				break;
			
			case 0x4F: // PCALL u
				addr = 0xFF00 | (mem [pc + 1] & 0xFF);
				pc += 2;
				break;
				
		//////// nz = operand, data = operand 2, addr = address of operand 2
	
			case 0x38: // AND   d, #i
			case 0x58: // EOR   d, #i
			case 0x78: // CMP   d, #i
			case 0x98: // ADC   d, #i
			case 0xB8: // SBC   d, #i
			case 0x18: // OR    d, #i
				nz = mem [pc + 1] & 0xFF;
				data = cpuRead( addr = mem [pc + 2] & 0xFF | dp );
				pc += 3;
				break;
				
			case 0x39: // AND   (X), (Y)
			case 0x59: // EOR   (X), (Y)
			case 0x79: // CMP   (X), (Y)
			case 0x99: // ADC   (X), (Y)
			case 0xB9: // SBC   (X), (Y)
			case 0x19: // OR    (X), (Y)
				nz = cpuRead( y + dp );
				data = cpuRead( addr = x + dp );
				pc++;
				break;
				
			case 0x29: // AND   dd, ds
			case 0x49: // EOR   dd, ds
			case 0x69: // CMP   dd, ds
			case 0x89: // ADC   dd, ds
			case 0xA9: // SBC   dd, ds
			case 0x09: // OR    dd, ds
				nz = cpuRead( mem [pc + 1] & 0xFF | dp );
				data = cpuRead( addr = mem [pc + 2] & 0xFF | dp );
				pc += 3;
				break;
			
		//////// nz = operand
	
			case 0x25: // AND   A, !a
			case 0x45: // EOR   A, !a
			case 0x65: // CMP   A, !a
			case 0x85: // ADC   A, !a
			case 0xA5: // SBC   A, !a
			case 0x05: // OR    A, !a
			case 0xE5: // MOV   A, !a
			case 0x0E: // TSET1 !a
			case 0x4E: // TCLR1 !a
			case 0x0C: // ASL   !a
			case 0x2C: // ROL   !a
			case 0x4C: // LSR   !a
			case 0x6C: // ROR   !a
			case 0x8C: // DEC   !a
			case 0xAC: // INC   !a
			case 0x1E: // CMP   X, !a
			case 0xE9: // MOV   X, !a
			case 0x5E: // CMP   Y, !a
			case 0xEC: // MOV   Y, !a
				nz = cpuRead( addr = (mem [pc + 2] & 0xFF) << 8 | (mem [pc + 1] & 0xFF) );
				pc += 3;
				break;
			
			case 0x35: // AND   A, !a+X
			case 0x55: // EOR   A, !a+X
			case 0x75: // CMP   A, !a+X
			case 0x95: // ADC   A, !a+X
			case 0xB5: // SBC   A, !a+X
			case 0x15: // OR    A, !a+X
				nz = cpuRead( ((mem [pc + 2] & 0xFF) << 8 | (mem [pc + 1] & 0xFF)) + x );
				pc += 3;
				break;
				
			case 0x36: // AND   A, !a+Y
			case 0x56: // EOR   A, !a+Y
			case 0x76: // CMP   A, !a+Y
			case 0x96: // ADC   A, !a+Y
			case 0xB6: // SBC   A, !a+Y
			case 0x16: // OR    A, !a+Y
			case 0xF6: // MOV   A, !a+Y
				nz = cpuRead( ((mem [pc + 2] & 0xFF) << 8 | (mem [pc + 1] & 0xFF)) + y );
				pc += 3;
				break;
				
			case 0x26: // AND   A, (X)
			case 0x46: // EOR   A, (X)
			case 0x66: // CMP   A, (X)
			case 0x86: // ADC   A, (X)
			case 0xA6: // SBC   A, (X)
			case 0x06: // OR    A, (X)
			case 0xE6: // MOV   A, (X)
				nz = cpuRead( x + dp );
				pc++;
				break;
			
			case 0x24: // AND   A, d
			case 0x44: // EOR   A, d
			case 0x64: // CMP   A, d
			case 0x84: // ADC   A, d
			case 0xA4: // SBC   A, d
			case 0x04: // OR    A, d
			case 0x0B: // ASL   d
			case 0x2B: // ROL   d
			case 0x4B: // LSR   d
			case 0x6B: // ROR   d
			case 0x8B: // DEC   d
			case 0xAB: // INC   d
			case 0x3E: // CMP   X, d
			case 0xF8: // MOV   X, d
			case 0x7E: // CMP   Y, d
				nz = cpuRead( addr = mem [pc + 1] & 0xFF | dp );
				pc += 2;
				break;
			
			case 0x34: // AND   A, d+X
			case 0x54: // EOR   A, d+X
			case 0x74: // CMP   A, d+X
			case 0x94: // ADC   A, d+X
			case 0xB4: // SBC   A, d+X
			case 0x14: // OR    A, d+X
			case 0x1B: // ASL   d+X
			case 0x3B: // ROL   d+X
			case 0x5B: // LSR   d+X
			case 0x7B: // ROR   d+X
			case 0x9B: // DEC   d+X
			case 0xBB: // INC   d+X
			case 0xFB: // MOV   Y, d+X
				nz = cpuRead( addr = (mem [pc + 1] + x) & 0xFF | dp );
				pc += 2;
				break;
				
			case 0x37: // AND   A, [d]+Y
			case 0x57: // EOR   A, [d]+Y
			case 0x77: // CMP   A, [d]+Y
			case 0x97: // ADC   A, [d]+Y
			case 0xB7: // SBC   A, [d]+Y
			case 0x17: // OR    A, [d]+Y
			case 0xF7:{// MOV   A, [d]+Y
				int t = mem [pc + 1];
				nz = cpuRead( ((mem [(t + 1) & 0xFF | dp] & 0xFF) << 8 | (mem [t & 0xFF | dp] & 0xFF)) + y );
				pc += 2;
				break;
			}
			
			case 0x27: // AND   A, [d+X]
			case 0x47: // EOR   A, [d+X]
			case 0x67: // CMP   A, [d+X]
			case 0x87: // ADC   A, [d+X]
			case 0xA7: // SBC   A, [d+X]
			case 0x07: // OR    A, [d+X]
			case 0xE7:{// MOV   A, [d+X]
				int t = mem [pc + 1] + x;
				nz = cpuRead( (mem [(t + 1) & 0xFF | dp] & 0xFF) << 8 | (mem [t & 0xFF | dp] & 0xFF) );
				pc += 2;
				break;
			}
				
			case 0x28: // AND   A, #i
			case 0x48: // EOR   A, #i
			case 0x68: // CMP   A, #i
			case 0x88: // ADC   A, #i
			case 0xA8: // SBC   A, #i
			case 0x08: // OR    A, #i
			case 0xE8: // MOV   A, #i
			case 0xC8: // CMP   X, #i
			case 0xCD: // MOV   X, #i
			case 0x8D: // MOV   Y, #i
			case 0xAD: // CMP   Y, #i
				nz = mem [pc + 1] & 0xFF;
				pc += 2;
				break;
			
			case 0xC4: // MOV   d, A
			case 0xD8: // MOV   d, X
			case 0xCB: // MOV   d, Y
				addr = mem [pc + 1] & 0xFF | dp;
				pc += 2;
				break;
			
			case 0xD4: // MOV   d+X, A
			case 0xDB: // MOV   d+X, Y
				addr = (mem [pc + 1] + x) & 0xFF | dp;
				pc += 2;
				break;
			}
			
			// Operation
			switch ( opcode )
			{
			case 0x5F: // JMP   !a
				pc = addr;
				continue;
			
			case 0x1F: // JMP   [!a+X]
				addr += x;
				pc = (mem [addr + 1] & 0xFF) << 8 | (mem [addr] & 0xFF);
				continue;
			
			case 0x13: // BBC   d.0, r
			case 0x33: // BBC   d.1, r
			case 0x53: // BBC   d.2, r
			case 0x73: // BBC   d.3, r
			case 0x93: // BBC   d.4, r
			case 0xB3: // BBC   d.5, r
			case 0xD3: // BBC   d.6, r
			case 0xF3: // BBC   d.7, r
			case 0x03: // BBS   d.0, r
			case 0x23: // BBS   d.1, r
			case 0x43: // BBS   d.2, r
			case 0x63: // BBS   d.3, r
			case 0x83: // BBS   d.4, r
			case 0xA3: // BBS   d.5, r
			case 0xC3: // BBS   d.6, r
			case 0xE3: // BBS   d.7, r
				if ( (((data >> (opcode >> 5)) ^ (opcode >> 4)) & 1) != 0 )
				{
					pc += mem [pc - 1];
					time += 2;
				}
				continue;
			
			case 0x6E: // DBNZ  d, r
				cpuWrite( addr, --data );
				if ( data != 0 )
				{
					pc += mem [pc - 1];
					time += 2;
				}
				continue;
			
			case 0xDE: // CBNE  d+X, r
			case 0x2E: // CBNE  d, r
				if ( data == a )
					continue;
			case 0xFE: // DBNZ  Y, r
				time += 2;
				pc += mem [pc - 1];
				continue;
			
			case 0x01: // TCALL 0
			case 0x11: // TCALL 1
			case 0x21: // TCALL 2
			case 0x31: // TCALL 3
			case 0x41: // TCALL 4
			case 0x51: // TCALL 5
			case 0x61: // TCALL 6
			case 0x71: // TCALL 7
			case 0x81: // TCALL 8
			case 0x91: // TCALL 9
			case 0xA1: // TCALL 10
			case 0xB1: // TCALL 11
			case 0xC1: // TCALL 12
			case 0xD1: // TCALL 13
			case 0xE1: // TCALL 14
			case 0xF1: // TCALL 15
				addr = 0xFFDE - (opcode >> 3);
				addr = (mem [addr + 1] & 0xFF) << 8 | (mem [addr] & 0xFF);
			case 0x4F: // PCALL u
			case 0x3F: // CALL  !a
				mem [(sp - 1) | 0x100] = (byte) (pc >> 8);
				mem [sp = (sp - 2) | 0x100] = (byte) pc;
				pc = addr;
				continue;
			
			case 0x65: // CMP   A, !a
			case 0x75: // CMP   A, !a+X
			case 0x76: // CMP   A, !a+Y
			case 0x68: // CMP   A, #i
			case 0x66: // CMP   A, (X)
			case 0x67: // CMP   A, [d+X]
			case 0x77: // CMP   A, [d]+Y
			case 0x64: // CMP   A, d
			case 0x74: // CMP   A, d+X
				c = ~(nz = a - nz);
				nz = (byte) nz;
				continue;
			
			case 0x1E: // CMP   X, !a
			case 0xC8: // CMP   X, #i
			case 0x3E: // CMP   X, d
				c = ~(nz = x - nz);
				nz = (byte) nz;
				continue;
			
			case 0x5E: // CMP   Y, !a
			case 0xAD: // CMP   Y, #i
			case 0x7E: // CMP   Y, d
				c = ~(nz = y - nz);
				nz = (byte) nz;
				continue;
	
			case 0x78: // CMP   d, #i
			case 0x69: // CMP   dd, ds
			case 0x79: // CMP   (X), (Y)
				c = ~(nz = data - nz);
				nz = (byte) nz;
				continue;
			
			case 0xA5: // SBC   A, !a
			case 0xB5: // SBC   A, !a+X
			case 0xB6: // SBC   A, !a+Y
			case 0xA8: // SBC   A, #i
			case 0xA6: // SBC   A, (X)
			case 0xA7: // SBC   A, [d+X]
			case 0xB7: // SBC   A, [d]+Y
			case 0xA4: // SBC   A, d
			case 0xB4: // SBC   A, d+X
				nz ^= 0xFF;
			case 0x85: // ADC   A, !a
			case 0x95: // ADC   A, !a+X
			case 0x96: // ADC   A, !a+Y
			case 0x88: // ADC   A, #i
			case 0x86: // ADC   A, (X)
			case 0x87: // ADC   A, [d+X]
			case 0x97: // ADC   A, [d]+Y
			case 0x84: // ADC   A, d
			case 0x94:{// ADC   A, d+X
				int flags = a ^ nz;
				flags ^= (c = nz += a + (c >> 8 & 1));
				psw = (psw & ~(v40 | h08)) |
						(flags >> 1 & h08) |
						((flags + 0x80) >> 2 & v40);
				a = nz & 0xFF;
				continue;
			}
			
			case 0xB9: // SBC   (X), (Y)
			case 0xB8: // SBC   d, #i
			case 0xA9: // SBC   dd, ds
				nz ^= 0xFF;
			case 0x99: // ADC   (X), (Y)
			case 0x98: // ADC   d, #i
			case 0x89:{// ADC   dd, ds
				int flags = nz ^ data;
				flags ^= (c = nz += data + (c >> 8 & 1));
				psw = (psw & ~(v40 | h08)) |
						(flags >> 1 & h08) |
						((flags + 0x80) >> 2 & v40);
				cpuWrite( addr, nz );
				continue;
			}
			
			case 0x25: // AND   A, !a
			case 0x35: // AND   A, !a+X
			case 0x36: // AND   A, !a+Y
			case 0x28: // AND   A, #i
			case 0x26: // AND   A, (X)
			case 0x27: // AND   A, [d+X]
			case 0x37: // AND   A, [d]+Y
			case 0x24: // AND   A, d
			case 0x34: // AND   A, d+X
				nz = a &= nz;
				continue;
			
			case 0x39: // AND   (X), (Y)
			case 0x38: // AND   d, #i
			case 0x29: // AND   dd, ds
				cpuWrite( addr, nz &= data );
				continue;
			
			case 0x05: // OR    A, !a
			case 0x15: // OR    A, !a+X
			case 0x16: // OR    A, !a+Y
			case 0x08: // OR    A, #i
			case 0x06: // OR    A, (X)
			case 0x07: // OR    A, [d+X]
			case 0x17: // OR    A, [d]+Y
			case 0x04: // OR    A, d
			case 0x14: // OR    A, d+X
				nz = a |= nz;
				continue;
			
			case 0x19: // OR    (X), (Y)
			case 0x18: // OR    d, #i
			case 0x09: // OR    dd, ds
				cpuWrite( addr, nz |= data );
				continue;
				
			case 0x45: // EOR   A, !a
			case 0x55: // EOR   A, !a+X
			case 0x56: // EOR   A, !a+Y
			case 0x48: // EOR   A, #i
			case 0x46: // EOR   A, (X)
			case 0x47: // EOR   A, [d+X]
			case 0x57: // EOR   A, [d]+Y
			case 0x44: // EOR   A, d
			case 0x54: // EOR   A, d+X
				nz = a ^= nz;
				continue;
			
			case 0x59: // EOR   (X), (Y)
			case 0x58: // EOR   d, #i
			case 0x49: // EOR   dd, ds
				cpuWrite( addr, nz ^= data );
				continue;
			
			case 0x8C: // DEC   !a
			case 0x8B: // DEC   d
			case 0x9B: // DEC   d+X
				cpuWrite( addr, --nz );
				continue;
			
			case 0xAC: // INC   !a
			case 0xAB: // INC   d
			case 0xBB: // INC   d+X
				cpuWrite( addr, ++nz );
				continue;
			
			case 0x0C: // ASL   !a
			case 0x0B: // ASL   d
			case 0x1B: // ASL   d+X
				c = 0;
			case 0x2C: // ROL   !a
			case 0x2B: // ROL   d
			case 0x3B:{// ROL   d+X
				int t = c >> 8 & 1;
				c = nz << 1;
				cpuWrite( addr, nz = c | t );
				continue;
			}
			
			case 0x4C: // LSR   !a
			case 0x4B: // LSR   d
			case 0x5B: // LSR   d+X
				c = 0;
			case 0x6C: // ROR   !a
			case 0x6B: // ROR   d
			case 0x7B:{// ROR   d+X
				int t = c & 0x100;
				c = nz << 8;
				cpuWrite( addr, nz = (nz | t) >> 1 );
				continue;
			}
			
			case 0x4E:{// TCLR1 !a
				int t = nz & ~a;
				nz = (byte) (a - nz);
				cpuWrite( addr, t );
				continue;
			}
			
			case 0x0E:{// TSET1 !a
				int t = nz | a;
				nz = (byte) (a - nz);
				cpuWrite( addr, t );
				continue;
			}
			
			case 0xC4: // MOV   d, A
			case 0xD4: // MOV   d+X, A
			case 0xC5: // MOV   !a, A
				cpuWrite( addr, a );
				continue;
			
			case 0xD8: // MOV   d, X
			case 0xC9: // MOV   !a, X
				cpuWrite( addr, x );
				continue;
			
			case 0xCB: // MOV   d, Y
			case 0xDB: // MOV   d+X, Y
			case 0xCC: // MOV   !a, Y
				cpuWrite( addr, y );
				continue;
			
			case 0xE5: // MOV   A, !a
			case 0xF6: // MOV   A, !a+Y
			case 0xE8: // MOV   A, #i
			case 0xE6: // MOV   A, (X)
			case 0xBF: // MOV   A, (X)+
			case 0x7D: // MOV   A, X
			case 0xDD: // MOV   A, Y
			case 0xE7: // MOV   A, [d+X]
			case 0xF7: // MOV   A, [d]+Y
				a = nz;
				continue;
			
			case 0xE9: // MOV   X, !a
			case 0xCD: // MOV   X, #i
			case 0x5D: // MOV   X, A
			case 0x9D: // MOV   X, SP
			case 0xF8: // MOV   X, d
				x = nz;
				continue;
			
			case 0xEC: // MOV   Y, !a
			case 0x8D: // MOV   Y, #i
			case 0xFD: // MOV   Y, A
			case 0xFB: // MOV   Y, d+X
				y = nz;
				continue;
			}
		}
	
		// calculate PSW
		psw &= ~(n80 | p20 | z02 | c01);
		psw |= c  >> 8 & c01;
		psw |= dp >> 3 & p20;
		psw |= ((nz >> 4) | nz) & n80;
		if ( ((byte) nz) == 0 ) psw |= z02;
	
		this.pc   = pc;
		this.a    = a;
		this.x    = x;
		this.y    = y;
		this.psw  = psw;
		this.sp   = (sp - 1) & 0xFF;
		this.time = time;
	}
}

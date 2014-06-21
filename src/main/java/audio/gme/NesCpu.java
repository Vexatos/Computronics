package audio.gme;

// Nintendo NES 6502 CPU emulator
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

public class NesCpu extends ClassicEmu
{
	// Resets registers and uses supplied physical memory
	public final void reset( byte [] mem, int unmapped )
	{
		this.mem = mem;
		a  = 0;
		x  = 0;
		y  = 0;
		s  = 0xFF;
		pc = 0;
		p  = 0x04;
		c  = 0;
		nz = 1;
		
		time = 0;
		
		for ( int i = 0; i < pageCount + 1; i++ )
			mapPage( i, unmapped );
	}
	
	static final int pageShift = 11;
	static final int pageCount = 0x10000 >> pageShift;
	public static final int pageSize = 1 << pageShift;
	
	// Maps address range to offset in physical memory
	public final void mapMemory( int addr, int size, int offset )
	{
		assert addr % pageSize == 0;
		assert size % pageSize == 0;
		int firstPage = addr / pageSize;
		for ( int i = size / pageSize; i-- > 0; )
			mapPage( firstPage + i, offset + i * pageSize );
	}
	
	// Maps address to memory
	public final int mapAddr( int addr ) { return pages [addr >> pageShift] + addr; }
	
// Emulation
	
	// Registers. NOT kept updated during runCpu()
	public int a, x, y, p, s, pc;
	
	// Current time
	public int time;
	
	// Memory read and write handlers
	protected int  cpuRead ( int addr ) { return 0; }
	protected void cpuWrite( int addr, int data ) { }
	
	final int pages [] = new int [pageCount + 1];
	int c, nz;
	byte [] mem;
	
	final void mapPage( int page, int offset )
	{
		if ( debug ) assert 0 <= page && page < pageCount + 1;
		pages [page] = offset - page * pageSize;
	}
	
	static final int [] instrTimes =
	{// 0 1 2 3 4 5 6 7 8 9 A B C D E F
		7,6,2,8,3,3,5,5,3,2,2,2,4,4,6,6,// 0
		2,5,2,8,4,4,6,6,2,4,2,7,4,4,7,7,// 1
		6,6,2,8,3,3,5,5,4,2,2,2,4,4,6,6,// 2
		2,5,2,8,4,4,6,6,2,4,2,7,4,4,7,7,// 3
		6,6,2,8,3,3,5,5,3,2,2,2,3,4,6,6,// 4
		2,5,2,8,4,4,6,6,2,4,2,7,4,4,7,7,// 5
		6,6,2,8,3,3,5,5,4,2,2,2,5,4,6,6,// 6
		2,5,2,8,4,4,6,6,2,4,2,7,4,4,7,7,// 7
		2,6,2,6,3,3,3,3,2,2,2,2,4,4,4,4,// 8
		2,6,2,6,4,4,4,4,2,5,2,5,5,5,5,5,// 9
		2,6,2,6,3,3,3,3,2,2,2,2,4,4,4,4,// A
		2,5,2,5,4,4,4,4,2,4,2,4,4,4,4,4,// B
		2,6,2,8,3,3,5,5,2,2,2,2,4,4,6,6,// C
		2,5,2,8,4,4,6,6,2,4,2,7,4,4,7,7,// D
		2,6,2,8,3,3,5,5,2,2,2,2,4,4,6,6,// E
		2,5,0,8,4,4,6,6,2,4,2,7,4,4,7,7 // F
	}; // 0xF2 was 2
	
	static final int [] illop_lens = {
		0x95, 0x95, 0x95, 0xD5, 0x95, 0x95, 0xD5, 0xF5
	};
	
	static final int N80 = 0x80;
	static final int V40 = 0x40;
	static final int R20 = 0x20;
	static final int B10 = 0x10;
	static final int D08 = 0x08;
	static final int I04 = 0x04;
	static final int Z02 = 0x02;
	static final int C01 = 0x01;
	
	// Runs until time >= 0
	public final void runCpu()
	{
		// locals are faster, and first three are more efficient to access
		final byte [] mem = this.mem;
		int nz = this.nz;
		int pc = this.pc;
		
		int time = this.time;
		int a  = this.a;
		int x  = this.x;
		int y  = this.y;
		int sp = (this.s + 1) | 0x100;
		int p  = this.p;
		int c  = this.c;
		final int pages [] = this.pages;
		final int instrTimes [] = this.instrTimes;
		
		int addr = 0;
		
	loop:
		while ( time < 0 )
		{
			if ( debug )
			{
				assert 0 <= a && a < 0x100;
				assert 0 <= x && x < 0x100;
				assert 0 <= y && y < 0x100;
				assert (p & ~(V40 | D08 | I04)) == 0;
				assert 0 <= pc && pc < 0x10000;
				assert 0x100 <= sp && sp < 0x200;
			}
			
			int instr;
			int opcode;
			this.time =
				(time += instrTimes [opcode =
					0xFF & mem [instr =
						pages [pc >> pageShift] + pc]]);
			instr++;
			
			// nz is used as variable for incoming data of instructions that
			// will be modifying nz anyway.
			
			// Source
			switch ( opcode )
			{
			
		//////// Often used
		
			case 0xD0:{// BNE r
				pc += 2;
				if ( ((byte) nz) != 0 )
				{
					int old = pc;
					time += (((pc += mem [instr]) ^ old) >> 8 & 1) + 1;
				}
				continue;
			}
			
			case 0xF0:{// BEQ r
				pc += 2;
				if ( ((byte) nz) == 0 )
				{
					int old = pc;
					time += (((pc += mem [instr]) ^ old) >> 8 & 1) + 1;
				}
				continue;
			}
			
			case 0xBD:{// LDA a,X
				pc += 3;
				int lsb;
				time += (lsb = (mem [instr] & 0xFF) + x) >> 8;
				a = nz = cpuRead( ((mem [instr + 1] & 0xFF) << 8) + lsb );
				continue;
			}
			
			case 0xC8: // INY
				pc++;
				y = (nz = y + 1) & 0xFF;
				continue;
			
			case 0x85: // STA z
				pc += 2;
				mem [mem [instr] & 0xFF] = (byte) a;
				continue;
			
			case 0xC9: // CMP #n
				pc += 2;
				c = ~(nz = a - (mem [instr] & 0xFF));
				nz = (byte) nz;
				continue;
			
			case 0x20:{// JSR a
				int t = pc + 2;
				pc = (mem [instr + 1] & 0xFF) << 8 | (mem [instr] & 0xFF);
				mem [(sp - 1) | 0x100] = (byte) (t >> 8);
				mem [sp = (sp - 2) | 0x100] = (byte) t;
				continue;
			}
			
		////////
		
			case 0x10: // BPL r
				pc += 2;
				if ( (nz & 0x8080) == 0 )
					break;
				continue;
			
			case 0x30: // BMI r
				pc += 2;
				if ( (nz & 0x8080) != 0 )
					break;
				continue;
			
			case 0x50: // BVC r
				pc += 2;
				if ( (p & V40) == 0 )
					break;
				continue;
			
			case 0x70: // BVS r
				pc += 2;
				if ( (p & V40) != 0 )
					break;
				continue;
			
			case 0x90: // BCC r
				pc += 2;
				if ( (c & 0x100) == 0 )
					break;
				continue;
			
			case 0xB0: // BCS r
				pc += 2;
				if ( (c & 0x100) != 0 )
					break;
				continue;
			
			case 0xBA: // TSX
				pc++;
				x = (nz = sp - 1) & 0xFF;
				continue;
			
			case 0x9A: // TXS
				pc++;
				sp = (x + 1) | 0x100;
				continue;
			
			case 0x18: // CLC
				pc++;
				c = 0;
				continue;
			
			case 0x38: // SEC
				pc++;
				c = ~0;
				continue;
			
			case 0xD8: // CLD
				pc++;
				p &= ~D08;
				continue;
			
			case 0xB8: // CLV
				pc++;
				p &= ~V40;
				continue;
			
			case 0x58: // CLI
				pc++;
				p &= ~I04;
				continue;
			
			case 0x78: // SEI
				pc++;
				p |= I04;
				continue;
			
			case 0xF8: // SED
				pc++;
				p |= D08;
				continue;
			
			case 0x48: // PHA
				pc++;
				mem [sp = (sp - 1) | 0x100] = (byte) a;
				continue;
			
			// SKW - Skip word
			case 0x1C: case 0x3C: case 0x5C: case 0x7C: case 0xDC: case 0xFC:
				time += ((mem [instr] & 0xFF) + x) >> 8;
			case 0x0C:
				pc += 3;
				continue;
			
			// SKB - Skip byte
			case 0x74: case 0x04: case 0x14: case 0x34: case 0x44: case 0x54: case 0x64:
			case 0x80: case 0x82: case 0x89: case 0xC2: case 0xD4: case 0xE2: case 0xF4:
				pc += 2;
				continue;
			
			// NOP
			case 0xEA: case 0x1A: case 0x3A: case 0x5A: case 0x7A: case 0xDA: case 0xFA:
				pc++;
				continue;
			
			// Illegal
			case 0xF2:
				if ( pc > 0xFFFF )
				{
					// handle wrap-around (assumes caller has put 0xF2 at 0x1000-0x10FF)
					pc &= 0xFFFF;
					break;
				}
			case 0x02: case 0x12: case 0x22: case 0x32: case 0x42: case 0x52:
			case 0x62: case 0x72: case 0x92: case 0xB2: case 0xD2:
				break loop;
			
			// Unimplemented
			default:
			/*
				assert false;
			case 0x03: case 0x07: case 0x0B: case 0x0F:
			case 0x13: case 0x17: case 0x1B: case 0x1F:
			case 0x23: case 0x27: case 0x2B: case 0x2F:
			case 0x33: case 0x37: case 0x3B: case 0x3F:
			case 0x43: case 0x47: case 0x4B: case 0x4F:
			case 0x53: case 0x57: case 0x5B: case 0x5F:
			case 0x63: case 0x67: case 0x6B: case 0x6F:
			case 0x73: case 0x77: case 0x7B: case 0x7F:
			case 0x83: case 0x87: case 0x8B: case 0x8F:
			case 0x93: case 0x97: case 0x9B: case 0x9F:
			case 0xA3: case 0xA7: case 0xAB: case 0xAF:
			case 0xB3: case 0xB7: case 0xBB: case 0xBF:
			case 0xC3: case 0xC7: case 0xCB: case 0xCF:
			case 0xD3: case 0xD7: case 0xDB: case 0xDF:
			case 0xE3: case 0xE7:            case 0xEF:
			case 0xF3: case 0xF7: case 0xFB: case 0xFF:
			case 0x9C: case 0x9E:
			*/
				if ( (opcode >> 4) == 0x0B )
				{
					int t = mem [instr] & 0xFF;
					if ( opcode == 0xB3 )
						t = mem [t] & 0xFF;
					if ( opcode != 0xB7 )
						time += (t + y) >> 8;
				}
				
				// skip over proper number of bytes
				int len = illop_lens [opcode >> 2 & 7] >> (opcode << 1 & 6) & 3;
				if ( opcode == 0x9C )
					len = 3;
				pc += len;
				continue;
			
			case 0x0A: // ASL
			case 0x2A: // ROL
			case 0x4A: // LSR
			case 0x6A: // ROR
				pc++;
				nz = a;
				break;
			
			case 0x09: // ORA #n
			case 0x29: // AND #n
			case 0x49: // EOR #n
			case 0x69: // ADC #n
			case 0xA0: // LDY #n
			case 0xA2: // LDX #n
			case 0xA9: // LDA #n
			case 0xC0: // CPY #n
			case 0xE0: // CPX #n
			case 0xE9: // SBC #n
			case 0xEB: // SBC #n (unofficial)
				pc += 2;
				nz = mem [instr] & 0xFF;
				break;
	
			case 0x84: // STY z
			case 0x86: // STX z
				pc += 2;
				addr = mem [instr] & 0xFF;
				break;
			
			case 0x06: // ASL z
			case 0x26: // ROL z
			case 0x66: // ROR z
			case 0xE6: // INC z
			case 0xC6: // DEC z
			case 0x46: // LSR z
				pc += 2;
				nz = mem [addr = mem [instr] & 0xFF] & 0xFF;
				break;
			
			case 0x05: // ORA z
			case 0x24: // BIT z
			case 0x25: // AND z
			case 0x45: // EOR z
			case 0x65: // ADC z
			case 0xA4: // LDY z
			case 0xA5: // LDA z
			case 0xA6: // LDX z
			case 0xC4: // CPY z
			case 0xC5: // CMP z
			case 0xE4: // CPX z
			case 0xE5: // SBC z
				pc += 2;
				nz = mem [mem [instr] & 0xFF] & 0xFF;
				break;
	
			case 0x11: // ORA (z),Y
			case 0x31: // AND (z),Y
			case 0x51: // EOR (z),Y
			case 0x71: // ADC (z),Y
			case 0xB1: // LDA (z),Y
			case 0xD1: // CMP (z),Y
			case 0xF1: // SBC (z),Y
			case 0x91:{// STA (z),Y
				pc += 2;
				int z = mem [instr];
				int lsb = (mem [z & 0xFF] & 0xFF) + y;
				addr = ((mem [(z + 1) & 0xFF] & 0xFF) << 8) + lsb;
				if ( opcode != 0x91 )
				{
					time += lsb >> 8;
					nz = cpuRead( addr );
				}
				break;
			}
			
			case 0x01: // ORA (z,X)
			case 0x21: // AND (z,X)
			case 0x41: // EOR (z,X)
			case 0x61: // ADC (z,X)
			case 0xA1: // LDA (z,X)
			case 0xC1: // CMP (z,X)
			case 0xE1: // SBC (z,X)
			case 0x81:{// STA (z,X)
				pc += 2;
				int z = mem [instr] + x;
				addr = (mem [(z + 1) & 0xFF] & 0xFF) << 8 | (mem [z & 0xFF] & 0xFF);
				if ( opcode != 0x81 )
					nz = cpuRead( addr );
				break;
			}
			
			case 0x6C: // JMP (a)
			case 0x8C: // STY a
			case 0x8D: // STA a
			case 0x8E: // STX a
			case 0x4C: // JMP a
				pc += 3;
				addr = (mem [instr + 1] & 0xFF) << 8 | (mem [instr] & 0xFF);
				break;
			
			case 0x0D: // ORA a
			case 0x0E: // ASL a
			case 0x2C: // BIT a
			case 0x2D: // AND a
			case 0x2E: // ROL a
			case 0x4D: // EOR a
			case 0x4E: // LSR a
			case 0x6D: // ADC a
			case 0x6E: // ROR a
			case 0xAC: // LDY a
			case 0xAD: // LDA a
			case 0xAE: // LDX a
			case 0xCC: // CPY a
			case 0xCD: // CMP a
			case 0xCE: // DEC a
			case 0xEC: // CPX a
			case 0xED: // SBC a
			case 0xEE: // INC a
				pc += 3;
				nz = cpuRead( addr = (mem [instr + 1] & 0xFF) << 8 | (mem [instr] & 0xFF) );
				break;
	
			case 0x1E: // ASL a,X
			case 0x3E: // ROL a,X
			case 0x5E: // LSR a,X
			case 0x7E: // ROR a,X
			case 0xDE: // DEC a,X
			case 0xFE: // INC a,X
				pc += 3;
				nz = cpuRead( addr = ((mem [instr + 1] & 0xFF) << 8 | (mem [instr] & 0xFF)) + x );
				// RMW instructions have no extra clock for page crossing
				break;
			
			case 0x1D: // ORA a,X
			case 0x3D: // AND a,X
			case 0x5D: // EOR a,X
			case 0x7D: // ADC a,X
			case 0xBC: // LDY a,X
			case 0xDD: // CMP a,X
			case 0xFD: // SBC a,X
			case 0x9D:{// STA a,X
				pc += 3;
				int lsb = (mem [instr] & 0xFF) + x;
				addr = ((mem [instr + 1] & 0xFF) << 8) + lsb;
				if ( opcode != 0x9D )
				{
					time += lsb >> 8;
					nz = cpuRead( addr );
				}
				break;
			}
			
			case 0x19: // ORA a,Y
			case 0x39: // AND a,Y
			case 0x59: // EOR a,Y
			case 0x79: // ADC a,Y
			case 0xB9: // LDA a,Y
			case 0xBE: // LDX a,Y
			case 0xD9: // CMP a,Y
			case 0xF9: // SBC a,Y
			case 0x99:{// STA a,Y
				pc += 3;
				int lsb = (mem [instr] & 0xFF) + y;
				addr = ((mem [instr + 1] & 0xFF) << 8) + lsb;
				if ( opcode != 0x99 )
				{
					time += lsb >> 8;
					nz = cpuRead( addr );
				}
				break;
			}
			
			case 0x15: // ORA z,X
			case 0x16: // ASL z,X
			case 0x35: // AND z,X
			case 0x36: // ROL z,X
			case 0x55: // EOR z,X
			case 0x56: // LSR z,X
			case 0x75: // ADC z,X
			case 0x76: // ROR z,X
			case 0xB4: // LDY z,X
			case 0xB5: // LDA z,X
			case 0xD5: // CMP z,X
			case 0xD6: // DEC z,X
			case 0xF5: // SBC z,X
			case 0xF6: // INC z,X
				pc += 2;
				nz = mem [addr = (mem [instr] + x) & 0xFF] & 0xFF;
				break;
			
			case 0x94: // STY z,X
			case 0x95: // STA z,X
				pc += 2;
				addr = (mem [instr] + x) & 0xFF;
				break;
			
			case 0xB6: // LDX z,Y
			case 0x96: // STX z,Y
				pc += 2;
				addr = (mem [instr] + y) & 0xFF;
				if ( opcode != 0x96 )
					nz = mem [addr] & 0xFF;
				break;
			
			case 0xAA: // TAX
			case 0xA8: // TAY
				pc++;
				nz = a;
				break;
			
			case 0xCA: // DEX
			case 0xE8: // INX
			case 0x8A: // TXA
				pc++;
				nz = x;
				break;
			
			case 0x88: // DEY
			case 0x98: // TYA
				pc++;
				nz = y;
				break;
			
			case 0x28: // PLP
			case 0x68: // PLA
				pc++;
				nz = mem [sp] & 0xFF;
				sp = (sp - 0xFF) | 0x100;
				break;
			
			case 0x40: // RTI
				nz = mem [sp] & 0xFF;
				sp = (sp - 0xFF) | 0x100;
			case 0x60: // RTS
				pc = ((mem [(sp - 0xFF) | 0x100] & 0xFF) << 8 | (mem [sp] & 0xFF)) + 1;
				sp = (sp - 0xFE) | 0x100;
				break;
			
			case 0x00: // BRK #n
			case 0x08: // PHP
				break;
			}
			
			// Operation
			switch ( opcode )
			{
			case 0x60: // RTS
				continue;
			
			case 0x91: // STA (z),Y
			case 0x81: // STA (z,X)
			case 0x8D: // STA a
			case 0x9D: // STA a,X
			case 0x99: // STA a,Y
				if ( addr > 0x7FF )
				{
					cpuWrite( addr, a );
					continue;
				}
			case 0x95: // STA z,X
				mem [addr] = (byte) a;
				continue;
			
			case 0x8E: // STX a
				if ( addr > 0x7FF )
				{
					cpuWrite( addr, x );
					continue;
				}
			case 0x86: // STX z
			case 0x96: // STX z,Y
				mem [addr] = (byte) x;
				continue;
			
			case 0x8C: // STY a
				if ( addr > 0x7FF )
				{
					cpuWrite( addr, y );
					continue;
				}
			case 0x84: // STY z
			case 0x94: // STY z,X
				mem [addr] = (byte) y;
				continue;
			
			case 0x10: // BPL r
			case 0x30: // BMI r
			case 0x50: // BVC r
			case 0x70: // BVS r
			case 0x90: // BCC r
			case 0xB0:{// BCS r
				int old = pc;
				time += (((pc += mem [instr]) ^ old) >> 8 & 1) + 1;
				continue;
			}
			
			case 0xEC: // CPX a
			case 0xE4: // CPX z
			case 0xE0: // CPX #n
				c = ~(nz = x - nz);
				nz = (byte) nz;
				continue;
			
			case 0xCC: // CPY a
			case 0xC4: // CPY z
			case 0xC0: // CPY #n
				c = ~(nz = y - nz);
				nz = (byte) nz;
				continue;
			
			case 0xD1: // CMP (z),Y
			case 0xC1: // CMP (z,X)
			case 0xCD: // CMP a
			case 0xDD: // CMP a,X
			case 0xD9: // CMP a,Y
			case 0xC5: // CMP z
			case 0xD5: // CMP z,X
				c = ~(nz = a - nz);
				nz = (byte) nz;
				continue;
			
			case 0xF1: // SBC (z),Y
			case 0xE1: // SBC (z,X)
			case 0xED: // SBC a
			case 0xFD: // SBC a,X
			case 0xF9: // SBC a,Y
			case 0xE5: // SBC z
			case 0xF5: // SBC z,X
			case 0xE9: // SBC #n
			case 0xEB: // SBC #n (unofficial)
				nz ^= 0xFF;
			case 0x71: // ADC (z),Y
			case 0x61: // ADC (z,X)
			case 0x6D: // ADC a
			case 0x7D: // ADC a,X
			case 0x79: // ADC a,Y
			case 0x65: // ADC z
			case 0x75: // ADC z,X
			case 0x69:{// ADC #n
				int t = nz ^ a;
				c = (nz += (c >> 8 & 1) + a);
				a = nz & 0xFF;
				p = (p & ~V40) | (((t ^ nz) + 0x80) >> 2 & V40);
				continue;
			}
			
			case 0x31: // AND (z),Y
			case 0x21: // AND (z,X)
			case 0x2D: // AND a
			case 0x3D: // AND a,X
			case 0x39: // AND a,Y
			case 0x25: // AND z
			case 0x35: // AND z,X
			case 0x29: // AND #n
				a = (nz &= a);
				continue;
			
			case 0x11: // ORA (z),Y
			case 0x01: // ORA (z,X)
			case 0x0D: // ORA a
			case 0x1D: // ORA a,X
			case 0x19: // ORA a,Y
			case 0x05: // ORA z
			case 0x15: // ORA z,X
			case 0x09: // ORA #n
				a = (nz |= a);
				continue;
			
			case 0x51: // EOR (z),Y
			case 0x41: // EOR (z,X)
			case 0x4D: // EOR a
			case 0x5D: // EOR a,X
			case 0x59: // EOR a,Y
			case 0x45: // EOR z
			case 0x55: // EOR z,X
			case 0x49: // EOR #n
				a = (nz ^= a);
				continue;
			
			case 0x2C: // BIT a
			case 0x24: // BIT z
				p = (p & ~V40) | (nz & V40);
				if ( (a & nz) == 0 )
					nz <<= 8; // result must be zero, even if N bit is set
				continue;
				
			case 0x6C: // JMP (a)
				pc = cpuRead( addr + 1 - (((addr & 0xFF) + 1) & 0x100) ) << 8 | cpuRead( addr );
				continue;
			
			case 0x4C: // JMP a
				pc = addr;
				continue;
			
			case 0x40: // RTI
				pc--;
			case 0x28: // PLP
				p = nz & (V40 | D08 | I04);
				nz = (c = nz << 8) | (~nz & Z02);
				continue;
			
			case 0x00:{// BRK #n
				int t = pc + 2;
				pc = cpuRead( 0xFFFF ) << 8 | cpuRead( 0xFFFE );
				mem [(sp - 1) | 0x100] = (byte) (t >> 8);
				mem [sp = (sp - 2) | 0x100] = (byte) t;
				break;
			}
			
			case 0x4E: // LSR a
			case 0x5E: // LSR a,X
			case 0x46: // LSR z
			case 0x56: // LSR z,X
			case 0x4A: // LSR
				c = nz << 8;
				nz >>= 1;
				break;
			
			case 0x0E: // ASL a
			case 0x1E: // ASL a,X
			case 0x06: // ASL z
			case 0x16: // ASL z,X
			case 0x0A: // ASL
				nz = (c = nz << 1) & 0xFF;
				break;
			
			case 0x6E: // ROR a
			case 0x7E: // ROR a,X
			case 0x66: // ROR z
			case 0x76: // ROR z,X
			case 0x6A:{// ROR
				int t = c & 0x100;
				c = nz << 8;
				nz = (nz | t) >> 1;
				break;
			}
			
			case 0x2E: // ROL a
			case 0x3E: // ROL a,X
			case 0x26: // ROL z
			case 0x36: // ROL z,X
			case 0x2A:{// ROL
				int t = c >> 8 & 1;
				nz = ((c = nz << 1) & 0xFF) | t;
				break;
			}
			
			case 0xCA: // DEX
			case 0x88: // DEY
			case 0xCE: // DEC a
			case 0xDE: // DEC a,X
			case 0xC6: // DEC z
			case 0xD6: // DEC z,X
				nz = (nz - 1) & 0xFF;
				break;
			
			case 0xE8: // INX
			case 0xEE: // INC a
			case 0xFE: // INC a,X
			case 0xE6: // INC z
			case 0xF6: // INC z,X
				nz = (nz + 1) & 0xFF;
				break;
			}
	
			// Destination
			switch ( opcode )
			{
			case 0x2A: // ROL
			case 0x0A: // ASL
			case 0x6A: // ROR
			case 0x4A: // LSR
			case 0x8A: // TXA
			case 0x98: // TYA
			case 0xB1: // LDA (z),Y
			case 0xA1: // LDA (z,X)
			case 0xAD: // LDA a
			case 0xB9: // LDA a,Y
			case 0xA5: // LDA z
			case 0xB5: // LDA z,X
			case 0xA9: // LDA #n
			case 0x68: // PLA
				a = nz;
				continue;
			
			case 0xCA: // DEX
			case 0xE8: // INX
			case 0xAA: // TAX
			case 0xAE: // LDX a
			case 0xBE: // LDX a,Y
			case 0xA6: // LDX z
			case 0xB6: // LDX z,Y
			case 0xA2: // LDX #n
				x = nz;
				continue;
			
			case 0x88: // DEY
			case 0xA8: // TAY
			case 0xAC: // LDY a
			case 0xBC: // LDY a,X
			case 0xA4: // LDY z
			case 0xB4: // LDY z,X
			case 0xA0: // LDY #n
				y = nz;
				continue;
			
			case 0x08: // PHP
				pc++;
			case 0x00:{// BRK #n
				int t = p | R20 | B10 | (c >> 8 & C01) | (((nz >> 8) | nz) & N80);
				if ( ((byte) nz) == 0 )
					t |= Z02;
				mem [sp = (sp - 1) | 0x100] = (byte) t;
				continue;
			}
			
			default:
			/*
				assert false;
			case 0x2E: // ROL a
			case 0x3E: // ROL a,X
			case 0x26: // ROL z
			case 0x36: // ROL z,X
			case 0x0E: // ASL a
			case 0x1E: // ASL a,X
			case 0x06: // ASL z
			case 0x16: // ASL z,X
			case 0xEE: // INC a
			case 0xFE: // INC a,X
			case 0xE6: // INC z
			case 0xF6: // INC z,X
			case 0xCE: // DEC a
			case 0xDE: // DEC a,X
			case 0xC6: // DEC z
			case 0xD6: // DEC z,X
			case 0x6E: // ROR a
			case 0x7E: // ROR a,X
			case 0x66: // ROR z
			case 0x76: // ROR z,X
			case 0x4E: // LSR a
			case 0x5E: // LSR a,X
			case 0x46: // LSR z
			case 0x56: // LSR z,X
			*/
				if ( addr <= 0x7FF )
				{
					mem [addr] = (byte) nz;
					continue;
				}
				cpuWrite( addr, nz );
				continue;
			}
		}
		
	stop:
		this.a  = a;
		this.x  = x;
		this.y  = y;
		this.s  = (sp - 1) & 0xFF;
		this.pc = pc;
		this.p  = p;
		this.c  = c;
		this.nz = nz;
		this.time = time;
	}
}

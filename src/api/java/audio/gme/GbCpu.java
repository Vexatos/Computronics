package audio.gme;

// Nintendo Game Boy GB-Z80 CPU emulator
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

public class GbCpu extends ClassicEmu
{
	public GbCpu() { rstBase = 0; }
	
	// Resets registers, uses supplied physical memory, and
	// maps all memory pages to unmapped
	public final void reset( byte [] mem, int unmapped )
	{
		this.mem = mem;
		a  = 0;
		bc = 0;
		de = 0;
		hl = 0;
		pc = 0;
		sp = 0xFFFF;
		ph = 0x100;
		cz = 1;
		
		time = 0;
		
		for ( int i = 0; i < pageCount + 1; i++ )
			mapPage( i, unmapped );
	}
	
	static final int pageShift = 13;
	static final int pageCount = 0x10000 >> pageShift;
	static final int pageSize = 1 << pageShift;
	
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
	public int a, bc, de, hl, sp, pc;
	
	// Base address for RST vectors (normally 0)
	public int rstBase;
	
	// Current time
	public int time;
	
	// Memory read and write handlers
	protected int  cpuRead ( int addr ) { return 0; }
	protected void cpuWrite( int addr, int data ) { }
	
	int pages [] = new int [pageCount + 1];
	int cz, ph;
	byte [] mem;
	
	final void mapPage( int page, int offset )
	{
		if ( debug ) assert 0 <= page && page < pageCount + 1;
		pages [page] = offset - page * pageSize;
	}
	
	// Runs until time >= 0
	public final void runCpu()
	{
		// locals are faster, and first three are more efficient to access
		final byte [] mem = this.mem;
		int pc = this.pc;
		int data = 0;
		
		int time = this.time;
		int a  = this.a;
		int bc = this.bc;
		int de = this.de;
		int hl = this.hl;
		int sp = this.sp;
		int cz = this.cz;
		int ph = this.ph;
		final int pages [] = this.pages;
		final int instrTimes [] = this.instrTimes;
		
	loop:
		while ( time < 0 )
		{
			if ( debug )
			{
				assert 0 <= a  && a  < 0x00100;
				assert 0 <= bc && bc < 0x10000;
				assert 0 <= de && de < 0x10000;
				assert 0 <= hl && hl < 0x10000;
				assert 0 <= pc && pc < 0x10000;
				assert 0 <= pc && pc < 0x10000;
			}
			
			int instr;
			int opcode;
			if ( (opcode = mem [instr = pages [pc >> pageShift] + pc] & 0xFF) == 0xCB )
			{
				// CB
				
				// Source
				this.time = (time += cbTimes [opcode = mem [instr + 1] & 0xFF]);
				pc += 2;
				int operand;
				switch ( (operand = opcode & 7) )
				{
				case 0: data = bc >>   8; break;
				case 1: data = bc & 0xFF; break;
				case 2: data = de >>   8; break;
				case 3: data = de & 0xFF; break;
				case 4: data = hl >>   8; break;
				case 5: data = hl & 0xFF; break;
				case 6: data = cpuRead( hl ); break;
				default: data = a; break;
				}
				
				// Operation
				int operation;
				switch ( (operation = opcode >> 3) )
				{
				case 0x08: // BIT  0,r
				case 0x09: // BIT  1,r
				case 0x0A: // BIT  2,r
				case 0x0B: // BIT  3,r
				case 0x0C: // BIT  4,r
				case 0x0D: // BIT  5,r
				case 0x0E: // BIT  6,r
				case 0x0F: // BIT  7,r
					cz = (cz & 0x100) | (data >> (operation - 0x08) & 1);
					ph = (cz | 0x100) ^ 0x10; // N=0 H=1
					continue;
				
				case 0x00: // RLC r
					cz = (data << 1 & 0x100) | data; // Z=* C=*
					ph = data | 0x100; // N=0 H=0
					data = (data << 1 & 0xFF) | (data >> 7);
					break;
				
				case 0x01: // RRC r
					cz = (data << 8) | data; // Z=* C=*
					ph = data | 0x100; // N=0 H=0
					data = ((data & 1) << 7) | (data >> 1);
					break;
				
				case 0x04: // SLA r
					cz = 0;
				case 0x02: // RL r
					cz = (data << 1) | (cz >> 8 & 1); // Z=* C=*
					data = cz & 0xFF;
					ph = cz | 0x100; // N=0 H=0
					break;
				
				case 0x05: // SRA r
					cz = data << 1;
				case 0x03: // RR r
					data |= cz & 0x100;
				case 0x07: // SRL r
					cz = data << 8; // Z=* C=*
					data >>= 1;
					cz |= data;
					ph = data | 0x100; // N=0 H=0
					break;
		
				case 0x06: // SWAP
					data = (data >> 4) | (data << 4 & 0xFF);
					cz = data;
					ph = cz | 0x100;
					break;
				
				case 0x10: // RES  0,r
				case 0x11: // RES  1,r
				case 0x12: // RES  2,r
				case 0x13: // RES  3,r
				case 0x14: // RES  4,r
				case 0x15: // RES  5,r
				case 0x16: // RES  6,r
				case 0x17: // RES  7,r
					data &= ~(1 << (operation - 0x10));
					break;
				
				default:
				/*
					assert false;
				case 0x18: // SET  0,r
				case 0x19: // SET  1,r
				case 0x1A: // SET  2,r
				case 0x1B: // SET  3,r
				case 0x1C: // SET  4,r
				case 0x1D: // SET  5,r
				case 0x1E: // SET  6,r
				case 0x1F: // SET  7,r
				*/
					data |= 1 << (operation - 0x18);
					break;
				}
				
				// Dest
				switch ( operand )
				{
				case 0: bc = data << 8 | (bc & 0xFF); continue;
				case 1: bc = (bc & 0xFF00) | data;    continue;
				case 2: de = data << 8 | (de & 0xFF); continue;
				case 3: de = (de & 0xFF00) | data;    continue;
				case 4: hl = data << 8 | (hl & 0xFF); continue;
				case 5: hl = (hl & 0xFF00) | data;    continue;
				case 6: cpuWrite( hl, data );         continue;
				default: a = data;                    continue;
				}
			}
			
			// Normal instruction
			pc++;
			this.time = (time += instrTimes [opcode]);
			
			// Source
			switch ( opcode )
			{
			case 0xF3: // DI
				// TODO: implement
				continue;
			
			case 0xFB: // EI
				// TODO: implement
				continue;
			
			case 0x76: // HALT
			case 0x10: // STOP
			case 0xD3:            case 0xDB:            case 0xDD: // Illegal
			case 0xE3: case 0xE4: case 0xEB: case 0xEC: case 0xED:
			           case 0xF4:            case 0xFC: case 0xFD:
				pc--;
				break loop;
			
			case 0xE9: // LD   PC,HL
				pc = hl;
				continue;
			
			case 0xF9: // LD   SP,HL
				sp = hl;
				continue;
	
			case 0x37: // SCF
				ph = cz | 0x100; // N=0 H=0
				cz |= 0x100; // C=1 Z=-
				continue;
	
			case 0x3F: // CCF
				ph = cz | 0x100; // N=0 H=0
				cz ^= 0x100; // C=* Z=-
				continue;
	
			case 0x2F: // CPL
				a ^= 0xFF;
				ph = ~cz & 0xFF; // N=1 H=1
				continue;
	
			case 0x27:{// DAA
				int h = ph ^ cz;
				if ( (ph & 0x100) != 0 )
				{
					if ( (h & 0x10) != 0 || (a & 0x0F) > 9 )
						a += 6;
					
					if ( (cz & 0x100) != 0 || a > 0x9F )
						a += 0x60;
				}
				else
				{
					if ( (h & 0x10) != 0 )
					{
						a -= 6;
						if ( (cz & 0x100) == 0 )
							a &= 0xFF;
					}
					
					if ( (cz & 0x100) != 0 )
						a -= 0x60;
				}
				
				cz = (cz & 0x100) | a;
				a &= 0xFF;
				ph = (ph & 0x100) | a;
				continue;
			}
			
			case 0xC0: // RET  NZ
				if ( ((byte) cz) != 0 )
					break;
				continue;
			
			case 0xC8: // RET  Z
				if ( ((byte) cz) == 0 )
					break;
				continue;
			
			case 0xD0: // RET  NC
				if ( (cz & 0x100) == 0 )
					break;
				continue;
			
			case 0xD8: // RET  C
				if ( (cz & 0x100) != 0 )
					break;
				continue;
			
			case 0xF5: // PUSH AF
				data = (cz >> 4 & 0x10) | (a << 8);
				data |= ~ph >> 2 & 0x40;
				data |= (ph ^ cz) << 1 & 0x20;
				if ( ((byte) cz) == 0 )
					data |= 0x80;
				break;
	
			case 0x22: // LD   (HL+),A
			case 0x2A: // LD   A,(HL+)
				data = hl;
				hl = (hl + 1) & 0xFFFF;
				break;
	
			case 0x32: // LD   (HL-),A
			case 0x3A: // LD   A,(HL-)
				data = hl;
				hl = (hl - 1) & 0xFFFF;
				break;
	
			case 0x7E: // LD   A,(HL)
			case 0x23: // INC  HL
			case 0x29: // ADD  HL,HL
			case 0x2B: // DEC  HL
			case 0xE5: // PUSH HL
				data = hl;
				break;
	
			case 0x02: // LD   (BC),A
			case 0x0A: // LD   A,(BC)
			case 0x03: // INC  BC
			case 0x09: // ADD  HL,BC
			case 0x0B: // DEC  BC
			case 0xC5: // PUSH BC
				data = bc;
				break;
	
			case 0x12: // LD   (DE),A
			case 0x1A: // LD   A,(DE)
			case 0x13: // INC  DE
			case 0x19: // ADD  HL,DE
			case 0x1B: // DEC  DE
			case 0xD5: // PUSH DE
				data = de;
				break;
	
			case 0x33: // INC  SP
			case 0x39: // ADD  HL,SP
			case 0x3B: // DEC  SP
				data = sp;
				break;
	
			case 0xF0: // LDH  A,(n)
			case 0xE0: // LDH  (n),A
			case 0x06: // LD   B,n
			case 0x0E: // LD   C,n
			case 0x16: // LD   D,n
			case 0x1E: // LD   E,n
			case 0x26: // LD   H,n
			case 0x2E: // LD   L,n
			case 0x36: // LD   (HL),n
			case 0x3E: // LD   A,n
			case 0xC6: // ADD  n
			case 0xCE: // ADC  n
			case 0xD6: // SUB  n
			case 0xDE: // SBC  n
			case 0xE6: // AND  n
			case 0xEE: // XOR  n
			case 0xF6: // OR   n
			case 0xFE: // CP   n
			case 0x18: // JR   r
			case 0x20: // JR   NZ,r
			case 0x28: // JR   Z,r
			case 0x30: // JR   NC,r
			case 0x38: // JR   C,r
			case 0xF8: // LD   HL,SPs
			case 0xE8: // ADD  SP,s
				data = mem [instr + 1] & 0xFF;
				pc++;
				break;
	
			case 0x01: // LD   BC,nn
			case 0x11: // LD   DE,nn
			case 0x21: // LD   HL,nn
			case 0x31: // LD   SP,nn
			case 0xC2: // JP   NZ,nn
			case 0xC3: // JP   nn
			case 0xC4: // CALL NZ,nn
			case 0xCA: // JP   Z,nn
			case 0xCC: // CALL Z,nn
			case 0xCD: // CALL nn
			case 0xD2: // JP   NC,nn
			case 0xD4: // CALL NC,nn
			case 0xDA: // JP   C,nn
			case 0xDC: // CALL C,nn
			case 0xEA: // LD   (nn),A
			case 0x08: // LD   (nn),SP
			case 0xFA: // LD   A,(nn)
				data = (mem [instr + 2] & 0xFF) << 8 | (mem [instr + 1] & 0xFF);
				pc += 2;
				break;
			
			case 0x34: // INC  (HL)
			case 0x35: // DEC  (HL)
			case 0x46: // LD   B,(HL)
			case 0x4E: // LD   C,(HL)
			case 0x56: // LD   D,(HL)
			case 0x5E: // LD   E,(HL)
			case 0x66: // LD   H,(HL)
			case 0x6E: // LD   L,(HL)
			case 0x86: // ADD  (HL)
			case 0x8E: // ADC  (HL)
			case 0x96: // SUB  (HL)
			case 0x9E: // SBC  (HL)
			case 0xA6: // AND  (HL)
			case 0xAE: // XOR  (HL)
			case 0xB6: // OR   (HL)
			case 0xBE: // CP   (HL)
				data = cpuRead( hl );
				break;
	
			case 0x3C: // INC  A
			case 0x3D: // DEC  A
			case 0x47: // LD   B,A
			case 0x4F: // LD   C,A
			case 0x57: // LD   D,A
			case 0x5F: // LD   E,A
			case 0x67: // LD   H,A
			case 0x6F: // LD   L,A
			case 0x77: // LD   (HL),A
			case 0x87: // ADD  A
			case 0x8F: // ADC  A
			case 0x97: // SUB  A
			case 0x9F: // SBC  A
			case 0xA7: // AND  A
			case 0xAF: // XOR  A
			case 0xB7: // OR   A
			case 0xBF: // CP   A
				data = a;
				break;
	
			case 0x04: // INC  B
			case 0x05: // DEC  B
			case 0x48: // LD   C,B
			case 0x50: // LD   D,B
			case 0x58: // LD   E,B
			case 0x60: // LD   H,B
			case 0x68: // LD   L,B
			case 0x70: // LD   (HL),B
			case 0x78: // LD   A,B
			case 0x80: // ADD  B
			case 0x88: // ADC  B
			case 0x90: // SUB  B
			case 0x98: // SBC  B
			case 0xA0: // AND  B
			case 0xA8: // XOR  B
			case 0xB0: // OR   B
			case 0xB8: // CP   B
				data = bc >> 8;
				break;
	
			case 0xF2: // LDH  A,(C)
			case 0xE2: // LDH  (C),A
			case 0x0C: // INC  C
			case 0x0D: // DEC  C
			case 0x41: // LD   B,C
			case 0x51: // LD   D,C
			case 0x59: // LD   E,C
			case 0x61: // LD   H,C
			case 0x69: // LD   L,C
			case 0x71: // LD   (HL),C
			case 0x79: // LD   A,C
			case 0x81: // ADD  C
			case 0x89: // ADC  C
			case 0x91: // SUB  C
			case 0x99: // SBC  C
			case 0xA1: // AND  C
			case 0xA9: // XOR  C
			case 0xB1: // OR   C
			case 0xB9: // CP   C
				data = bc & 0xFF;
				break;
	
			case 0x14: // INC  D
			case 0x15: // DEC  D
			case 0x42: // LD   B,D
			case 0x4A: // LD   C,D
			case 0x5A: // LD   E,D
			case 0x62: // LD   H,D
			case 0x6A: // LD   L,D
			case 0x72: // LD   (HL),D
			case 0x7A: // LD   A,D
			case 0x82: // ADD  D
			case 0x8A: // ADC  D
			case 0x92: // SUB  D
			case 0x9A: // SBC  D
			case 0xA2: // AND  D
			case 0xAA: // XOR  D
			case 0xB2: // OR   D
			case 0xBA: // CP   D
				data = de >> 8;
				break;
	
			case 0x1C: // INC  E
			case 0x1D: // DEC  E
			case 0x43: // LD   B,E
			case 0x4B: // LD   C,E
			case 0x53: // LD   D,E
			case 0x63: // LD   H,E
			case 0x6B: // LD   L,E
			case 0x73: // LD   (HL),E
			case 0x7B: // LD   A,E
			case 0x83: // ADD  E
			case 0x8B: // ADC  E
			case 0x93: // SUB  E
			case 0x9B: // SBC  E
			case 0xA3: // AND  E
			case 0xAB: // XOR  E
			case 0xB3: // OR   E
			case 0xBB: // CP   E
				data = de & 0xFF;
				break;
	
			case 0x24: // INC  H
			case 0x25: // DEC  H
			case 0x44: // LD   B,H
			case 0x4C: // LD   C,H
			case 0x54: // LD   D,H
			case 0x5C: // LD   E,H
			case 0x6C: // LD   L,H
			case 0x74: // LD   (HL),H
			case 0x7C: // LD   A,H
			case 0x84: // ADD  H
			case 0x8C: // ADC  H
			case 0x94: // SUB  H
			case 0x9C: // SBC  H
			case 0xA4: // AND  H
			case 0xAC: // XOR  H
			case 0xB4: // OR   H
			case 0xBC: // CP   H
				data = hl >> 8;
				break;
	
			case 0x2C: // INC  L
			case 0x2D: // DEC  L
			case 0x45: // LD   B,L
			case 0x4D: // LD   C,L
			case 0x55: // LD   D,L
			case 0x5D: // LD   E,L
			case 0x65: // LD   H,L
			case 0x75: // LD   (HL),L
			case 0x7D: // LD   A,L
			case 0x85: // ADD  L
			case 0x8D: // ADC  L
			case 0x95: // SUB  L
			case 0x9D: // SBC  L
			case 0xA5: // AND  L
			case 0xAD: // XOR  L
			case 0xB5: // OR   L
			case 0xBD: // CP   L
				data = hl & 0xFF;
				break;
			}
	
			// Operation
			switch ( opcode )
			{
			case 0x09: // ADD  HL,BC
			case 0x19: // ADD  HL,DE
			case 0x29: // ADD  HL,HL
			case 0x39: // ADD  HL,SP
				ph = hl ^ data;
				data += hl;
				hl = data & 0xFFFF;
				ph ^= data;
				cz = (cz & 0xFF) | (data >> 8 & 0x100); // C=* Z=-
				ph = ((ph >> 8) ^ cz) | 0x100; // N=0 H=*
				continue;
			
			case 0x88: // ADC  B
			case 0x89: // ADC  C
			case 0x8A: // ADC  D
			case 0x8B: // ADC  E
			case 0x8C: // ADC  H
			case 0x8D: // ADC  L
			case 0x8E: // ADC  (HL)
			case 0x8F: // ADC  A
			case 0xCE: // ADC  n
				ph = 0x100 | (a ^ data); // N=0 H=*
				cz = a + data + (cz >> 8 & 1); // C=* Z=*
				a  = cz & 0xFF;
				continue;
	
			case 0x80: // ADD  B
			case 0x81: // ADD  C
			case 0x82: // ADD  D
			case 0x83: // ADD  E
			case 0x84: // ADD  H
			case 0x85: // ADD  L
			case 0x86: // ADD  (HL)
			case 0x87: // ADD  A
			case 0xC6: // ADD  n
				ph = 0x100 | (a ^ data); // N=0 H=*
				cz = a + data; // C=* Z=*
				a  = cz & 0xFF;
				continue;
	
			case 0xB8: // CP   B
			case 0xB9: // CP   C
			case 0xBA: // CP   D
			case 0xBB: // CP   E
			case 0xBC: // CP   H
			case 0xBD: // CP   L
			case 0xBE: // CP   (HL)
			case 0xBF: // CP   A
			case 0xFE: // CP   n
				ph = a ^ data; // N=1 H=*
				cz = a - data; // C=* Z=*
				continue;
	
			case 0x90: // SUB  B
			case 0x91: // SUB  C
			case 0x92: // SUB  D
			case 0x93: // SUB  E
			case 0x94: // SUB  H
			case 0x95: // SUB  L
			case 0x96: // SUB  (HL)
			case 0x97: // SUB  A
			case 0xD6: // SUB  n
				ph = a ^ data; // N=1 H=*
				cz = a - data; // C=* Z=*
				a  = cz & 0xFF;
				continue;
	
			case 0x98: // SBC  B
			case 0x99: // SBC  C
			case 0x9A: // SBC  D
			case 0x9B: // SBC  E
			case 0x9C: // SBC  H
			case 0x9D: // SBC  L
			case 0x9E: // SBC  (HL)
			case 0x9F: // SBC  A
			case 0xDE: // SBC  n
				ph = a ^ data; // N=1 H=*
				cz = a - data - (cz >> 8 & 1); // C=* Z=*
				a  = cz & 0xFF;
				continue;
	
			case 0xA0: // AND  B
			case 0xA1: // AND  C
			case 0xA2: // AND  D
			case 0xA3: // AND  E
			case 0xA4: // AND  H
			case 0xA5: // AND  L
			case 0xA6: // AND  (HL)
			case 0xA7: // AND  A
			case 0xE6: // AND  n
				a &= data;
				cz = a; // C=0 Z=*
				ph = ~a; // N=0 H=1
				continue;
	
			case 0xB0: // OR   B
			case 0xB1: // OR   C
			case 0xB2: // OR   D
			case 0xB3: // OR   E
			case 0xB4: // OR   H
			case 0xB5: // OR   L
			case 0xB6: // OR   (HL)
			case 0xB7: // OR   A
			case 0xF6: // OR   n
				a |= data;
				cz = a; // C=0 Z=*
				ph = a | 0x100; // N=0 H=0
				continue;
	
			case 0xA8: // XOR  B
			case 0xA9: // XOR  C
			case 0xAA: // XOR  D
			case 0xAB: // XOR  E
			case 0xAC: // XOR  H
			case 0xAD: // XOR  L
			case 0xAE: // XOR  (HL)
			case 0xAF: // XOR  A
			case 0xEE: // XOR  n
				a ^= data;
				cz = a; // C=0 Z=*
				ph = a | 0x100; // N=0 H=0
				continue;
			
			case 0x17: // RLA
				cz = (a << 1) | (cz >> 8 & 1);
				ph = cz | 0x100;
				a  = cz & 0xFF;
				cz |= 1;
				continue;
			
			case 0x07: // RLCA
				cz = a << 1;
				a  = (cz & 0xFF) | (a >> 7);
				ph = a | 0x100;
				cz |= 1;
				continue;
	
			case 0x1F: // RRA
				a |= cz & 0x100;
				cz = a << 8 | 1; // Z=0 C=*
				a >>= 1;
				ph = 0x100; // N=0 H=0
				continue;
	
			case 0x0F: // RRCA
				cz = a << 8 | 1; // Z=0 C=*
				a = ((a & 1) << 7) | (a >> 1);
				ph = 0x100; // N=0 H=0
				continue;
	
			case 0xE8: // ADD  SP,s
			case 0xF8:{// LD   HL,SPs
				int t = (sp + (byte) data) & 0xFFFF;
				cz = (((sp & 0xFF) + data) & 0x100) | 1; // Z=0 C=*
				ph = (sp ^ data ^ t) | 0x100; // N=0 H=*
				data = t;
				break;
			}
			
			case 0x0B: // DEC  BC
			case 0x1B: // DEC  DE
			case 0x2B: // DEC  HL
			case 0x3B: // DEC  SP
				data = (data - 1) & 0xFFFF;
				break;
			
			case 0x05: // DEC  B
			case 0x0D: // DEC  C
			case 0x15: // DEC  D
			case 0x1D: // DEC  E
			case 0x25: // DEC  H
			case 0x2D: // DEC  L
			case 0x35: // DEC  (HL)
			case 0x3D: // DEC  A
				ph   = data; // N=1 H=*
				data = (data - 1) & 0xFF;
				cz   = (cz & 0x100) | data; // C=- Z=*
				break;
	
			case 0x03: // INC  BC
			case 0x13: // INC  DE
			case 0x23: // INC  HL
			case 0x33: // INC  SP
				data = (data + 1) & 0xFFFF;
				break;
			
			case 0x04: // INC  B
			case 0x0C: // INC  C
			case 0x14: // INC  D
			case 0x1C: // INC  E
			case 0x24: // INC  H
			case 0x2C: // INC  L
			case 0x34: // INC  (HL)
			case 0x3C: // INC  A
				ph   = data | 0x100; // N=0 H=*
				data = (data + 1) & 0xFF;
				cz   = (cz & 0x100) | data; // C=- Z=*
				break;
	
			case 0xD9: // RETI
				// TODO: EI
			case 0xC0: // RET  NZ
			case 0xC8: // RET  Z
			case 0xD0: // RET  NC
			case 0xD8: // RET  C
				time += 12;
			case 0xC9:{// RET
				data = pages [sp >> pageShift] + sp;
				pc = (mem [data + 1] & 0xFF) << 8 | (mem [data] & 0xFF);
				sp = (sp + 2) & 0xFFFF;
				continue;
			}
			
			case 0xC1: // POP  BC
			case 0xD1: // POP  DE
			case 0xE1: // POP  HL
			case 0xF1:{// POP  AF
				data = pages [sp >> pageShift] + sp;
				data = (mem [data + 1] & 0xFF) << 8 | (mem [data] & 0xFF);
				sp = (sp + 2) & 0xFFFF;
				break;
			}
	
			case 0xC4: // CALL NZ,nn
			case 0x20: // JR   NZ,r
			case 0xC2: // JP   NZ,nn
				if ( ((byte) cz) != 0 )
					break;
				continue;
			
			case 0xCC: // CALL Z,nn
			case 0x28: // JR   Z,r
			case 0xCA: // JP   Z,nn
				if ( ((byte) cz) == 0 )
					break;
				continue;
			
			case 0xD4: // CALL NC,nn
			case 0x30: // JR   NC,r
			case 0xD2: // JP   NC,nn
				if ( (cz & 0x100) == 0 )
					break;
				continue;
			
			case 0xDC: // CALL C,nn
			case 0x38: // JR   C,r
			case 0xDA: // JP   C,nn
				if ( (cz & 0x100) != 0 )
					break;
				continue;
				
			case 0xFF: // RST  $38
			case 0xC7: // RST  $00
			case 0xCF: // RST  $08
			case 0xD7: // RST  $10
			case 0xDF: // RST  $18
			case 0xE7: // RST  $20
			case 0xEF: // RST  $28
			case 0xF7: // RST  $30
				data = (opcode & 0x38) + rstBase;
				break;
			
			}
	
			// Destination
			switch ( opcode )
			{
			case 0xC2: // JP   NZ,nn
			case 0xCA: // JP   Z,nn
			case 0xD2: // JP   NC,nn
			case 0xDA: // JP   C,nn
				time += 4;
			case 0xC3: // JP   nn
				pc = data;
				continue;
	
			case 0x20: // JR   NZ,r
			case 0x28: // JR   Z,r
			case 0x30: // JR   NC,r
			case 0x38: // JR   C,r
				time += 4;
			case 0x18: // JR   r
				pc = (pc + (byte) data) & 0xFFFF;
				continue;
	
			case 0xC4: // CALL NZ,nn
			case 0xCC: // CALL Z,nn
			case 0xD4: // CALL NC,nn
			case 0xDC: // CALL C,nn
				time += 12;
			case 0xC7: // RST  $00
			case 0xCF: // RST  $08
			case 0xD7: // RST  $10
			case 0xDF: // RST  $18
			case 0xE7: // RST  $20
			case 0xEF: // RST  $28
			case 0xF7: // RST  $30
			case 0xFF: // RST  $38
			case 0xCD:{// CALL nn
				int t = pc;
				pc = data;
				data = t;
			}
			case 0xC5: // PUSH BC
			case 0xD5: // PUSH DE
			case 0xE5: // PUSH HL
			case 0xF5:{// PUSH AF
				sp = (sp - 2) & 0xFFFF;
				int offset = pages [sp >> pageShift] + sp;
				mem [offset + 1] = (byte) (data >> 8);
				mem [offset    ] = (byte) data;
				continue;
			}
	
			case 0xF1:{// POP  AF
				cz = (data << 4 & 0x100) | ((data >> 7 & 1) ^ 1);
				ph = (~data << 2 & 0x100) | (data >> 1 & 0x10);
				a = data >> 8;
				continue;
			}
			
			case 0xF0: // LDH  A,(n)
			case 0xF2: // LDH  A,(C)
				data += 0xFF00;
			case 0xFA: // LD   A,(nn)
			case 0x0A: // LD   A,(BC)
			case 0x1A: // LD   A,(DE)
			case 0x7E: // LD   A,(HL)
			case 0x2A: // LD   A,(HL+)
			case 0x3A: // LD   A,(HL-)
				a = cpuRead( data );
				continue;
			
			case 0xE0: // LDH  (n),A
			case 0xE2: // LDH  (C),A
				data += 0xFF00;
			case 0xEA: // LD   (nn),A
			case 0x02: // LD   (BC),A
			case 0x12: // LD   (DE),A
			case 0x22: // LD   (HL+),A
			case 0x32: // LD   (HL-),A
				cpuWrite( data, a );
				continue;
	
			case 0x08: // LD   (nn),SP
				cpuWrite( data, sp & 0xFF );
				cpuWrite( (data + 1) & 0xFFFF, sp >> 8 );
				continue;
			
			case 0x34: // INC  (HL)
			case 0x35: // DEC  (HL)
			case 0x36: // LD   (HL),n
			case 0x70: // LD   (HL),B
			case 0x71: // LD   (HL),C
			case 0x72: // LD   (HL),D
			case 0x73: // LD   (HL),E
			case 0x74: // LD   (HL),H
			case 0x75: // LD   (HL),L
			case 0x77: // LD   (HL),A
				cpuWrite( hl, data );
				continue;
	
			case 0x01: // LD   BC,nn
			case 0x03: // INC  BC
			case 0x0B: // DEC  BC
			case 0xC1: // POP  BC
				bc = data;
				continue;
	
			case 0x11: // LD   DE,nn
			case 0x13: // INC  DE
			case 0x1B: // DEC  DE
			case 0xD1: // POP  DE
				de = data;
				continue;
	
			case 0xF8: // LD   HL,SPs
			case 0x21: // LD   HL,nn
			case 0x23: // INC  HL
			case 0x2B: // DEC  HL
			case 0xE1: // POP  HL
				hl = data;
				continue;
	
			case 0xE8: // ADD  SP,s
			case 0x31: // LD   SP,nn
			case 0x33: // INC  SP
			case 0x3B: // DEC  SP
				sp = data;
				continue;
			
			case 0x3C: // INC  A
			case 0x3D: // DEC  A
			case 0x3E: // LD   A,n
			case 0x78: // LD   A,B
			case 0x79: // LD   A,C
			case 0x7A: // LD   A,D
			case 0x7B: // LD   A,E
			case 0x7C: // LD   A,H
			case 0x7D: // LD   A,L
				a = data;
				continue;
	
			case 0x04: // INC  B
			case 0x05: // DEC  B
			case 0x06: // LD   B,n
			case 0x41: // LD   B,C
			case 0x42: // LD   B,D
			case 0x43: // LD   B,E
			case 0x44: // LD   B,H
			case 0x45: // LD   B,L
			case 0x46: // LD   B,(HL)
			case 0x47: // LD   B,A
				bc = (data << 8) | (bc & 0xFF);
				continue;
	
			case 0x14: // INC  D
			case 0x15: // DEC  D
			case 0x16: // LD   D,n
			case 0x50: // LD   D,B
			case 0x51: // LD   D,C
			case 0x53: // LD   D,E
			case 0x54: // LD   D,H
			case 0x55: // LD   D,L
			case 0x56: // LD   D,(HL)
			case 0x57: // LD   D,A
				de = (data << 8) | (de & 0xFF);
				continue;
	
			case 0x24: // INC  H
			case 0x25: // DEC  H
			case 0x26: // LD   H,n
			case 0x60: // LD   H,B
			case 0x61: // LD   H,C
			case 0x62: // LD   H,D
			case 0x63: // LD   H,E
			case 0x65: // LD   H,L
			case 0x66: // LD   H,(HL)
			case 0x67: // LD   H,A
				hl = (data << 8) | (hl & 0xFF);
				continue;
	
			case 0x0C: // INC  C
			case 0x0D: // DEC  C
			case 0x0E: // LD   C,n
			case 0x48: // LD   C,B
			case 0x4A: // LD   C,D
			case 0x4B: // LD   C,E
			case 0x4C: // LD   C,H
			case 0x4D: // LD   C,L
			case 0x4E: // LD   C,(HL)
			case 0x4F: // LD   C,A
				bc = (bc & 0xFF00) | data;
				continue;
	
			case 0x1C: // INC  E
			case 0x1D: // DEC  E
			case 0x1E: // LD   E,n
			case 0x58: // LD   E,B
			case 0x59: // LD   E,C
			case 0x5A: // LD   E,D
			case 0x5C: // LD   E,H
			case 0x5D: // LD   E,L
			case 0x5E: // LD   E,(HL)
			case 0x5F: // LD   E,A
				de = (de & 0xFF00) | data;
				continue;
	
			case 0x2C: // INC  L
			case 0x2D: // DEC  L
			case 0x2E: // LD   L,n
			case 0x68: // LD   L,B
			case 0x69: // LD   L,C
			case 0x6A: // LD   L,D
			case 0x6B: // LD   L,E
			case 0x6C: // LD   L,H
			case 0x6E: // LD   L,(HL)
			case 0x6F: // LD   L,A
				hl = (hl & 0xFF00) | data;
				continue;
			}
		}
		
		this.a  = a;
		this.bc = bc;
		this.de = de;
		this.hl = hl;
		this.sp = sp;
		this.pc = pc;
		this.ph = ph;
		this.cz = cz;
		this.time = time;
	}
	
	static final int [] instrTimes = {
	//	 0  1  2  3  4  5  6  7  8  9  A  B  C  D  E  F
		 4,12, 8, 8, 4, 4, 8, 4,20, 8, 8, 8, 4, 4, 8, 4,// 0
		 4,12, 8, 8, 4, 4, 8, 4,12, 8, 8, 8, 4, 4, 8, 4,// 1
		 8,12, 8, 8, 4, 4, 8, 4, 8, 8, 8, 8, 4, 4, 8, 4,// 2
		 8,12, 8, 8,12,12,12, 4, 8, 8, 8, 8, 4, 4, 8, 4,// 3
		 4, 4, 4, 4, 4, 4, 8, 4, 4, 4, 4, 4, 4, 4, 8, 4,// 4
		 4, 4, 4, 4, 4, 4, 8, 4, 4, 4, 4, 4, 4, 4, 8, 4,// 5
		 4, 4, 4, 4, 4, 4, 8, 4, 4, 4, 4, 4, 4, 4, 8, 4,// 6
		 8, 8, 8, 8, 8, 8, 0, 8, 4, 4, 4, 4, 4, 4, 8, 4,// 7
		 4, 4, 4, 4, 4, 4, 8, 4, 4, 4, 4, 4, 4, 4, 8, 4,// 8
		 4, 4, 4, 4, 4, 4, 8, 4, 4, 4, 4, 4, 4, 4, 8, 4,// 9
		 4, 4, 4, 4, 4, 4, 8, 4, 4, 4, 4, 4, 4, 4, 8, 4,// A
		 4, 4, 4, 4, 4, 4, 8, 4, 4, 4, 4, 4, 4, 4, 8, 4,// B
		 8,12,12,16,12,16, 8,16, 8,16,12, 0,12,24, 8,16,// C
		 8,12,12, 0,12,16, 8,16, 8, 4,12, 0,12, 0, 8,16,// D
		12,12, 8, 0, 0,16, 8,16,16, 4,16, 0, 0, 0, 8,16,// E
		12,12, 8, 4, 0,16, 8,16,12, 8,16, 4, 0, 0, 8,16,// F
	};
	
	static final int [] cbTimes = {
	//	 0  1  2  3  4  5  6  7  8  9  A  B  C  D  E  F
		 8, 8, 8, 8, 8, 8,16, 8, 8, 8, 8, 8, 8, 8,16, 8,// 0
		 8, 8, 8, 8, 8, 8,16, 8, 8, 8, 8, 8, 8, 8,16, 8,// 1
		 8, 8, 8, 8, 8, 8,16, 8, 8, 8, 8, 8, 8, 8,16, 8,// 2
		 8, 8, 8, 8, 8, 8,16, 8, 8, 8, 8, 8, 8, 8,16, 8,// 3
		 8, 8, 8, 8, 8, 8,12, 8, 8, 8, 8, 8, 8, 8,12, 8,// 4
		 8, 8, 8, 8, 8, 8,12, 8, 8, 8, 8, 8, 8, 8,12, 8,// 5
		 8, 8, 8, 8, 8, 8,12, 8, 8, 8, 8, 8, 8, 8,12, 8,// 6
		 8, 8, 8, 8, 8, 8,12, 8, 8, 8, 8, 8, 8, 8,12, 8,// 7
		 8, 8, 8, 8, 8, 8,16, 8, 8, 8, 8, 8, 8, 8,16, 8,// 8
		 8, 8, 8, 8, 8, 8,16, 8, 8, 8, 8, 8, 8, 8,16, 8,// 9
		 8, 8, 8, 8, 8, 8,16, 8, 8, 8, 8, 8, 8, 8,16, 8,// A
		 8, 8, 8, 8, 8, 8,16, 8, 8, 8, 8, 8, 8, 8,16, 8,// B
		 8, 8, 8, 8, 8, 8,16, 8, 8, 8, 8, 8, 8, 8,16, 8,// C
		 8, 8, 8, 8, 8, 8,16, 8, 8, 8, 8, 8, 8, 8,16, 8,// D
		 8, 8, 8, 8, 8, 8,16, 8, 8, 8, 8, 8, 8, 8,16, 8,// E
		 8, 8, 8, 8, 8, 8,16, 8, 8, 8, 8, 8, 8, 8,16, 8,// F
	};
}

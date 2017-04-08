package pl.asie.computronics.util;

import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.NoteBlockEvent;

public class NoteUtils {

	private static final String[] instruments = new String[] { "harp", "bd", "snare", "hat", "bassattack", "pling", "bass" };

	public static void playNoteRaw(World world, int x, int y, int z, String instrument, int note, float volume) {
		float f = (float) Math.pow(2.0D, (double) (note - 12) / 12.0D);

		world.playSoundEffect((double) x + 0.5D, (double) y + 0.5D, (double) z + 0.5D, instrument, volume, f);
		ParticleUtils.sendParticlePacket("note", world, (double) x + 0.5D, (double) y + 1.2D, (double) z + 0.5D, (double) note / 24.0D, 1.0D, 0.0D);
	}

	public static NoteTask playNote(World world, int x, int y, int z, String instrument, int note, float volume) {
		return new NoteTask(checkInstrument(instrument), checkNote(note), volume);
	}

	public static NoteTask playNote(World world, int x, int y, int z, String instrument, int note) {
		return new NoteTask(checkInstrument(instrument), checkNote(note), 3.0F);
	}

	public static NoteTask playNote(World world, int x, int y, int z, int instrument, int note) {
		return playNote(world, x, y, z, instrument, note, -1F);
	}

	public static NoteTask playNote(World world, int x, int y, int z, int instrument, int note, final float volume) {
		if(instrument < 0) {
			// Get default instrument
			byte b0 = 0;
			if(y > 0) {
				Material m = world.getBlock(x, y - 1, z).getMaterial();
				if(m == Material.rock) {
					b0 = 1;
				}
				if(m == Material.sand) {
					b0 = 2;
				}
				if(m == Material.glass) {
					b0 = 3;
				}
				if(m == Material.wood) {
					b0 = 4;
				}
			}
			instrument = b0;
		}
		instrument %= 7;

		return new NoteTask(instrument, checkNote(note), volume < 0 ? 3.0F : volume);
	}

	public static float toVolume(int index, double value) {
		if(value < 0.0D || value > 1.0D) {
			throw new IllegalArgumentException("bad argument #" + index + " (number between 0 and 1 expected, got " + value + ")");
		}
		return Math.min(Math.max((float) value * 3.0F, 0F), 3.0F);
	}

	//For ComputerCraft optional values
	public static float toVolume(Object[] arguments, int index) {
		double value = 1.0D;
		if(arguments.length > index) {
			if(arguments[index] instanceof Double) {
				value = ((Double) arguments[index]);
			}
		}
		return toVolume(index + 1, value);
	}

	public static String checkInstrument(String instrument) {
		for(String s : instruments) {
			if(s.equals(instrument)) {
				return "note." + instrument;
			}
		}
		throw new IllegalArgumentException("invalid instrument: " + instrument);
	}

	public static int checkNote(int note) {
		if(note >= 0) {
			return note;
		}
		throw new IllegalArgumentException("invalid note: " + note);
	}

	public static class NoteTask {

		private String instrument;
		private int instrumentID = -1;
		private int note;
		private final float volume;

		public NoteTask(String instrument, int note, float volume) {
			this.instrument = instrument;
			this.note = note;
			this.volume = volume;
		}

		public NoteTask(int instrumentID, int note, float volume) {
			this.instrumentID = instrumentID;
			this.note = note;
			this.volume = volume;
		}

		public void play(World world, int x, int y, int z) {
			if(instrument == null && instrumentID >= 0) {
				if(instrumentID <= 4) {
					NoteBlockEvent.Play e = new NoteBlockEvent.Play(world, x, y, z, 32767, note, instrumentID);
					if(MinecraftForge.EVENT_BUS.post(e)) {
						return;
					}
					instrumentID = e.instrument.ordinal();
					note = e.getVanillaNoteId();
				}

				instrument = instruments[0];
				if(instrumentID > 0 && instrumentID < instruments.length) {
					instrument = instruments[instrumentID];
				}
				instrument = "note." + instrument;
			}
			if(instrument != null) {
				playNoteRaw(world, x, y, z, instrument, note, volume);
			}
		}
	}
}

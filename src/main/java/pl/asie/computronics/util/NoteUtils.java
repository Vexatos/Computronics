package pl.asie.computronics.util;

import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.NoteBlockEvent;

public class NoteUtils {

	public static void playNote(World worldObj, int xCoord, int yCoord, int zCoord, String instrument, int note, float volume) {
		float f = (float) Math.pow(2.0D, (double) (note - 12) / 12.0D);

		worldObj.playSoundEffect((double) xCoord + 0.5D, (double) yCoord + 0.5D, (double) zCoord + 0.5D, "note." + instrument, volume, f);
	}

	public static void playNote(World worldObj, int xCoord, int yCoord, int zCoord, String instrument, int note) {
		playNote(worldObj, xCoord, yCoord, zCoord, instrument, note, 3.0F);
	}

	public static void playNote(World worldObj, int xCoord, int yCoord, int zCoord, int instrument, int note) {
		playNote(worldObj, xCoord, yCoord, zCoord, instrument, note, 3.0F);
	}

	public static void playNote(World worldObj, int xCoord, int yCoord, int zCoord, int instrument, int note, float volume) {
		if(instrument < 0) {
			// Get default instrument
			byte b0 = 0;
			if(yCoord > 0) {
				Material m = worldObj.getBlock(xCoord, yCoord - 1, zCoord).getMaterial();
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

		if(instrument <= 4) {
			NoteBlockEvent.Play e = new NoteBlockEvent.Play(worldObj, xCoord, yCoord, zCoord, 32767, note, instrument);
			if(MinecraftForge.EVENT_BUS.post(e)) {
				return;
			}
			instrument = e.instrument.ordinal();
			note = e.getVanillaNoteId();
		}

		String s = "harp";
		if(instrument == 1) {
			s = "bd";
		} else if(instrument == 2) {
			s = "snare";
		} else if(instrument == 3) {
			s = "hat";
		} else if(instrument == 4) {
			s = "bassattack";
		} else if(instrument == 5) {
			s = "pling";
		} else if(instrument == 6) {
			s = "bass";
		}

		playNote(worldObj, xCoord, yCoord, zCoord, s, note, volume);
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
}

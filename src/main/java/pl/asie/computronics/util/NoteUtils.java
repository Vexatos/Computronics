package pl.asie.computronics.util;

import com.github.soniex2.notebetter.api.NoteBetterAPI;
import com.github.soniex2.notebetter.api.NoteBetterInstrument;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.reference.Mods;

public class NoteUtils {

	private static final String[] instruments = new String[] { "harp", "bd", "snare", "hat", "bassattack", "pling", "bass" };

	public static void playNote(World worldObj, BlockPos pos, String instrument, int note, float volume) {
		float f = (float) Math.pow(2.0D, (double) (note - 12) / 12.0D);

		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();

		worldObj.playSoundEffect((double) xCoord + 0.5D, (double) yCoord + 0.5D, (double) zCoord + 0.5D, instrument, volume, f);
		ParticleUtils.sendParticlePacket(EnumParticleTypes.NOTE, worldObj, (double) xCoord + 0.5D, (double) yCoord + 1.2D, (double) zCoord + 0.5D, (double) note / 24.0D, 1.0D, 0.0D);
	}

	public static void playNote(World worldObj, BlockPos pos, String instrument, int note) {
		playNote(worldObj, pos, "note." + instrument, note, 3.0F);
	}

	public static void playNote(World worldObj, BlockPos pos, int instrument, int note) {
		playNote(worldObj, pos, instrument, note, -1F);
	}

	public static void playNote(World worldObj, BlockPos pos, int instrument, int note, final float volume) {
		if(instrument < 0) {
			// Get default instrument
			byte b0 = 0;
			if(pos.getY() > 0) {
				if(Mods.API.hasAPI(Mods.API.NoteBetter)) {
					if(playNoteNoteBetter(worldObj, pos, note, volume)) {
						return;
					}
				}
				Material m = worldObj.getBlockState(pos.down()).getBlock().getMaterial();
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
			NoteBlockEvent.Play e = new NoteBlockEvent.Play(worldObj, pos, worldObj.getBlockState(pos), note, instrument);
			if(MinecraftForge.EVENT_BUS.post(e)) {
				return;
			}
			instrument = e.instrument.ordinal();
			note = e.getVanillaNoteId();
		}

		String s = instruments[0];
		if(instrument > 0 && instrument < instruments.length) {
			s = instruments[instrument];
		}

		playNote(worldObj, pos, "note." + s, note, volume < 0 ? 3.0F : volume);
	}

	@Optional.Method(modid = Mods.API.NoteBetter)
	private static boolean playNoteNoteBetter(World world, BlockPos pos, int note, float volume) {
		NoteBetterInstrument instr = NoteBetterAPI.getInstrument(world, pos.down());
		if(instr != null) {
			ResourceLocation soundEvent = instr.getSoundEvent();
			if(soundEvent != null) {
				// TODO Note Block Event
				playNote(world, pos, soundEvent.toString(), note, volume < 0 ? instr.getVolume() : volume);
			}
			// soundEvent == null means play no sound, so we are returning true, i.e. cancelling here.
			return true;
		}
		return false;
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

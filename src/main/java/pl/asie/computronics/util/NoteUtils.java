package pl.asie.computronics.util;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.NoteBlockEvent;

public class NoteUtils {

	private static final String[] instruments = new String[] { "harp", "bd", "snare", "hat", "bassattack", "pling", "bass" };

	public static void playNoteRaw(World worldObj, BlockPos pos, String instrument, int note, float volume) {
		float f = (float) Math.pow(2.0D, (double) (note - 12) / 12.0D);

		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();

		worldObj.playSound(null, (double) xCoord + 0.5D, (double) yCoord + 0.5D, (double) zCoord + 0.5D, SoundEvent.REGISTRY.getObject(new ResourceLocation(instrument)), SoundCategory.BLOCKS, volume, f);
		ParticleUtils.sendParticlePacket(EnumParticleTypes.NOTE, worldObj, (double) xCoord + 0.5D, (double) yCoord + 1.2D, (double) zCoord + 0.5D, (double) note / 24.0D, 1.0D, 0.0D);
	}

	public static NoteTask playNote(World worldObj, BlockPos pos, String instrument, int note, float volume) {
		return new NoteTask(checkInstrument(instrument), note, volume);
	}

	public static NoteTask playNote(World worldObj, BlockPos pos, String instrument, int note) {
		return new NoteTask(checkInstrument(instrument), note, 3.0F);
	}

	public static NoteTask playNote(World worldObj, BlockPos pos, int instrument, int note) {
		return playNote(worldObj, pos, instrument, note, -1F);
	}

	public static NoteTask playNote(World worldObj, BlockPos pos, int instrument, int note, final float volume) {
		if(instrument < 0) {
			// Get default instrument
			byte b0 = 0;
			if(pos.getY() > 0) {
				/*if(Mods.API.hasAPI(Mods.API.NoteBetter)) { TODO NoteBetter
					NoteTask task = playNoteNoteBetter(worldObj, pos, note, volume);
					if(task != null) {
						return task;
					}
				}*/
				final IBlockState state = worldObj.getBlockState(pos.down());
				Material m = state.getMaterial();
				if(m == Material.ROCK) {
					b0 = 1;
				}
				if(m == Material.SAND) {
					b0 = 2;
				}
				if(m == Material.GLASS) {
					b0 = 3;
				}
				if(m == Material.WOOD) {
					b0 = 4;
				}
			}
			instrument = b0;
		}
		instrument %= 7;

		return new NoteTask(instrument, note, volume < 0 ? 3.0F : volume);
	}

	/*@Optional.Method(modid = Mods.API.NoteBetter)
	private static NoteTask playNoteNoteBetter(World world, BlockPos pos, int note, float volume) { TODO NoteBetter
		NoteBetterInstrument instr = NoteBetterAPI.getInstrument(world, pos.down());
		if(instr != null) {
			ResourceLocation soundEvent = instr.getSoundEvent();
			if(soundEvent != null) {
				// TODO Note Block Event
				return new NoteTask(soundEvent.toString(), note, volume < 0 ? instr.getVolume() : volume);
			}
			// soundEvent == null means play no sound, so we are returning true, i.e. cancelling here.
			return new NoteTask(null, note, volume < 0 ? instr.getVolume() : volume);
		}
		return null;
	}*/

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
				return "block.note." + instrument;
			}
		}
		/*if(Config.NOTEBETTER_ANY_INSTRUMENT && Mods.API.hasAPI(Mods.API.NoteBetter)) { TODO NoteBetter
			if(NoteBetterAPI.isNoteBetterInstrument(instrument)) {
				return instrument;
			}
		}*/
		throw new IllegalArgumentException("invalid instrument: " + instrument);
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

		public void play(World worldObj, BlockPos pos) {
			if(instrument == null && instrumentID >= 0) {
				if(instrumentID <= 4) {
					NoteBlockEvent.Play e = new NoteBlockEvent.Play(worldObj, pos, worldObj.getBlockState(pos), note, instrumentID);
					if(MinecraftForge.EVENT_BUS.post(e)) {
						return;
					}
					instrumentID = e.getInstrument().ordinal();
					note = e.getVanillaNoteId();
				}

				instrument = instruments[0];
				if(instrumentID > 0 && instrumentID < instruments.length) {
					instrument = instruments[instrumentID];
				}
				instrument = "note." + instrument;
			}
			if(instrument != null) {
				playNoteRaw(worldObj, pos, instrument, note, volume);
			}
		}
	}
}

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

import javax.annotation.Nullable;

public class NoteUtils {

	// Make sure to check BlockNote.INSTRUMENTS during updates
	private static final String[] instruments = new String[] { "harp", "basedrum", "snare", "hat", "bass", "flute", "bell", "guitar", "chime", "xylophone", "pling" };

	public static void playNoteRaw(World world, BlockPos pos, String instrument, int note, float volume) {
		float f = (float) Math.pow(2.0D, (double) (note - 12) / 12.0D);

		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();

		SoundEvent ev = SoundEvent.REGISTRY.getObject(new ResourceLocation(instrument));
		if(ev != null) {
			world.playSound(null, (double) xCoord + 0.5D, (double) yCoord + 0.5D, (double) zCoord + 0.5D, ev, SoundCategory.RECORDS, volume, f);
		}
		ParticleUtils.sendParticlePacket(EnumParticleTypes.NOTE, world, (double) xCoord + 0.5D, (double) yCoord + 1.2D, (double) zCoord + 0.5D, (double) note / 24.0D, 1.0D, 0.0D);
	}

	public static NoteTask playNote(World world, BlockPos pos, String instrument, int note, float volume) {
		return new NoteTask(checkInstrument(instrument), checkNote(note), volume);
	}

	public static NoteTask playNote(World world, BlockPos pos, String instrument, int note) {
		return new NoteTask(checkInstrument(instrument), checkNote(note), 3.0F);
	}

	public static NoteTask playNote(World world, BlockPos pos, int instrument, int note) {
		return playNote(world, pos, instrument, note, -1F);
	}

	public static NoteTask playNote(World world, BlockPos pos, int instrument, int note, final float volume) {
		if(instrument < 0) {
			// Get default instrument
			byte b0 = 0;
			if(pos.getY() > 0) {
				/*if(Mods.API.hasAPI(Mods.API.NoteBetter)) {
					NoteTask task = playNoteNoteBetter(world, pos, note, volume);
					if(task != null) {
						return task;
					}
				}*/
				final IBlockState state = world.getBlockState(pos.down());
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

		return new NoteTask(instrument, checkNote(note), volume < 0 ? 3.0F : volume);
	}

	/*@Nullable
	@Optional.Method(modid = Mods.API.NoteBetter)
	private static NoteTask playNoteNoteBetter(World world, BlockPos pos, int note, float volume) {
		NoteBetterInstrument instr = NoteBetterAPI.getInstrument(world, pos.down());
		if(instr != null) {
			ISoundEvent soundEvent = instr.soundEvent();
			if(soundEvent != null) {
				return new BetterNoteTask(soundEvent.toString(), note, volume)
					.setInstrument(instr);
			}
			// soundEvent == null means play no sound, so we are returning true, i.e. cancelling here.
			return new NoteTask(null, note, volume < 0 ? instr.volume() : volume);
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
		/*if(Config.NOTEBETTER_ANY_INSTRUMENT && Mods.API.hasAPI(Mods.API.NoteBetter)) {
			if(NoteBetterAPI.isNoteBetterInstrument(instrument)) {
				return instrument;
			}
		}*/
		throw new IllegalArgumentException("invalid instrument: " + instrument);
	}

	public static int checkNote(int note) {
		if(note >= 0) {
			return note;
		}
		throw new IllegalArgumentException("invalid note: " + note);
	}

	public static class NoteTask {

		protected String instrument;
		protected int instrumentID = -1;
		protected int note;
		protected final float volume;

		public NoteTask(@Nullable String instrument, int note, float volume) {
			this.instrument = instrument;
			this.note = note;
			this.volume = volume;
		}

		public NoteTask(int instrumentID, int note, float volume) {
			this.instrumentID = instrumentID;
			this.note = note;
			this.volume = volume;
		}

		public void play(World world, BlockPos pos) {
			if(instrument == null && instrumentID >= 0) {
				if(instrumentID <= 4) {
					NoteBlockEvent.Play e = new NoteBlockEvent.Play(world, pos, world.getBlockState(pos), note, instrumentID);
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
				instrument = "block.note." + instrument;
			}
			if(instrument != null) {
				playNoteRaw(world, pos, instrument, note, volume);
			}
		}
	}

	/*private static class BetterNoteTask extends NoteTask {

		private NoteBetterInstrument instr;

		public BetterNoteTask(@Nullable String instrument, int note, float volume) {
			super(instrument, note, volume);
		}

		@Optional.Method(modid = Mods.API.NoteBetter)
		private BetterNoteTask setInstrument(@Nullable NoteBetterInstrument instr) {
			this.instr = instr;
			return this;
		}

		@Override
		@Optional.Method(modid = Mods.API.NoteBetter)
		public void play(World world, BlockPos pos) {
			ISoundEvent soundEvent = instr.soundEvent();
			if(soundEvent != null) {
				NoteBetterPlayEvent event = new NoteBetterPlayEvent(world, pos, world.getBlockState(pos), note, instr);
				if(MinecraftForge.EVENT_BUS.post(event)) {
					// soundEvent == null means play no sound, so we are returning true, i.e. cancelling here.
					return;
				}
				instr = event.noteBetterInstrument();
				soundEvent = instr.soundEvent();
				if(soundEvent != null) {
					soundEvent.play(world, pos, SoundCategory.RECORDS, volume < 0 ? instr.volume() : volume, note);
				}
			} else {
				super.play(world, pos);
			}
		}
	}*/
}

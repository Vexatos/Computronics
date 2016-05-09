package pl.asie.computronics.audio;

import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;

/**
 * @author SleepyTrousers, Vexatos
 */
public class MachineSound extends PositionedSound implements ITickableSound {

	private boolean donePlaying;

	public MachineSound(ResourceLocation sound, BlockPos pos, float volume, float pitch) {
		this(sound, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, volume, pitch, true);
	}

	public MachineSound(ResourceLocation sound, float x, float y, float z, float volume, float pitch) {
		this(sound, x, y, z, volume, pitch, true);
	}

	public MachineSound(ResourceLocation sound, BlockPos pos, float volume, float pitch, boolean repeat) {
		this(sound, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, volume, pitch, repeat);
	}

	public MachineSound(ResourceLocation sound, float x, float y, float z, float volume, float pitch, boolean repeat) {
		super(sound, SoundCategory.BLOCKS);
		this.xPosF = x;
		this.yPosF = y;
		this.zPosF = z;
		this.volume = volume;
		this.pitch = pitch;
		this.repeat = repeat;
	}

	@Override
	public void update() {
	}

	public void endPlaying() {
		donePlaying = true;
	}

	public void startPlaying() {
		donePlaying = false;
	}

	@Override
	public boolean isDonePlaying() {
		return donePlaying;
	}

}

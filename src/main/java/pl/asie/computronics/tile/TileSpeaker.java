package pl.asie.computronics.tile;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.audio.AudioPacket;
import pl.asie.computronics.api.audio.IAudioReceiver;
import pl.asie.computronics.api.audio.IAudioSource;
import pl.asie.computronics.reference.Config;

public class TileSpeaker extends TileEntityPeripheralBase implements IAudioReceiver, ITickable {

	private final TIntHashSet packetIds = new TIntHashSet();
	private IAudioSource lastSource;

	public TileSpeaker() {
		super("speaker");
	}

	@Override
	public void update() {
		packetIds.clear();
	}

	@Override
	public World getSoundWorld() {
		return worldObj;
	}

	@Override
	public BlockPos getSoundPos() {
		return getPos();
	}

	@Override
	public int getSoundDistance() {
		return Config.TAPEDRIVE_DISTANCE;
	}

	@Override
	public void receivePacket(AudioPacket packet, EnumFacing direction) {
		if(!packetIds.contains(packet.id)) {
			packetIds.add(packet.id);

			lastSource = packet.source;
			packet.addReceiver(this);
		}
	}

	@Override
	public String[] getMethodNames() {
		return new String[0];
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		return new Object[0];
	}

	@Override
	public boolean connectsAudio(EnumFacing side) {
		return worldObj.getBlockState(getPos()).getValue(Computronics.speaker.rotation.FACING) != side;
	}
}

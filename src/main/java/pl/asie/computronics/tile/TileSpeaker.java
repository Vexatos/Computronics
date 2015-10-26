package pl.asie.computronics.tile;

import gnu.trove.set.hash.TIntHashSet;

import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import pl.asie.computronics.audio.AudioPacket;
import pl.asie.computronics.audio.IAudioReceiver;
import pl.asie.computronics.audio.IAudioSource;
import pl.asie.computronics.reference.Config;

public class TileSpeaker extends TileEntityPeripheralBase implements IAudioReceiver {
	private final TIntHashSet packetIds = new TIntHashSet();
	private IAudioSource lastSource;

	public TileSpeaker() {
		super("speaker");
	}

	@Override
	public void updateEntity() {
		packetIds.clear();
	}

	@Override
	public World getSoundWorld() {
		return worldObj;
	}

	@Override
	public int getSoundX() {
		return xCoord;
	}

	@Override
	public int getSoundY() {
		return yCoord;
	}

	@Override
	public int getSoundZ() {
		return zCoord;
	}

	@Override
	public int getSoundDistance() {
		return Config.TAPEDRIVE_DISTANCE;
	}

	@Override
	public void receivePacket(AudioPacket packet, ForgeDirection direction) {
		if (!packetIds.contains(packet.id)) {
			packetIds.add(packet.id);

			lastSource = packet.source;
			packet.addReceiver(this);
		}
	}

	@Override
	public short busRead(int addr) {
		return 0;
	}

	@Override
	public void busWrite(int addr, short data) {

	}

	@Override
	public String[] getMethodNames() {
		return new String[0];
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		return new Object[0];
	}
}

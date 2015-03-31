package pl.asie.computronics.audio.tts;

import com.google.common.base.Throwables;
import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.nbt.NBTTagCompound;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.network.Packets;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileEntityPeripheralBase;
import pl.asie.lib.network.Packet;

import java.io.IOException;

/**
 * @author Vexatos
 */
public class TileTTSBox extends TileEntityPeripheralBase {

	public TileTTSBox() {
		super("speech_box");
	}

	@Override
	public boolean canUpdate() {
		return Config.MUST_UPDATE_TILE_ENTITIES;
	}

	private int codecId, codecTick, packetId;
	protected int packetSize = 1024;
	private byte[] buffer;

	private Packet createMusicPacket() {
		byte[] packet = new byte[packetSize];
		int amount = read(packet, false); // read data into packet array
		try {
			if(amount <= 0) {
				stop();
				return Computronics.packet.create(Packets.PACKET_AUDIO_STOP).writeInt(codecId);
			}
			Packet pkt = Computronics.packet.create(Packets.PACKET_AUDIO_DATA)
				.writeInt(worldObj.provider.dimensionId)
				.writeInt(xCoord).writeInt(yCoord).writeInt(zCoord)
				.writeInt(packetId++)
				.writeInt(codecId)
				.writeShort((short) packetSize)
				.writeByte((byte) 127)
				.writeByteArrayData(packet);
			if(amount < packetSize) {
				stop();
			}
			return pkt;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private void stop() {
		Computronics.tts.removePlayer(codecId);
		this.buffer = null;
		this.position = 0;
	}

	private int position;

	private int read(byte[] v, boolean simulate) {
		if(buffer == null || buffer.length <= 0) {
			return 0;
		}
		int len = Math.min(buffer.length, v.length);
		if(position + len > buffer.length) {
			len = buffer.length - position;
		}
		System.arraycopy(buffer, position, v, 0, len);
		if(!simulate) {
			position += len;
		}

		return len;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(!worldObj.isRemote && buffer != null) {
			if(codecTick % 5 == 0) {
				codecTick++;
				Packet pkt = createMusicPacket();
				if(pkt != null) {
					Computronics.packet.sendToAllAround(pkt, this, Config.TAPEDRIVE_DISTANCE);
				}
			} else {
				codecTick++;
			}
		}
	}

	private Object[] sendNewText(String text) throws IOException {
		if(buffer != null) {
			return new Object[] { false, "there is already something being said" };
		}
		buffer = Computronics.tts.say(xCoord, yCoord, zCoord, text);
		if(buffer == null || buffer.length <= 0) {
			buffer = null;
			return new Object[] { false, "an unknown error occured" };
		}
		codecId = Computronics.tts.newPlayer();
		codecTick = 0;
		packetId = 0;
		Computronics.instance.audio.getPlayer(codecId);
		Packet pkt = createMusicPacket();
		if(pkt != null) {
			Computronics.packet.sendToAllAround(pkt, this, Config.TAPEDRIVE_DISTANCE);
			return new Object[] { true };
		}
		return new Object[] { false, "an unknown error occured" };
	}

	@Callback
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] say(Context context, Arguments args) {
		try {
			return this.sendNewText(args.checkString(0));
		} catch(IOException e) {
			throw new IllegalArgumentException("could not send string");
		} catch(Exception e) {
			e.printStackTrace();
			Throwables.propagate(e);
		}
		return new Object[] { false };
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String[] getMethodNames() {
		return new String[] { "say" };
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		switch(method) {
			case 0: {
				if(arguments.length < 1 || !(arguments[0] instanceof String)) {
					throw new LuaException("first argument needs to be a string");
				}
				try {
					return new Object[] { this.sendNewText((String) arguments[0]) };
				} catch(IOException e) {
					throw new LuaException("could not send string");
				}
			}
		}
		return null;
	}

	@Override
	@Optional.Method(modid = Mods.NedoComputers)
	public boolean connectable(int side) {
		return false;
	}

	@Override
	@Optional.Method(modid = Mods.NedoComputers)
	public short busRead(int addr) {
		return 0;
	}

	@Override
	@Optional.Method(modid = Mods.NedoComputers)
	public void busWrite(int addr, short data) {

	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if(nbt.hasKey("bufferdata")) {
			buffer = nbt.getByteArray("bufferdata");
			codecId = nbt.getInteger("buffer:codecId");
			position = nbt.getInteger("buffer:pos");
			codecTick = nbt.getInteger("buffer:tick");
			packetId = nbt.getInteger("buffer:packet");
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if(buffer != null && buffer.length > 0) {
			nbt.setByteArray("buffer:data", buffer);
			nbt.setInteger("buffer:codecId", codecId);
			nbt.setInteger("buffer:pos", position);
			nbt.setInteger("buffer:tick", codecTick);
			nbt.setInteger("buffer:packet", packetId);
		}
	}
}

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
import pl.asie.computronics.network.PacketType;
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
		return true;
	}

	private int lockedTicks = 0;

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(lockedTicks > 0) {
			--lockedTicks;
			if(lockedTicks <= 0 && worldObj.isRemote) {
				Computronics.tts.stopSource(this);
			}
		}
	}

	public void setLocked(int ticks) {
		this.lockedTicks = ticks;
	}

	private Object[] sendNewText(String text) throws IOException {
		Packet packet = Computronics.packet.create(PacketType.TTS.ordinal()).writeTileLocation(this).writeString(text);
		Computronics.packet.sendToAllAround(packet, this, Config.TAPEDRIVE_DISTANCE);
		return new Object[] { true };
	}

	@Callback
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] say(Context context, Arguments args) {
		if(lockedTicks > 0) {
			return new Object[] { false, "already talking" };
		}
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
				if(lockedTicks > 0) {
					return new Object[] { false, "already talking" };
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
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		/*if(nbt.hasKey("lockedTicks")) {
			lockedTicks = nbt.getInteger("lockedTicks");
		}*/
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		//nbt.setInteger("lockedTicks", lockedTicks);
	}
}

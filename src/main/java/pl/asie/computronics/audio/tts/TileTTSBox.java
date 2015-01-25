package pl.asie.computronics.audio.tts;

import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileEntityPeripheralBase;

import java.io.IOException;

/**
 * @author Vexatos
 */
public class TileTTSBox extends TileEntityPeripheralBase {

	public TileTTSBox() {
		super("speak");
	}

	@Override
	public boolean canUpdate() {
		return Config.MUST_UPDATE_TILE_ENTITIES;
	}

	@Callback(direct = true, limit = 1)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] say(Context context, Arguments args) {
		try {
			Computronics.packet.sendToAllAround(Computronics.packet.create(5).writeString(args.checkString(0)),
				this, Config.CHATBOX_DISTANCE);
			return new Object[] { true };
		} catch(IOException e) {
			throw new IllegalArgumentException("could not send string");
		}
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String[] getMethodNames() {
		return new String[] { "say" };
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		switch(method){
			case 0:{
				if(arguments.length < 1 || !(arguments[0] instanceof String)) {
					throw new LuaException("first argument needs to be a string");
				}
				try {
					Computronics.packet.sendToAllAround(Computronics.packet.create(5).writeString((String) arguments[0]),
						this, Config.CHATBOX_DISTANCE);
					return new Object[] { true };
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
}

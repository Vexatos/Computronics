package pl.asie.computronics.tile;

import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.SimpleComponent;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.util.NoteUtils;
import pl.asie.lib.block.TileEntityBase;

public class TileIronNote extends TileEntityPeripheralBase {
	public TileIronNote() {
		super("iron_noteblock");
	}

	@Override
	public boolean canUpdate() { return Computronics.MUST_UPDATE_TILE_ENTITIES; }
	
	// OpenComputers
	
    @Callback(direct = true)
    @Optional.Method(modid="OpenComputers")
    public Object[] playNote(Context context, Arguments args) {
    	if(args.count() >= 1) {
    		if(args.count() >= 2 && args.isInteger(1)) {
    			if(args.isInteger(0)) {
    				NoteUtils.playNote(worldObj, xCoord, yCoord, zCoord, args.checkInteger(0), args.checkInteger(1));
    			} else if(args.isString(0)) {
    				NoteUtils.playNote(worldObj, xCoord, yCoord, zCoord, args.checkString(0), args.checkInteger(1));
    			}
    		} else if(args.isInteger(0)) {
    			NoteUtils.playNote(worldObj, xCoord, yCoord, zCoord, -1, args.checkInteger(0));
    		}
    	}
    	return null;
    }

	@Override
    @Optional.Method(modid="ComputerCraft")
	public String[] getMethodNames() {
		return new String[]{"playNote"};
	}

	@Override
    @Optional.Method(modid="ComputerCraft")
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
			InterruptedException {
		if(arguments.length == 1 && (arguments[0] instanceof Double)) {
			NoteUtils.playNote(worldObj, xCoord, yCoord, zCoord, 0, ((Double)arguments[0]).intValue());
		} else if(arguments.length == 2 && (arguments[1] instanceof Double)) {
			if(arguments[0] instanceof Double) {
				NoteUtils.playNote(worldObj, xCoord, yCoord, zCoord, ((Double)arguments[0]).intValue(), ((Double)arguments[1]).intValue());
			} else if(arguments[0] instanceof String) {
				NoteUtils.playNote(worldObj, xCoord, yCoord, zCoord, (String)arguments[0], ((Double)arguments[1]).intValue());
			}
		}
		return null;
	}

	@Override
    @Optional.Method(modid="nedocomputers")
	public short busRead(int addr) {
		return 0;
	}

	@Override
    @Optional.Method(modid="nedocomputers")
	public void busWrite(int addr, short data) {
		NoteUtils.playNote(worldObj, xCoord, yCoord, zCoord, (data >> 5), (data & 31));
	}
}

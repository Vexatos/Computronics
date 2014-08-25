package pl.asie.computronics.tile;

import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import mods.immibis.redlogic.api.wiring.IBundledEmitter;
import mods.immibis.redlogic.api.wiring.IBundledUpdatable;
import mods.immibis.redlogic.api.wiring.IConnectable;
import mods.immibis.redlogic.api.wiring.IWire;
import mrtjp.projectred.api.IBundledTile;
import mrtjp.projectred.api.ProjectRedAPI;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.util.NoteUtils;

@Optional.InterfaceList({
	@Optional.Interface(iface = "mods.immibis.redlogic.api.wiring.IBundledUpdatable", modid = "RedLogic"),
	@Optional.Interface(iface = "mods.immibis.redlogic.api.wiring.IConnectable", modid = "RedLogic"),
	@Optional.Interface(iface = "mrtjp.projectred.api.IBundledTile", modid = "ProjRed|Core")
})
public class TileIronNote extends TileEntityPeripheralBase implements IBundledTile, IBundledUpdatable, IConnectable {
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
	
	private void parseBundledInput(byte[] data) {
		int baseNote = 4;
		if(data != null) for(int i = 0; i < 16; i++) {
			if(data[i] != 0)
				NoteUtils.playNote(worldObj, xCoord, yCoord, zCoord, -1, baseNote + i);
		}
	}

	@Override
	@Optional.Method(modid="ProjRed|Core")
	public byte[] getBundledSignal(int side) {
		return new byte[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	}

	@Override
	@Optional.Method(modid="ProjRed|Core")
	public boolean canConnectBundled(int side) { return true; }
	
	@Optional.Method(modid = "ProjRed|Core")
	public void onProjectRedBundledInputChanged() {
		for(int i = 0; i < 6; i++) {
			parseBundledInput(ProjectRedAPI.transmissionAPI.getBundledInput(worldObj, xCoord, yCoord, zCoord, i));
		}
	}

	@Override
	@Optional.Method(modid = "RedLogic")
	public boolean connects(IWire wire, int blockFace, int fromDirection) { return (wire instanceof IBundledEmitter); }
	@Override
	@Optional.Method(modid = "RedLogic")
	public boolean connectsAroundCorner(IWire wire, int blockFace, int fromDirection) { return false; }
	@Override
	@Optional.Method(modid = "RedLogic")
	public void onBundledInputChanged() {
		for(int side = 0; side < 6; side++) {
			ForgeDirection dir = ForgeDirection.getOrientation(side);
			TileEntity input = worldObj.getTileEntity(xCoord+dir.offsetX, yCoord+dir.offsetY, zCoord+dir.offsetZ);
			if(!(input instanceof IBundledEmitter)) continue;
			for(int direction = -1; direction < 6; direction++) {
 				byte[] data = ((IBundledEmitter)input).getBundledCableStrength(direction, side ^ 1);
 				if(data != null) parseBundledInput(data);
			}
		}
	}
}

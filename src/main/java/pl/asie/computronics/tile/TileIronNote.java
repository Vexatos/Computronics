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
import pl.asie.lib.block.TileEntityBase;

public class TileIronNote extends TileEntityPeripheralBase {
	public TileIronNote() {
		super("iron_noteblock");
	}
	
	public void playNote(String instrument, int note) {
		note %= 25;
		
        float f = (float)Math.pow(2.0D, (double)(note- 12) / 12.0D);

        worldObj.playSoundEffect((double)xCoord + 0.5D, (double)yCoord + 0.5D, (double)zCoord + 0.5D, "note." + instrument, 3.0F, f);
        worldObj.spawnParticle("note", (double)xCoord + 0.5D, (double)yCoord + 1.2D, (double)zCoord + 0.5D, (double)f / 24.0D, 0.0D, 0.0D);
	}
	
	public void playNote(int instrument, int note) {
		instrument %= 7;
		
        String s = "harp";
        if (instrument == 1) s = "bd";
        else if(instrument == 2) s = "snare";
        else if(instrument == 3) s = "hat";
        else if(instrument == 4) s = "bassattack";
        else if(instrument == 5) s = "pling";
        else if(instrument == 6) s = "bass";
        
        playNote(s, note);
	}

	@Override
	public boolean canUpdate() { return false; }
	
	// OpenComputers
	
    @Callback(direct = true)
    @Optional.Method(modid="OpenComputers")
    public Object[] playNote(Context context, Arguments args) {
    	if(args.count() >= 1) {
    		if(args.count() >= 2 && args.isInteger(1)) {
    			if(args.isInteger(0)) {
    				playNote(args.checkInteger(0), args.checkInteger(1));
    			} else if(args.isString(0)) {
    				playNote(args.checkString(0), args.checkInteger(1));
    			}
    		} else if(args.isInteger(0)) {
    			playNote(0, args.checkInteger(0));
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
			playNote(0, ((Double)arguments[0]).intValue());
		} else if(arguments.length == 2 && (arguments[1] instanceof Double)) {
			if(arguments[0] instanceof Double) {
				playNote(((Double)arguments[0]).intValue(), ((Double)arguments[1]).intValue());
			} else if(arguments[0] instanceof String) {
				playNote((String)arguments[0], ((Double)arguments[1]).intValue());
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
		playNote((data >> 5), (data & 31));
	}
}

package pl.asie.computronics.tile;

import cpw.mods.fml.common.Optional;
import dan200.computer.api.IComputerAccess;
import openperipheral.api.Arg;
import openperipheral.api.LuaCallable;
import openperipheral.api.LuaType;
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
}

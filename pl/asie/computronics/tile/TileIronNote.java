package pl.asie.computronics.tile;

import dan200.computer.api.IComputerAccess;
import openperipheral.api.Arg;
import openperipheral.api.LuaCallable;
import openperipheral.api.LuaType;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.SimpleComponent;
import pl.asie.lib.block.TileEntityBase;

public class TileIronNote extends TileEntityBase implements SimpleComponent {
	public void playNote(int instrument, int note) {
		instrument %= 5;
		note %= 25;
		
        float f = (float)Math.pow(2.0D, (double)(note- 12) / 12.0D);
        String s = "harp";
        if (instrument == 1) s = "bd";
        else if(instrument == 2) s = "snare";
        else if(instrument == 3) s = "hat";
        else if(instrument == 4) s = "bassattack";

        worldObj.playSoundEffect((double)xCoord + 0.5D, (double)yCoord + 0.5D, (double)zCoord + 0.5D, "note." + s, 3.0F, f);
	}

	@Override
	public String getComponentName() {
		return "iron_noteblock";
	}

	@Override
	public boolean canUpdate() { return false; }
	
	// OpenComputers
	
    @Callback
    public Object[] playNote(Context context, Arguments args) {
    	if(args.count() >= 1 && args.isInteger(0)) {
    		if(args.count() >= 2 && args.isInteger(1)) {
    			playNote(args.checkInteger(0), args.checkInteger(1));
    		} else {
    			playNote(0, args.checkInteger(0));
    		}
    	}
    	return null;
    }
    
    // OpenPeripheral
    @LuaCallable(description = "Plays a note.")
	public void playNote(
		IComputerAccess computer,
		@Arg(name = "instrument", type = LuaType.NUMBER, description = "The instrument to play, 0-4") int instrument,
		@Arg(name = "note", type = LuaType.NUMBER, description = "The note to play, 0-24") int note
	) {
    	playNote(instrument, note);
    }
}

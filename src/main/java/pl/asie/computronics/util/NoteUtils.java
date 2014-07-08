package pl.asie.computronics.util;

import net.minecraft.world.World;

public class NoteUtils {
	public static void playNote(World worldObj, int xCoord, int yCoord, int zCoord, String instrument, int note) {
		note %= 25;
		
        float f = (float)Math.pow(2.0D, (double)(note- 12) / 12.0D);

        worldObj.playSoundEffect((double)xCoord + 0.5D, (double)yCoord + 0.5D, (double)zCoord + 0.5D, "note." + instrument, 3.0F, f);
	}
	
	public static void playNote(World worldObj, int xCoord, int yCoord, int zCoord, int instrument, int note) {
		instrument %= 7;
		
        String s = "harp";
        if (instrument == 1) s = "bd";
        else if(instrument == 2) s = "snare";
        else if(instrument == 3) s = "hat";
        else if(instrument == 4) s = "bassattack";
        else if(instrument == 5) s = "pling";
        else if(instrument == 6) s = "bass";
        
        playNote(worldObj, xCoord, yCoord, zCoord, s, note);
	}
}

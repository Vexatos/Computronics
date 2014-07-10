package pl.asie.computronics.util;

import pl.asie.computronics.Computronics;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.NoteBlockEvent;

public class NoteUtils {
	public static void playNote(World worldObj, int xCoord, int yCoord, int zCoord, String instrument, int note) {
        float f = (float)Math.pow(2.0D, (double)(note- 12) / 12.0D);

        worldObj.playSoundEffect((double)xCoord + 0.5D, (double)yCoord + 0.5D, (double)zCoord + 0.5D, "note." + instrument, 3.0F, f);
	}
	
	public static void playNote(World worldObj, int xCoord, int yCoord, int zCoord, int instrument, int note) {
		if(instrument < 0) {
			// Get default instrument
			instrument = 0;
            byte b0 = 0;
            if(yCoord > 0) {
	            Material m = worldObj.getBlock(xCoord, yCoord - 1, zCoord).getMaterial();
	            if(m == Material.rock) b0 = 1;
	            if(m == Material.sand) b0 = 2;
	            if(m == Material.glass) b0 = 3;
	            if(m == Material.wood) b0 = 4;
            }
		}
		instrument %= 7;
		
		if(instrument <= 4 && !Computronics.DISABLE_IRONNOTE_FORGE_EVENTS) {
			NoteBlockEvent.Play e = new NoteBlockEvent.Play(worldObj, xCoord, yCoord, zCoord, 32767, note, instrument);
			if(MinecraftForge.EVENT_BUS.post(e)) return;
			instrument = e.instrument.ordinal();
			note = e.getVanillaNoteId();
		}
		
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

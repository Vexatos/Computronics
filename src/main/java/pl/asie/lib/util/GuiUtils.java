package pl.asie.lib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import pl.asie.lib.gui.GuiBase;
import pl.asie.lib.tile.TileEntityBase;

public class GuiUtils {
	public static TileEntityBase currentTileEntity() {
		GuiScreen gc = Minecraft.getMinecraft().currentScreen;
		if(gc instanceof GuiBase) {
			return ((GuiBase)gc).container.getEntity();
		}
		return null;
	}
	
	public static GuiBase currentGui() {
		GuiScreen gc = Minecraft.getMinecraft().currentScreen;
		if(gc instanceof GuiBase) return ((GuiBase)gc);
		return null;
	}
}

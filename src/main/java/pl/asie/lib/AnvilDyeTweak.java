package pl.asie.lib;

import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.init.Items;
import net.minecraftforge.event.AnvilUpdateEvent;
import pl.asie.lib.util.ChatUtils;

public class AnvilDyeTweak {
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void event(AnvilUpdateEvent e) {
		if(e.left == null || e.right == null || e.left.getItem() == null || e.right.getItem() == null || e.isCanceled()) {
			return;
		}
		if(e.right.getItem().equals(Items.dye) && e.right.stackSize == e.left.stackSize) {
			e.cost = e.left.isItemStackDamageable() ? 7 : e.left.stackSize * 5;
			String colorPrefix = "\u00a7" + Integer.toHexString(ChatUtils.dyeToChat(e.right.getItemDamage()));
			String name = e.name;
			if(name == null || name.length() == 0) {
				// no custom name set
				name = e.left.getDisplayName();
			}
			if(name.length() > 0) {
				if(name.codePointAt(0) == 167) {
					name = name.substring(2);
				} else if(e.left.getDisplayName().length() > 0 && e.left.getDisplayName().codePointAt(0) == 167 && name.codePointAt(0) != 167) {
					// workaround around a screwup
					name = name.substring(1);
				}
			}
			if(e.output == null) {
				e.output = e.left.copy();
			}
			e.output.setStackDisplayName(colorPrefix + name);
		}
	}
}

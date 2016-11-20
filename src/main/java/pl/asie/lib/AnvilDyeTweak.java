package pl.asie.lib;

import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pl.asie.lib.util.ChatUtils;
import pl.asie.lib.util.ColorUtils;

public class AnvilDyeTweak {

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void event(AnvilUpdateEvent e) {
		if(e.getLeft().isEmpty() || e.getRight().isEmpty() || e.isCanceled()) {
			return;
		}
		ColorUtils.Color color = ColorUtils.getColor(e.getRight());
		if(color != null && e.getRight().getCount() == e.getLeft().getCount()) {
			e.setCost(e.getLeft().isItemStackDamageable() ? 7 : e.getLeft().getCount() * 5);
			String colorPrefix = "\u00a7" + Integer.toHexString(ChatUtils.dyeToChat(color.ordinal()));
			String name = e.getName();
			if(name == null || name.length() == 0) {
				// no custom name set
				name = e.getLeft().getDisplayName();
			}
			if(name.length() > 0) {
				if(name.codePointAt(0) == 167) {
					name = name.substring(2);
				} else if(e.getLeft().getDisplayName().length() > 0 && e.getLeft().getDisplayName().codePointAt(0) == 167 && name.codePointAt(0) != 167) {
					// workaround around a screwup
					name = name.substring(1);
				}
			}
			if(e.getOutput().isEmpty()) {
				e.setOutput(e.getLeft().copy());
			}
			e.getOutput().setStackDisplayName(colorPrefix + name);
		}
	}
}

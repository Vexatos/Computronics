package pl.asie.computronics.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.oc.manual.IItemWithDocumentation;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.OCUtils;
import pl.asie.lib.item.ItemMultiple;

import java.util.List;

/**
 * @author Vexatos
 */
public class ItemOCSpecialParts extends ItemMultiple implements IItemWithDocumentation {

	public ItemOCSpecialParts() {
		super(Mods.Computronics, new String[] {
			"magical_memory"
		});
		this.setCreativeTab(Computronics.tab);
	}

	@Override
	public String getDocumentationName(ItemStack stack) {
		switch(stack.getItemDamage()) {
			case 0:
				return "magical_memory";
			default:
				return "index";
		}
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean par4) {
		OCUtils.addTooltip(stack, tooltip);
	}
}

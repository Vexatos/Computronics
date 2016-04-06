package pl.asie.computronics.oc.driver;

import li.cil.oc.api.driver.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import pl.asie.computronics.oc.IntegrationOpenComputers;
import pl.asie.computronics.util.OCUtils;

/**
 * @author Vexatos
 */
public abstract class DriverOCSpecialPart implements Item {

	private final int metadata;

	protected DriverOCSpecialPart(int metadata) {
		this.metadata = metadata;
	}

	@Override
	public boolean worksWith(ItemStack stack) {
		return stack.getItem().equals(IntegrationOpenComputers.itemOCSpecialParts) && stack.getItemDamage() == metadata;
	}

	@Override
	public NBTTagCompound dataTag(ItemStack stack) {
		return OCUtils.dataTag(stack);
	}
}

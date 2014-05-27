package pl.asie.computronics.item;

import li.cil.oc.api.driver.Slot;
import li.cil.oc.api.network.ManagedEnvironment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.oc.RobotUpgradeCamera;
import pl.asie.lib.item.ItemMultiple;

public class ItemOpenComputers extends ItemMultiple implements li.cil.oc.api.driver.Item {
	public ItemOpenComputers() {
		super("computronics", new String[]{"robot_upgrade_camera" /*, "card_sound_chiptune" */});
		this.setCreativeTab(Computronics.tab);
	}

	@Override
	public boolean worksWith(ItemStack stack) {
		return stack.getItem().equals(this);
	}

	@Override
	public ManagedEnvironment createEnvironment(ItemStack stack,
			TileEntity container) {
		switch(stack.getItemDamage()) {
			case 0: return new RobotUpgradeCamera(container);
			default: return null;
		}
	}

	@Override
	public Slot slot(ItemStack stack) {
		switch(stack.getItemDamage()) {
			case 0: return Slot.Upgrade;
			//case 1: return Slot.Card;
			default: return Slot.None;
		}
	}

	@Override
	public int tier(ItemStack stack) {
		switch(stack.getItemDamage()) {
			case 0: return 1; // Tier 2
			//case 1: return 1; // Tier 2
			default: return 0; // Tier 1 default
		}
	}

	@Override
	public NBTTagCompound dataTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        final NBTTagCompound nbt = stack.getTagCompound();
        // This is the suggested key under which to store item component data.
        // You are free to change this as you please.
        if (!nbt.hasKey("oc:data")) {
            nbt.setTag("oc:data", new NBTTagCompound());
        }
        return nbt.getCompoundTag("oc:data");
	}
}

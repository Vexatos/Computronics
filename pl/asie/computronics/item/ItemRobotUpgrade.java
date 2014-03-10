package pl.asie.computronics.item;

import li.cil.oc.api.driver.Slot;
import li.cil.oc.api.network.ManagedEnvironment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.robot.RobotUpgradeCamera;
import pl.asie.lib.item.ItemMultiple;

public class ItemRobotUpgrade extends ItemMultiple implements li.cil.oc.api.driver.Item {
	public ItemRobotUpgrade(int id) {
		super(id, "computronics", new String[]{"robot_upgrade_camera"});
		this.setCreativeTab(Computronics.tab);
	}

	@Override
	public boolean worksWith(ItemStack stack) {
		return stack.itemID == this.itemID;
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
		return Slot.Upgrade;
	}

	@Override
	public int tier(ItemStack stack) {
		return 0;
	}

	@Override
	public NBTTagCompound dataTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound("tag"));
        }
        final NBTTagCompound nbt = stack.getTagCompound();
        // This is the suggested key under which to store item component data.
        // You are free to change this as you please.
        if (!nbt.hasKey("oc:data")) {
            nbt.setCompoundTag("oc:data", new NBTTagCompound());
        }
        return nbt.getCompoundTag("oc:data");
	}
}

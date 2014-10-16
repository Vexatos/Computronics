package pl.asie.computronics.item;

import cpw.mods.fml.common.Optional;
import li.cil.oc.api.driver.EnvironmentHost;
import li.cil.oc.api.network.ManagedEnvironment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.oc.DriverCardFX;
import pl.asie.computronics.oc.RobotUpgradeCamera;
import pl.asie.computronics.oc.RobotUpgradeChatBox;
import pl.asie.computronics.oc.RobotUpgradeRadar;
import pl.asie.computronics.reference.Mods;
import pl.asie.lib.item.ItemMultiple;

@Optional.Interface(iface="li.cil.oc.api.driver.Item", modid = Mods.OpenComputers)
public class ItemOpenComputers extends ItemMultiple implements li.cil.oc.api.driver.Item {
	public ItemOpenComputers() {
		super("computronics", new String[]{"robot_upgrade_camera", "robot_upgrade_chatbox", "robot_upgrade_radar", "card_fx"});
		this.setCreativeTab(Computronics.tab);
	}

	@Override
	@Optional.Method(modid=Mods.OpenComputers)
	public boolean worksWith(ItemStack stack) {
		return stack.getItem().equals(this);
	}

	@Override
	@Optional.Method(modid=Mods.OpenComputers)
	public ManagedEnvironment createEnvironment(ItemStack stack,
			EnvironmentHost container) {
		switch(stack.getItemDamage()) {
			case 0: return new RobotUpgradeCamera(container);
			case 1: return new RobotUpgradeChatBox(container);
			case 2: return new RobotUpgradeRadar(container);
			case 3: return new DriverCardFX(container);
			default: return null;
		}
	}

	@Override
	@Optional.Method(modid=Mods.OpenComputers)
	public String slot(ItemStack stack) {
		switch(stack.getItemDamage()) {
			case 0: return "upgrade";
			case 1: return "upgrade";
			case 2: return "upgrade";
			case 3: return "card";
			default: return "none";
		}
	}

	@Override
	@Optional.Method(modid=Mods.OpenComputers)
	public int tier(ItemStack stack) {
		switch(stack.getItemDamage()) {
			case 0: return 1; // Tier 2
			case 1: return 1; // Tier 2
			case 2: return 2; // Tier 3
			case 3: return 1; // Tier 2
			default: return 0; // Tier 1 default
		}
	}

	@Override
	@Optional.Method(modid=Mods.OpenComputers)
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

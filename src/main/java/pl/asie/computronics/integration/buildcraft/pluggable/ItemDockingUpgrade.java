package pl.asie.computronics.integration.buildcraft.pluggable;

import cpw.mods.fml.common.Optional;
import li.cil.oc.api.driver.EnvironmentAware;
import li.cil.oc.api.driver.EnvironmentHost;
import li.cil.oc.api.driver.Item;
import li.cil.oc.api.driver.item.HostAware;
import li.cil.oc.api.driver.item.Slot;
import li.cil.oc.api.internal.Drone;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.ManagedEnvironment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.reference.Mods;
import pl.asie.lib.item.ItemMultiple;

/**
 * @author Vexatos
 */
@Optional.InterfaceList({
	@Optional.Interface(iface = "li.cil.oc.api.driver.Item", modid = Mods.OpenComputers),
	@Optional.Interface(iface = "li.cil.oc.api.driver.EnvironmentAware", modid = Mods.OpenComputers),
	@Optional.Interface(iface = "li.cil.oc.api.driver.item.HostAware", modid = Mods.OpenComputers)
})
public class ItemDockingUpgrade extends ItemMultiple implements Item, EnvironmentAware, HostAware {

	public ItemDockingUpgrade() {
		super(Mods.Computronics, new String[] {
			"drone_upgrade_docking",
		});
		this.setCreativeTab(Computronics.tab);
	}

	@Override
	public Class<? extends Environment> providedEnvironment(ItemStack itemStack) {
		return DriverDroneStation.class;
	}

	@Override
	public boolean worksWith(ItemStack stack, Class<? extends EnvironmentHost> host) {
		return this.worksWith(stack) && Drone.class.isAssignableFrom(host);
	}

	@Override
	public boolean worksWith(ItemStack stack) {
		return stack.getItem().equals(this);
	}

	@Override
	public ManagedEnvironment createEnvironment(ItemStack stack, EnvironmentHost host) {
		if(!(host instanceof Drone)) {
			return null;
		}
		return new DriverDroneStation(((Drone) host));
	}

	@Override
	public String slot(ItemStack itemStack) {
		return Slot.Upgrade;
	}

	@Override
	public int tier(ItemStack itemStack) {
		return 0; // Tier 1
	}

	@Override
	public NBTTagCompound dataTag(ItemStack stack) {
		if(!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		final NBTTagCompound nbt = stack.getTagCompound();
		// This is the suggested key under which to store item component data.
		// You are free to change this as you please.
		if(!nbt.hasKey("oc:data")) {
			nbt.setTag("oc:data", new NBTTagCompound());
		}
		return nbt.getCompoundTag("oc:data");
	}
}

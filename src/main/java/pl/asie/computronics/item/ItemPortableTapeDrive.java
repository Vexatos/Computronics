package pl.asie.computronics.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.tape.PortableDriveManager;
import pl.asie.computronics.tape.PortableTapeDrive;
import pl.asie.computronics.tile.TapeDriveState.State;
import pl.asie.computronics.util.StringUtil;

import java.util.List;
import java.util.Locale;

/**
 * @author Vexatos
 */
public class ItemPortableTapeDrive extends Item {

	public ItemPortableTapeDrive() {
		super();
		this.setCreativeTab(Computronics.tab);
		this.setHasSubtypes(false);
		this.setUnlocalizedName("computronics.portableTapeDrive");
		this.setTextureName("computronics:portable_tape_drive");
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		this.setNoRepair();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean advanced) {
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey("state")) {
			byte state = stack.getTagCompound().getByte("state");
			if(state >= 0 && state < State.VALUES.length) {
				info.add(StringUtil.localizeAndFormat("tooltip.computronics.tape.state",
					StringUtil.localize("tooltip.computronics.tape.state."
						+ State.VALUES[state].name().toLowerCase(Locale.ENGLISH))));
			}
		}
		super.addInformation(stack, player, info, advanced);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity carrier, int slot, boolean isSelected) {
		super.onUpdate(stack, world, carrier, slot, isSelected);
		PortableTapeDrive drive = PortableDriveManager.INSTANCE.getOrCreate(stack, world.isRemote);
		drive.resetTime();
		drive.updateCarrier(carrier, stack);
		drive.update();
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		PortableTapeDrive drive = PortableDriveManager.INSTANCE.getOrCreate(stack, world.isRemote);
		drive.updateCarrier(player, stack);
		if(world.isRemote) {
			return super.onItemRightClick(stack, world, player);
		}
		if(player.isSneaking()) {
			player.openGui(Computronics.instance, Computronics.guiPortableTapeDrive.getGuiID(), world, 0, 0, 0);
		} else {
			drive.switchState(drive.getEnumState() != State.STOPPED ? State.STOPPED : State.PLAYING);
		}
		return super.onItemRightClick(stack, world, player);
	}
}

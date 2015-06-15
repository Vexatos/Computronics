package pl.asie.computronics.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.tape.PortableTapeDriveManager;
import pl.asie.computronics.tile.TapeDriveState;
import pl.asie.computronics.tile.TapeDriveState.State;
import pl.asie.computronics.util.internal.ITapeDriveItem;
import pl.asie.lib.gui.managed.IGuiProvider;

/**
 * @author Vexatos
 */
public class ItemPortableTapeDrive extends Item implements ITapeDriveItem {

	private IIcon icon_off;
	private IIcon icon_on;

	public ItemPortableTapeDrive() {
		super();
		this.setCreativeTab(Computronics.tab);
		this.setHasSubtypes(false);
		this.setUnlocalizedName("computronics.portable_tape_drive");
		this.setTextureName("computronics:portable_tape_drive");
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		this.setFull3D();
		this.setNoRepair();
	}

	@Override
	public void registerIcons(IIconRegister ir) {
		icon_off = ir.registerIcon("computronics:portable_tape_drive");
		icon_on = ir.registerIcon("computronics:portable_tape_drive");
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		/*if(stack.hasTagCompound() && stack.getTagCompound().getBoolean("bound")) {
			return icon_on;
		}*/
		return icon_off;
	}

	@Override
	public IIcon getIconIndex(ItemStack stack) {
		return getIcon(stack, 0);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if(player.isSneaking()) {
			// TODO Make this safe.
			IGuiProvider guiProvider = Computronics.tapeReader.getGuiProvider(world, (int) player.posX, (int) player.posY, (int) player.posZ, player, -1);
			if(guiProvider != null) {
				if(guiProvider.canOpen(world, (int) player.posX, (int) player.posY, (int) player.posZ, player, -1)) {
					player.openGui(Computronics.instance, guiProvider.getGuiID(), world, player.getEntityId(), -1, -1);
				}
			}
		} else {
			NBTTagCompound data = stack.getTagCompound();
			if(data != null) {
				State state = State.values()[data.getInteger("computronics:state")];
				if(state == State.STOPPED) {
					data.setInteger("computronics:state", State.PLAYING.ordinal());
				} else if(state == State.PLAYING) {
					data.setInteger("computronics:state", State.STOPPED.ordinal());
				}
			}
		}
		return super.onItemRightClick(stack, world, player);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		super.onUpdate(stack, world, entity, slot, selected);
		if(!world.isRemote) {
			NBTTagCompound data = stack.getTagCompound();
			if(data != null) {
				State state = State.values()[data.getInteger("computronics:state")];
				int id = data.getInteger("computronics:id");
				TapeDriveState tapeDriveState = PortableTapeDriveManager.getOrMakeState(id);
				tapeDriveState.switchState(world, state);
			}
		}
	}
}

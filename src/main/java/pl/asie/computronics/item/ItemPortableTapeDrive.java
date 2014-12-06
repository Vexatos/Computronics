package pl.asie.computronics.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.tape.IItemTapeStorage;
import pl.asie.computronics.tape.ITapeDrive;
import pl.asie.computronics.tile.TapeDriveState;
import pl.asie.lib.gui.inventory.ItemStackInventory;
import pl.asie.lib.item.IInventoryItem;

/**
 * @author Vexatos
 */
public class ItemPortableTapeDrive extends Item implements IInventoryItem {

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
			player.openGui(Computronics.instance, 0, world, (int) player.posX, (int) player.posY, (int) player.posZ);
		}
		return super.onItemRightClick(stack, world, player);
	}

	@Override
	public ItemStackInventory createItemStackInventory(ItemStack stack) {
		return new TapeDriveInventory(stack);
	}

	public class TapeDriveInventory extends ItemStackInventory implements ITapeDrive {

		protected TapeDriveInventory(ItemStack container) {
			super(container);
		}

		@Override
		public int getSizeInventory() {
			return 1;
		}

		@Override
		public String getInventoryName() {
			return this.container().getUnlocalizedName() + ".inventory";
		}

		@Override
		public int getInventoryStackLimit() {
			return 64;
		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
			return false;
		}

		@Override
		public boolean isItemValidForSlot(int slot, ItemStack stack) {
			return stack.getItem() instanceof IItemTapeStorage;
		}

		@Override
		public TapeDriveState.State getEnumState() {
			return TapeDriveState.State.values()[this.container().getTagCompound().getInteger("computronics:state")];
		}

		public void switchState(ItemStack stack, TapeDriveState.State s) {
			NBTTagCompound tag = container.getTagCompound();
			tag.setInteger("computronics:state", s.ordinal());
			this.save(tag);
			container.setTagCompound(tag);
		}
	}
}

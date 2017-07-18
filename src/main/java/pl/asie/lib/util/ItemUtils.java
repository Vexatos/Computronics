package pl.asie.lib.util;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pl.asie.lib.AsieLibMod;

public class ItemUtils {

	public static void dropItems(World world, BlockPos pos, IInventory inventory) {
		for(int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack item = inventory.getStackInSlot(i);

			if(!item.isEmpty() && item.getCount() > 0) {
				inventory.setInventorySlotContents(i, ItemStack.EMPTY);
				dropItem(world, pos, item);
				item.setCount(0);
			}
		}
	}

	public static void dropItems(World world, BlockPos pos) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if(tileEntity == null || !(tileEntity instanceof IInventory)) {
			return;
		}
		IInventory inventory = (IInventory) tileEntity;
		dropItems(world, pos, inventory);
	}

	public static void dropItem(World world, BlockPos pos, ItemStack item) {
		float rx = AsieLibMod.rand.nextFloat() * 0.8F + 0.1F;
		float ry = AsieLibMod.rand.nextFloat() * 0.8F + 0.1F;
		float rz = AsieLibMod.rand.nextFloat() * 0.8F + 0.1F;

		EntityItem entityItem = new EntityItem(world,
			pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz,
			new ItemStack(item.getItem(), item.getCount(), item.getItemDamage()));

		if(item.hasTagCompound()) {
			entityItem.getItem().setTagCompound(item.getTagCompound().copy());
		}

		float factor = 0.05F;
		entityItem.motionX = AsieLibMod.rand.nextGaussian() * factor;
		entityItem.motionY = AsieLibMod.rand.nextGaussian() * factor + 0.2F;
		entityItem.motionZ = AsieLibMod.rand.nextGaussian() * factor;
		world.spawnEntity(entityItem);
	}
}

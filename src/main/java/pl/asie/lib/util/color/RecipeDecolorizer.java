package pl.asie.lib.util.color;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

/**
 * @author Vexatos
 */
public class RecipeDecolorizer implements IRecipe {

	private final Item targetItem;

	public RecipeDecolorizer(Item item) {
		this.targetItem = item;
	}

	public boolean matches(InventoryCrafting crafting, World par2World) {
		boolean hasTargetStack = false;
		boolean hasDye = false;

		for(int i = 0; i < crafting.getSizeInventory(); i++) {
			ItemStack stack = crafting.getStackInSlot(i);
			if(stack != null) {
				if(targetItem == stack.getItem()) {
					hasTargetStack = true; // We need to be more specific here.
				} else if(stack.getItem().equals(Items.dye)) {
					hasDye = true;
				} else {
					return false;
				}
			}
		}

		return hasTargetStack && hasDye;
	}

	/**
	 * Returns an Item that is the result of this recipe
	 */
	public ItemStack getCraftingResult(InventoryCrafting crafting) {
		ItemStack targetStack = null;

		for(int i = 0; i < crafting.getSizeInventory(); i++) {
			ItemStack stack = crafting.getStackInSlot(i);

			if(stack != null) {
				if(targetItem == stack.getItem()) {
					targetStack = stack.copy();
					targetStack.stackSize = 1;
				} else if(stack.getItem() != Items.water_bucket) {
					return null;
				}
			}
		}

		if(targetStack == null) {
			return null;
		}

		ItemColorizer.removeColor(targetStack);
		return targetStack;
	}

	/**
	 * Returns the size of the recipe area
	 */
	public int getRecipeSize() {
		return 10;
	}

	public ItemStack getRecipeOutput() {
		return null;
	}

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inv) {
		return ForgeHooks.defaultRecipeGetRemainingItems(inv);
	}
}

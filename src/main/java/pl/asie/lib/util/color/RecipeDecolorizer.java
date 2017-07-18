package pl.asie.lib.util.color;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import pl.asie.lib.util.FluidUtils;

/**
 * @author Vexatos
 */
public class RecipeDecolorizer extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	private final Item targetItem;

	public RecipeDecolorizer(Item item) {
		this.targetItem = item;
	}

	@Override
	public boolean matches(InventoryCrafting crafting, World par2World) {
		boolean hasTargetStack = false;
		boolean hasBucket = false;

		for(int i = 0; i < crafting.getSizeInventory(); i++) {
			ItemStack stack = crafting.getStackInSlot(i);
			if(!stack.isEmpty()) {
				if(targetItem == stack.getItem()) {
					hasTargetStack = true; // We need to be more specific here.
				} else if(!hasBucket && FluidUtils.containsFluid(stack, FluidRegistry.WATER)) {
					hasBucket = true;
				} else {
					return false;
				}
			}
		}

		return hasTargetStack && hasBucket;
	}

	/**
	 * Returns an Item that is the result of this recipe
	 */
	@Override
	public ItemStack getCraftingResult(InventoryCrafting crafting) {
		ItemStack targetStack = ItemStack.EMPTY;

		for(int i = 0; i < crafting.getSizeInventory(); i++) {
			ItemStack stack = crafting.getStackInSlot(i);

			if(!stack.isEmpty()) {
				if(targetItem == stack.getItem()) {
					targetStack = stack.copy();
					targetStack.setCount(1);
				} else if(!FluidUtils.containsFluid(stack, FluidRegistry.WATER)) {
					return ItemStack.EMPTY;
				}
			}
		}

		if(targetStack.isEmpty()) {
			return ItemStack.EMPTY;
		}

		ItemColorizer.removeColor(targetStack);
		return targetStack;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		return ForgeHooks.defaultRecipeGetRemainingItems(inv);
	}
}

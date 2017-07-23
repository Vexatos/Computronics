package pl.asie.lib.util.color;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry;
import pl.asie.lib.util.ColorUtils;

import java.util.ArrayList;
import java.util.List;

public class RecipeColorizer extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	private final Item targetItem;
	private List<Item> sourceItems;

	public RecipeColorizer(Item targetItem, List<Item> sourceItems) {
		this.targetItem = targetItem;
		this.sourceItems = sourceItems;
	}

	public RecipeColorizer(Item item) {
		this.targetItem = item;
		this.sourceItems = new ArrayList<Item>(1);
		this.sourceItems.add(item);
	}

	@Override
	public boolean matches(InventoryCrafting crafting, World par2World) {
		boolean hasTargetStack = false;
		boolean hasDye = false;

		for(int i = 0; i < crafting.getSizeInventory(); i++) {
			ItemStack stack = crafting.getStackInSlot(i);
			if(!stack.isEmpty()) {
				if(!hasTargetStack &&
					(sourceItems.contains(stack.getItem())
						|| targetItem == stack.getItem())) {
					hasTargetStack = true; // We need to be more specific here.
				} else if(ColorUtils.getColor(stack) != null) {
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
	@Override
	public ItemStack getCraftingResult(InventoryCrafting crafting) {
		ItemStack targetStack = ItemStack.EMPTY;
		int[] color = new int[3];
		int colorCount = 0;
		int maximum = 0;

		for(int i = 0; i < crafting.getSizeInventory(); i++) {
			ItemStack stack = crafting.getStackInSlot(i);

			if(!stack.isEmpty()) {
				if(sourceItems.contains(stack.getItem())
					|| targetItem == stack.getItem()) {
					targetStack = stack.copy();
					targetStack.setCount(1);
				} else {
					ColorUtils.Color stackColor = ColorUtils.getColor(stack);
					if(stackColor == null) {
						return ItemStack.EMPTY;
					}

					float[] itemColor = EnumDyeColor.byDyeDamage(stackColor.ordinal()).getColorComponentValues();
					int red = (int) (itemColor[0] * 255.0F);
					int green = (int) (itemColor[1] * 255.0F);
					int blue = (int) (itemColor[2] * 255.0F);
					maximum += Math.max(red, Math.max(green, blue));
					color[0] += red;
					color[1] += green;
					color[2] += blue;
					++colorCount;
				}
			}
		}

		if(targetStack.isEmpty()) {
			return ItemStack.EMPTY;
		}

		if(targetItem.getClass().isInstance(targetStack.getItem())) {
			if(ItemColorizer.hasColor(targetStack)) {
				int itemColor = ItemColorizer.getColor(targetStack);
				float red = (float) (itemColor >> 16 & 255) / 255.0F;
				float green = (float) (itemColor >> 8 & 255) / 255.0F;
				float blue = (float) (itemColor & 255) / 255.0F;
				maximum = (int) ((float) maximum + Math.max(red, Math.max(green, blue)) * 255.0F);
				color[0] = (int) ((float) color[0] + red * 255.0F);
				color[1] = (int) ((float) color[1] + green * 255.0F);
				color[2] = (int) ((float) color[2] + blue * 255.0F);
				colorCount++;
			}
		} else if(sourceItems.contains(targetStack.getItem())) {
			targetStack = new ItemStack(targetItem, targetStack.getCount(), targetStack.getItemDamage());
		}

		int red = color[0] / colorCount;
		int green = color[1] / colorCount;
		int blue = color[2] / colorCount;
		float max = (float) maximum / (float) colorCount;
		float div = (float) Math.max(red, Math.max(green, blue));
		red = (int) ((float) red * max / div);
		green = (int) ((float) green * max / div);
		blue = (int) ((float) blue * max / div);
		ItemColorizer.setColor(targetStack, (red << 16) | (green << 8) | blue);
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

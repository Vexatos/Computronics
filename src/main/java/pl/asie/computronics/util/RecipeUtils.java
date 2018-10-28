package pl.asie.computronics.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import pl.asie.computronics.Computronics;

import java.util.Arrays;

/**
 * @author asie, Vexatos
 */
public class RecipeUtils {

	private static int recipeCounter = 0;

	public static void addShapedRecipe(ItemStack result, Object... recipe) {
		if(result.isEmpty()) {
			warnCrafting(result, recipe);
			return;
		}
		for(Object o : recipe) {
			if(o == null) {
				warnCrafting(result, recipe);
				return;
			}
			if(o instanceof Block && Block.getIdFromBlock((Block) o) < 0) {
				warnCrafting(result, recipe);
				return;
			}
			if(o instanceof Item && Item.getIdFromItem((Item) o) < 0) {
				warnCrafting(result, recipe);
				return;
			}
			if(o instanceof ItemStack && ((ItemStack) o).isEmpty()) {
				warnCrafting(result, recipe);
				return;
			}
		}
		GameRegistry.findRegistry(IRecipe.class).register(new ShapedOreRecipe(null, result, recipe).setRegistryName(result.getItem().getRegistryName().getPath() + recipeCounter));
		recipeCounter++;
	}

	public static void addShapelessRecipe(ItemStack result, Object... recipe) {
		for(Object o : recipe) {
			if(o == null) {
				warnCrafting(result, recipe);
				return;
			}
			if(o instanceof Block && Block.getIdFromBlock((Block) o) < 0) {
				warnCrafting(result, recipe);
				return;
			}
			if(o instanceof Item && Item.getIdFromItem((Item) o) < 0) {
				warnCrafting(result, recipe);
				return;
			}
			if(o instanceof ItemStack && ((ItemStack) o).isEmpty()) {
				warnCrafting(result, recipe);
				return;
			}
		}
		GameRegistry.findRegistry(IRecipe.class).register(new ShapelessOreRecipe(null, result, recipe).setRegistryName(result.getItem().getRegistryName().getPath() + recipeCounter));
		recipeCounter++;
	}

	private static void warnCrafting(ItemStack result, Object[] recipe) {
		recipe = recipe.clone();
		for(int i = 0; i < recipe.length; i++) {
			if(recipe[i] == null) {
				recipe[i] = "null";
			}
		}
		Computronics.log.warn(String.format("Invalid recipe: %s -> %s", Arrays.toString(recipe), result));
		//"Invalid recipe: " + Arrays.toString(recipe) + " -> " + result + "; %s was null");
	}
}

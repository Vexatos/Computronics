package pl.asie.computronics.util;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import pl.asie.computronics.Computronics;

import java.util.Arrays;

/**
 * @author asie, Vexatos
 */
public class RecipeUtils {
	public static void addShapedRecipe(ItemStack result, Object... recipe) {
		if(result == null || result.getItem() == null) {
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
			if(o instanceof ItemStack && ((ItemStack) o).getItem() == null) {
				warnCrafting(result, recipe);
				return;
			}
		}
		GameRegistry.addRecipe(new ShapedOreRecipe(result, recipe));
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
			if(o instanceof ItemStack && ((ItemStack) o).getItem() == null) {
				warnCrafting(result, recipe);
				return;
			}
		}
		GameRegistry.addRecipe(new ShapelessOreRecipe(result, recipe));
	}

	private static void warnCrafting(ItemStack result, Object[] recipe) {
		recipe = recipe.clone();
		for(int i = 0; i < recipe.length; i++) {
			if(recipe[i] == null) {
				recipe[i] = "null";
			} else if(recipe[i] instanceof ItemStack && ((ItemStack) recipe[i]).getItem() == null){
				recipe[i] = "null";
			}
		}
		Computronics.log.warn(String.format("Invalid recipe: %s -> %s", Arrays.toString(recipe), result));
		//"Invalid recipe: " + Arrays.toString(recipe) + " -> " + result + "; %s was null");
	}
}

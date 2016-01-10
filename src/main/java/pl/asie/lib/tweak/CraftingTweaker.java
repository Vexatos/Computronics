package pl.asie.lib.tweak;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;
import java.util.List;

public class CraftingTweaker {
	@SuppressWarnings("deprecation")
	public static boolean equal(Object from, Object to, boolean ignoreMeta) {
		if(from == null || to == null) return false;
		else if(from instanceof ItemStack && to instanceof ItemStack) {
			ItemStack ifrom = (ItemStack)from;
			ItemStack ito = (ItemStack)to;
			return ifrom.getItem().equals(ito.getItem()) && (ignoreMeta || ifrom.getItemDamage() == ito.getItemDamage());
		} else if(from instanceof String && to instanceof String) return ((String)from).equalsIgnoreCase((String)to);
		else if(from instanceof ItemStack && to instanceof String) {
			return OreDictionary.getOreID((ItemStack)from) == OreDictionary.getOreID((String)to);
		} else if(from instanceof String && to instanceof ItemStack) {
			return OreDictionary.getOreID((String)from) == OreDictionary.getOreID((ItemStack)to);			
		} else return false;
	}
	
	public static boolean removeOutputRecipe(List list, IRecipe recipe, ItemStack out, boolean ignoreMeta) {
		if(equal(out, recipe.getRecipeOutput(), ignoreMeta)) { list.remove(recipe); return true; }
		else return false;
	}
	
	public static IRecipe replaceInRecipe(List list, IRecipe recipe, Object _from, Object _to, boolean ignoreMeta) {
		boolean changed = false;
		IRecipe newRecipe = null;
		
		ItemStack to = null;
		if(_to instanceof String) to = OreDictionary.getOres((String)_to).get(0);
		else if(_to instanceof ItemStack) to = (ItemStack)_to;
		
		ArrayList<ItemStack> fromList = new ArrayList<ItemStack>();
		if(_from instanceof String) {
			fromList = OreDictionary.getOres((String)_from);
		} else if(_from instanceof ItemStack) {
			fromList.add((ItemStack)_from);
		}
		
		if(recipe instanceof ShapedRecipes && to != null) {
			ShapedRecipes shaped = (ShapedRecipes)recipe;
			
			ItemStack[] input = new ItemStack[shaped.recipeItems.length];
			ItemStack output = shaped.getRecipeOutput();
			for(ItemStack from: fromList) {
				for(int i = 0; i < shaped.recipeItems.length; i++) {
					if(equal(shaped.recipeItems[i], from, ignoreMeta)) {
						input[i] = to;
						changed = true;
					} else input[i] = shaped.recipeItems[i];
				}
				
				if(equal(output, from, ignoreMeta)) { output = to; changed = true; }
			}
			
			if(changed) {
				newRecipe = new ShapedRecipes(shaped.recipeWidth, shaped.recipeHeight, input, output);
			}
		} else if(recipe instanceof ShapelessRecipes && to != null) {
			ShapelessRecipes shapeless = (ShapelessRecipes)recipe;
			ArrayList input = new ArrayList();
			
			ItemStack output = shapeless.getRecipeOutput();
			for(ItemStack from: fromList) {		
				for(Object o: shapeless.recipeItems) {
					if(!(o instanceof ItemStack)) { input.add(o); continue; }
					ItemStack is = (ItemStack)o;
					if(equal(is, from, ignoreMeta)) {
						input.add(to);
						changed = true;
					} else input.add(o);
				}
				
				if(equal(output, from, ignoreMeta)) { output = to; changed = true; }
			}
			
			if(changed) {
				newRecipe = new ShapelessRecipes(output, input);
			}
		} else if(recipe instanceof ShapedOreRecipe) {
			ShapedOreRecipe shaped = (ShapedOreRecipe)recipe;
			
			// Correct input - changing via array change is documented here
			Object[] input = shaped.getInput();
			for(int i = 0; i < input.length; i++) {
				if(_from instanceof String && equal(input[i], (String)_from, ignoreMeta)) input[i] = _to;
				else for(ItemStack is: fromList) {
					if(equal(input[i], is, ignoreMeta)) input[i] = _to;
				}
			}
			
			// Correct output
			ItemStack output = shaped.getRecipeOutput();
			if(_from instanceof String && equal(output, (String)_from, ignoreMeta)) {
				output = (ItemStack)to;
				// Reflection!
				try {
					shaped.getClass().getField("output").set(shaped, output);
				} catch(Exception e) { e.printStackTrace(); }
			}
			
			for(ItemStack from: fromList) {
				if(equal(output, from, ignoreMeta)) {
					output = (ItemStack)to;
					// Reflection!
					try {
						shaped.getClass().getField("output").set(shaped, output);
					} catch(Exception e) { e.printStackTrace(); }
				}
			}
		} else if(recipe instanceof ShapelessOreRecipe) {
			ShapelessOreRecipe shapeless = (ShapelessOreRecipe)recipe;
			ArrayList input = shapeless.getInput();
			
			// Correct input
			for(int i = 0; i < input.size(); i++) {
				Object o = input.get(i);
				if(_from instanceof String && equal(o, (String)_from, ignoreMeta)) {
					input.set(i, _to);
					changed = true;
				} else for(ItemStack is: fromList) {
					if(equal(o, is, ignoreMeta)) {
						input.set(i, _to);
						changed = true;
					}
				}
			}
			
			// Correct output
			ItemStack output = shapeless.getRecipeOutput();
			if(_from instanceof String && equal(output, (String)_from, ignoreMeta)) {
				output = (ItemStack)to; changed = true;
			}
			for(ItemStack from : fromList) {
				if(equal(output, from, ignoreMeta)) {
					output = (ItemStack)to; changed = true;
				}
			}
			
			if(changed) newRecipe = new ShapelessOreRecipe(output, input);
		}
		if(newRecipe != null) {
			list.remove(recipe);
			list.add(newRecipe);
			return newRecipe;
		} else return recipe;
	}
}

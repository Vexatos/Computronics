package appeng.api.features;

import java.util.List;

import net.minecraft.item.ItemStack;

/**
 * Lets you manipulate Grinder Recipes, by adding or editing existing ones.
 */
public interface IGrinderRegistry
{

	/**
	 * Current list of registered recipes, you can modify this if you want too.
	 * 
	 * @return currentlyRegistredRecipes
	 */
	public List<IGrinderEntry> getRecipes();

	/**
	 * add a new recipe the easy way, in -> out, how many turns., duplicates will not be added.
	 * 
	 * @param in
	 * @param out
	 * @param turns
	 */
	public void addRecipe(ItemStack in, ItemStack out, int turns);

	/**
	 * add a new recipe with optional outputs, duplicates will not be added.
	 * 
	 * @param in
	 * @param out
	 * @param optional
	 * @param chance
	 *            ( value 0.0 - 1.0 )
	 * @param turns
	 */
	void addRecipe(ItemStack in, ItemStack out, ItemStack optional, float chance, int turns);

	/**
	 * add a new recipe with optional outputs, duplicates will not be added.
	 * 
	 * @param in
	 * @param out
	 * @param optional
	 * @param chance
	 *            ( value 0.0 - 1.0 )
	 * @param optional2
	 * @param chance2
	 *            ( value 0.0 - 1.0 )
	 * @param turns
	 */
	void addRecipe(ItemStack in, ItemStack out, ItemStack optional, float chance, ItemStack optional2, float chance2, int cost);

	/**
	 * Searches for a recipe for a given input, and returns it.
	 * 
	 * @param input
	 * @return identified recipe, or null
	 */
	public IGrinderEntry getRecipeForInput(ItemStack input);

}

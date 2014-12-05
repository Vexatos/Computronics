package pl.asie.computronics.integration.forestry;

import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IPollinatable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import pl.asie.computronics.util.StringUtil;

/**
 * @author Vexatos
 */
public class FlowerProviderSea implements IFlowerProvider {

	@Override
	public boolean isAcceptedFlower(World world, IIndividual individual, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		return block.getMaterial() == Material.water && meta == 0;
	}

	@Override
	public boolean isAcceptedPollinatable(World world, IPollinatable iPollinatable) {
		return false;
	}

	@Override
	public boolean growFlower(World world, IIndividual iIndividual, int i, int i1, int i2) {
		return false;
	}

	@Override
	public String getDescription() {
		return StringUtil.localize("for.computronics.flowers.sea");
	}

	@Override
	public ItemStack[] affectProducts(World world, IIndividual individual, int x, int y, int z, ItemStack[] products) {
		return products;
	}

	@Override
	public ItemStack[] getItemStacks() {
		return null;
	}
}

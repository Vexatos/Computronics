package pl.asie.computronics.integration.forestry;

import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.apiculture.genetics.JubilanceDefault;
import net.minecraft.block.Block;
import net.minecraft.world.World;

/**
 * This is a merge of JubilanceDefault and JubilanceReqRes
 */
public class JubilanceSea extends JubilanceDefault {

	private final Block blockRequired;
	private final int metaRequired;

	public JubilanceSea(Block blockRequired, int metaRequired) {
		this.blockRequired = blockRequired;
		this.metaRequired = metaRequired;
	}

	@Override
	public boolean isJubilant(IAlleleBeeSpecies species, IBeeGenome genome, IBeeHousing housing) {
		if(!super.isJubilant(species, genome, housing)) {
			return false;
		}

		if(blockRequired == null) {
			return !housing.getWorld().isDaytime() && genome.getFlowerProvider() instanceof FlowerProviderSea;
		}

		World world = housing.getWorld();

		Block block = world.getBlock(housing.getXCoord(), housing.getYCoord() - 1, housing.getZCoord());
		int meta = world.getBlockMetadata(housing.getXCoord(), housing.getYCoord() - 1, housing.getZCoord());
		return block == blockRequired && meta == metaRequired
			&& !world.isDaytime() && genome.getFlowerProvider() instanceof FlowerProviderSea;
	}
}

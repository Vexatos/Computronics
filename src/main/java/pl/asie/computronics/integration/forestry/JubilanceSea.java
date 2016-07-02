package pl.asie.computronics.integration.forestry;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IJubilanceProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

/**
 * This is a merge of JubilanceDefault and JubilanceReqRes
 */
public class JubilanceSea implements IJubilanceProvider {

	private final IJubilanceProvider defaultJubilance;
	private final IJubilanceProvider reqResJubilance;

	public JubilanceSea(IBlockState... states) {
		this.defaultJubilance = BeeManager.jubilanceFactory.getDefault();
		this.reqResJubilance = BeeManager.jubilanceFactory.getRequiresResource(states);
	}

	@Override
	public boolean isJubilant(IAlleleBeeSpecies species, IBeeGenome genome, IBeeHousing housing) {
		if(!defaultJubilance.isJubilant(species, genome, housing)) {
			return false;
		}

		if(!reqResJubilance.isJubilant(species, genome, housing)) {
			return false;
		}

		World world = housing.getWorldObj();
		return !world.isDaytime() && genome.getFlowerProvider() instanceof FlowerProviderSea;
	}
}

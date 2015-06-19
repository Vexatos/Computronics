package pl.asie.computronics.integration.forestry;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IMutationCondition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

/**
 * @author Vexatos
 */
public class MutationConditionStrictBiome implements IMutationCondition {

	private final Type type;

	public MutationConditionStrictBiome(Type type) {
		this.type = type;
	}

	@Override
	public float getChance(World world, int x, int y, int z, IAllele allele0, IAllele allele1, IGenome genome0, IGenome genome1) {
		BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
		Type[] types = BiomeDictionary.getTypesForBiome(biome);
		if(types != null && types.length <= 1 && types[0] == type) {
			return 1;
		}
		return 0;
	}

	@Override
	public String getDescription() {
		String biomeType = type.toString();
		return String.format("Is restricted to %s biomes.", biomeType);
	}
}

package pl.asie.computronics.integration.forestry;

import forestry.api.apiculture.FlowerManager;
import forestry.api.genetics.IFlowerAcceptableRule;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IPollinatable;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import pl.asie.computronics.util.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author Vexatos
 */
public class FlowerProviderSea implements IFlowerProvider, IFlowerAcceptableRule {

	private static List<String> waterTypes = Arrays.asList("water");
	private static final List<String> saltwaterTypes = Arrays.asList("saltwater", "saltWater", "Saltwater", "SaltWater");
	private static boolean hasCheckedSaltwater = false;

	private static void checkSaltwater() {
		if(hasCheckedSaltwater) {
			return;
		}
		for(String saltwaterType : saltwaterTypes) {
			if(FluidRegistry.isFluidRegistered(saltwaterType)) {
				waterTypes = saltwaterTypes;
				break;
			}
		}
		hasCheckedSaltwater = true;
	}

	@Override
	public boolean isAcceptableFlower(String flowerType, World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		if(block != null) {
			Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
			if(fluid != null && FluidRegistry.isFluidRegistered(fluid)) {
				if(!hasCheckedSaltwater) {
					checkSaltwater();
				}
				if(waterTypes.contains(fluid.getName())) {
					if(block instanceof IFluidBlock) {
						return ((IFluidBlock) block).canDrain(world, x, y, z);
					} else {
						return world.getBlockMetadata(x, y, z) == 0;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean isAcceptedPollinatable(World world, IPollinatable iPollinatable) {
		return false;
	}

	@Override
	public String getFlowerType() {
		return "computronics.flowers.sea";
	}

	@Override
	public String getDescription() {
		return StringUtil.localize(getFlowerType());
	}

	@Override
	public ItemStack[] affectProducts(World world, IIndividual individual, int x, int y, int z, ItemStack[] products) {
		return products;
	}

	@Override
	@Deprecated
	@SuppressWarnings("deprecation")
	public Set<forestry.api.genetics.IFlower> getFlowers() {
		return FlowerManager.flowerRegistry.getAcceptableFlowers(getFlowerType());
	}

	@Override
	@Deprecated
	public boolean growFlower(World world, IIndividual iIndividual, int i, int i1, int i2) {
		return false;
	}
}

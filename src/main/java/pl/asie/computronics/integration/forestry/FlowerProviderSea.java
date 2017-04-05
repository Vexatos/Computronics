package pl.asie.computronics.integration.forestry;

import forestry.api.genetics.ICheckPollinatable;
import forestry.api.genetics.IFlowerAcceptableRule;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IIndividual;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import pl.asie.computronics.util.StringUtil;

import java.util.Arrays;
import java.util.List;

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
	public boolean isAcceptableFlower(IBlockState state, World world, BlockPos pos, String flowerType) {
		Fluid fluid = FluidRegistry.lookupFluidForBlock(state.getBlock());
		if(fluid != null && FluidRegistry.isFluidRegistered(fluid)) {
			if(!hasCheckedSaltwater) {
				checkSaltwater();
			}
			if(waterTypes.contains(fluid.getName())) {
				if(state.getBlock() instanceof IFluidBlock) {
					return ((IFluidBlock) state).canDrain(world, pos);
				} else if(state.getBlock() instanceof BlockLiquid) {
					return state.getValue(BlockLiquid.LEVEL) == 0;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isAcceptedPollinatable(World world, ICheckPollinatable iPollinatable) {
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
	public NonNullList<ItemStack> affectProducts(World world, IIndividual individual, BlockPos pos, NonNullList<ItemStack> products) {
		return products;
	}
}

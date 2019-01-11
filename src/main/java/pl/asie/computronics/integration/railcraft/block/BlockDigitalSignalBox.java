package pl.asie.computronics.integration.railcraft.block;

import li.cil.oc.api.network.Environment;
import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.blocks.machine.wayobjects.boxes.BlockMachineSignalBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.integration.railcraft.SignalTypes;
import pl.asie.computronics.oc.block.IComputronicsEnvironmentBlock;
import pl.asie.computronics.oc.manual.IBlockWithPrefix;
import pl.asie.computronics.reference.Mods;

/**
 * @author CovertJaguar, Vexatos
 */
@Optional.InterfaceList({
	@Optional.Interface(iface = "pl.asie.computronics.oc.block.IComputronicsEnvironmentBlock", modid = Mods.OpenComputers)
})
@BlockMeta.Variant(SignalTypes.class)
public class BlockDigitalSignalBox extends BlockMachineSignalBox<SignalTypes> implements IComputronicsEnvironmentBlock, IBlockWithPrefix {

	public BlockDigitalSignalBox() {
		super();
		this.setTranslationKey("computronics.digital_box");
		this.setCreativeTab(Computronics.tab);
	}

	@Override
	public boolean canProvidePower(IBlockState state) {
		return false;
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public boolean shouldCheckWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	//private String documentationName;
	private final String prefix = "railcraft/";

	@Override
	public String getDocumentationName(World world, BlockPos pos) {
		return getVariant(world.getBlockState(pos)).getBaseTag();
	}

	@Override
	public String getDocumentationName(ItemStack stack) {
		return getVariant(getStateFromMeta(stack.getItemDamage())).getBaseTag();
	}

	@Override
	public String getPrefix(World world, BlockPos pos) {
		return this.prefix;
	}

	@Override
	public String getPrefix(ItemStack stack) {
		return this.prefix;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	@SuppressWarnings("unchecked")
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return (Class<? extends Environment>) this.getVariant(getStateFromMeta(meta)).getTileClass();
	}
}

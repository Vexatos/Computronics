package pl.asie.computronics.integration.railcraft.block;

import li.cil.oc.api.network.Environment;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.RailcraftBlockMetadata;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.integration.railcraft.SignalTypes;
import pl.asie.computronics.integration.railcraft.tile.TileDigitalReceiverBox;
import pl.asie.computronics.oc.block.IComputronicsEnvironmentBlock;
import pl.asie.computronics.oc.manual.IBlockWithPrefix;
import pl.asie.computronics.reference.Mods;

/**
 * @author CovertJaguar, Vexatos
 */
@RailcraftBlockMetadata(variant = SignalTypes.class)
public class BlockDigitalReceiverBox extends BlockDigitalBoxBase implements IComputronicsEnvironmentBlock, IBlockWithPrefix {

	public BlockDigitalReceiverBox() {
		super("digital_receiver_box");
		this.setUnlocalizedName("computronics.digitalReceiverBox");
		this.setCreativeTab(Computronics.tab);
	}

	@Override
	public IEnumMachine<SignalTypes> getMachineType(IBlockState state) {
		return SignalTypes.DigitalReceiver;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileDigitalReceiverBox();
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileDigitalReceiverBox.class;
	}
}

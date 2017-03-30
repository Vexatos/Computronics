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
import pl.asie.computronics.integration.railcraft.tile.TileDigitalControllerBox;
import pl.asie.computronics.reference.Mods;

/**
 * @author Vexatos
 */
@RailcraftBlockMetadata(variant = SignalTypes.class)
public class BlockDigitalControllerBox extends BlockDigitalBoxBase {

	public BlockDigitalControllerBox() {
		super("digital_controller_box");
		this.setUnlocalizedName("computronics.digitalControllerBox");
		this.setCreativeTab(Computronics.tab);
	}

	@Override
	public IEnumMachine<SignalTypes> getMachineType(IBlockState state) {
		return SignalTypes.DigitalController;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileDigitalControllerBox();
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileDigitalControllerBox.class;
	}
}

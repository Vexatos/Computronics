package pl.asie.computronics.integration.railcraft.block;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import li.cil.oc.api.network.Environment;
import mods.railcraft.common.blocks.signals.ISignalTileDefinition;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.integration.railcraft.SignalTypes;
import pl.asie.computronics.integration.railcraft.tile.TileDigitalControllerBox;
import pl.asie.computronics.reference.Mods;

/**
 * @author Vexatos
 */
public class BlockDigitalControllerBox extends BlockDigitalBoxBase {

	public BlockDigitalControllerBox() {
		super("digital_controller_box");
		this.setBlockName("computronics.digitalControllerBox");
		this.setCreativeTab(Computronics.tab);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		super.registerBlockIcons(iconRegister);
		texturesBoxTop = iconRegister.registerIcon("computronics:digital_controller_box");
	}

	@Override
	public ISignalTileDefinition getSignalType(int meta) {
		return SignalTypes.DigitalController;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileDigitalControllerBox();
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileDigitalControllerBox.class;
	}
}

package pl.asie.computronics.block;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import li.cil.oc.api.network.Environment;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import pl.asie.computronics.oc.manual.IBlockWithPrefix;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileLocomotiveRelay;

/**
 * @author Vexatos
 */
public class BlockLocomotiveRelay extends BlockPeripheral implements IBlockWithPrefix {
	private IIcon mTop, mSide, mBottom;

	public BlockLocomotiveRelay() {
		super("locomotive_relay");
		this.setIconName("computronics:machine_top");
		this.setBlockName("computronics.locomotiveRelay");
		this.setRotation(Rotation.NONE);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileLocomotiveRelay();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister r) {
		super.registerBlockIcons(r);
		mTop = r.registerIcon("computronics:locorelay_top");
		mSide = r.registerIcon("computronics:machine_side");
		mBottom = r.registerIcon("computronics:machine_bottom");
	}

	@SideOnly(Side.CLIENT)
	public IIcon getAbsoluteIcon(int side, int metadata) {
		switch(side){
			case 0:
				return mBottom;
			case 1:
				return mTop;
			default:
				return mSide;
		}
	}

	@Override
	@Optional.Method(modid= Mods.OpenComputers)
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileLocomotiveRelay.class;
	}

	@Override
	public String getPrefix(World world, int x, int y, int z) {
		return "railcraft/";
	}
}

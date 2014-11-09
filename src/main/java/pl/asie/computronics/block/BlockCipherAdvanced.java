package pl.asie.computronics.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pl.asie.computronics.tile.TileCipherBlockAdvanced;

/**
 * @author Vexatos
 */
public class BlockCipherAdvanced extends BlockMachineSidedIcon {

	private IIcon mFront;

	public BlockCipherAdvanced() {
		super();
		this.setBlockName("computronics.cipher_advanced");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileCipherBlockAdvanced();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getAbsoluteSideIcon(int sideNumber, int metadata) {
		return sideNumber == 2 ? mFront : super.getAbsoluteSideIcon(sideNumber, metadata);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister r) {
		mSide = r.registerIcon("computronics:advanced/machine_side");
		mTop = r.registerIcon("computronics:advanced/machine_top");
		mBottom = r.registerIcon("computronics:advanced/machine_bottom");
		mFront = r.registerIcon("computronics:advanced/cipher_front");
	}
}

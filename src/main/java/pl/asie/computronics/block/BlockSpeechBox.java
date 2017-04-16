package pl.asie.computronics.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import li.cil.oc.api.network.Environment;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.tile.TileSpeechBox;

/**
 * @author Vexatos
 */
public class BlockSpeechBox extends BlockMachineSidedIcon {

	private IIcon mFront;

	public BlockSpeechBox() {
		super("speechbox");
		this.setCreativeTab(Computronics.tab);
		this.setIconName("computronics:speechbox");
		this.setBlockName("computronics.speechBox");
	}

	@Override
	public int getRenderColor(int meta) {
		return 0x00FFFF;
	}

	@Override
	public int colorMultiplier(IBlockAccess blockAccess, int x, int y, int z) {
		return getRenderColor(blockAccess.getBlockMetadata(x, y, z));
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileSpeechBox();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getAbsoluteSideIcon(int sideNumber, int metadata) {
		return sideNumber == 2 ? mFront : super.getAbsoluteSideIcon(sideNumber, metadata);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister r) {
		super.registerBlockIcons(r);
		mFront = r.registerIcon("computronics:speechbox_front");
	}

	@Override
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileSpeechBox.class;
	}
}

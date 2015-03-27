package pl.asie.computronics.audio.tts;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.block.BlockMachineSidedIcon;

/**
 * @author Vexatos
 */
public class BlockTTSBox extends BlockMachineSidedIcon {
	private IIcon mSide;

	public BlockTTSBox() {
		super();
		this.setCreativeTab(Computronics.tab);
		this.setIconName("computronics:chatbox");
		this.setBlockName("computronics.ttsBox");
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
		return new TileTTSBox();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getAbsoluteSideIcon(int sideNumber, int metadata) {
		return mSide;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister r) {
		super.registerBlockIcons(r);
		mSide = r.registerIcon("computronics:chatbox_side");
	}
}

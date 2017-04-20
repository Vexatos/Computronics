package pl.asie.computronics.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import li.cil.oc.api.network.Environment;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.tile.TileSpeechBox;

/**
 * @author Vexatos
 */
public class BlockSpeechBox extends BlockMachineSidedIcon {

	private IIcon mFront;

	public BlockSpeechBox() {
		super("speech_box");
		this.setCreativeTab(Computronics.tab);
		this.setBlockName("computronics.speechBox");
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
		mFront = r.registerIcon("computronics:speech_box_front");
	}

	@Override
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileSpeechBox.class;
	}
}

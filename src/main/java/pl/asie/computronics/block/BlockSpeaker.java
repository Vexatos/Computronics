package pl.asie.computronics.block;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import li.cil.oc.api.network.Environment;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileSpeaker;

public class BlockSpeaker extends BlockMachineSidedIcon {
	private IIcon mFront;

	public BlockSpeaker() {
		super("speaker");
		this.setBlockName("computronics.speaker");
		this.setRotation(Rotation.SIX);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileSpeaker();
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
		mFront = r.registerIcon("computronics:speaker_front");
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileSpeaker.class;
	}
}

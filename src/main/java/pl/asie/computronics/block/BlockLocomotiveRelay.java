package pl.asie.computronics.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import pl.asie.computronics.tile.TileLocomotiveRelay;

/**
 * @author Vexatos
 */
public class BlockLocomotiveRelay extends BlockPeripheral {
	private IIcon mTop, mSide, mBottom;

	public BlockLocomotiveRelay() {
		super();
		this.setIconName("computronics:locorelay");
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
		mTop = r.registerIcon("computronics:locorelay_side");
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
}

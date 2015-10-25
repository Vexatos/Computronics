package pl.asie.computronics.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import pl.asie.computronics.Computronics;
import pl.asie.computronics.client.AudioCableRender;
import pl.asie.computronics.tile.TileAudioCable;
import pl.asie.lib.block.BlockBase;

public class BlockAudioCable extends BlockBase {
	private IIcon mCable;
	private int connectionMask = 0x3f;

	public BlockAudioCable() {
		super(Material.iron, Computronics.instance);
		this.setCreativeTab(Computronics.tab);
		this.setBlockName("computronics.audiocable");
	}

	public void setRenderMask(int m) {
		this.connectionMask = m;
	}

	/* @Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List boxes, Entity entity) {
		boxes.add(AxisAlignedBB.getBoundingBox(x + 0.3125, y + 0.3125, z + 0.3125, x + 1 - 0.3125, y + 1 - 0.3125, z + 1 - 0.3125));
		TileAudioCable tac = (TileAudioCable) world.getTileEntity(x, y, z);
		if (tac != null) {
			for (int i = 0; i < 6; i++) {
				if (tac.connects(ForgeDirection.getOrientation(i))) {
					switch (i) {
						case 0:
							boxes.add(AxisAlignedBB.getBoundingBox(x + 0.3125, y, z + 0.3125, x + 1 - 0.3125, y + 0.3125, z + 1 - 0.3125));
							break;
						case 1:
							boxes.add(AxisAlignedBB.getBoundingBox(x + 0.3125, y + 1 - 0.3125, z + 0.3125, x + 1 - 0.3125, y + 1, z + 1 - 0.3125));
							break;
						case 2:
							boxes.add(AxisAlignedBB.getBoundingBox(x + 0.3125, y + 0.3125, z, x + 1 - 0.3125, y + 1 - 0.3125, z + 0.3125));
							break;
						case 3:
							boxes.add(AxisAlignedBB.getBoundingBox(x + 0.3125, y + 0.3125, z + 1 - 0.3125, x + 1 - 0.3125, y + 1 - 0.3125, z + 1));
							break;
						case 4:
							boxes.add(AxisAlignedBB.getBoundingBox(x, y + 0.3125, z + 0.3125, x + 0.3125, y + 1 - 0.3125, z + 1 - 0.3125));
							break;
						case 5:
							boxes.add(AxisAlignedBB.getBoundingBox(x + 1 - 0.3125, y + 0.3125, z + 0.3125, x + 1, y + 1 - 0.3125, z + 1 - 0.3125));
							break;
					}
				}
			}
		}
	} */

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		return (connectionMask & (1 << side)) != 0;
	}

	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileAudioCable();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getAbsoluteIcon(int sideNumber, int metadata) {
		return mCable;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister r) {
		super.registerBlockIcons(r);
		mCable = r.registerIcon("computronics:audio_cable");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() {
		return AudioCableRender.id();
	}
}

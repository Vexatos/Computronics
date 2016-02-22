package pl.asie.computronics.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.client.AudioCableRender;
import pl.asie.computronics.oc.manual.IBlockWithDocumentation;
import pl.asie.computronics.tile.TileAudioCable;
import pl.asie.lib.block.BlockBase;
import pl.asie.lib.util.ColorUtils;

import java.util.ArrayList;

public class BlockAudioCable extends BlockBase implements IBlockWithDocumentation {
	private IIcon mCable;
	private int connectionMask = 0x3f;
	private int ImmibisMicroblocks_TransformableBlockMarker;

	public BlockAudioCable() {
		super(Material.iron, Computronics.instance);
		this.setCreativeTab(Computronics.tab);
		this.setBlockName("computronics.audiocable");
	}

	public void setRenderMask(int m) {
		this.connectionMask = m;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int a, float _x, float _y, float _z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileAudioCable) {
			ColorUtils.Color color = ColorUtils.getColor(player.getHeldItem());
			if(color != null) {
				((TileAudioCable) tile).setColor(color.color);
				world.markBlockForUpdate(x, y, z);
				return true;
			}
		}
		return super.onBlockActivated(world, x, y, z, player, a, _x, _y, _z);
	}

	@Override
	public int getRenderColor(int meta) {
		return ColorUtils.Color.LightGray.color;
	}

	@Override
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileAudioCable) {
			return ((TileAudioCable) tile).getColor();
		}
		return getRenderColor(world.getBlockMetadata(x, y, z));
	}

	@Override
	public boolean recolourBlock(World world, int x, int y, int z, ForgeDirection side, int colour) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileAudioCable) {
			((TileAudioCable) tile).setColor(ColorUtils.fromWoolMeta(colour).color);
			world.markBlockForUpdate(x, y, z);
			return true;
		}
		return super.recolourBlock(world, x, y, z, side, colour);
	}

	// Collision box magic

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		doSetBlockBoundsBasedOnState(world, x, y, z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		//doSetBlockBoundsBasedOnState(world, x, y, z);
		return super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public final void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		synchronized(this) {
			this.doSetBlockBoundsBasedOnState(world, x, y, z);
		}
	}

	/**
	 * @author Sangar, Vexatos
	 */
	private static class BoundingBox {
		private static AxisAlignedBB[] bounds;
		private static final AxisAlignedBB DEFAULT_BOX = AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, 1);

		private static AxisAlignedBB getBox(int msk) {
			if(bounds == null) {
				setupBounds();
			}
			if(msk < 0 || msk >= bounds.length || bounds[msk] == null) {
				return DEFAULT_BOX.copy();
			}
			return bounds[msk].copy();
		}

		private static void setupBounds() {
			ArrayList<AxisAlignedBB> newbounds = new ArrayList<AxisAlignedBB>(0xFF >> 2);
			for(int mask = 0; mask <= 0xFF >> 2; ++mask) {
				AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(-0.195, -0.195, -0.195, 0.195, 0.195, 0.195);
				for(ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
					if((side.flag & mask) != 0) {
						if(side.offsetX < 0) {
							bounds.minX += side.offsetX * 0.375;
						} else {
							bounds.maxX += side.offsetX * 0.375;
						}
						if(side.offsetY < 0) {
							bounds.minY += side.offsetY * 0.375;
						} else {
							bounds.maxY += side.offsetY * 0.375;
						}
						if(side.offsetZ < 0) {
							bounds.minZ += side.offsetZ * 0.375;
						} else {
							bounds.maxZ += side.offsetZ * 0.375;
						}
					}
				}
				bounds.setBounds(
					clamp(bounds.minX + 0.5), clamp(bounds.minY + 0.5), clamp(bounds.minZ + 0.5),
					clamp(bounds.maxX + 0.5), clamp(bounds.maxY + 0.5), clamp(bounds.maxZ + 0.5));
				newbounds.add(bounds);
			}
			bounds = newbounds.toArray(new AxisAlignedBB[newbounds.size()]);
		}

		private static double clamp(double val) {
			return Math.min(Math.max(val, 0D), 1D);
		}
	}

	protected void doSetBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		setBlockBounds(BoundingBox.getBox(neighbors(world, x, y, z)));
	}

	private int neighbors(IBlockAccess world, int x, int y, int z) {
		int result = 0;
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileAudioCable) {
			for(ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
				if(((TileAudioCable) tile).connectsAudio(side)) {
					result |= side.flag;
				}
			}
		}
		return result;
	}

	protected void setBlockBounds(AxisAlignedBB bounds) {
		setBlockBounds((float) bounds.minX, (float) bounds.minY, (float) bounds.minZ, (float) bounds.maxX, (float) bounds.maxY, (float) bounds.maxZ);
	}

	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end) {
		synchronized(this) {
			return super.collisionRayTrace(world, x, y, z, start, end);
		}
	}

	/*@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List boxes, Entity entity) {
		boxes.add(AxisAlignedBB.getBoundingBox(x + 0.3125, y + 0.3125, z + 0.3125, x + 1 - 0.3125, y + 1 - 0.3125, z + 1 - 0.3125));
		TileAudioCable tac = (TileAudioCable) world.getTileEntity(x, y, z);
		if(tac != null) {
			for(int i = 0; i < 6; i++) {
				if(tac.connects(ForgeDirection.getOrientation(i))) {
					switch(i) {
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
	}*/

	// End of collision box magic

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

	protected String documentationName = "audio_cable";

	@Override
	public String getDocumentationName(World world, int x, int y, int z) {
		return this.documentationName;
	}

	@Override
	public String getDocumentationName(ItemStack stack) {
		return this.documentationName;
	}
}

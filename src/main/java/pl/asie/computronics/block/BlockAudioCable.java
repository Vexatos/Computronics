package pl.asie.computronics.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.client.AudioCableRender;
import pl.asie.computronics.oc.manual.IBlockWithDocumentation;
import pl.asie.computronics.tile.TileAudioCable;
import pl.asie.computronics.util.ColorUtils;
import pl.asie.computronics.util.internal.IColorable;
import pl.asie.lib.block.BlockBase;

public class BlockAudioCable extends BlockBase implements IBlockWithDocumentation {

	private int connectionMask = 0x3f;
	private int ImmibisMicroblocks_TransformableBlockMarker;

	public BlockAudioCable() {
		super(Material.iron, Computronics.instance, Rotation.NONE);
		this.setCreativeTab(Computronics.tab);
		//this.setBlockName("computronics.audiocable");
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
	public int getRenderColor(IBlockState state) {
		return ColorUtils.Color.LightGray.color;
	}

	@Override
	public int colorMultiplier(IBlockAccess world, BlockPos pos, int renderPass) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof IColorable) {
			return ((IColorable) tile).getColor();
		}
		return getRenderColor(world.getBlockState(pos));
	}

	@Override
	public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, EnumDyeColor color) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof IColorable && ((IColorable) tile).canBeColored()) {
			((IColorable) tile).setColor(ColorUtils.fromColor(color).color);
			world.markBlockForUpdate(pos);
			return true;
		}
		return super.recolorBlock(world, pos, side, color);
	}

	// Collision box magic

	@Override
	public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
		setBlockBoundsBasedOnState(world, pos);
		return super.getCollisionBoundingBox(world, pos, state);
	}

	@Override
	public final void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
		setBlockBounds(BoundingBox.getBox(neighbors(world, pos)));
	}

	/**
	 * @author Sangar, Vexatos, asie
	 */
	@SuppressWarnings("PointlessBitwiseExpression")
	private static class BoundingBox {

		private static final AxisAlignedBB[] bounds = new AxisAlignedBB[0x40];

		static {
			for(int mask = 0; mask < 0x40; ++mask) {
				bounds[mask] = AxisAlignedBB.fromBounds(
					((mask & (1 << 4)) != 0 ? 0 : 0.375),
					((mask & (1 << 0)) != 0 ? 0 : 0.375),
					((mask & (1 << 2)) != 0 ? 0 : 0.375),
					((mask & (1 << 5)) != 0 ? 1 : 0.375),
					((mask & (1 << 1)) != 0 ? 1 : 0.375),
					((mask & (1 << 3)) != 0 ? 1 : 0.375)
				);
			}
		}

		private static AxisAlignedBB getBox(int msk) {
			return bounds[msk];
		}
	}

	private int neighbors(IBlockAccess world, BlockPos pos) {
		int result = 0;
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileAudioCable) {
			for(EnumFacing side : EnumFacing.VALUES) {
				if(((TileAudioCable) tile).connectsAudio(side)) {
					result |= 1 << side.ordinal();
				}
			}
		}
		return result;
	}

	protected void setBlockBounds(AxisAlignedBB bounds) {
		setBlockBounds((float) bounds.minX, (float) bounds.minY, (float) bounds.minZ, (float) bounds.maxX, (float) bounds.maxY, (float) bounds.maxZ);
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

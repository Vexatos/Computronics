package pl.asie.computronics.integration.railcraft.block;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import li.cil.oc.api.network.Environment;
import mods.railcraft.client.util.textures.TextureAtlasSheet;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.block.BlockPeripheral;
import pl.asie.computronics.integration.railcraft.tile.TileDigitalDetector;
import pl.asie.computronics.oc.manual.IBlockWithPrefix;
import pl.asie.computronics.reference.Mods;

/**
 * @author CovertJaguar, Vexatos, marcin212, Kubuxu
 */
public class BlockDigitalDetector extends BlockPeripheral implements IBlockWithPrefix {

	public BlockDigitalDetector() {
		super("digital_detector");
		this.setBlockName("computronics.detector");
		this.setRotation(Rotation.NONE);
		this.setResistance(4.5F);
		this.setHardness(2.0F);
		this.setStepSound(soundTypeStone);
		this.setCreativeTab(Computronics.tab);
		this.setHarvestLevel("pickaxe", 2);
		this.setHarvestLevel("crowbar", 0);
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int i, int j, int k, ForgeDirection side) {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileDigitalDetector();
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entityliving, ItemStack stack) {
		TileEntity tile = world.getTileEntity(i, j, k);
		if((tile instanceof TileDigitalDetector)) {
			((TileDigitalDetector) tile).direction = MiscTools.getSideClosestToPlayer(world, i, j, k, entityliving);
			world.notifyBlocksOfNeighborChange(i, j, k, this);
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float u1, float u2, float u3) {
		return false;
	}

	@Override
	public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if((tile instanceof TileDigitalDetector)) {
			TileDigitalDetector detector = (TileDigitalDetector) tile;
			if(detector.direction == axis) {
				detector.direction = axis.getOpposite();
			} else {
				detector.direction = axis;
			}
			world.notifyBlocksOfNeighborChange(x, y, z, this);
			world.markBlockForUpdate(x, y, z);
			return true;
		}
		return false;
	}

	@Override
	public ForgeDirection[] getValidRotations(World worldObj, int x, int y, int z) {
		return ForgeDirection.VALID_DIRECTIONS;
	}

	private IIcon mSide, mConnect;

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		IIcon[] icons = TextureAtlasSheet.unstitchIcons(iconRegister, "computronics:digital_detector", 2);
		mSide = icons[0];
		mConnect = icons[1];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if((tile instanceof TileDigitalDetector)) {
			TileDigitalDetector detectorTile = (TileDigitalDetector) tile;
			if(detectorTile.direction.ordinal() == side) {
				return mConnect;
			}
			return mSide;
		}
		return null;
	}

	@Override
	public int getFrontSide(int m) {
		return ForgeDirection.NORTH.ordinal();
	}

	@Override
	public int relToAbs(int side, int metadata) {
		return side;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getAbsoluteIcon(int side, int metadata) {
		return getIcon(side, metadata);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getAbsoluteIcon(World world, int x, int y, int z, int side, int metadata) {
		return getIcon(world, x, y, z, side);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return side == ForgeDirection.NORTH.ordinal() ? mConnect : mSide;
	}

	@Override
	public void onBlockAdded(World world, int i, int j, int k) {
		super.onBlockAdded(world, i, j, k);
		world.markBlockForUpdate(i, j, k);
		if(world.isRemote) {
			return;
		}
		world.notifyBlocksOfNeighborChange(i + 1, j, k, this);
		world.notifyBlocksOfNeighborChange(i - 1, j, k, this);
		world.notifyBlocksOfNeighborChange(i, j, k + 1, this);
		world.notifyBlocksOfNeighborChange(i, j, k - 1, this);
		world.notifyBlocksOfNeighborChange(i, j - 1, k, this);
		world.notifyBlocksOfNeighborChange(i, j + 1, k, this);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int metadata) {
		super.breakBlock(world, x, y, z, this, metadata);
		if(world.isRemote) {
			return;
		}
		world.notifyBlocksOfNeighborChange(x + 1, y, z, this);
		world.notifyBlocksOfNeighborChange(x - 1, y, z, this);
		world.notifyBlocksOfNeighborChange(x, y, z + 1, this);
		world.notifyBlocksOfNeighborChange(x, y, z - 1, this);
		world.notifyBlocksOfNeighborChange(x, y - 1, z, this);
		world.notifyBlocksOfNeighborChange(x, y + 1, z, this);
	}

	@Override
	public boolean canConnectRedstone(IBlockAccess world, int i, int j, int k, int dir) {
		return false;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileDigitalDetector.class;
	}

	private static final String prefix = "railcraft/";

	@Override
	public String getPrefix(World world, int x, int y, int z) {
		return prefix;
	}

	@Override
	public String getPrefix(ItemStack stack) {
		return prefix;
	}
}

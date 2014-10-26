package pl.asie.computronics.block;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import li.cil.oc.api.network.Environment;
import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.client.util.textures.TextureAtlasSheet;
import mods.railcraft.common.blocks.signals.ISignalTile;
import mods.railcraft.common.blocks.signals.MaterialStructure;
import mods.railcraft.common.items.IActivationBlockingItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.tile.TileDigitalReceiverBox;

import java.util.List;

/**
 * @author CovertJaguar, Vexatos
 */
public class BlockDigitalReceiverBox extends BlockContainer
	implements IPostConnection {

	public static IIcon[] texturesBox;
	public static IIcon texturesBoxTop;
	private final int renderType;

	public BlockDigitalReceiverBox() {
		super(new MaterialStructure());
		this.renderType = RenderingRegistry.getNextAvailableRenderId();
		setBlockName("computronics.signalBox");
		setStepSound(Block.soundTypeMetal);
		setResistance(50.0F);
		setCreativeTab(Computronics.tab);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		list.add(new ItemStack(item, 1, 0));
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		texturesBox = TextureAtlasSheet.unstitchIcons(iconRegister, "railcraft:signal.box", 6);
		texturesBoxTop = iconRegister.registerIcon("computronics:signal_box_receiver");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		switch(side){
			case 0:
				return texturesBox[2];
			case 1:
				return texturesBoxTop;
			default:
				return texturesBox[0];
		}
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int side, float u1, float u2, float u3) {
		ItemStack current = player.getCurrentEquippedItem();
		if((current != null) &&
			((current.getItem() instanceof IActivationBlockingItem))) {
			return false;
		}
		TileEntity tile = world.getTileEntity(i, j, k);
		return (tile instanceof TileDigitalReceiverBox)
			&& ((TileDigitalReceiverBox) tile).blockActivated(side, player);
	}

	@Override
	public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
		TileEntity tile = world.getTileEntity(x, y, z);
		return (tile instanceof TileDigitalReceiverBox)
			&& ((TileDigitalReceiverBox) tile).rotateBlock(axis);
	}

	@Override
	public ForgeDirection[] getValidRotations(World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if((tile instanceof TileDigitalReceiverBox)) {
			return ((TileDigitalReceiverBox) tile).getValidRotations();
		}
		return super.getValidRotations(world, x, y, z);
	}

	@Override
	public void onPostBlockPlaced(World world, int i, int j, int k, int meta) {
		super.onPostBlockPlaced(world, i, j, k, meta);
		TileEntity tile = world.getTileEntity(i, j, k);
		if((tile instanceof TileDigitalReceiverBox)) {
			((TileDigitalReceiverBox) tile).onBlockPlaced();
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entityliving, ItemStack stack) {
		TileEntity tile = world.getTileEntity(i, j, k);
		if((tile instanceof TileDigitalReceiverBox)) {
			((TileDigitalReceiverBox) tile).onBlockPlacedBy(entityliving);
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		try {
			TileEntity tile = world.getTileEntity(x, y, z);
			if((tile instanceof TileDigitalReceiverBox)) {
				TileDigitalReceiverBox structure = (TileDigitalReceiverBox) tile;
				if((structure.needsSupport())
					&& (!world.isSideSolid(x, y - 1, z, ForgeDirection.UP))
					&& !(world.getTileEntity(x, y - 1, z) instanceof Environment)) {
					world.func_147480_a(x, y, z, true);
				} else {
					structure.onNeighborBlockChange(block);
				}
			}
		} catch(StackOverflowError error) {
			Computronics.log.error("Error in BlockSignal.onNeighborBlockChange()");
			throw error;
		}
	}

	@Override
	public void breakBlock(World world, int i, int j, int k, Block block, int meta) {
		TileEntity tile = world.getTileEntity(i, j, k);
		if((tile instanceof TileDigitalReceiverBox)) {
			((TileDigitalReceiverBox) tile).onBlockRemoval();
		}
		super.breakBlock(world, i, j, k, block, meta);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int i, int j, int k) {
		TileEntity tile = world.getTileEntity(i, j, k);
		if((tile instanceof TileDigitalReceiverBox)) {
			((TileDigitalReceiverBox) tile).setBlockBoundsBasedOnState(world, i, j, k);
		} else {
			setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
		TileEntity tile = world.getTileEntity(i, j, k);
		if((tile instanceof TileDigitalReceiverBox)) {
			return ((TileDigitalReceiverBox) tile).getCollisionBoundingBoxFromPool(world, i, j, k);
		}
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		return super.getCollisionBoundingBoxFromPool(world, i, j, k);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if((tile instanceof TileDigitalReceiverBox)) {
			return ((TileDigitalReceiverBox) tile).getSelectedBoundingBoxFromPool(world, x, y, z);
		}
		return AxisAlignedBB.getBoundingBox(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX, y + this.maxY, z + this.maxZ);
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		if(y < 0) {
			return 0;
		}
		TileEntity tile = world.getTileEntity(x, y, z);
		if((tile instanceof ISignalTile)) {
			return ((ISignalTile) tile).getLightValue();
		}
		return 0;
	}

	@Override
	public float getBlockHardness(World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if((tile instanceof TileDigitalReceiverBox)) {
			return ((TileDigitalReceiverBox) tile).getHardness();
		}
		return 3.0F;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int i, int j, int k, ForgeDirection side) {
		TileEntity tile = world.getTileEntity(i, j, k);
		return (tile instanceof TileDigitalReceiverBox)
			&& ((TileDigitalReceiverBox) tile).isSideSolid(world, i, j, k, side);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return this.renderType;
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int meta) {
		return new TileDigitalReceiverBox();
	}

	@Override
	public boolean canProvidePower() {
		return false;
	}

	@Override
	public boolean canConnectRedstone(IBlockAccess world, int i, int j, int k, int dir) {
		return false;
	}

	@Override
	public boolean isBlockNormalCube() {
		return false;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		return true;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, int i, int j, int k, int side) {
		return 0;
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

	@Override
	public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
		return false;
	}

	@Override
	public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
		return false;
	}

	@Override
	public IPostConnection.ConnectStyle connectsToPost(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		TileEntity t = world.getTileEntity(x, y, z);
		if((t instanceof ISignalTile)) {
			return IPostConnection.ConnectStyle.TWO_THIN;
		}
		return IPostConnection.ConnectStyle.NONE;
	}
}

package pl.asie.lib.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pl.asie.lib.gui.managed.IGuiProvider;
import pl.asie.lib.integration.Integration;
import pl.asie.lib.reference.Mods;
import pl.asie.lib.tile.TileEntityBase;
import pl.asie.lib.util.ItemUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;

/*@Optional.InterfaceList({
	@Optional.Interface(iface = "gregtech.api.interfaces.IDebugableBlock", modid = Mods.GregTech),
	@Optional.Interface(iface = "cofh.api.block.IBlockInfo", modid = Mods.API.CoFHBlocks)
})*/
public abstract class BlockBase extends Block /*implements
	IBlockInfo, IDebugableBlock */ {

	public static final PropertyBool BUNDLED = PropertyBool.create("bundled");

	public enum Rotation {
		NONE(PropertyDirection.create("facing", Collections.singleton(EnumFacing.NORTH))),
		FOUR(PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL)),
		SIX(PropertyDirection.create("facing"));

		public final PropertyDirection FACING;

		Rotation(PropertyDirection facing) {
			FACING = facing;
		}
	}

	//private Rotation rotation = Rotation.NONE;
	public final Rotation rotation;
	private final Object parent;
	private int gui = -1;
	protected IGuiProvider guiProvider;
	protected final BlockStateContainer blockState;

	public BlockBase(Material material, Object parent, Rotation rotation) {
		super(material);
		this.setCreativeTab(CreativeTabs.MISC);
		this.rotation = rotation;
		this.setHardness(2.0F);
		this.parent = parent;
		this.blockState = this.createActualBlockState();
		this.setDefaultState(this.createDefaultState());
	}

	protected IBlockState createDefaultState() {
		IBlockState state = this.blockState.getBaseState();
		if(rotation != Rotation.NONE) {
			state = state.withProperty(rotation.FACING, EnumFacing.NORTH);
		}
		return state;
	}

	public boolean supportsBundledRedstone() {
		return false;
	}

	@Override
	public BlockStateContainer getBlockState() {
		return this.blockState;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this);
	}

	protected BlockStateContainer createActualBlockState() {
		final ArrayList<IProperty> properties = new ArrayList<IProperty>();
		if(rotation != Rotation.NONE) {
			properties.add(rotation.FACING);
		}
		if(this.supportsBundledRedstone()) {
			properties.add(BUNDLED);
		}
		return new BlockStateContainer(this, properties.toArray(new IProperty[properties.size()]));
	}

	@Override
	@Deprecated
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		IBlockState actualState = super.getActualState(state, world, pos);
		if(this.supportsBundledRedstone()) {
			actualState = actualState.withProperty(BUNDLED, Mods.hasBundledRedstoneMod());
		}
		return actualState;
	}

	@Override
	@Deprecated
	public IBlockState getStateFromMeta(int meta) {
		IBlockState state = this.getDefaultState();
		switch(rotation) {
			case FOUR:
				return state.withProperty(rotation.FACING, EnumFacing.byHorizontalIndex(meta));
			case SIX:
				return state.withProperty(rotation.FACING, EnumFacing.byIndex(meta));
		}
		return state;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		switch(rotation) {
			case FOUR:
				return state.getValue(rotation.FACING).getHorizontalIndex();
			case SIX:
				return state.getValue(rotation.FACING).getIndex();
		}
		return 0;
	}

	// Handler: Redstone

	public boolean emitsRedstone(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	public boolean receivesRedstone(IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	@Deprecated
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	// Vanilla
	public int getVanillaRedstoneValue(World world, BlockPos pos) {
		return world.getRedstonePower(pos, getFacingDirection(world, pos));
	}

	@Override
	@Deprecated
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos otherPos) {
		if(receivesRedstone(world, pos)) {
			TileEntity tile = world.getTileEntity(pos);
			if(tile != null && tile instanceof TileEntityBase) {
				int newSignal = getVanillaRedstoneValue(world, pos);
				int oldSignal = ((TileEntityBase) tile).getOldRedstoneSignal();
				if(newSignal == oldSignal) {
					return;
				}
				((TileEntityBase) tile).setRedstoneSignal(newSignal);
			}
		}
		/*if(Mods.isLoaded(Mods.ProjectRed)) {
			TileEntity tile = world.getTileEntity(pos);
			if(tile != null && tile instanceof TileMachine) {
				((TileMachine) tile).onProjectRedBundledInputChanged();
			}
		}*/
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return (emitsRedstone(world, pos, side) || receivesRedstone(world, pos));
	}

	@Override
	@Deprecated
	public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		if(!emitsRedstone(world, pos, side)) {
			return 0;
		}
		TileEntity te = world.getTileEntity(pos);
		if(te != null && te instanceof TileEntityBase) {
			return ((TileEntityBase) te).requestCurrentRedstoneValue(side);
		}
		return 0;
	}

	@Override
	public abstract boolean hasTileEntity(IBlockState state);

	@Override
	public abstract TileEntity createTileEntity(World world, IBlockState state);

	/*public int getFrontSide(int m) {
		switch(this.rotation) {
			case FOUR:
				return (m & 3) + 2;
			case SIX:
				return (m & 7) % 6;
			case NONE:
			default:
				return 2;
		}
	}*/

	/*public int relToAbs(int side, int metadata) {
		int frontSide = getFrontSide(metadata);
		return MiscUtils.getAbsoluteSide(side, frontSide);
	}*/

	public EnumFacing getFacingDirection(World world, BlockPos pos) {
		return world.getBlockState(pos).getValue(rotation.FACING);
	}

	public EnumFacing getFacingDirection(IBlockState state) {
		return state.getValue(rotation.FACING);
	}

	/*private void setDefaultRotation(World world, BlockPos pos, IBlockState state) {
		if(!world.isRemote && this.rotation != Rotation.NONE) {
			Block block = world.getBlockState(pos.offset(EnumFacing.NORTH)).getBlock();
			Block block1 = world.getBlockState(pos.offset(EnumFacing.SOUTH)).getBlock();
			Block block2 = world.getBlockState(pos.offset(EnumFacing.WEST)).getBlock();
			Block block3 = world.getBlockState(pos.offset(EnumFacing.EAST)).getBlock();
			//int m = world.getBlockMetadata(x, y, z);
			byte b0 = 3;

			if(block.isFullBlock() && !block1.isFullBlock()) {
				b0 = 3;
			} else if(block1.isFullBlock() && !block.isFullBlock()) {
				b0 = 2;
			}

			if(block2.isFullBlock() && !block3.isFullBlock()) {
				b0 = 5;
			} else if(block3.isFullBlock() && !block2.isFullBlock()) {
				b0 = 4;
			}

			if(this.rotation == Rotation.SIX && pos.getY() > 0 && pos.getY() < 255) {
				Block block4 = world.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock();
				Block block5 = world.getBlockState(pos.offset(EnumFacing.UP)).getBlock();

				if(block4.isFullBlock() && !block5.isFullBlock()) {
					b0 = 1;
				} else if(block5.isFullBlock() && !block4.isFullBlock()) {
					b0 = 0;
				}
			} else {
				b0 -= 2;
			}

			state = getStateFromMeta(getMetaFromState(state) | b0);
			world.setBlockState(pos, state, 2);
			//world.setBlockMetadataWithNotify(x, y, z, m | b0, 2);
		}
	}*/

	//private static final int[] ROT_TRANSFORM4 = { 2, 5, 3, 4 };

	@Nullable
	private EnumFacing determineRotation(World world, BlockPos pos, EntityLivingBase entity) {
		if(this.rotation == Rotation.NONE) {
			return null;
		}

		if(this.rotation == Rotation.SIX) {
			if(MathHelper.abs((float) entity.posX - (float) pos.getX()) < 2.0F && MathHelper.abs((float) entity.posZ - (float) pos.getZ()) < 2.0F) {
				double d0 = entity.posY + entity.getEyeHeight();

				if(d0 - (double) pos.getY() > 2.0D) {
					return EnumFacing.UP;
				}
				if((double) pos.getY() - d0 > 0.0D) {
					return EnumFacing.DOWN;
				}
			}
		}
		return entity.getHorizontalFacing().getOpposite();
		/*int l = MathHelper.floor((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		return ROT_TRANSFORM4[l];*/
	}

	/*@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
		//int m = stack.getItemDamage();
		/*if(this.rotation == Rotation.SIX) {
			state = state.withProperty(rotation.FACING,rot );
			world.setBlockState(pos, state);
			//world.setBlockMetadataWithNotify(x, y, z, (m & (~7)) | rot, 2);
		} else if(this.rotation == Rotation.FOUR) {
			//world.setBlockMetadataWithNotify(x, y, z, (m & (~3)) | (rot - 2), 2);
		}* /
		if(this.rotation != Rotation.NONE) {
			EnumFacing rot = determineRotation(world, pos, entity, stack);
			state = state.withProperty(rotation.FACING, rot);
			world.setBlockState(pos, state);
		}
	}*/

	@Deprecated
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		if(this.rotation != Rotation.NONE) {
			EnumFacing rot = determineRotation(world, pos, placer);
			return getDefaultState().withProperty(rotation.FACING, rot);
		}
		return super.getStateForPlacement(world, pos, side, hitX, hitY, hitZ, meta, placer, hand);
	}

	/*@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		super.onBlockAdded(world, pos, state);
		//this.setDefaultRotation(world, pos, state);
	}*/

	// GUI handling

	public Object getOwner() {
		return parent;
	}

	public boolean hasGui(World world, BlockPos pos, EntityPlayer player, EnumFacing side) {
		if(guiProvider != null) {
			this.gui = guiProvider.getGuiID();
		}
		return guiProvider != null && gui >= 0;
	}

	@Nullable
	public IGuiProvider getGuiProvider(World world, BlockPos pos, EntityPlayer player, EnumFacing side) {
		return guiProvider;
	}

	public void setGuiProvider(IGuiProvider provider) {
		this.guiProvider = provider;
		this.gui = guiProvider.getGuiID();
	}

	protected boolean rotate(World world, BlockPos pos, EntityPlayer player, EnumFacing side) {
		return !player.isSneaking() && rotateBlock(world, pos, side);
	}

	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing side) {
		if(rotation == Rotation.NONE) {
			return false;
		}
		IBlockState state = world.getBlockState(pos);

		EnumFacing f = state.getValue(rotation.FACING);
		if(side == f && isValidFacing(world, pos, f.getOpposite())) {
			world.setBlockState(pos, state.withProperty(rotation.FACING, f.getOpposite()), 3);
			return true;
		} else if(isValidFacing(world, pos, side)) {
			world.setBlockState(pos, state.withProperty(rotation.FACING, side), 3);
			return true;
		} else if(rotation == Rotation.FOUR) {
			world.setBlockState(pos, state.withProperty(rotation.FACING, f.rotateY()), 3);
			return true;
		}
		return false;
	}

	protected boolean isValidFacing(World world, BlockPos pos, EnumFacing f) {
		return rotation.FACING.getAllowedValues().contains(f);
	}

	protected boolean onToolUsed(World world, BlockPos pos, EntityPlayer player, EnumFacing side) {
		return false;
	}

	protected boolean useTool(World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side) {
		ItemStack held = player.inventory.getCurrentItem();
		if(!held.isEmpty() && Integration.isTool(held, player, hand, pos) && this.rotation != null) {
			boolean wrenched = Integration.useTool(held, player, hand, pos);
			return wrenched && (this.onToolUsed(world, pos, player, side) || this.rotate(world, pos, player, side));
		}
		return false;
	}

	protected boolean canUseTool(World world, BlockPos pos, EntityPlayer player, EnumFacing side) {
		return this.rotation != Rotation.NONE;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(!world.isRemote) {
			if(!this.canUseTool(world, pos, player, side) || !this.useTool(world, pos, player, hand, side)) {
				IGuiProvider guiProvider = getGuiProvider(world, pos, player, side);
				if(guiProvider != null) {
					if(guiProvider.canOpen(world, pos.getX(), pos.getY(), pos.getZ(), player, side) && this.onOpenGui(world, pos, player, side)) {
						player.openGui(this.parent, guiProvider.getGuiID(), world, pos.getX(), pos.getY(), pos.getZ());
						return true;
					} else {
						this.onOpenGuiDenied(world, pos, player, side);
					}
				}
			}
			return false;
		}
		return true;
	}

	protected boolean onOpenGui(World world, BlockPos pos, EntityPlayer player, EnumFacing side) {
		return true;
	}

	protected void onOpenGuiDenied(World world, BlockPos pos, EntityPlayer player, EnumFacing side) {
	}

	/*// Simple textures

	private String iconName = null;

	public void setIconName(String name) {
		iconName = name;
	}*/

	// Block destroy unified handler and whatnot.
	public void onBlockDestroyed(World world, BlockPos pos, IBlockState state) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if(tileEntity != null) {
			if(tileEntity instanceof TileEntityBase) {
				((TileEntityBase) tileEntity).onBlockDestroy();
			}
			if(tileEntity instanceof IInventory && !world.isRemote) {
				ItemUtils.dropItems(world, pos, (IInventory) tileEntity);
			}
			tileEntity.invalidate();
		}
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		super.onBlockHarvested(world, pos, state, player);
		this.onBlockDestroyed(world, pos, state);
	}

	@Override
	public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
		super.onBlockExploded(world, pos, explosion);
		this.onBlockDestroyed(world, pos, getStateFromMeta(0));
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		this.onBlockDestroyed(world, pos, state);
		super.breakBlock(world, pos, state);
	}

    /* IInformationProvider boilerplate code */
/*
	@Override
	@Optional.Method(modid = Mods.GregTech)
	public ArrayList<String> getDebugInfo(EntityPlayer aPlayer, int aX, int aY,
		int aZ, int aLogLevel) {
		TileEntity te = aPlayer.world.getTileEntity(aX, aY, aZ);
		ArrayList<String> data = new ArrayList<String>();
		if(te instanceof IInformationProvider) {
			((IInformationProvider) te).getInformation(aPlayer, ForgeDirection.UNKNOWN, data, true);
		}
		return data;
	}

	@Override
	@Optional.Method(modid = Mods.API.CoFHBlocks)
	public void getBlockInfo(IBlockAccess world, int x, int y, int z,
		ForgeDirection side, EntityPlayer player,
		List<IChatComponent> info, boolean debug) {
		TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof IInformationProvider) {
			ArrayList<String> data = new ArrayList<String>();
			((IInformationProvider) te).getInformation(player, side, data, true);
			for(String s : data) {
				info.add(new ChatComponentText(s));
			}
		}
	}*/
}

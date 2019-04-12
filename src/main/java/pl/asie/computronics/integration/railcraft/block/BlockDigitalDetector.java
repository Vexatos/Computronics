package pl.asie.computronics.integration.railcraft.block;

import li.cil.oc.api.network.Environment;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.block.BlockPeripheral;
import pl.asie.computronics.integration.railcraft.tile.TileDigitalDetector;
import pl.asie.computronics.oc.manual.IBlockWithPrefix;
import pl.asie.computronics.reference.Mods;

import static pl.asie.lib.util.WorldUtils.notifyBlockUpdate;

/**
 * @author CovertJaguar, Vexatos, marcin212, Kubuxu
 */
public class BlockDigitalDetector extends BlockPeripheral implements IBlockWithPrefix {

	public static final PropertyEnum<EnumFacing> FRONT = PropertyEnum.create("front", EnumFacing.class);

	public BlockDigitalDetector() {
		super("digital_detector", Rotation.NONE);
		this.setTranslationKey("computronics.detector");
		this.setResistance(4.5F);
		this.setHardness(2.0F);
		this.setSoundType(SoundType.STONE);
		this.setCreativeTab(Computronics.tab);
		this.setHarvestLevel("pickaxe", 2);
		this.setHarvestLevel("crowbar", 0);
	}

	@Deprecated
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		state = super.getActualState(state, world, pos);
		TileEntity t = world.getTileEntity(pos);

		if(t instanceof TileDigitalDetector) {
			return state.withProperty(FRONT, ((TileDigitalDetector) t).direction);
		}
		return state;
	}

	@Override
	protected BlockStateContainer createActualBlockState() {
		return new BlockStateContainer(this, FRONT);
	}

	@Override
	@Deprecated
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileDigitalDetector();
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		TileEntity tile = world.getTileEntity(pos);
		if((tile instanceof TileDigitalDetector)) {
			((TileDigitalDetector) tile).direction = MiscTools.getSideFacingPlayer(pos, placer);
			notifyBlockUpdate(world, pos, state);
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		return false;
	}

	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
		TileEntity tile = world.getTileEntity(pos);
		if((tile instanceof TileDigitalDetector)) {
			TileDigitalDetector detector = (TileDigitalDetector) tile;
			if(detector.direction == axis) {
				detector.direction = axis.getOpposite();
			} else {
				detector.direction = axis;
			}
			notifyBlockUpdate(world, pos);
			return true;
		}
		return false;
	}

	@Override
	public EnumFacing[] getValidRotations(World world, BlockPos pos) {
		return EnumFacing.VALUES;
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		super.onBlockAdded(world, pos, state);
		notifyBlockUpdate(world, pos, state);
		if(world.isRemote) {
			return;
		}
		for(EnumFacing side : EnumFacing.VALUES) {
			world.notifyNeighborsOfStateChange(pos.offset(side), state.getBlock(), false);
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		super.breakBlock(world, pos, state);
		if(world.isRemote) {
			return;
		}
		for(EnumFacing side : EnumFacing.VALUES) {
			world.notifyNeighborsOfStateChange(pos.offset(side), state.getBlock(), false);
		}
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing sid) {
		return false;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileDigitalDetector.class;
	}

	private static final String prefix = "railcraft/";

	@Override
	public String getPrefix(World world, BlockPos pos) {
		return prefix;
	}

	@Override
	public String getPrefix(ItemStack stack) {
		return prefix;
	}
}

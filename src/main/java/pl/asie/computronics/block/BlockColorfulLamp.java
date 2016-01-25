package pl.asie.computronics.block;

import li.cil.oc.api.network.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileColorfulLamp;
//import powercrystals.minefactoryreloaded.api.rednet.IRedNetInputNode;
//import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;

/*@Optional.InterfaceList({
	@Optional.Interface(iface = "powercrystals.minefactoryreloaded.api.rednet.IRedNetInputNode", modid = Mods.MFR)
})*/
public class BlockColorfulLamp extends BlockPeripheral /*implements IRedNetInputNode*/ {

	public static final PropertyInteger BRIGHTNESS = PropertyInteger.create("brightness", 0, 15);

	public BlockColorfulLamp() {
		super("colorful_lamp", Rotation.NONE);
		this.setUnlocalizedName("computronics.colorfulLamp");
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileColorfulLamp();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		/*if(!world.isRemote && Mods.isLoaded(Mods.MFR) && player.isSneaking()) {
			TileEntity tile = world.getTileEntity(pos);
			if(tile instanceof TileColorfulLamp) {
				ItemStack held = player.getCurrentEquippedItem();
				if(held != null && held.getItem() != null && Integration.isTool(held, player, pos) && Integration.useTool(held, player, pos)) {
					TileColorfulLamp lamp = (TileColorfulLamp) tile;
					lamp.setBinaryMode(!lamp.isBinaryMode());
					player.addChatMessage(new ChatComponentTranslation("chat.computronics.lamp.binary." + (lamp.isBinaryMode() ? "on" : "off")));
					return true;
				}
			}
		}*/
		return super.onBlockActivated(world, pos, state, player, side, hitX, hitY, hitZ);
	}

	@Override
	protected BlockState createActualBlockState() {
		return new BlockState(this, BUNDLED, BRIGHTNESS);
	}

	@Override
	protected IBlockState createDefaultState() {
		return super.createDefaultState().withProperty(BRIGHTNESS, 0);
	}

	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block) {
		/*TileEntity tile = world.getTileEntity(x, y, z);
		if(Mods.isLoaded(Mods.ProjectRed) && tile instanceof TileColorfulLamp) {
			((TileColorfulLamp) tile).onProjectRedBundledInputChanged();
		}*/
		super.onNeighborBlockChange(world, pos, state, block);
	}

	@Override
	public int getLightValue(IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileColorfulLamp) {
			int color = ((TileColorfulLamp) tile).getLampColor();
			//this.lightValue = world.getBlockState(pos).getValue(BRIGHTNESS);
			this.lightValue = color == 0 ? 0 : 15; //TODO do this
			if(world instanceof World) {
				((World) world).notifyLightSet(pos);
				((World) world).markBlockRangeForRenderUpdate(pos.add(-1, -1, -1), pos.add(1, 1, 1));
			}
			return this.lightValue;
		}
		return this.lightValue = world.getBlockState(pos).getValue(BRIGHTNESS);
	}

	@Override
	public int getLightOpacity() {
		return super.getLightOpacity();
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileColorfulLamp) {
			return state.withProperty(BRIGHTNESS, ((TileColorfulLamp) tile).getLampColor() == 0 ? 0 : 15);
		} else {
			return state;
		}
	}

	@Override
	public boolean canRenderInLayer(EnumWorldBlockLayer layer) {
		return layer == EnumWorldBlockLayer.CUTOUT_MIPPED || super.canRenderInLayer(layer);
	}

	@Override
	public int colorMultiplier(IBlockAccess world, BlockPos pos, int pass) {
		if(pass != 0) {
			return super.colorMultiplier(world, pos, pass);
		}
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileColorfulLamp) {
			int color = ((TileColorfulLamp) tile).getLampColor();
			return (color & (0x1F << 10)) << 9 | (color & (0x1F << 5)) << 6 | ((color & 0x1F) << 3);
		}
		return super.colorMultiplier(world, pos, pass);
	}

	@Override
	public boolean supportsBundledRedstone() {
		return true;
	}
/*@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() {
		return LampRender.id();
	}*/

	/*@Override
	@Optional.Method(modid = Mods.MFR)
	public RedNetConnectionType getConnectionType(World world, int x, int y,
		int z, ForgeDirection side) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileColorfulLamp && ((TileColorfulLamp) tile).isBinaryMode()) {
			return RedNetConnectionType.CableAll;
		}
		return RedNetConnectionType.CableSingle;
	}

	@Override
	@Optional.Method(modid = Mods.MFR)
	public void onInputsChanged(World world, int x, int y, int z,
		ForgeDirection side, int[] inputValues) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileColorfulLamp && ((TileColorfulLamp) tile).isBinaryMode()) {
			int c = 0;
			for(int i = 0; i < 15; i++) {
				if(inputValues[i] != 0) {
					c |= (1 << i);
				}
			}
			((TileColorfulLamp) tile).setLampColor(c);
		}
	}

	@Override
	@Optional.Method(modid = Mods.MFR)
	public void onInputChanged(World world, int x, int y, int z,
		ForgeDirection side, int inputValue) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileColorfulLamp && !((TileColorfulLamp) tile).isBinaryMode()) {
			((TileColorfulLamp) tile).setLampColor(inputValue & 0x7FFF);
		}
	}*/

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileColorfulLamp.class;
	}
}

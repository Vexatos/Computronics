package pl.asie.computronics.block;

import li.cil.oc.api.network.Environment;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.item.block.IBlockWithDifferentColors;
import pl.asie.computronics.item.block.IBlockWithSpecialText;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileChatBox;
import pl.asie.computronics.util.StringUtil;
import pl.asie.lib.tile.TileEntityBase;

import java.util.List;

public class BlockChatBox extends BlockPeripheral implements IBlockWithSpecialText, IBlockWithDifferentColors {

	public static final PropertyBool CREATIVE = PropertyBool.create("creative");

	public BlockChatBox() {
		super("chatbox", Rotation.NONE);
		this.setCreativeTab(Computronics.tab);
		this.setDefaultState(this.blockState.getBaseState().withProperty(CREATIVE, false));
		this.setUnlocalizedName("computronics.chatBox");
	}

	// I'm such a cheater.
	@Override
	public int getRenderColor(IBlockState state) {
		return state.getValue(CREATIVE) ? 0xFF60FF : 0xFFFFFF;
	}

	// Cheaters never win! ~ jaquadro
	@Override
	public int colorMultiplier(IBlockAccess world, BlockPos pos, int renderPass) {
		IBlockState state = world.getBlockState(pos);
		return state.getValue(CREATIVE) ? getRenderColor(state) : super.colorMultiplier(world, pos, renderPass);
	}

	@Override
	public int getColorFromItemStack(ItemStack stack, int pass) {
		return (stack.getItemDamage() & 8) != 0 ? 0xFF60FF : 0xFFFFFF;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileChatBox();
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs creativeTabs, List<ItemStack> blockList) {
		blockList.add(new ItemStack(item, 1, 0));
		if(Config.CHATBOX_CREATIVE) {
			blockList.add(new ItemStack(item, 1, 8));
		}
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return super.getStateFromMeta(meta).withProperty(CREATIVE, (meta & 8) != 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return super.getMetaFromState(state) | (state.getValue(CREATIVE) ? 8 : 0);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}

	@Override
	protected BlockState createActualBlockState() {
		return new BlockState(this, CREATIVE);
	}

	@Override
	protected IBlockState createDefaultState() {
		return super.createDefaultState().withProperty(CREATIVE, false);
	}

	@Override
	public boolean emitsRedstone(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return Config.REDSTONE_REFRESH;
	}

	@Override
	public boolean hasComparatorInputOverride() {
		return Config.REDSTONE_REFRESH;
	}

	@Override
	public int getComparatorInputOverride(World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityBase) {
			return ((TileEntityBase) tile).requestCurrentRedstoneValue(null);
		}
		return super.getComparatorInputOverride(world, pos);
	}

	@Override
	public boolean hasSubTypes() {
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean wat) {
		if(stack.getItemDamage() >= 8) {
			list.add(EnumChatFormatting.GRAY + StringUtil.localize("tooltip.computronics.chatBox.creative"));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return (stack.getItemDamage() >= 8 ? "tile.computronics.chatBox.creative" : this.getUnlocalizedName());
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileChatBox.class;
	}
}

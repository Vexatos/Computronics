package pl.asie.computronics.block;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import li.cil.oc.api.network.Environment;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import pl.asie.computronics.item.block.IBlockWithSpecialText;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileCipherBlockAdvanced;
import pl.asie.computronics.util.StringUtil;

import java.util.List;

/**
 * @author Vexatos
 */
public class BlockCipherAdvanced extends BlockMachineSidedIcon implements IBlockWithSpecialText {

	private IIcon mFront;

	public BlockCipherAdvanced() {
		super("cipher_advanced");
		this.setBlockName("computronics.cipher_advanced");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileCipherBlockAdvanced();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getAbsoluteSideIcon(int sideNumber, int metadata) {
		return sideNumber == 2 ? mFront : super.getAbsoluteSideIcon(sideNumber, metadata);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister r) {
		mSide = r.registerIcon("computronics:advanced/machine_side");
		mTop = r.registerIcon("computronics:advanced/machine_top");
		mBottom = r.registerIcon("computronics:advanced/machine_bottom");
		mFront = r.registerIcon("computronics:advanced/cipher_front");
	}

	@Override
	public boolean hasSubTypes() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean wat) {
		list.add(EnumChatFormatting.GRAY + StringUtil.localize("tooltip.computronics.cipher.advanced"));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return this.getUnlocalizedName();
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileCipherBlockAdvanced.class;
	}
}

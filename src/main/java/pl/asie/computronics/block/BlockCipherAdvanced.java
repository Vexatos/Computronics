package pl.asie.computronics.block;

import li.cil.oc.api.network.Environment;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.item.block.IBlockWithSpecialText;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileCipherBlockAdvanced;
import pl.asie.computronics.util.StringUtil;

import java.util.List;

/**
 * @author Vexatos
 */
public class BlockCipherAdvanced extends BlockPeripheral implements IBlockWithSpecialText {

	public BlockCipherAdvanced() {
		super("cipher_advanced", Rotation.NONE);
		this.setUnlocalizedName("computronics.cipher_advanced");
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileCipherBlockAdvanced();
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

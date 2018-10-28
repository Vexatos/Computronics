package pl.asie.computronics.block;

import li.cil.oc.api.network.Environment;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pl.asie.computronics.item.block.IBlockWithSpecialText;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileCipherBlockAdvanced;
import pl.asie.computronics.util.StringUtil;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Vexatos
 */
public class BlockCipherAdvanced extends BlockPeripheral implements IBlockWithSpecialText {

	public BlockCipherAdvanced() {
		super("cipher_advanced", Rotation.NONE);
		this.setTranslationKey("computronics.cipher_advanced");
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileCipherBlockAdvanced();
	}

	@Override
	public boolean hasSubTypes() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag flag) {
		list.add(StringUtil.localize("tooltip.computronics.cipher.advanced"));
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return this.getTranslationKey();
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileCipherBlockAdvanced.class;
	}
}

package pl.asie.computronics.item.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * @author Vexatos
 */
public interface IBlockWithSpecialText {

	public boolean hasSubTypes();

	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean wat);

	public String getUnlocalizedName(ItemStack stack);
}

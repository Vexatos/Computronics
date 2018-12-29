package pl.asie.lib.api.tool;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

/**
 * @author Vexatos
 */
public interface IToolProvider {

	/**
	 * @param stack The ItemStack to check
	 * @param player The player holding the item
	 * @param hand
	 * @param pos The position the tool is used on
	 * @return true if the provided ItemStack is a valid tool
	 */
	boolean isTool(ItemStack stack, EntityPlayer player, EnumHand hand, BlockPos pos);

	/**
	 * @param stack The ItemStack to check
	 * @param player The player holding the item
	 * @param hand
	 * @param pos The position the tool is used on
	 * @return true if the tool has been successfully used
	 */
	boolean useTool(ItemStack stack, EntityPlayer player, EnumHand hand, BlockPos pos);
}

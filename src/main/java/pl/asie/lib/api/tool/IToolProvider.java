package pl.asie.lib.api.tool;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

/**
 * @author Vexatos
 */
public interface IToolProvider {

	/**
	 * @param stack The ItemStack to check
	 * @param player The player holding the item
	 * @param pos The position the tool is used on
	 * @return true if the provided ItemStack is a valid tool
	 */
	public boolean isTool(ItemStack stack, EntityPlayer player, BlockPos pos);

	/**
	 * @param stack The ItemStack to check
	 * @param player The player holding the item
	 * @param pos The position the tool is used on
	 * @return true if the tool has been successfully used
	 */
	public boolean useTool(ItemStack stack, EntityPlayer player, BlockPos pos);
}

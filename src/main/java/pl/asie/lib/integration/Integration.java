package pl.asie.lib.integration;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import pl.asie.lib.AsieLibMod;
import pl.asie.lib.api.tool.IToolProvider;
import pl.asie.lib.integration.tool.ToolRegistry;

public class Integration {

	public static final ToolRegistry toolRegistry = new ToolRegistry();

	/**
	 * Registers a new {@link IToolProvider}
	 * @param provider the {@link IToolProvider to register}
	 */
	public static void registerToolProvider(IToolProvider provider) {
		toolRegistry.registerToolProvider(provider);
	}

	/**
	 * Checks whether the ItemStack is a valid tool
	 * @param stack the ItemStack to check
	 * @param player the player holding the item
	 * @param hand
	 * @param pos The position the tool is used on
	 * @return Wether the ItemStack is a valid tool
	 */
	public static boolean isTool(ItemStack stack, EntityPlayer player, EnumHand hand, BlockPos pos) {
		for(IToolProvider provider : toolRegistry) {
			try {
				if(provider.isTool(stack, player, hand, pos)) {
					return true;
				}
			} catch(Exception e) {
				AsieLibMod.log.error("An error occured trying to identify a tool", e);
			}
		}
		return false;
	}

	/**
	 * Uses the tool on the block
	 * @param stack The ItemStack to check
	 * @param player The player holding the item
	 * @param hand
	 * @param pos The position the tool is used on
	 * @return true if the tool has been successfully used
	 */
	public static boolean useTool(ItemStack stack, EntityPlayer player, EnumHand hand, BlockPos pos) {
		for(IToolProvider provider : toolRegistry) {
			try {
				if(provider.useTool(stack, player, hand, pos)) {
					return true;
				}
			} catch(Exception e) {
				AsieLibMod.log.error("An error occured trying to use a tool", e);
			}
		}
		return false;
	}
}

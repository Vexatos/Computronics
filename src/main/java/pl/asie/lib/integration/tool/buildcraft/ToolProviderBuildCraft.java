package pl.asie.lib.integration.tool.buildcraft;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import pl.asie.lib.api.tool.IToolProvider;

/**
 * @author Vexatos
 */
public class ToolProviderBuildCraft implements IToolProvider {

	@Override
	public boolean isTool(ItemStack stack, EntityPlayer player, BlockPos pos) {
		return (stack.getItem() instanceof IToolWrench);
	}

	@Override
	public boolean useTool(ItemStack stack, EntityPlayer player, BlockPos pos) {
		if(stack.getItem() instanceof IToolWrench) {
			IToolWrench wrench = (IToolWrench) stack.getItem();
			if(wrench.canWrench(player, pos)) {
				wrench.wrenchUsed(player, pos);
				return true;
			}
		}
		return false;
	}
}

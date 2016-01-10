package pl.asie.lib.integration.tool.buildcraft;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import pl.asie.lib.api.tool.IToolProvider;

/**
 * @author Vexatos
 */
public class ToolProviderBuildCraft implements IToolProvider {

	@Override
	public boolean isTool(ItemStack stack, EntityPlayer player, int x, int y, int z) {
		return (stack.getItem() instanceof IToolWrench);
	}

	@Override
	public boolean useTool(ItemStack stack, EntityPlayer player, int x, int y, int z) {
		if(stack.getItem() instanceof IToolWrench) {
			IToolWrench wrench = (IToolWrench) stack.getItem();
			if(wrench.canWrench(player, x, y, z)) {
				wrench.wrenchUsed(player, x, y, z);
				return true;
			}
		}
		return false;
	}
}

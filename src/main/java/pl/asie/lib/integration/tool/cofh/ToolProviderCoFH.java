package pl.asie.lib.integration.tool.cofh;

import cofh.api.item.IToolHammer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import pl.asie.lib.api.tool.IToolProvider;

/**
 * @author Vexatos
 */
public class ToolProviderCoFH implements IToolProvider {

	@Override
	public boolean isTool(ItemStack stack, EntityPlayer player, int x, int y, int z) {
		return (stack.getItem() instanceof IToolHammer);
	}

	@Override
	public boolean useTool(ItemStack stack, EntityPlayer player, int x, int y, int z) {
		if(stack.getItem() instanceof IToolHammer) {
			IToolHammer hammer = (IToolHammer) stack.getItem();
			if(hammer.isUsable(stack, player, x, y, z)) {
				hammer.toolUsed(stack, player, x, y, z);
				return true;
			}
		}
		return false;
	}
}

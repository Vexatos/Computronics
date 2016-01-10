package pl.asie.lib.integration.tool.enderio;

import crazypants.enderio.api.tool.ITool;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import pl.asie.lib.api.tool.IToolProvider;

/**
 * @author Vexatos
 */
public class ToolProviderEnderIO implements IToolProvider {

	@Override
	public boolean isTool(ItemStack stack, EntityPlayer player, int x, int y, int z) {
		return (stack.getItem() instanceof ITool);
	}

	@Override
	public boolean useTool(ItemStack stack, EntityPlayer player, int x, int y, int z) {
		if(stack.getItem() instanceof ITool) {
			ITool tool = ((ITool) stack.getItem());
			if(tool.canUse(stack, player, x, y, z)) {
				tool.used(stack, player, x, y, z);
				return true;
			}
		}
		return false;
	}
}

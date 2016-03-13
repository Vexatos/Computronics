package pl.asie.lib.integration.tool.enderio;

import crazypants.enderio.api.tool.ITool;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import pl.asie.lib.api.tool.IToolProvider;

/**
 * @author Vexatos
 */
public class ToolProviderEnderIO implements IToolProvider {

	@Override
	public boolean isTool(ItemStack stack, EntityPlayer player, BlockPos pos) {
		return (stack.getItem() instanceof ITool);
	}

	@Override
	public boolean useTool(ItemStack stack, EntityPlayer player, BlockPos pos) {
		if(stack.getItem() instanceof ITool) {
			ITool tool = ((ITool) stack.getItem());
			if(tool.canUse(stack, player, pos)) {
				tool.used(stack, player, pos);
				return true;
			}
		}
		return false;
	}
}

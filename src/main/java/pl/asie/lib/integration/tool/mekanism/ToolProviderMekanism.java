package pl.asie.lib.integration.tool.mekanism;

import mekanism.api.IMekWrench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import pl.asie.lib.api.tool.IToolProvider;

/**
 * @author Vexatos
 */
public class ToolProviderMekanism implements IToolProvider {

	@Override
	public boolean isTool(ItemStack stack, EntityPlayer player, int x, int y, int z) {
		return stack.getItem() instanceof IMekWrench;
	}

	@Override
	public boolean useTool(ItemStack stack, EntityPlayer player, int x, int y, int z) {
		if(stack.getItem() instanceof IMekWrench) {
			IMekWrench wrench = (IMekWrench) stack.getItem();
			if(wrench.canUseWrench(player, x, y, z)) {
				return true;
			}
		}
		return false;
	}
}

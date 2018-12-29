package pl.asie.lib.integration.tool.oc;

import li.cil.oc.api.internal.Wrench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import pl.asie.lib.api.tool.IToolProvider;

/**
 * @author Vexatos
 */
public class ToolProviderOC implements IToolProvider {

	@Override
	public boolean isTool(ItemStack stack, EntityPlayer player, EnumHand hand, BlockPos pos) {
		return stack.getItem() instanceof Wrench;
	}

	@Override
	public boolean useTool(ItemStack stack, EntityPlayer player, EnumHand hand, BlockPos pos) {
		if(stack.getItem() instanceof Wrench) {
			Wrench wrench = (Wrench) stack.getItem();
			if(wrench.useWrenchOnBlock(player, player.world, pos, true)) {
				wrench.useWrenchOnBlock(player, player.world, pos, false);
				return true;
			}
		}
		return false;
	}
}

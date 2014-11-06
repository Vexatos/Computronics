package pl.asie.computronics.integration.waila;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * @author Vexatos
 */
public class WailaComputronics implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,
		IWailaConfigHandler config) {

		return accessor.getStack();
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip,
		IWailaDataAccessor accessor, IWailaConfigHandler config) {

		for(WailaProviders p : WailaProviders.VALUES) {
			if(p.isInstance(accessor.getBlock())) {
				currenttip = p.getProvider().getWailaHead(itemStack, currenttip, accessor, config);
			}
		}

		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {

		for(WailaProviders p : WailaProviders.VALUES) {
			if(p.isInstance(accessor.getBlock())) {
				currenttip = p.getProvider().getWailaBody(itemStack, currenttip, accessor, config);
			}
		}

		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
		IWailaConfigHandler config) {

		for(WailaProviders p : WailaProviders.VALUES) {
			if(p.isInstance(accessor.getBlock())) {
				currenttip = p.getProvider().getWailaTail(itemStack, currenttip, accessor, config);
			}
		}

		return currenttip;
	}
}

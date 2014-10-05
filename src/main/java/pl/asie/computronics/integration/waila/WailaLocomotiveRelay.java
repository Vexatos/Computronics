package pl.asie.computronics.integration.waila;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import java.util.List;

/**
 * @author Vexatos
 */
public class WailaLocomotiveRelay implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,
		IWailaConfigHandler config) {

		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip,
		IWailaDataAccessor accessor, IWailaConfigHandler config) {

		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
		IWailaConfigHandler config) {

		NBTTagCompound nbt = accessor.getNBTData();
		String boundKey = "tooltip.computronics.waila.relay." + (nbt.getBoolean("bound") ? "bound" : "notbound");
		currenttip.add(StatCollector.translateToLocal(boundKey));
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
		IWailaConfigHandler config) {

		return currenttip;
	}
}

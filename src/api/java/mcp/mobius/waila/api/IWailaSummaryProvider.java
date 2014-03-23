package mcp.mobius.waila.api;

import java.util.LinkedHashMap;
import mcp.mobius.waila.api.IWailaConfigHandler;
import net.minecraft.item.ItemStack;

public interface IWailaSummaryProvider {

   LinkedHashMap getSummary(ItemStack var1, LinkedHashMap var2, IWailaConfigHandler var3);
}

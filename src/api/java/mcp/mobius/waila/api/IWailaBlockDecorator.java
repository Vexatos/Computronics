package mcp.mobius.waila.api;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.item.ItemStack;

public interface IWailaBlockDecorator {

   void decorateBlock(ItemStack var1, IWailaDataAccessor var2, IWailaConfigHandler var3);
}

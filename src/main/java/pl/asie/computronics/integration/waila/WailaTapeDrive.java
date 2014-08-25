package pl.asie.computronics.integration.waila;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.item.ItemStack;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.tile.TileTapeDrive;

import java.util.List;

public class WailaTapeDrive implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack,
			List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack,
			List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		TileTapeDrive drive = (TileTapeDrive)accessor.getTileEntity();
		ItemStack is = drive.getStackInSlot(0);
		if(is.getItem().equals(Computronics.itemTape)) {
			String label = Computronics.itemTape.getLabel(is);
			if(label.length() > 0) currenttip.add("Tape \"" + label +"\" inserted");
			else currenttip.add("Tape inserted");
		} else currenttip.add("No tape inserted");
		return currenttip;
	}

}

package pl.asie.computronics.integration.waila;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.SpecialChars;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.tile.TileTapeDrive;

import java.util.List;
import java.util.Locale;

public class WailaTapeDrive implements IWailaDataProvider {

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
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip,
		IWailaDataAccessor accessor, IWailaConfigHandler config) {

		TileTapeDrive drive = (TileTapeDrive) accessor.getTileEntity();
		ItemStack is = drive.getStackInSlot(0);
		if(is != null && is.getItem() == Computronics.itemTape) {
			String label = Computronics.itemTape.getLabel(is);
			if(label.length() > 0) {
				currenttip.add(StatCollector.translateToLocalFormatted("tooltip.computronics.waila.tape.labeltapeinserted",
					label + SpecialChars.RESET));
			} else {
				currenttip.add(StatCollector.translateToLocal("tooltip.computronics.waila.tape.tapeinserted"));
			}
			currenttip.add(StatCollector.translateToLocalFormatted("tooltip.computronics.waila.tape.state",
				StatCollector.translateToLocal("tooltip.computronics.waila.tape.state."
					+ drive.getEnumState().toString().toLowerCase(Locale.ENGLISH))));
		} else {
			currenttip.add(StatCollector.translateToLocal("tooltip.computronics.waila.tape.notapeinserted"));
		}
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip,
		IWailaDataAccessor accessor, IWailaConfigHandler config) {

		return currenttip;
	}

}

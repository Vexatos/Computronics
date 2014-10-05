package pl.asie.computronics.integration.waila;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.SpecialChars;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.tile.TileTapeDrive;

import java.util.List;
import java.util.Locale;

public class WailaTapeDrive extends WailaComputronics {

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip,
		IWailaDataAccessor accessor, IWailaConfigHandler config) {

		if(!ConfigValues.Tape.getConfig(config)) {
			return currenttip;
		}

		TileTapeDrive drive = (TileTapeDrive) accessor.getTileEntity();
		ItemStack is = drive.getStackInSlot(0);
		if(is != null && is.getItem() == Computronics.itemTape) {
			String label = Computronics.itemTape.getLabel(is);
			if(label.length() > 0 && ConfigValues.TapeName.getConfig(config)) {
				currenttip.add(StatCollector.translateToLocalFormatted("tooltip.computronics.waila.tape.labeltapeinserted",
					label + SpecialChars.RESET));
			} else {
				currenttip.add(StatCollector.translateToLocal("tooltip.computronics.waila.tape.tapeinserted"));
			}
			if(ConfigValues.DriveState.getConfig(config)) {
				currenttip.add(StatCollector.translateToLocalFormatted("tooltip.computronics.waila.tape.state",
					StatCollector.translateToLocal("tooltip.computronics.waila.tape.state."
						+ drive.getEnumState().toString().toLowerCase(Locale.ENGLISH))));
			}
		} else {
			currenttip.add(StatCollector.translateToLocal("tooltip.computronics.waila.tape.notapeinserted"));
		}
		return currenttip;
	}
}

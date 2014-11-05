package pl.asie.computronics.integration.waila.providers;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.SpecialChars;
import net.minecraft.item.ItemStack;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.tape.IItemTapeStorage;
import pl.asie.computronics.integration.waila.ConfigValues;
import pl.asie.computronics.tile.TileTapeDrive;
import pl.asie.computronics.util.StringUtil;

import java.util.List;
import java.util.Locale;

public class WailaTapeDrive extends ComputronicsWailaProvider {

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip,
		IWailaDataAccessor accessor, IWailaConfigHandler config) {

		if(!ConfigValues.Tape.getConfig(config)) {
			return currenttip;
		}

		TileTapeDrive drive = (TileTapeDrive) accessor.getTileEntity();
		ItemStack is = drive.getStackInSlot(0);
		if(is != null && is.getItem() instanceof IItemTapeStorage) {
			String label = Computronics.itemTape.getLabel(is);
			if(label.length() > 0 && ConfigValues.TapeName.getConfig(config)) {
				currenttip.add(StringUtil.localizeAndFormat("tooltip.computronics.waila.tape.labeltapeinserted",
					label + SpecialChars.RESET));
			} else {
				currenttip.add(StringUtil.localize("tooltip.computronics.waila.tape.tapeinserted"));
			}
			if(ConfigValues.DriveState.getConfig(config)) {
				currenttip.add(StringUtil.localizeAndFormat("tooltip.computronics.waila.tape.state",
					StringUtil.localize("tooltip.computronics.waila.tape.state."
						+ drive.getEnumState().toString().toLowerCase(Locale.ENGLISH))));
			}
		} else {
			currenttip.add(StringUtil.localize("tooltip.computronics.waila.tape.notapeinserted"));
		}
		return currenttip;
	}
}

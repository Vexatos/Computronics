package pl.asie.computronics.integration.railcraft.gui.tooltip;

import com.google.common.base.Splitter;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.gui.tooltips.ToolTipLine;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;
import pl.asie.computronics.util.StringUtil;

import java.util.List;

/**
 * @author CovertJaguar, Vexatos
 */
public class ToolTips {
	private static final Splitter keyValueSplitter = Splitter.on('=').trimResults();
	private static final Splitter lineSplitter = Splitter.on("\n").trimResults();

	public static ToolTip buildToolTip(String tipTag, String... vars) {
		return buildToolTip(tipTag, 750, vars);
	}

	public static ToolTip buildToolTip(String tipTag, int delay, String... vars) {
		if(!StringUtil.canTranslate(tipTag)) {
			return null;
		} else {
			try {
				ToolTip ex = new ToolTip(delay);
				String text = LocalizationPlugin.translate(tipTag);

				for(String var : vars) {
					List pair = keyValueSplitter.splitToList(var);
					text = text.replace((CharSequence) pair.get(0), (CharSequence) pair.get(1));
				}

				for(String var11 : lineSplitter.split(text)) {
					ex.add(new ToolTipLine(var11));
				}
				return ex;
			} catch(RuntimeException var9) {
				Game.logThrowable("Failed to parse tooltip: " + tipTag, var9);
				throw var9;
			}
		}
	}
}

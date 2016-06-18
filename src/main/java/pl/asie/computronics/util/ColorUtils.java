package pl.asie.computronics.util;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import pl.asie.computronics.reference.Mods;
import pl.asie.lib.util.internal.IColorable;

import javax.annotation.Nullable;

import static pl.asie.lib.reference.Capabilities.COLORABLE_CAPABILITY;

/**
 * @author Vexatos
 */
public class ColorUtils extends pl.asie.lib.util.ColorUtils {

	@Nullable
	public static IColorable getColorable(@Nullable ICapabilityProvider provider, EnumFacing side) {
		if(Mods.isLoaded(Mods.OpenComputers)) {
			return OCUtils.getColorable(provider, side);
		}
		if(provider != null && provider.hasCapability(COLORABLE_CAPABILITY, side)) {
			return provider.getCapability(COLORABLE_CAPABILITY, side);
		}
		return null;
	}
}

package pl.asie.computronics.integration.info.providers;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.reference.Mods;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Vexatos
 */
public abstract class ComputronicsInfoProvider implements IComputronicsInfoProvider {

	@Nullable
	@Override
	@Optional.Method(modid = Mods.Waila)
	public ItemStack getWailaStack(IWailaDataAccessor accessor,
		IWailaConfigHandler config) {

		return null;
	}

	@Override
	@Optional.Method(modid = Mods.Waila)
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip,
		IWailaDataAccessor accessor, IWailaConfigHandler config) {

		return currenttip;
	}

	@Override
	@Optional.Method(modid = Mods.Waila)
	public abstract List<String> getWailaBody(ItemStack stack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config);

	@Override
	@Optional.Method(modid = Mods.Waila)
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
		IWailaConfigHandler config) {

		return currenttip;
	}

	@Override
	@Optional.Method(modid = Mods.TheOneProbe)
	public String getID() {
		return Mods.Computronics + ":" + getUID();
	}

	protected abstract String getUID();
}

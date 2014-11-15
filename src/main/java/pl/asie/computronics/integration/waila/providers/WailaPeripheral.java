package pl.asie.computronics.integration.waila.providers;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import pl.asie.computronics.integration.waila.ConfigValues;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.StringUtil;

import java.util.List;

/**
 * @author Vexatos
 */
public class WailaPeripheral extends ComputronicsWailaProvider {

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
		IWailaConfigHandler config) {

		if(!ConfigValues.Address.getValue(config)) {
			return currenttip;
		}

		NBTTagCompound nbt = accessor.getNBTData();
		if(Loader.isModLoaded(Mods.OpenComputers)) {
			currenttip = getWailaOCBody(nbt, currenttip);
		}
		if(Loader.isModLoaded(Mods.NedoComputers)) {
			currenttip = getWailaNCBody(nbt, currenttip);
		}
		return currenttip;
	}

	@Optional.Method(modid = Mods.OpenComputers)
	private List<String> getWailaOCBody(NBTTagCompound nbt, List<String> currenttip) {
		NBTTagCompound node = nbt.getCompoundTag("oc:node");
		if(node.hasKey("address")) {
			currenttip.add(StringUtil.localizeAndFormat("oc:gui.Analyzer.Address", node.getString("address")));
		}
		return currenttip;
	}

	@Optional.Method(modid = Mods.NedoComputers)
	private List<String> getWailaNCBody(NBTTagCompound nbt, List<String> currenttip) {
		if(nbt.hasKey("nc:bus")) {
			currenttip.add(StringUtil.localizeAndFormat(
				"tooltip.computronics.waila.base.bus", nbt.getShort("nc:bus")));
		}
		return currenttip;
	}
}

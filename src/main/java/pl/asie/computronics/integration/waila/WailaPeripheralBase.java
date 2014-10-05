package pl.asie.computronics.integration.waila;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import pl.asie.computronics.reference.Mods;

import java.util.List;

/**
 * @author Vexatos
 */
public class WailaPeripheralBase extends WailaComputronics {

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
		IWailaConfigHandler config) {

		if(!ConfigValues.Address.getConfig(config)) {
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
			currenttip.add(StatCollector.translateToLocalFormatted("oc:gui.Analyzer.Address", node.getString("address")));
		}
		return currenttip;
	}

	@Optional.Method(modid = Mods.NedoComputers)
	private List<String> getWailaNCBody(NBTTagCompound nbt, List<String> currenttip) {
		if(nbt.hasKey("nc:bus")) {
			currenttip.add(StatCollector.translateToLocalFormatted(
				"tooltip.computronics.waila.base.bus", nbt.getShort("nc:bus")));
		}
		return currenttip;
	}
}

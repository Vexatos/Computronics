package pl.asie.computronics.integration.waila.providers;

import cpw.mods.fml.common.Optional;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Node;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.integration.waila.ConfigValues;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.StringUtil;
import pl.asie.computronics.util.internal.IComputronicsPeripheral;

import java.util.List;

/**
 * @author Vexatos
 */
public class WailaPeripheral extends ComputronicsWailaProvider {

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
		IWailaConfigHandler config) {

		NBTTagCompound nbt = accessor.getNBTData();
		if(Mods.isLoaded(Mods.OpenComputers) && ConfigValues.OCAddress.getValue(config)) {
			currenttip = getWailaOCBody(nbt, currenttip);
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

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		if(te != null && te instanceof IComputronicsPeripheral) {
			if(Mods.isLoaded(Mods.OpenComputers)) {
				tag = getNBTData_OC(te, tag);
			}
		}
		return tag;
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public NBTTagCompound getNBTData_OC(TileEntity te, NBTTagCompound tag) {
		if(!(te instanceof Environment)) {
			return tag;
		}
		Environment tile = ((Environment) te);
		Node node = tile.node();
		if(node != null && node.host() == tile) {
			final NBTTagCompound nodeNbt = new NBTTagCompound();
			node.save(nodeNbt);
			tag.setTag("oc:node", nodeNbt);
		}
		return tag;
	}
}

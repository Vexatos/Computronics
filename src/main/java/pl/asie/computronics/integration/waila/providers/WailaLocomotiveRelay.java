package pl.asie.computronics.integration.waila.providers;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.integration.railcraft.tile.TileLocomotiveRelay;
import pl.asie.computronics.integration.waila.ConfigValues;
import pl.asie.computronics.util.StringUtil;

import java.util.List;

/**
 * @author Vexatos
 */
public class WailaLocomotiveRelay extends ComputronicsWailaProvider {

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
		IWailaConfigHandler config) {

		if(!ConfigValues.RelayBound.getValue(config)) {
			return currenttip;
		}

		NBTTagCompound nbt = accessor.getNBTData();
		String boundKey = "tooltip.computronics.waila.relay." + (nbt.getBoolean("bound") ? "bound" : "notbound");
		currenttip.add(StringUtil.localize(boundKey));
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		if(te instanceof TileLocomotiveRelay) {
			TileLocomotiveRelay relay = (TileLocomotiveRelay) te;
			tag.setBoolean("bound", relay.isBound());
		}
		return tag;
	}
}

package pl.asie.computronics.integration.waila.providers;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.integration.waila.ConfigValues;
import pl.asie.computronics.tile.TileColorfulLamp;
import pl.asie.computronics.util.StringUtil;

import java.util.List;

/**
 * @author Vexatos
 */
public class WailaColorfulLamp extends ComputronicsWailaProvider {

	@Override
	public List<String> getWailaBody(ItemStack stack, List<String> currenttip,
		IWailaDataAccessor accessor, IWailaConfigHandler config) {

		if(!ConfigValues.LampColor.getValue(config)) {
			return currenttip;
		}

		NBTTagCompound nbt = accessor.getNBTData();
		short color = nbt.getShort("clc");
		int r = (color & 0x7C00) >>> 10,
			g = (color & 0x03E0) >>> 5,
			b = color & 0x001F;
		currenttip.add(StringUtil.localizeAndFormat("tooltip.computronics.waila.lamp.red", r));
		currenttip.add(StringUtil.localizeAndFormat("tooltip.computronics.waila.lamp.green", g));
		currenttip.add(StringUtil.localizeAndFormat("tooltip.computronics.waila.lamp.blue", b));
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		if(te != null && te instanceof TileColorfulLamp) {
			tag.setShort("clc", (short) (((TileColorfulLamp) te).getLampColor() & 32767));
		}
		return tag;
	}
}

package pl.asie.computronics.integration.info.providers;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.integration.info.ConfigValues;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileColorfulLamp;
import pl.asie.computronics.util.StringUtil;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Vexatos
 */
public class InfoColorfulLamp extends ComputronicsInfoProvider {

	@Override
	@Optional.Method(modid = Mods.Waila)
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
	@Optional.Method(modid = Mods.Waila)
	public NBTTagCompound getNBTData(EntityPlayerMP player, @Nullable TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
		if(te != null && te instanceof TileColorfulLamp) {
			tag.setShort("clc", (short) (((TileColorfulLamp) te).getLampColor() & 32767));
		}
		return tag;
	}

	@Override
	public String getUID() {
		return "lamp";
	}

	@Override
	@Optional.Method(modid = Mods.TheOneProbe)
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
		TileEntity tileEntity = world.getTileEntity(data.getPos());
		if(!(tileEntity instanceof TileColorfulLamp)) {
			return;
		}
		int color = ((TileColorfulLamp) tileEntity).getLampColor();
		int r = (color & 0x7C00) >>> 10,
			g = (color & 0x03E0) >>> 5,
			b = color & 0x001F;
		probeInfo.text(StringUtil.localizeAndFormat("tooltip.computronics.waila.lamp.red", r));
		probeInfo.text(StringUtil.localizeAndFormat("tooltip.computronics.waila.lamp.green", g));
		probeInfo.text(StringUtil.localizeAndFormat("tooltip.computronics.waila.lamp.blue", b));
	}
}

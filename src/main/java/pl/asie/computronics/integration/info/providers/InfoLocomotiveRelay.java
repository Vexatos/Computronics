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
import pl.asie.computronics.integration.railcraft.tile.TileLocomotiveRelay;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.StringUtil;

import java.util.List;

/**
 * @author Vexatos
 */
public class InfoLocomotiveRelay extends ComputronicsInfoProvider {

	@Override
	@Optional.Method(modid = Mods.Waila)
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
	@Optional.Method(modid = Mods.Waila)
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
		if(te instanceof TileLocomotiveRelay) {
			TileLocomotiveRelay relay = (TileLocomotiveRelay) te;
			tag.setBoolean("bound", relay.isBound());
		}
		return tag;
	}

	@Override
	protected String getUID() {
		return "locomotive_relay";
	}

	@Override
	@Optional.Method(modid = Mods.TheOneProbe)
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
		TileEntity tile = world.getTileEntity(data.getPos());
		if(!(tile instanceof TileLocomotiveRelay)) {
			return;
		}
		TileLocomotiveRelay relay = (TileLocomotiveRelay) tile;
		String boundKey = "tooltip.computronics.waila.relay." + (relay.isBound() ? "bound" : "notbound");
		probeInfo.text(StringUtil.localize(boundKey));
	}
}


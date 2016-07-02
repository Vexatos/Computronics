package pl.asie.computronics.integration.info.providers;

import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Node;
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
import pl.asie.computronics.util.StringUtil;
import pl.asie.computronics.util.internal.IComputronicsPeripheral;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vexatos
 */
public class InfoPeripheral extends ComputronicsInfoProvider {

	@Override
	@Optional.Method(modid = Mods.Waila)
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
		IWailaConfigHandler config) {

		if(Mods.isLoaded(Mods.OpenComputers) && ConfigValues.OCAddress.getValue(config)) {
			NBTTagCompound nbt = accessor.getNBTData();
			currenttip = getInfo_OC(nbt, currenttip);
		}
		return currenttip;
	}

	@Optional.Method(modid = Mods.OpenComputers)
	private List<String> getInfo_OC(NBTTagCompound nbt, List<String> currenttip) {
		NBTTagCompound node = nbt.getCompoundTag("oc:node");
		if(node.hasKey("address")) {
			currenttip.add(StringUtil.localizeAndFormat("oc:gui.Analyzer.Address", node.getString("address")));
		}
		return currenttip;
	}

	@Override
	@Optional.Method(modid = Mods.Waila)
	public NBTTagCompound getNBTData(EntityPlayerMP player, @Nullable TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
		if(te != null && te instanceof IComputronicsPeripheral) {
			if(Mods.isLoaded(Mods.OpenComputers)) {
				tag = getNBTData_OC(te, tag);
			}
		}
		return tag;
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public NBTTagCompound getNBTData_OC(@Nullable TileEntity te, NBTTagCompound tag) {
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

	@Override
	@Optional.Method(modid = Mods.TheOneProbe)
	public String getUID() {
		return "component";
	}

	@Override
	@Optional.Method(modid = Mods.TheOneProbe)
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
		TileEntity tile = world.getTileEntity(data.getPos());
		if(Mods.isLoaded(Mods.OpenComputers) && mode == ProbeMode.EXTENDED) {
			for(String s : getInfo_OC(getNBTData_OC(tile, new NBTTagCompound()), new ArrayList<String>(1))) {
				probeInfo.text(s);
			}
		}
	}
}

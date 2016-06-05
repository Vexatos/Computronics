package pl.asie.computronics.integration.info;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.reference.Mods;

import java.util.List;

/**
 * @author Vexatos
 */
@Optional.InterfaceList({
	@Optional.Interface(iface = "mcp.mobius.waila.api.IWailaDataProvider", modid = Mods.Waila),
	@Optional.Interface(iface = "mcjty.theoneprobe.api.IProbeInfoProvider", modid = Mods.TheOneProbe)
})
public class InfoComputronics implements IWailaDataProvider, IProbeInfoProvider {

	@Override
	@Optional.Method(modid = Mods.Waila)
	public ItemStack getWailaStack(IWailaDataAccessor accessor,
		IWailaConfigHandler config) {

		return accessor.getStack();
	}

	@Override
	@Optional.Method(modid = Mods.Waila)
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip,
		IWailaDataAccessor accessor, IWailaConfigHandler config) {

		for(InfoProviders p : InfoProviders.VALUES) {
			if(p.isInstance(accessor.getBlock())) {
				currenttip = p.getProvider().getWailaHead(itemStack, currenttip, accessor, config);
			}
		}

		return currenttip;
	}

	@Override
	@Optional.Method(modid = Mods.Waila)
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {

		for(InfoProviders p : InfoProviders.VALUES) {
			if(p.isInstance(accessor.getBlock())) {
				currenttip = p.getProvider().getWailaBody(itemStack, currenttip, accessor, config);
			}
		}

		return currenttip;
	}

	@Override
	@Optional.Method(modid = Mods.Waila)
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
		IWailaConfigHandler config) {

		for(InfoProviders p : InfoProviders.VALUES) {
			if(p.isInstance(accessor.getBlock())) {
				currenttip = p.getProvider().getWailaTail(itemStack, currenttip, accessor, config);
			}
		}

		return currenttip;
	}

	@Override
	@Optional.Method(modid = Mods.Waila)
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
		for(InfoProviders p : InfoProviders.VALUES) {
			if(p.isInstance(te.getBlockType())) {
				tag = p.getProvider().getNBTData(player, te, tag, world, pos);
			}
		}

		return tag;
	}

	@Override
	@Optional.Method(modid = Mods.TheOneProbe)
	public String getID() {
		return Mods.Computronics + ":block";
	}

	@Override
	@Optional.Method(modid = Mods.TheOneProbe)
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState state, IProbeHitData data) {
		TileEntity tile = world.getTileEntity(data.getPos());
		if(tile == null) {
			return;
		}
		for(InfoProviders p : InfoProviders.VALUES) {
			if(p.isInstance(state.getBlock())) {
				p.getProvider().addProbeInfo(mode, probeInfo, player, world, state, data);
			}
		}
	}
}

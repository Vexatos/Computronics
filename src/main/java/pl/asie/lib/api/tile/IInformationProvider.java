package pl.asie.lib.api.tile;

/*import cofh.api.tileentity.ITileInfo;
import net.minecraftforge.fml.common.Optional;
import gregtech.api.interfaces.tileentity.IGregTechDeviceInformation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.lib.reference.Mods;*/

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;

import java.util.List;

/**
 * Loosely inspired by CoFH's ITileInfo and generally serves as a wrapper for that and some other APIs.
 * @author asie
 */
/*@Optional.InterfaceList({
	//@Optional.Interface(iface = "gregtech.api.interfaces.tileentity.IGregTechDeviceInformation", modid = Mods.GregTech),
	@Optional.Interface(iface = "cofh.api.tileentity.ITileInfo", modid = Mods.API.CoFHTileEntities)
})*/
public interface IInformationProvider /*extends ITileInfo, IGregTechDeviceInformation*/ {

	void getInformation(EntityPlayer player, EnumFacing side, List<String> info, boolean debug);
}

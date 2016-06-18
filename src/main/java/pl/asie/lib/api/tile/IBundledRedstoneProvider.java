package pl.asie.lib.api.tile;

/*import net.minecraftforge.fml.common.Optional;
import mods.immibis.redlogic.api.wiring.IBundledEmitter;
import mods.immibis.redlogic.api.wiring.IBundledUpdatable;
import mods.immibis.redlogic.api.wiring.IConnectable;
import mrtjp.projectred.api.IBundledTile;
import pl.asie.lib.reference.Mods;*/

import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

/*@Optional.InterfaceList({
	@Optional.Interface(iface = "mods.immibis.redlogic.api.wiring.IBundledEmitter", modid = Mods.RedLogic),
	@Optional.Interface(iface = "mods.immibis.redlogic.api.wiring.IBundledUpdatable", modid = Mods.RedLogic),
	@Optional.Interface(iface = "mods.immibis.redlogic.api.wiring.IConnectable", modid = Mods.RedLogic),
	@Optional.Interface(iface = "mrtjp.projectred.api.IBundledTile", modid = Mods.ProjectRed)
})*/
public interface IBundledRedstoneProvider /*extends IBundledEmitter, IBundledUpdatable, IConnectable, IBundledTile*/ {

	public boolean canBundledConnectToInput(@Nullable EnumFacing side);

	public boolean canBundledConnectToOutput(@Nullable EnumFacing side);

	@Nullable
	public byte[] getBundledOutput(@Nullable EnumFacing side);

	public void onBundledInputChange(@Nullable EnumFacing side, @Nullable byte[] data);
}

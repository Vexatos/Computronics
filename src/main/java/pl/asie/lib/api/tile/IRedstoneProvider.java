package pl.asie.lib.api.tile;

/*import net.minecraftforge.fml.common.Optional;
import mods.immibis.redlogic.api.wiring.IConnectable;
import mods.immibis.redlogic.api.wiring.IRedstoneEmitter;
import mods.immibis.redlogic.api.wiring.IRedstoneUpdatable;
import mrtjp.projectred.api.IBundledTile;
import pl.asie.lib.reference.Mods;*/

/*@Optional.InterfaceList({
	@Optional.Interface(iface = "mods.immibis.redlogic.api.wiring.IRedstoneEmitter", modid = Mods.RedLogic),
	@Optional.Interface(iface = "mods.immibis.redlogic.api.wiring.IRedstoneUpdatable", modid = Mods.RedLogic),
	@Optional.Interface(iface = "mods.immibis.redlogic.api.wiring.IConnectable", modid = Mods.RedLogic)
})*/
public interface IRedstoneProvider /*extends IRedstoneEmitter, IRedstoneUpdatable, IConnectable, IBundledTile*/ {

	public boolean canRedstoneConnectTo(int side, int face);

	public int getRedstoneOutput(int side, int face);

	public void onRedstoneInputChange(int side, int face, int input);
}

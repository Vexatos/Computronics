package pl.asie.computronics.tile;

import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.Network;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.SidedEnvironment;
import li.cil.oc.api.network.Visibility;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.cc.ISidedPeripheral;
import pl.asie.computronics.reference.Mods;

/**
 * @author Vexatos
 */
@Optional.InterfaceList({
	@Optional.Interface(iface = "li.cil.oc.api.network.SidedEnvironment", modid = Mods.OpenComputers)
})
public class TileGrogergizer extends TileEntityPeripheralBase implements ISidedPeripheral, SidedEnvironment {

	public TileGrogergizer() {
		super();
		this.peripheralName = "grogergizer";
		node = Network.newNode(this, Visibility.Network).withConnector(1000).create();
	}

	@Override
	public Node sidedNode(ForgeDirection side) {
		return side != ForgeDirection.UP ? node() : null;
	}

	@Override
	public boolean canConnect(ForgeDirection side) {
		return side != ForgeDirection.UP;
	}

	@Override
	public boolean canConnectPeripheralOnSide(int side) {
		return false;
	}

	@Override
	public boolean connectable(int side) {
		return false;
	}

	@Override
	public short busRead(int addr) {
		return 0;
	}

	@Override
	public void busWrite(int addr, short data) {

	}

	@Override
	public String[] getMethodNames() {
		return null;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		return null;
	}
}

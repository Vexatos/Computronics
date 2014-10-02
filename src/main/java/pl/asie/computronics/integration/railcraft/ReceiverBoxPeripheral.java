package pl.asie.computronics.integration.railcraft;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import mods.railcraft.common.blocks.signals.TileBoxReceiver;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.integration.CCTilePeripheral;

/**
 * @author Vexatos
 */
public class ReceiverBoxPeripheral extends CCTilePeripheral<TileBoxReceiver> {

	public ReceiverBoxPeripheral() {
	}

	public ReceiverBoxPeripheral(TileBoxReceiver box, World world, int x, int y, int z) {
		super(box, "receiver_box", world, x, y, z);
	}

	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
		TileEntity te = world.getTileEntity(x, y, z);
		if(te != null && te instanceof TileBoxReceiver) {
			return new ReceiverBoxPeripheral((TileBoxReceiver) te, world, x, y, z);
		}
		return null;
	}

	@Override
	public String[] getMethodNames() {
		return new String[] { "getSignal" };
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
		int method, Object[] arguments) throws LuaException,
		InterruptedException {
		if(method < getMethodNames().length) {
			TileBoxReceiver box = this.tile;
			if(!box.isSecure()) {
				int signal = box.getTriggerAspect().ordinal();
				if(signal == 5) {
					signal = -1;
				}
				return new Object[] { signal };
			} else {
				return new Object[] { null, "signal receiver box is locked" };
			}
		}
		return null;
	}
}

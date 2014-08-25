package pl.asie.computronics.integration.railcraft;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import mods.railcraft.common.blocks.signals.TileBoxReceiver;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * @author Vexatos
 */
public class ReceiverBoxPeripheral implements IPeripheral, IPeripheralProvider {
	private TileEntity box;
	private IBlockAccess w;
	private int x, y, z;

	public ReceiverBoxPeripheral() {
	}

	public ReceiverBoxPeripheral(TileEntity box, World world, int x2, int y2, int z2) {
		this.box = box;
		w = world;
		x = x2;
		y = y2;
		z = z2;
	}

	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
		TileEntity te = world.getTileEntity(x, y, z);
		if(te != null && te instanceof TileBoxReceiver) {
			return new ReceiverBoxPeripheral(te, world, x, y, z);
		}
		return null;
	}

	@Override
	public String getType() {
		return "receiver_box";
	}

	@Override
	public String[] getMethodNames() {
		return new String[] { "getSignal" };
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
		int method, Object[] arguments) throws LuaException,
		InterruptedException {
		if(method < 1) {
			TileBoxReceiver box = (TileBoxReceiver) this.box;
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

	@Override
	public void attach(IComputerAccess computer) {
	}

	@Override
	public void detach(IComputerAccess computer) {
	}

	@Override
	public boolean equals(IPeripheral other) {
		if(other == null) {
			return false;
		}
		if(this == other) {
			return true;
		}
		if(other instanceof ReceiverBoxPeripheral) {
			ReceiverBoxPeripheral o = (ReceiverBoxPeripheral) other;
			if(w == o.w && x == o.x && z == o.z && y == o.y) {
				return true;
			}
		}

		return false;
	}
}

package pl.asie.computronics.integration;

import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import li.cil.oc.api.network.BlacklistedPeripheral;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pl.asie.computronics.reference.Mods;

/**
 * @author Vexatos
 */
@Optional.Interface(iface = "li.cil.oc.api.network.BlacklistedPeripheral", modid = Mods.OpenComputers)
public abstract class CCTilePeripheral<T> implements IPeripheral, IPeripheralProvider, BlacklistedPeripheral {
	protected T tile;
	protected IBlockAccess w;
	protected int x, y, z;
	protected String name;

	public CCTilePeripheral() {

	}

	public CCTilePeripheral(T tile, final String name, World world, int x, int y, int z) {
		this.tile = tile;
		this.name = name;
		this.w = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public String getType() {
		return name;
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
		if(this.getClass().isInstance(other)) {
			CCTilePeripheral o = this.getClass().cast(other);
			if(w == o.w && x == o.x && z == o.z && y == o.y) {
				return true;
			}
		}

		return false;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public boolean isPeripheralBlacklisted() {
		return true;
	}
}

package pl.asie.computronics.integration;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * @author Vexatos
 */
public abstract class CCTilePeripheral<T> implements IPeripheral, IPeripheralProvider {
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
}

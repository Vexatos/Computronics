package pl.asie.computronics.integration.railcraft;

import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverTileEntity;
import mods.railcraft.api.electricity.IElectricGrid;
import net.minecraft.world.World;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;

/**
 * @author Vexatos
 */
public class DriverElectricGrid extends DriverTileEntity {

	public class ManagedEnvironmentElectricGrid extends ManagedEnvironmentOCTile<IElectricGrid> {

		public ManagedEnvironmentElectricGrid(IElectricGrid tile, String name) {
			super(tile, name);
		}

		@Callback(doc = "function():number; Returns the current charge of the electric tile")
		public Object[] getCharge(Context c, Arguments a) {
			return new Object[] { tile.getChargeHandler().getCharge() };
		}

		@Callback(doc = "function():number; Returns the maximum capacity of the electric tile")
		public Object[] getCapacity(Context c, Arguments a) {
			return new Object[] { tile.getChargeHandler().getCapacity() };
		}

		@Callback(doc = "function():number; Returns the loss per tick of the electric tile.")
		public Object[] getLoss(Context c, Arguments a) {
			return new Object[] { tile.getChargeHandler().getLosses() };
		}
	}

	@Override
	public Class<?> getTileEntityClass() {
		return IElectricGrid.class;
	}

	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
		return new ManagedEnvironmentElectricGrid((IElectricGrid) world.getTileEntity(x, y, z), "electric_tile");
	}
}

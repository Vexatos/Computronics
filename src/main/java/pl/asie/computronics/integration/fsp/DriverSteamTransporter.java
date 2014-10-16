package pl.asie.computronics.integration.fsp;

import flaxbeard.steamcraft.api.ISteamTransporter;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverTileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;

public class DriverSteamTransporter extends DriverTileEntity {
	public class ManagedEnvironmentST extends ManagedEnvironmentOCTile<ISteamTransporter> {
		public ManagedEnvironmentST(ISteamTransporter tile, String name) {
			super(tile, name);
		}

		@Callback(direct = true)
		public Object[] getSteamPressure(Context c, Arguments a) {
			return new Object[] { tile.getPressure() };
		}

		@Callback(direct = true)
		public Object[] getSteamCapacity(Context c, Arguments a) {
			return new Object[] { tile.getCapacity() };
		}

		@Callback(direct = true)
		public Object[] getSteamAmount(Context c, Arguments a) {
			return new Object[] { tile.getSteam() };
		}
	}

	@Override
	public Class<?> getTileEntityClass() {
		return ISteamTransporter.class;
	}

	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
		return new ManagedEnvironmentST((ISteamTransporter) world.getTileEntity(x, y, z), "steam_transporter");
	}
}

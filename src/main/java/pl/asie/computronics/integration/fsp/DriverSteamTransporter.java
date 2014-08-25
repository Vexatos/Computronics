package pl.asie.computronics.integration.fsp;

import flaxbeard.steamcraft.api.ISteamTransporter;
import li.cil.oc.api.Network;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.DriverTileEntity;
import net.minecraft.world.World;

public class DriverSteamTransporter extends DriverTileEntity {
	public class ManagedEnvironmentST extends li.cil.oc.api.prefab.ManagedEnvironment {
		private ISteamTransporter st;
		
		public ManagedEnvironmentST(ISteamTransporter st) {
			this.st = st;
			node = Network.newNode(this, Visibility.Network).withComponent("steamTransporter", Visibility.Network).create();
		}
		
		@Callback(direct = true)
		public Object[] getSteamPressure(Context c, Arguments a) {
			return new Object[]{st.getPressure()};
		}
		
		@Callback(direct = true)
		public Object[] getSteamCapacity(Context c, Arguments a) {
			return new Object[]{st.getCapacity()};
		}
		
		@Callback(direct = true)
		public Object[] getSteamAmount(Context c, Arguments a) {
			return new Object[]{st.getSteam()};
		}
	}
	
	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
		return new ManagedEnvironmentST((ISteamTransporter)world.getTileEntity(x, y, z));
	}

	@Override
	public Class<?> getTileEntityClass() {
		return ISteamTransporter.class;
	}
}

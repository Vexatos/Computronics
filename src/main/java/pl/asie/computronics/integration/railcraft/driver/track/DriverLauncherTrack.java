package pl.asie.computronics.integration.railcraft.driver.track;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import mods.railcraft.common.blocks.tracks.TileTrack;
import mods.railcraft.common.blocks.tracks.TrackLauncher;
import mods.railcraft.common.core.RailcraftConfig;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheral;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;
import pl.asie.computronics.reference.Names;

/**
 * @author Vexatos
 */
public class DriverLauncherTrack {

	private static Object[] getForce(TrackLauncher tile) {
		return new Object[] { ((int) tile.getLaunchForce()) };
	}

	private static Object[] setForce(TrackLauncher tile, Object[] arguments) {
		int force = ((Double) arguments[0]).intValue();
		if(force >= 5 && force <= RailcraftConfig.getLaunchRailMaxForce()) {
			tile.setLaunchForce(force);
			tile.sendUpdateToClient();
			return new Object[] { true };
		}
		return new Object[] { false, "not a valid force value, needs to be between 5 and " + RailcraftConfig.getLaunchRailMaxForce() };
	}

	public static class OCDriver extends DriverSidedTileEntity {

		public static class InternalManagedEnvironment extends ManagedEnvironmentOCTile<TrackLauncher> {

			public InternalManagedEnvironment(TrackLauncher tile) {
				super(tile, Names.Railcraft_LauncherTrack);
			}

			@Callback(doc = "function():number; returns the current force of the track")
			public Object[] getForce(Context c, Arguments a) {
				return DriverLauncherTrack.getForce(tile);
			}

			@Callback(doc = "function():boolean; sets the force of the track; returns true on success")
			public Object[] setForce(Context c, Arguments a) {
				a.checkInteger(0);
				return DriverLauncherTrack.setForce(tile, a.toArray());
			}
		}

		@Override
		public Class<?> getTileEntityClass() {
			return TileTrack.class;
		}

		@Override
		public boolean worksWith(World world, int x, int y, int z, ForgeDirection side) {
			TileEntity tileEntity = world.getTileEntity(x, y, z);
			return (tileEntity != null) && tileEntity instanceof TileTrack
				&& ((TileTrack) tileEntity).getTrackInstance() instanceof TrackLauncher;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
			return new InternalManagedEnvironment((TrackLauncher) ((TileTrack) world.getTileEntity(x, y, z)).getTrackInstance());
		}
	}

	public static class CCDriver extends CCMultiPeripheral<TrackLauncher> {

		public CCDriver() {
		}

		public CCDriver(TrackLauncher track, World world, int x, int y, int z) {
			super(track, Names.Railcraft_LauncherTrack, world, x, y, z);
		}

		@Override
		public IMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof TileTrack && ((TileTrack) te).getTrackInstance() instanceof TrackLauncher) {
				return new CCDriver((TrackLauncher) ((TileTrack) te).getTrackInstance(), world, x, y, z);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getForce", "setForce" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			switch(method) {
				case 0: {
					return DriverLauncherTrack.getForce(tile);
				}
				case 1: {
					if(arguments.length < 1 || !(arguments[0] instanceof Double)) {
						throw new LuaException("first argument needs to be a number");
					}
					return DriverLauncherTrack.setForce(tile, arguments);
				}
			}
			return null;
		}
	}
}

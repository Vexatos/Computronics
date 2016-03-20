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
import mods.railcraft.common.blocks.tracks.TrackPriming;
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
public class DriverPrimingTrack {

	private static Object[] getFuse(TrackPriming tile) {
		return new Object[] { tile.getFuse() };
	}

	private static Object[] setFuse(TrackPriming tile, Object[] arguments) {
		int fuse = ((Double) arguments[0]).intValue();
		if(fuse >= 0 && fuse <= 500) {
			tile.setFuse((short) fuse);
			tile.sendUpdateToClient();
			return new Object[] { true };
		}
		return new Object[] { false, "not a valid fuse time value, needs to be between 0 and 500" };
	}

	public static class OCDriver extends DriverSidedTileEntity {

		public static class InternalManagedEnvironment extends ManagedEnvironmentOCTile<TrackPriming> {

			public InternalManagedEnvironment(TrackPriming tile) {
				super(tile, Names.Railcraft_PrimingTrack);
			}

			@Callback(doc = "function():number; returns the current fuse time, in ticks, of the track")
			public Object[] getFuse(Context c, Arguments a) {
				return DriverPrimingTrack.getFuse(tile);
			}

			@Callback(doc = "function():boolean; sets the fuse time, in ticks,  of the track; returns true on success")
			public Object[] setFuse(Context c, Arguments a) {
				a.checkInteger(0);
				return DriverPrimingTrack.setFuse(tile, a.toArray());
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
				&& ((TileTrack) tileEntity).getTrackInstance() instanceof TrackPriming;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
			return new InternalManagedEnvironment((TrackPriming) ((TileTrack) world.getTileEntity(x, y, z)).getTrackInstance());
		}
	}

	public static class CCDriver extends CCMultiPeripheral<TrackPriming> {

		public CCDriver() {
		}

		public CCDriver(TrackPriming track, World world, int x, int y, int z) {
			super(track, Names.Railcraft_PrimingTrack, world, x, y, z);
		}

		@Override
		public IMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof TileTrack && ((TileTrack) te).getTrackInstance() instanceof TrackPriming) {
				return new CCDriver((TrackPriming) ((TileTrack) te).getTrackInstance(), world, x, y, z);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getFuse", "setFuse" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			switch(method) {
				case 0: {
					return DriverPrimingTrack.getFuse(tile);
				}
				case 1: {
					if(arguments.length < 1 || !(arguments[0] instanceof Double)) {
						throw new LuaException("first argument needs to be a number");
					}
					return DriverPrimingTrack.setFuse(tile, arguments);
				}
			}
			return null;
		}
	}
}

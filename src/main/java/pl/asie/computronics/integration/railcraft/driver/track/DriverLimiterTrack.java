package pl.asie.computronics.integration.railcraft.driver.track;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import li.cil.oc.api.prefab.ManagedEnvironment;
import mods.railcraft.common.blocks.tracks.TileTrack;
import mods.railcraft.common.blocks.tracks.TrackLimiter;
import net.minecraft.nbt.NBTTagCompound;
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
public class DriverLimiterTrack {

	private static Object[] setLimit(TrackLimiter tile, Object[] arguments) {
		byte mode = ((Double) arguments[0]).byteValue();
		NBTTagCompound data = new NBTTagCompound();
		tile.writeToNBT(data);
		data.setByte("mode", (byte) Math.abs(mode - 4));
		tile.readFromNBT(data);
		tile.sendUpdateToClient();
		return new Object[] { true };
	}

	private static Object[] getLimit(TrackLimiter tile) {
		NBTTagCompound data = new NBTTagCompound();
		tile.writeToNBT(data);
		return new Object[] { data.hasKey("mode") ? Math.abs(data.getByte("mode") % 4 - 4) : null };
	}

	public static class OCDriver extends DriverSidedTileEntity {

		public static class InternalManagedEnvironment extends ManagedEnvironmentOCTile<TrackLimiter> {

			public InternalManagedEnvironment(TrackLimiter tile) {
				super(tile, Names.Railcraft_LimiterTrack);
			}

			@Callback(doc = "function(mode:number):boolean; sets the speed limit to the specified value; returns true on success")
			public Object[] setLimit(Context c, Arguments a) {
				a.checkInteger(0);
				if(a.checkInteger(0) > 4 || a.checkInteger(0) < 1) {
					throw new IllegalArgumentException(
						"bad argument #1 (number between 1 and 4 expected, got " + a.checkInteger(0) + ")");
				}
				return DriverLimiterTrack.setLimit(tile, a.toArray());
			}

			@Callback(doc = "function():number; returns the current speed limit")
			public Object[] getLimit(Context c, Arguments a) {
				return DriverLimiterTrack.getLimit(tile);
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
				&& ((TileTrack) tileEntity).getTrackInstance() instanceof TrackLimiter;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
			return new InternalManagedEnvironment((TrackLimiter) ((TileTrack) world.getTileEntity(x, y, z)).getTrackInstance());
		}
	}

	public static class CCDriver extends CCMultiPeripheral<TrackLimiter> {

		public CCDriver() {
		}

		public CCDriver(TrackLimiter track, World world, int x, int y, int z) {
			super(track, Names.Railcraft_LimiterTrack, world, x, y, z);
		}

		@Override
		public IMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof TileTrack && ((TileTrack) te).getTrackInstance() instanceof TrackLimiter) {
				return new CCDriver((TrackLimiter) ((TileTrack) te).getTrackInstance(), world, x, y, z);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "setLimit", "getLimit" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
			InterruptedException {
			switch(method) {
				case 0: {
					if(arguments.length < 1 || !(arguments[0] instanceof Double)) {
						throw new LuaException("first argument needs to be a number");
					}
					if((Double) arguments[0] > 4 || (Double) arguments[0] < 1) {
						throw new LuaException("mode needs to be between 1 and 4");
					}
					return DriverLimiterTrack.setLimit(tile, arguments);
				}
				case 1: {
					return DriverLimiterTrack.getLimit(tile);
				}
			}
			return null;
		}
	}
}

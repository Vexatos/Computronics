package pl.asie.computronics.integration.railcraft;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.prefab.DriverTileEntity;
import li.cil.oc.api.prefab.ManagedEnvironment;
import mods.railcraft.common.blocks.tracks.TileTrack;
import mods.railcraft.common.blocks.tracks.TrackLimiter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.integration.CCTilePeripheral;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;
import pl.asie.computronics.reference.Names;

/**
 * @author Vexatos
 */
public class DriverLimiterTrack {

	private static Object[] setLimit(TileTrack tile, Object[] arguments) {
		byte mode = ((Double) arguments[0]).byteValue();
		mode--;
		NBTTagCompound data = new NBTTagCompound();
		tile.getTrackInstance().writeToNBT(data);
		data.setByte("mode", mode);
		tile.getTrackInstance().readFromNBT(data);
		((TrackLimiter) tile.getTrackInstance()).sendUpdateToClient();
		return new Object[] { true };
	}

	private static Object[] getLimit(TileTrack tile) {
		NBTTagCompound data = new NBTTagCompound();
		tile.getTrackInstance().writeToNBT(data);
		return new Object[] { data.getByte("mode") % 4 + 1 };
	}

	private static Object[] isPowered(TileTrack tile) {
		return new Object[] { ((TrackLimiter) tile.getTrackInstance()).isPowered() };
	}

	public static class OCDriver extends DriverTileEntity {
		public class InternalManagedEnvironment extends ManagedEnvironmentOCTile<TileTrack> {
			public InternalManagedEnvironment(TileTrack tile) {
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

			@Callback(doc = "function():boolean; returns whether the track is currently receiving a redstone signal")
			public Object[] isPowered(Context c, Arguments a) {
				return DriverLimiterTrack.isPowered(tile);
			}
		}

		@Override
		public Class<?> getTileEntityClass() {
			return TileTrack.class;
		}

		@Override
		public boolean worksWith(World world, int x, int y, int z) {
			TileEntity tileEntity = world.getTileEntity(x, y, z);
			return (tileEntity != null) && tileEntity instanceof TileTrack
				&& ((TileTrack) tileEntity).getTrackInstance() instanceof TrackLimiter;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
			return new InternalManagedEnvironment((TileTrack) world.getTileEntity(x, y, z));
		}
	}

	public static class CCDriver extends CCTilePeripheral<TileTrack> {
		public CCDriver() {
		}

		public CCDriver(TileTrack track, World world, int x, int y, int z) {
			super(track, Names.Railcraft_LimiterTrack, world, x, y, z);
		}

		@Override
		public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof TileTrack && ((TileTrack) te).getTrackInstance() instanceof TrackLimiter) {
				return new CCDriver((TileTrack) te, world, x, y, z);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "setLimit", "getLimit", "isPowered" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
			InterruptedException {
			switch(method){
				case 0:{
					if(arguments.length < 1 || !(arguments[0] instanceof Double)) {
						throw new LuaException("first argument needs to be a number");
					}
					if((Double) arguments[0] > 4 || (Double) arguments[0] < 1) {
						throw new LuaException("mode needs to be between 1 and 4");
					}
					return DriverLimiterTrack.setLimit(tile, arguments);
				}
				case 1:{
					return DriverLimiterTrack.getLimit(tile);
				}
				case 2:{
					return DriverLimiterTrack.isPowered(tile);
				}
			}
			return null;
		}
	}
}

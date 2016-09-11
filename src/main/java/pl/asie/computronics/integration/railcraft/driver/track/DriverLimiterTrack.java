package pl.asie.computronics.integration.railcraft.driver.track;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.driver.SidedBlock;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.prefab.ManagedEnvironment;
import mods.railcraft.api.tracks.IOutfittedTrackTile;
import mods.railcraft.common.blocks.tracks.outfitted.kits.TrackKitLimiter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheral;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.integration.NamedManagedEnvironment;
import pl.asie.computronics.reference.Names;

/**
 * @author Vexatos
 */
public class DriverLimiterTrack {

	private static Object[] setLimit(TrackKitLimiter tile, Object[] arguments) {
		byte mode = ((Double) arguments[0]).byteValue();
		NBTTagCompound data = new NBTTagCompound();
		tile.writeToNBT(data);
		data.setByte("mode", (byte) Math.abs(mode - 4));
		tile.readFromNBT(data);
		tile.sendUpdateToClient();
		return new Object[] { true };
	}

	private static Object[] getLimit(TrackKitLimiter tile) {
		NBTTagCompound data = new NBTTagCompound();
		tile.writeToNBT(data);
		return new Object[] { data.hasKey("mode") ? Math.abs(data.getByte("mode") % 4 - 4) : null };
	}

	public static class OCDriver implements SidedBlock {

		public static class InternalManagedEnvironment extends NamedManagedEnvironment<TrackKitLimiter> {

			public InternalManagedEnvironment(TrackKitLimiter tile) {
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
		public boolean worksWith(World world, BlockPos pos, EnumFacing side) {
			TileEntity tileEntity = world.getTileEntity(pos);
			return (tileEntity != null) && tileEntity instanceof IOutfittedTrackTile
				&& ((IOutfittedTrackTile) tileEntity).getTrackKitInstance() instanceof TrackKitLimiter;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, BlockPos pos, EnumFacing side) {
			return new InternalManagedEnvironment((TrackKitLimiter) ((IOutfittedTrackTile) world.getTileEntity(pos)).getTrackKitInstance());
		}
	}

	public static class CCDriver extends CCMultiPeripheral<TrackKitLimiter> {

		public CCDriver() {
		}

		public CCDriver(TrackKitLimiter track, World world, BlockPos pos) {
			super(track, Names.Railcraft_LimiterTrack, world, pos);
		}

		@Override
		public IMultiPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
			TileEntity te = world.getTileEntity(pos);
			if(te != null && te instanceof IOutfittedTrackTile && ((IOutfittedTrackTile) te).getTrackKitInstance() instanceof TrackKitLimiter) {
				return new CCDriver((TrackKitLimiter) ((IOutfittedTrackTile) te).getTrackKitInstance(), world, pos);
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

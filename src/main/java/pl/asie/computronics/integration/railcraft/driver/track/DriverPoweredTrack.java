package pl.asie.computronics.integration.railcraft.driver.track;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import mods.railcraft.api.tracks.IOutfittedTrackTile;
import mods.railcraft.api.tracks.ITrackKitPowered;
import mods.railcraft.common.util.misc.ISecureObject;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheral;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.integration.NamedManagedEnvironment;
import pl.asie.computronics.reference.Names;

import javax.annotation.Nullable;

/**
 * @author Vexatos
 */
public class DriverPoweredTrack {

	private static Object[] isPowered(ITrackKitPowered tile) {
		if(!(tile instanceof ISecureObject && ((ISecureObject) tile).isSecure())) {
			return new Object[] { tile.isPowered() };
		} else {
			return new Object[] { null, "track is locked" };
		}
	}

	public static class OCDriver extends DriverTrack<ITrackKitPowered> {

		public static class InternalManagedEnvironment extends NamedManagedEnvironment<ITrackKitPowered> {

			public InternalManagedEnvironment(ITrackKitPowered tile) {
				super(tile, Names.Railcraft_PoweredTrack);
			}

			@Override
			public int priority() {
				return -1;
			}

			@Callback(doc = "function():boolean; returns whether the track is currently receiving a redstone signal, or nil if it cannot be accessed")
			public Object[] isPowered(Context c, Arguments a) {
				return DriverPoweredTrack.isPowered(tile);
			}
		}

		public OCDriver() {
			super(ITrackKitPowered.class);
		}

		@Nullable
		@Override
		protected NamedManagedEnvironment<ITrackKitPowered> createEnvironment(World world, BlockPos pos, EnumFacing side, ITrackKitPowered tile) {
			return new InternalManagedEnvironment(tile);
		}
	}

	public static class CCDriver extends CCMultiPeripheral<ITrackKitPowered> {

		public CCDriver() {
		}

		public CCDriver(ITrackKitPowered track, World world, BlockPos pos) {
			super(track, Names.Railcraft_PoweredTrack, world, pos);
		}

		@Override
		public int peripheralPriority() {
			return -1;
		}

		@Override
		public IMultiPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
			TileEntity te = world.getTileEntity(pos);
			if(te != null && te instanceof IOutfittedTrackTile && ((IOutfittedTrackTile) te).getTrackKitInstance() instanceof ITrackKitPowered) {
				return new CCDriver((ITrackKitPowered) ((IOutfittedTrackTile) te).getTrackKitInstance(), world, pos);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "isPowered" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			return new Object[] { DriverPoweredTrack.isPowered(tile) };
		}
	}
}

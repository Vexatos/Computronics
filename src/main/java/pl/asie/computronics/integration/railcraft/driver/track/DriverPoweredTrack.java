package pl.asie.computronics.integration.railcraft.driver.track;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import mods.railcraft.api.tracks.ITrackPowered;
import mods.railcraft.common.blocks.signals.ISecure;
import mods.railcraft.common.blocks.tracks.TileTrack;
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
public class DriverPoweredTrack {

	private static Object[] isPowered(ITrackPowered tile) {
		if(!(tile instanceof ISecure && ((ISecure) tile).isSecure())) {
			return new Object[] { tile.isPowered() };
		} else {
			return new Object[] { null, "track is locked" };
		}
	}

	public static class OCDriver extends DriverSidedTileEntity {

		public static class InternalManagedEnvironment extends ManagedEnvironmentOCTile<ITrackPowered> {

			public InternalManagedEnvironment(ITrackPowered tile) {
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

		@Override
		public Class<?> getTileEntityClass() {
			return TileTrack.class;
		}

		@Override
		public boolean worksWith(World world, int x, int y, int z, ForgeDirection side) {
			TileEntity tileEntity = world.getTileEntity(x, y, z);
			return (tileEntity != null) && tileEntity instanceof TileTrack
				&& ((TileTrack) tileEntity).getTrackInstance() instanceof ITrackPowered;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
			return new InternalManagedEnvironment((ITrackPowered) ((TileTrack) world.getTileEntity(x, y, z)).getTrackInstance());
		}
	}

	public static class CCDriver extends CCMultiPeripheral<ITrackPowered> {

		public CCDriver() {
		}

		public CCDriver(ITrackPowered track, World world, int x, int y, int z) {
			super(track, Names.Railcraft_PoweredTrack, world, x, y, z);
		}

		@Override
		public int peripheralPriority() {
			return -1;
		}

		@Override
		public IMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof TileTrack && ((TileTrack) te).getTrackInstance() instanceof ITrackPowered) {
				return new CCDriver((ITrackPowered) ((TileTrack) te).getTrackInstance(), world, x, y, z);
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

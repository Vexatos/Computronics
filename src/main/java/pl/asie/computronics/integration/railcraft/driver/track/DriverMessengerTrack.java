package pl.asie.computronics.integration.railcraft.driver.track;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import mods.railcraft.api.tracks.IOutfittedTrackTile;
import mods.railcraft.common.blocks.tracks.outfitted.kits.TrackKitMessenger;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheral;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.integration.NamedManagedEnvironment;
import pl.asie.computronics.reference.Names;

import javax.annotation.Nullable;

/**
 * @author Vexatos
 */
public class DriverMessengerTrack {

	public static class OCDriver extends DriverTrack<TrackKitMessenger> {

		public class InternalManagedEnvironment extends NamedManagedEnvironment<TrackKitMessenger> {

			public InternalManagedEnvironment(TrackKitMessenger tile) {
				super(tile, Names.Railcraft_MessengerTrack);
			}

			@Callback(doc = "function():string; Returns the title the track is set to.")
			public Object[] getTitle(Context c, Arguments a) {
				return new Object[] { tile.getTitle().getFormattedText() };
			}

			@Callback(doc = "function():string; Returns the subtitle the track is set to.")
			public Object[] getSubtitle(Context c, Arguments a) {
				return new Object[] { tile.getSubtitle().getFormattedText() };
			}

			@Callback(doc = "function(title:string):string; Sets the title the track is set to. Returns true on success.")
			public Object[] setTitle(Context c, Arguments a) {
				tile.setTitle(new TextComponentString(a.checkString(0)));
				return new Object[] { true };
			}

			@Callback(doc = "function(subtitle:string):string; Sets the subtitle the track is set to. Returns true on success.")
			public Object[] setSubtitle(Context c, Arguments a) {
				tile.setSubtitle(new TextComponentString(a.checkString(0)));
				return new Object[] { true };
			}
		}

		public OCDriver() {
			super(TrackKitMessenger.class);
		}

		@Nullable
		@Override
		protected NamedManagedEnvironment<TrackKitMessenger> createEnvironment(World world, BlockPos pos, EnumFacing side, TrackKitMessenger tile) {
			return new InternalManagedEnvironment(tile);
		}
	}

	public static class CCDriver extends CCMultiPeripheral<TrackKitMessenger> {

		public CCDriver() {
		}

		public CCDriver(TrackKitMessenger tile, World world, BlockPos pos) {
			super(tile, Names.Railcraft_MessengerTrack, world, pos);
		}

		@Override
		public int peripheralPriority() {
			return 2;
		}

		@Override
		public IMultiPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
			TileEntity te = world.getTileEntity(pos);
			if(te != null && te instanceof IOutfittedTrackTile && ((IOutfittedTrackTile) te).getTrackKitInstance() instanceof TrackKitMessenger) {
				return new CCDriver((TrackKitMessenger) ((IOutfittedTrackTile) te).getTrackKitInstance(), world, pos);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getTitle", "getSubtitle", "setTitle", "setSubtitle" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			switch(method) {
				case 0: {
					return new Object[] { tile.getTitle().getFormattedText() };
				}
				case 1: {
					return new Object[] { tile.getSubtitle().getFormattedText() };
				}
				case 2: {
					if(arguments.length < 1 || !(arguments[0] instanceof String)) {
						throw new LuaException("first argument needs to be a string");
					}
					tile.setTitle(new TextComponentString((String) arguments[0]));
					return new Object[] { true };
				}
				case 3: {
					if(arguments.length < 1 || !(arguments[0] instanceof String)) {
						throw new LuaException("first argument needs to be a string");
					}
					tile.setSubtitle(new TextComponentString((String) arguments[0]));
					return new Object[] { true };
				}
			}
			return new Object[] {};
		}
	}
}

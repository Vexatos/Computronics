package pl.asie.computronics.integration.railcraft.driver.track;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;
import mods.railcraft.api.tracks.IOutfittedTrackTile;
import mods.railcraft.common.blocks.tracks.outfitted.kits.TrackKitRouting;
import mods.railcraft.common.items.ItemTicketGold;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheral;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.integration.NamedManagedEnvironment;
import pl.asie.computronics.reference.Names;

import javax.annotation.Nullable;

;

/**
 * @author Vexatos
 */
public class DriverRoutingTrack {

	private static Object[] setDestination(TrackKitRouting tile, Object[] arguments) {
		ItemStack ticket = tile.getInventory().getStackInSlot(0);
		if(ticket != null && ticket.getItem() instanceof ItemTicketGold) {
			if(!tile.isSecure()) {
				String destination = (String) arguments[0];
				tile.setTicket(destination, destination, ItemTicketGold.getOwner(ticket));
				ItemTicketGold.setTicketData(ticket, destination, destination, ItemTicketGold.getOwner(ticket));
				return new Object[] { true };
			} else {
				return new Object[] { false, "routing track is locked" };
			}
		} else {
			return new Object[] { false, "there is no golden ticket inside the track" };
		}
	}

	private static Object[] getDestination(TrackKitRouting tile) {
		ItemStack ticket = tile.getInventory().getStackInSlot(0);
		if(ticket != null && ticket.getItem() instanceof ItemTicketGold) {
			if(!tile.isSecure()) {
				return new Object[] { ItemTicketGold.getDestination(ticket) };
			} else {
				return new Object[] { false, "routing track is locked" };
			}
		} else {
			return new Object[] { false, "there is no golden ticket inside the track" };
		}
	}

	public static class OCDriver extends DriverTrack<TrackKitRouting> {

		public static class InternalManagedEnvironment extends NamedManagedEnvironment<TrackKitRouting> {

			public InternalManagedEnvironment(TrackKitRouting track) {
				super(track, Names.Railcraft_RoutingTrack);
			}

			@Callback(doc = "function(destination:String):boolean; Sets the ticket destination")
			public Object[] setDestination(Context c, Arguments a) {
				a.checkString(0);
				return DriverRoutingTrack.setDestination(tile, a.toArray());
			}

			@Callback(doc = "function():String; gets the destination the routing track is currently set to")
			public Object[] getDestination(Context c, Arguments a) {
				return DriverRoutingTrack.getDestination(tile);
			}

        /*@Callback(doc = "function():String; gets the current owner of the ticket inside the track")
		public Object[] getOwner(Context c, Arguments a) {
            ItemStack ticket = ((TrackKitRouting) ((ITrackTile) track).getTrackKitInstance()).getInventory().getStackInSlot(0);
            if(ticket != null && ticket.getItem() instanceof ItemTicketGold) {
                if(!((TrackKitRouting) ((ITrackTile) track).getTrackKitInstance()).isSecure()) {
                    return new Object[] { ItemTicketGold.getOwner(ticket) };
                } else {
                    return new Object[] { false, "routing track is locked" };
                }
            } else {
                return new Object[] { false, "there is no golden ticket inside the track" };
            }
        }*/
		}

		public OCDriver() {
			super(TrackKitRouting.class);
		}

		@Nullable
		@Override
		protected NamedManagedEnvironment<TrackKitRouting> createEnvironment(World world, BlockPos pos, EnumFacing side, TrackKitRouting tile) {
			return new InternalManagedEnvironment(tile);
		}
	}

	public static class CCDriver extends CCMultiPeripheral<TrackKitRouting> {

		public CCDriver() {
		}

		public CCDriver(TrackKitRouting track, World world, BlockPos pos) {
			super(track, Names.Railcraft_RoutingTrack, world, pos);
		}

		@Override
		public IMultiPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
			TileEntity te = world.getTileEntity(pos);
			if(te != null && te instanceof IOutfittedTrackTile && ((IOutfittedTrackTile) te).getTrackKitInstance() instanceof TrackKitRouting) {
				return new CCDriver((TrackKitRouting) ((IOutfittedTrackTile) te).getTrackKitInstance(), world, pos);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "setDestination", "getDestination" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
			InterruptedException {
			switch(method) {
				case 0: {
					if(arguments.length < 1 || !(arguments[0] instanceof String)) {
						throw new LuaException("first argument needs to be a string");
					}
					return DriverRoutingTrack.setDestination(tile, arguments);
				}
				case 1: {
					return DriverRoutingTrack.getDestination(tile);
				}
			}
			return null;
		}
	}
}

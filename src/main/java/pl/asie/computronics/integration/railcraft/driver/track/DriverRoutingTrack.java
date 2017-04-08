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
import mods.railcraft.common.blocks.tracks.TrackRouting;
import mods.railcraft.common.items.ItemTicketGold;
import net.minecraft.item.ItemStack;
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
public class DriverRoutingTrack {

	private static Object[] setDestination(TrackRouting tile, Object[] arguments) {
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

	private static Object[] getDestination(TrackRouting tile) {
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

	public static class OCDriver extends DriverSidedTileEntity {

		public static class InternalManagedEnvironment extends ManagedEnvironmentOCTile<TrackRouting> {

			public InternalManagedEnvironment(TrackRouting track) {
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
            ItemStack ticket = ((TrackRouting) ((ITrackTile) track).getTrackInstance()).getInventory().getStackInSlot(0);
            if(ticket != null && ticket.getItem() instanceof ItemTicketGold) {
                if(!((TrackRouting) ((ITrackTile) track).getTrackInstance()).isSecure()) {
                    return new Object[] { ItemTicketGold.getOwner(ticket) };
                } else {
                    return new Object[] { false, "routing track is locked" };
                }
            } else {
                return new Object[] { false, "there is no golden ticket inside the track" };
            }
        }*/
		}

		@Override
		public Class<?> getTileEntityClass() {
			return TileTrack.class;
		}

		@Override
		public boolean worksWith(World world, int x, int y, int z, ForgeDirection side) {
			TileEntity tileEntity = world.getTileEntity(x, y, z);
			return (tileEntity != null) && tileEntity instanceof TileTrack
				&& ((TileTrack) tileEntity).getTrackInstance() instanceof TrackRouting;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
			return new InternalManagedEnvironment((TrackRouting) ((TileTrack) world.getTileEntity(x, y, z)).getTrackInstance());
		}
	}

	public static class CCDriver extends CCMultiPeripheral<TrackRouting> {

		public CCDriver() {
		}

		public CCDriver(TrackRouting track, World world, int x, int y, int z) {
			super(track, Names.Railcraft_RoutingTrack, world, x, y, z);
		}

		@Override
		public IMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof TileTrack && ((TileTrack) te).getTrackInstance() instanceof TrackRouting) {
				return new CCDriver((TrackRouting) ((TileTrack) te).getTrackInstance(), world, x, y, z);
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

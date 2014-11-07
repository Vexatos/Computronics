package pl.asie.computronics.integration.railcraft;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.prefab.DriverTileEntity;
import li.cil.oc.api.prefab.ManagedEnvironment;
import mods.railcraft.common.blocks.tracks.TileTrack;
import mods.railcraft.common.blocks.tracks.TrackRouting;
import mods.railcraft.common.items.ItemTicketGold;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheral;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;
import pl.asie.computronics.reference.Names;

/**
 * @author Vexatos
 */
public class DriverRoutingTrack {

	private static Object[] setDestination(TileTrack tile, Object[] arguments) {
		ItemStack ticket = ((TrackRouting) tile.getTrackInstance()).getInventory().getStackInSlot(0);
		if(ticket != null && ticket.getItem() instanceof ItemTicketGold) {
			if(!((TrackRouting) tile.getTrackInstance()).isSecure()) {
				String destination = (String) arguments[0];
				((TrackRouting) tile.getTrackInstance()).setTicket(destination, destination, ItemTicketGold.getOwner(ticket));
				ItemTicketGold.setTicketData(ticket, destination, destination, ItemTicketGold.getOwner(ticket));
				return new Object[] { true };
			} else {
				return new Object[] { false, "routing track is locked" };
			}
		} else {
			return new Object[] { false, "there is no golden ticket inside the track" };
		}
	}

	private static Object[] getDestination(TileTrack tile) {
		ItemStack ticket = ((TrackRouting) tile.getTrackInstance()).getInventory().getStackInSlot(0);
		if(ticket != null && ticket.getItem() instanceof ItemTicketGold) {
			if(!((TrackRouting) tile.getTrackInstance()).isSecure()) {
				return new Object[] { ItemTicketGold.getDestination(ticket) };
			} else {
				return new Object[] { false, "routing track is locked" };
			}
		} else {
			return new Object[] { false, "there is no golden ticket inside the track" };
		}
	}

	private static Object[] isPowered(TileTrack tile) {
		if(!((TrackRouting) tile.getTrackInstance()).isSecure()) {
			return new Object[] { ((TrackRouting) tile.getTrackInstance()).isPowered() };
		} else {
			return new Object[] { null, "routing track is locked" };
		}
	}

	public static class OCDriver extends DriverTileEntity {

		public class InternalManagedEnvironment extends ManagedEnvironmentOCTile<TileTrack> implements NamedBlock {

			public InternalManagedEnvironment(TileTrack track) {
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

			@Callback(doc = "function():boolean; returns whether the track is currently receiving a redstone signal, or nil if it cannot be accessed")
			public Object[] isPowered(Context c, Arguments a) {
				return DriverRoutingTrack.isPowered(tile);
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
				&& ((TileTrack) tileEntity).getTrackInstance() instanceof TrackRouting;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
			return new InternalManagedEnvironment((TileTrack) world.getTileEntity(x, y, z));
		}
	}

	public static class CCDriver extends CCMultiPeripheral<TileTrack> {

		public CCDriver() {
		}

		public CCDriver(TileTrack track, World world, int x, int y, int z) {
			super(track, Names.Railcraft_RoutingTrack, world, x, y, z);
		}

		@Override
		public IMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof TileTrack && ((TileTrack) te).getTrackInstance() instanceof TrackRouting) {
				return new CCDriver((TileTrack) te, world, x, y, z);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "setDestination", "getDestination", "isPowered" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
			InterruptedException {
			switch(method){
				case 0:{
					if(arguments.length < 1 || !(arguments[0] instanceof String)) {
						throw new LuaException("first argument needs to be a string");
					}
					return DriverRoutingTrack.setDestination(tile, arguments);
				}
				case 1:{
					return DriverRoutingTrack.getDestination(tile);
				}
				case 2:{
					return DriverRoutingTrack.isPowered(tile);
				}
			}
			return null;
		}
	}
}

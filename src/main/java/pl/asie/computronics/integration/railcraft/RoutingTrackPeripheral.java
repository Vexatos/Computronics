package pl.asie.computronics.integration.railcraft;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import mods.railcraft.api.tracks.ITrackTile;
import mods.railcraft.common.blocks.tracks.TileTrack;
import mods.railcraft.common.blocks.tracks.TrackRouting;
import mods.railcraft.common.items.ItemTicketGold;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * @author Vexatos
 */
public class RoutingTrackPeripheral implements IPeripheral, IPeripheralProvider {
	private TileEntity track;
	private IBlockAccess w;
	private int x, y, z;

	public RoutingTrackPeripheral() {
	}

	public RoutingTrackPeripheral(TileEntity track, World world, int x2, int y2, int z2) {
		this.track = track;
		w = world;
		x = x2;
		y = y2;
		z = z2;
	}

	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
		TileEntity te = world.getTileEntity(x, y, z);
		if(te != null && te instanceof TileTrack && ((TileTrack) te).getTrackInstance() instanceof TrackRouting) {
			return new RoutingTrackPeripheral(te, world, x, y, z);
		}
		return null;
	}

	@Override
	public String getType() {
		return "routing_track";
	}

	@Override
	public String[] getMethodNames() {
		return new String[] { "setDestination", "getDestination", "isPowered" };
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
		int method, Object[] arguments) throws LuaException,
		InterruptedException {
		if(method < 3) {
			switch(method){
				case 0:{
					if(arguments.length < 1 || !(arguments[0] instanceof String)) {
						throw new LuaException("first argument needs to be a string");
					}
					ItemStack ticket = ((TrackRouting) ((ITrackTile) track).getTrackInstance()).getInventory().getStackInSlot(0);
					if(ticket != null && ticket.getItem() instanceof ItemTicketGold) {
						if(!((TrackRouting) ((ITrackTile) track).getTrackInstance()).isSecure()) {
							String destination = (String) arguments[0];
							((TrackRouting) ((ITrackTile) track).getTrackInstance()).setTicket(destination, destination, ItemTicketGold.getOwner(ticket));
							ItemTicketGold.setTicketData(ticket, destination, destination, ItemTicketGold.getOwner(ticket));
							return new Object[] { true };
						} else {
							return new Object[] { false, "routing track is locked" };
						}
					} else {
						return new Object[] { false, "there is no golden ticket inside the track" };
					}
				}
				case 1:{
					ItemStack ticket = ((TrackRouting) ((ITrackTile) track).getTrackInstance()).getInventory().getStackInSlot(0);
					if(ticket != null && ticket.getItem() instanceof ItemTicketGold) {
						if(!((TrackRouting) ((ITrackTile) track).getTrackInstance()).isSecure()) {
							return new Object[] { ItemTicketGold.getDestination(ticket) };
						} else {
							return new Object[] { false, "routing track is locked" };
						}
					} else {
						return new Object[] { false, "there is no golden ticket inside the track" };
					}
				}
				case 2:{
					if(!((TrackRouting) ((ITrackTile) track).getTrackInstance()).isSecure()) {
						return new Object[] { ((TrackRouting) ((ITrackTile) track).getTrackInstance()).isPowered() };
					} else {
						return new Object[] { null, "routing track is locked" };
					}
				}
			}
		}
		return null;
	}

	@Override
	public void attach(IComputerAccess computer) {
	}

	@Override
	public void detach(IComputerAccess computer) {
	}

	@Override
	public boolean equals(IPeripheral other) {
		if(other == null) {
			return false;
		}
		if(this == other) {
			return true;
		}
		if(other instanceof RoutingTrackPeripheral) {
			RoutingTrackPeripheral o = (RoutingTrackPeripheral) other;
			if(w == o.w && x == o.x && z == o.z && y == o.y) {
				return true;
			}
		}

		return false;
	}
}

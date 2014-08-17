package pl.asie.computronics.integration.railcraft;

import li.cil.oc.api.Network;
import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.DriverTileEntity;
import li.cil.oc.api.prefab.ManagedEnvironment;
import mods.railcraft.api.tracks.ITrackTile;
import mods.railcraft.common.blocks.tracks.TileTrack;
import mods.railcraft.common.blocks.tracks.TrackRouting;
import mods.railcraft.common.items.ItemTicketGold;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @author Vexatos
 */
public class DriverRoutingTrack extends DriverTileEntity {

    public class ManagedEnvironmentRoutingTrack extends ManagedEnvironment implements NamedBlock {
        private TileEntity track;

        public ManagedEnvironmentRoutingTrack(TileEntity track) {
            this.track = track;
            node = Network.newNode(this, Visibility.Network).withComponent("routing_track", Visibility.Network).create();
        }

        @Override
        public String preferredName() {
            return "routing_track";
        }

        @Callback(doc = "function(destination:String):boolean; Sets the ticket destination")
        public Object[] setDestination(Context c, Arguments a) {
            ItemStack ticket = ((TrackRouting) ((ITrackTile) track).getTrackInstance()).getInventory().getStackInSlot(0);
            if(ticket != null && ticket.getItem() instanceof ItemTicketGold) {
                if(!((TrackRouting) ((ITrackTile) track).getTrackInstance()).isSecure()) {
                    String destination = a.checkString(0);
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

        @Callback(doc = "function():String; gets the destination the routing track is currently set to")
        public Object[] getDestination(Context c, Arguments a) {
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
            if(!((TrackRouting) ((ITrackTile) track).getTrackInstance()).isSecure()) {
                return new Object[] { ((TrackRouting) ((ITrackTile) track).getTrackInstance()).isPowered() };
            } else {
                return new Object[] { null, "routing track is locked" };
            }

        }
    }

    @Override
    public Class<?> getTileEntityClass() {
        return TileTrack.class;
    }

    @Override
    public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
        if(world.getTileEntity(x, y, z) instanceof ITrackTile
            && ((ITrackTile) world.getTileEntity(x, y, z)).getTrackInstance() instanceof TrackRouting) {
            return new ManagedEnvironmentRoutingTrack(world.getTileEntity(x, y, z));
        }
        return null;
    }
}

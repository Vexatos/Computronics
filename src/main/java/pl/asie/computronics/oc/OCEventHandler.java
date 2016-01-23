package pl.asie.computronics.oc;

import li.cil.oc.api.Network;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Vexatos
 */
public class OCEventHandler {

	private final Set<TileEntity> tileQueue = new HashSet<TileEntity>();

	@SubscribeEvent
	public void onServerTick(ServerTickEvent e) {
		if(e.phase != TickEvent.Phase.START || tileQueue.isEmpty()) {
			return;
		}
		for(TileEntity tile : tileQueue) {
			Network.joinOrCreateNetwork(tile);
		}
		tileQueue.clear();
	}

	public void schedule(TileEntity tile) {
		if(!tile.isInvalid() && !tile.getWorld().isRemote) {
			tileQueue.add(tile);
		}
	}
}

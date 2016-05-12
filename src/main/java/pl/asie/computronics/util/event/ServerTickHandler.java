package pl.asie.computronics.util.event;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Vexatos
 */
public class ServerTickHandler {

	private final Set<Runnable> taskQueue = new HashSet<Runnable>();

	@SubscribeEvent
	public void onServerTick(ServerTickEvent e) {
		synchronized(taskQueue) {
			if(e.phase != TickEvent.Phase.START || taskQueue.isEmpty()) {
				return;
			}
			for(Runnable task : taskQueue) {
				task.run();
			}
			taskQueue.clear();
		}
	}

	public void schedule(Runnable task) {
		synchronized(taskQueue) {
			taskQueue.add(task);
		}
	}
}

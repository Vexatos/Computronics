package pl.asie.computronics.util.event;

import com.google.common.collect.Queues;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

import java.util.Queue;

/**
 * @author Vexatos
 */
public class ServerTickHandler {

	private final Queue<Runnable> taskQueue = Queues.newArrayDeque();

	@SubscribeEvent
	public void onServerTick(ServerTickEvent e) {
		if(e.phase != TickEvent.Phase.START || taskQueue.isEmpty()) {
			return;
		}
		
		synchronized(taskQueue) {
			while (!taskQueue.isEmpty()) {
				taskQueue.poll().run();
			}
		}
	}

	public void schedule(Runnable task) {
		synchronized(taskQueue) {
			taskQueue.add(task);
		}
	}
}

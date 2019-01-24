package pl.asie.computronics.util.event;

import com.google.common.collect.Queues;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * @author Vexatos
 */
public class ServerTickHandler {

	private final ArrayDeque<Runnable> taskQueue = Queues.newArrayDeque();

	@SubscribeEvent
	public void onServerTick(ServerTickEvent e) {
		if(e.phase != TickEvent.Phase.START || taskQueue.isEmpty()) {
			return;
		}

		Queue<Runnable> currentQueue;
		synchronized(taskQueue) {
			currentQueue = taskQueue.clone();
			taskQueue.clear();
		}
		while (!currentQueue.isEmpty()) {
			currentQueue.poll().run();
		}
	}

	public void schedule(Runnable task) {
		synchronized(taskQueue) {
			taskQueue.add(task);
		}
	}
}

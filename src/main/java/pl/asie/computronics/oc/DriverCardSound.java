package pl.asie.computronics.oc;

import li.cil.oc.api.Network;
import li.cil.oc.api.driver.EnvironmentHost;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.ManagedEnvironment;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.util.sound.AudioUtil;
import pl.asie.computronics.util.sound.Instruction;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Vexatos
 */
public class DriverCardSound extends ManagedEnvironment {

	protected final EnvironmentHost host;

	public DriverCardSound(EnvironmentHost host) {
		this.host = host;
		this.setNode(Network.newNode(this, Visibility.Neighbors).
			withComponent("sound").
			withConnector(Config.SOUND_ENERGY_COST * 42).
			create());
		process = new AudioUtil.AudioProcess(8);
	}

	protected Queue<Instruction> instructions = new LinkedList<Instruction>();
	protected Queue<Instruction> sendBuffer = new LinkedList<Instruction>();

	// the process simulated on the server side.
	protected AudioUtil.AudioProcess process;

	protected boolean isRunning = false;
	private long time;

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void update() {
		if(isRunning) {
			while(!instructions.isEmpty() || process.delay > 0) {
				if(process.delay > 0) {
					final long newTime = System.currentTimeMillis();
					process.delay -= newTime - time;
					time = newTime;
					if(process.delay < 0) {
						process.delay = 0;
					}
					break;
				} else {
					Instruction inst = instructions.poll();
					inst.encounter(process);
				}
			}
		}
		if(sendBuffer != null) {
			try {
				// TODO send sound
			} catch(Exception e) {
				e.printStackTrace();
			}
			sendBuffer = null;
		}
	}
}

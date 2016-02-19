package pl.asie.computronics.oc;

import li.cil.oc.api.Network;
import li.cil.oc.api.driver.EnvironmentHost;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.ManagedEnvironment;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.util.sound.Channel;

/**
 * @author Vexatos
 */
public class DriverCardSound extends ManagedEnvironment {

	protected final EnvironmentHost host;
	protected final Long[] expirationList;

	public DriverCardSound(EnvironmentHost host) {
		this.host = host;
		this.expirationList = new Long[8];
		this.setNode(Network.newNode(this, Visibility.Neighbors).
			withComponent("sound").
			withConnector(Config.SOUND_ENERGY_COST * 42).
			create());
		channels = new Channel[8];
		for(int i = 0; i < channels.length; i++) {
			channels[i] = new Channel();
		}
	}

	protected final Channel[] channels;
	protected Channel[] channelSendBuffer;

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void update() {
		final long currentTime = host.world().getTotalWorldTime();
		for(int i = 0; i < expirationList.length; i++) {
			if(expirationList[i] != null && expirationList[i] <= currentTime) {
				expirationList[i] = null;
			}
		}
		if(channelSendBuffer != null) {
			try {
				// TODO send sound
			} catch(Exception e) {
				e.printStackTrace();
			}
			channelSendBuffer = null;
		}
	}
}

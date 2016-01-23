package pl.asie.computronics.oc;

import li.cil.oc.api.Network;
import li.cil.oc.api.driver.EnvironmentHost;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Connector;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.ManagedEnvironment;
import li.cil.oc.util.Audio;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.network.Packets;
import pl.asie.computronics.reference.Config;
import pl.asie.lib.network.Packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Vexatos
 */
public class DriverCardSound extends ManagedEnvironment {

	protected final EnvironmentHost host;
	protected final ArrayList<Long> expirationList;

	public DriverCardSound(EnvironmentHost host) {
		this.host = host;
		this.expirationList = new ArrayList<Long>();
		this.setNode(Network.newNode(this, Visibility.Neighbors).
			withComponent("beep").
			withConnector(Config.SOUND_ENERGY_COST * 42).
			create());
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void update() {
		if(this.expirationList.isEmpty()) {
			return;
		}

		while(expirationList.size() > 0 && expirationList.get(0) <= host.world().getTotalWorldTime()) {
			expirationList.remove(0);
		}
	}

	@Callback(doc = "function():number; returns the current amount of beeps currently being played", direct = true, limit = 10)
	public Object[] getBeepCount(Context context, Arguments args) {
		return (new Object[] { this.expirationList.size() });
	}

	@Callback(doc = "function(frequencyDurationTable:table):boolean; table needs to contain frequency-duration pairs; plays each frequency for the specified duration.")
	public Object[] beep(Context context, Arguments args) throws Exception {
		Map map = args.checkTable(0);
		HashMap<Integer, Integer> freqMap = new HashMap<Integer, Integer>();
		double longest = 0.0;
		if(map.size() > 8) {
			return new Object[] { false, "table must not contain more than 8 frequencies" };
		}
		if(this.expirationList.size() + map.size() > 8) {
			return new Object[] { false, "already too many sounds playing, maximum is 8" };
		}
		for(Object entryObj : map.entrySet()) {
			if(entryObj instanceof Entry) {
				Object freqObj = ((Entry) entryObj).getKey();
				if(!(freqObj instanceof Number)) {
					throw new IllegalArgumentException("frequency " + String.valueOf(freqObj) + "is not a number");
				}
				//Object durObj = map.get(freqObj);
				Object durObj = ((Entry) entryObj).getValue();
				if(durObj != null && !(durObj instanceof Number)) {
					throw new IllegalArgumentException("duration '" + String.valueOf(durObj) + "'is not a number");
				}
				int frequency = ((Number) freqObj).intValue();
				if(frequency < 20 || frequency > 2000) {
					throw new IllegalArgumentException("invalid frequency, must be in [20, 2000]");
				}
				double duration = optDouble(durObj != null ? (Number) durObj : null, 0.1);
				int durationInMilliseconds = Math.max(50, Math.min(5000, (int) (duration * 1000)));
				longest = Math.max(longest, Math.max(50, Math.min(5000, (duration * 1000))));
				this.expirationList.add(host.world().getTotalWorldTime() + (long) (durationInMilliseconds / 1000 * 20));
				Collections.sort(expirationList);
				freqMap.put(frequency, durationInMilliseconds);
			}
		}
		return trySendSound(freqMap, new Object[] { true }, Config.SOUND_ENERGY_COST * map.size() * (longest / 1000d), "beep");
	}

	private Object[] trySendSound(HashMap<Integer, Integer> freqMap, Object[] result, double v, String methodName) throws Exception {
		if(this.node() instanceof Connector) {
			int power = this.tryConsumeEnergy(v);
			if(power < 0) {
				return new Object[] { false, power + ": " + methodName + ": not enough energy available: required"
					+ v + ", found " + ((Connector) node()).globalBuffer() };
			}
		}
		sendSound(host.world(), host.xPosition(),
			host.yPosition(), host.zPosition(), freqMap);
		return result;
	}

	private int tryConsumeEnergy(double v) {
		if(v < 0) {
			return -2;
		}
		v = -v;
		if(this.node() instanceof Connector) {
			Connector connector = ((Connector) this.node());
			return connector.tryChangeBuffer(v) ? 1 : -1;
		}
		return 0;
	}

	private static double optDouble(Number value, double def) {
		if(value == null) {
			return def;
		}
		return value.doubleValue();
	}

	private static void sendSound(World world, double x, double y, double z, HashMap<Integer, Integer> freqMap) throws Exception {
		Packet packet = Computronics.packet.create(Packets.PACKET_COMPUTER_BEEP)
			.writeInt(world.provider.getDimensionId())
			.writeInt((int) Math.floor(x))
			.writeInt((int) Math.floor(y))
			.writeInt((int) Math.floor(z))
			.writeInt(freqMap.size());
		for(Entry<Integer, Integer> e : freqMap.entrySet()) {
			packet.writeShort((short) (int) e.getKey())
				.writeShort((short) (int) e.getValue());
		}
		Computronics.packet.sendToAllAround(packet, new NetworkRegistry.TargetPoint(
			world.provider.getDimensionId(),
			(int) Math.floor(x),
			(int) Math.floor(y),
			(int) Math.floor(z), 16));

	}

	public static void onSound(Packet packet, EntityPlayer player) throws IOException {
		int dimension = packet.readInt();
		if(isInDimension(player, dimension)) {
			int x = packet.readInt();
			int y = packet.readInt();
			int z = packet.readInt();
			int pairs = packet.readInt();
			for(int i = 0; i < pairs; i++) {
				short frequency = packet.readShort();
				short duration = packet.readShort();
				Audio.play(x + 0.5f, y + 0.5f, z + 0.5f, frequency & 0xFFFF, duration & 0xFFFF);
			}
		}
	}

	private static boolean isInDimension(EntityPlayer player, int dimension) {
		return player != null && player.worldObj != null && player.worldObj.provider != null
			&& player.worldObj.provider.getDimensionId() == dimension;
	}

}

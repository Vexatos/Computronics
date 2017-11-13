package pl.asie.computronics.oc.driver;

import li.cil.oc.api.Network;
import li.cil.oc.api.driver.DeviceInfo;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Connector;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.util.OCUtils;
import pl.asie.computronics.util.RadarUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RobotUpgradeRadar extends AbstractManagedEnvironment implements DeviceInfo {

	private final EnvironmentHost container;
	private static final int CALL_LIMIT = 15;

	public RobotUpgradeRadar(EnvironmentHost container) {
		this.container = container;
		this.setNode(Network.newNode(this, Visibility.Network).withConnector(Config.RADAR_ENERGY_COST_OC * Config.RADAR_RANGE * 1.75).withComponent("radar", Visibility.Neighbors).create());
	}

	private int getDistance(Arguments args) {
		if(args.isInteger(0)) {
			return args.checkInteger(0);
		} else {
			return Config.RADAR_RANGE;
		}
	}

	private AxisAlignedBB getBounds(int d) {
		int distance = Math.min(d, Config.RADAR_RANGE);
		if(distance < 1) {
			distance = 1;
		}
		return new AxisAlignedBB(
			container.xPosition() - 0.5, container.yPosition() - 0.5, container.zPosition() - 0.5, container.xPosition() + 0.5, container.yPosition() + 0.5, container.zPosition() + 0.5).
			grow(distance, distance, distance);
	}

	@Callback(doc = "function([distance:number]):table; Returns a list of all entities detected within the specified or the maximum range", direct = true, limit = CALL_LIMIT)
	public Object[] getEntities(Context context, Arguments args) {
		List<Map> entities = new ArrayList<Map>();
		int distance = getDistance(args);
		if(((Connector) this.node()).tryChangeBuffer(0 - (Config.RADAR_ENERGY_COST_OC * distance * 1.75))) {
			AxisAlignedBB bounds = getBounds(distance);
			entities.addAll(RadarUtils.getEntities(container.world(), container.xPosition(), container.yPosition(), container.zPosition(), bounds, EntityPlayer.class));
			entities.addAll(RadarUtils.getEntities(container.world(), container.xPosition(), container.yPosition(), container.zPosition(), bounds, EntityLiving.class));
			context.pause(0.5);
		}
		// The returned array is treated as a tuple, meaning if we return the
		// entities as an array directly, we'd end up with each entity as an
		// individual result value (i.e. in Lua we'd have to write
		//   result = {radar.getEntities()}
		// and we'd be limited in the number of entities, due to the limit of
		// return values. So we wrap it in an array to return it as a list.
		return new Object[] { entities.toArray() };
	}

	@Callback(doc = "function([distance:number]):table; Returns a list of all players detected within the specified or the maximum range", direct = true, limit = CALL_LIMIT)
	public Object[] getPlayers(Context context, Arguments args) {
		List<Map> entities = new ArrayList<Map>();
		int distance = getDistance(args);
		if(((Connector) this.node()).tryChangeBuffer(0 - (Config.RADAR_ENERGY_COST_OC * distance * 1.0))) {
			AxisAlignedBB bounds = getBounds(distance);
			entities.addAll(RadarUtils.getEntities(container.world(), container.xPosition(), container.yPosition(), container.zPosition(), bounds, EntityPlayer.class));
			context.pause(0.5);
		}
		return new Object[] { entities.toArray() };
	}

	@Callback(doc = "function([distance:number]):table; Returns a list of all mobs detected within the specified or the maximum range", direct = true, limit = CALL_LIMIT)
	public Object[] getMobs(Context context, Arguments args) {
		List<Map> entities = new ArrayList<Map>();
		int distance = getDistance(args);
		if(((Connector) this.node()).tryChangeBuffer(0 - (Config.RADAR_ENERGY_COST_OC * distance * 1.0))) {
			AxisAlignedBB bounds = getBounds(distance);
			entities.addAll(RadarUtils.getEntities(container.world(), container.xPosition(), container.yPosition(), container.zPosition(), bounds, EntityLiving.class));
			context.pause(0.5);
		}
		return new Object[] { entities.toArray() };
	}

	@Callback(doc = "function([distance:number]):table; Returns a list of all items detected within the specified or the maximum range", direct = true, limit = CALL_LIMIT)
	public Object[] getItems(Context context, Arguments args) {
		List<Map> entities = new ArrayList<Map>();
		int distance = getDistance(args);
		if(((Connector) this.node()).tryChangeBuffer(0 - (Config.RADAR_ENERGY_COST_OC * distance * 2.0))) {
			AxisAlignedBB bounds = getBounds(distance);
			entities.addAll(RadarUtils.getItems(container.world(), container.xPosition(), container.yPosition(), container.zPosition(), bounds, EntityItem.class));
			context.pause(0.5);
		}
		return new Object[] { entities.toArray() };
	}

	protected Map<String, String> deviceInfo;

	@Override
	public Map<String, String> getDeviceInfo() {
		if(deviceInfo == null) {
			return deviceInfo = new OCUtils.Device(
				DeviceClass.Multimedia,
				"Radar",
				OCUtils.Vendors.Trumbour,
				"Detectotron M1 Mini"
			).deviceInfo();
		}
		return deviceInfo;
	}
}

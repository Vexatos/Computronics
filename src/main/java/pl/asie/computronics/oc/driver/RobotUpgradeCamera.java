package pl.asie.computronics.oc.driver;

import li.cil.oc.api.Network;
import li.cil.oc.api.driver.DeviceInfo;
import li.cil.oc.api.internal.Rotatable;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import pl.asie.computronics.util.Camera;
import pl.asie.computronics.util.OCUtils;

import java.util.Map;

public class RobotUpgradeCamera extends AbstractManagedEnvironment implements DeviceInfo {

	private final EnvironmentHost entity;

	public RobotUpgradeCamera(EnvironmentHost entity) {
		this.entity = entity;
		this.setNode(Network.newNode(this, Visibility.Network).withConnector().withComponent("camera", Visibility.Neighbors).create());
	}

	private final Camera camera = new Camera();
	private static final int CALL_LIMIT = 15;

	private EnumFacing getFacingDirection() {
		if(entity instanceof Rotatable) {
			return ((Rotatable) entity).facing();
		} else {
			int l = MathHelper.floor((double) (entity.world().getClosestPlayer(entity.xPosition(), entity.yPosition(), entity.zPosition(), 1.0D, false).rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
			return EnumFacing.byHorizontalIndex(l);
		}
	}

	@Callback(doc = "function([x:number, y:number]):number; "
		+ "Returns the distance to the block the ray is shot at with the specified x-y offset, "
		+ "or of the block directly in front", direct = true, limit = CALL_LIMIT)
	public Object[] distance(Context context, Arguments args) {
		float x = 0.0f;
		float y = 0.0f;
		if(args.count() == 2) {
			x = (float) args.checkDouble(0);
			y = (float) args.checkDouble(1);
		}
		camera.ray(entity.world(), entity.xPosition(), entity.yPosition(), entity.zPosition(),
			getFacingDirection(), x, y, entity instanceof TileEntity);
		return new Object[] { camera.getDistance() };
	}

	@Callback(doc = "function([x:number, y:number]):number; "
		+ "Returns the distance to the block the ray is shot at with the specified x-y offset facing upwards, "
		+ "or of the block directly above", direct = true, limit = CALL_LIMIT)
	public Object[] distanceUp(Context context, Arguments args) {
		float x = 0.0f;
		float y = 0.0f;
		if(args.count() == 2) {
			x = (float) args.checkDouble(0);
			y = (float) args.checkDouble(1);
		}
		camera.ray(entity.world(), entity.xPosition(), entity.yPosition(), entity.zPosition(),
			EnumFacing.UP, x, y, entity instanceof TileEntity);
		return new Object[] { camera.getDistance() };
	}

	@Callback(doc = "function([x:number, y:number]):number; "
		+ "Returns the distance to the block the ray is shot at with the specified x-y offset facing downwards, "
		+ "or of the block directly below", direct = true, limit = CALL_LIMIT)
	public Object[] distanceDown(Context context, Arguments args) {
		float x = 0.0f;
		float y = 0.0f;
		if(args.count() == 2) {
			x = (float) args.checkDouble(0);
			y = (float) args.checkDouble(1);
		}
		camera.ray(entity.world(), entity.xPosition(), entity.yPosition(), entity.zPosition(),
			EnumFacing.DOWN, x, y, entity instanceof TileEntity);
		return new Object[] { camera.getDistance() };
	}

	protected Map<String, String> deviceInfo;

	@Override
	public Map<String, String> getDeviceInfo() {
		if(deviceInfo == null) {
			return deviceInfo = new OCUtils.Device(
				DeviceClass.Multimedia,
				"Rangefinder",
				OCUtils.Vendors.Siekierka,
				"Compact Spatiometer 1-C"
			).deviceInfo();
		}
		return deviceInfo;
	}
}

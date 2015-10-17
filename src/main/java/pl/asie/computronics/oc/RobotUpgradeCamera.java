package pl.asie.computronics.oc;

import li.cil.oc.api.Network;
import li.cil.oc.api.driver.EnvironmentHost;
import li.cil.oc.api.internal.Rotatable;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.ManagedEnvironment;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.util.Camera;

public class RobotUpgradeCamera extends ManagedEnvironment {
	private final EnvironmentHost entity;

	public RobotUpgradeCamera(EnvironmentHost entity) {
		this.entity = entity;
		this.setNode(Network.newNode(this, Visibility.Network).withConnector().withComponent("camera", Visibility.Neighbors).create());
	}

	private final Camera camera = new Camera();
	private static final int CALL_LIMIT = 15;

	private ForgeDirection getFacingDirection() {
		if(entity instanceof Rotatable) {
			return ((Rotatable) entity).facing();
		} else {
			int l = MathHelper.floor_double((double) (entity.world().getClosestPlayer(entity.xPosition(), entity.yPosition(), entity.zPosition(), 1.0D).rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
			l = Direction.directionToFacing[l];
			return ForgeDirection.getOrientation(l);
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
		camera.ray(entity.world(), (int) Math.floor(entity.xPosition()), (int) Math.floor(entity.yPosition()), (int) Math.floor(entity.zPosition()),
			getFacingDirection(), x, y);
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
		camera.ray(entity.world(), (int) Math.floor(entity.xPosition()), (int) Math.floor(entity.yPosition()), (int) Math.floor(entity.zPosition()),
			ForgeDirection.UP, x, y);
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
		camera.ray(entity.world(), (int) Math.floor(entity.xPosition()), (int) Math.floor(entity.yPosition()), (int) Math.floor(entity.zPosition()),
			ForgeDirection.DOWN, x, y);
		return new Object[] { camera.getDistance() };
	}
}

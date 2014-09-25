package pl.asie.computronics.oc;

import li.cil.oc.api.Network;
import li.cil.oc.api.driver.Container;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.ManagedEnvironment;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.util.Camera;

public class RobotUpgradeCamera extends ManagedEnvironment {
	private final Container entity;
	public RobotUpgradeCamera(Container entity) {
		this.entity = entity;
		this.node = Network.newNode(this, Visibility.Network).withConnector().withComponent("camera", Visibility.Neighbors).create();
	}

	private final Camera camera = new Camera();
	private static final int CALL_LIMIT = 15;
	
	private int getFacingDirection() {
		int l = MathHelper.floor_double((double)(entity.world().getClosestPlayer(entity.xPosition(),entity.yPosition(),entity.zPosition(), 1.0D).rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		l = Direction.directionToFacing[l];
		return l;
	}
	
    @Callback(direct = true, limit = CALL_LIMIT)
    public Object[] distance(Context context, Arguments args) {
    	float x = 0.0f;
    	float y = 0.0f;
    	if(args.count() == 2) {
    		x = (float)args.checkDouble(0);
    		y = (float)args.checkDouble(1);
    	}
		camera.ray(entity.world(), (int)Math.floor(entity.xPosition()), (int)Math.floor(entity.yPosition()), (int)Math.floor(entity.zPosition()),
				ForgeDirection.getOrientation(getFacingDirection()), x, y);
		return new Object[]{camera.getDistance()};
    }
    
    @Callback(direct = true, limit = CALL_LIMIT)
    public Object[] distanceUp(Context context, Arguments args) {
    	float x = 0.0f;
    	float y = 0.0f;
    	if(args.count() == 2) {
    		x = (float)args.checkDouble(0);
    		y = (float)args.checkDouble(1);
    	}
		camera.ray(entity.world(), (int)Math.floor(entity.xPosition()), (int)Math.floor(entity.yPosition()), (int)Math.floor(entity.zPosition()),
				ForgeDirection.UP, x, y);
		return new Object[]{camera.getDistance()};
    }
    
    @Callback(direct = true, limit = CALL_LIMIT)
    public Object[] distanceDown(Context context, Arguments args) {
    	float x = 0.0f;
    	float y = 0.0f;
    	if(args.count() == 2) {
    		x = (float)args.checkDouble(0);
    		y = (float)args.checkDouble(1);
    	}
		camera.ray(entity.world(), (int)Math.floor(entity.xPosition()), (int)Math.floor(entity.yPosition()), (int)Math.floor(entity.zPosition()),
				ForgeDirection.DOWN, x, y);
		return new Object[]{camera.getDistance()};
    }
}

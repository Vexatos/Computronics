package pl.asie.computronics.robot;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.ForgeDirection;
import pl.asie.computronics.util.Camera;
import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Robot;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.ManagedEnvironment;

public class RobotUpgradeCamera extends ManagedEnvironment {
	private final TileEntity entity;
	private final Robot robot;
	public RobotUpgradeCamera(TileEntity entity) {
		this.entity = entity;
		this.robot = (Robot)entity;
		this.node = Network.newNode(this, Visibility.Network).withConnector().withComponent("camera", Visibility.Neighbors).create();
	}

	private final Camera camera = new Camera();
	private static final int CALL_LIMIT = 15;
	
    @Callback(direct = true, limit = CALL_LIMIT)
    public Object[] setRayDirection(Context context, Arguments args) {
    	if(args.count() == 2) {
        	int l = MathHelper.floor_double((double)(robot.player().rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
    		return new Object[]{
    			camera.setRayDirection(entity.worldObj, entity.xCoord, entity.yCoord, entity.zCoord,
    					ForgeDirection.getOrientation(l),
    					(float)args.checkDouble(0), (float)args.checkDouble(1))
    		};
    	}
    	return null;
    }
    
    @Callback(direct = true, limit = CALL_LIMIT)
    public Object[] distance(Context context, Arguments args) {
    	setRayDirection(context, args);
    	return new Object[]{camera.getDistance()};
    }
    
    @Callback(direct = true, limit = CALL_LIMIT / 2)
    public Object[] block(Context context, Arguments args) {
    	setRayDirection(context, args);
    	return new Object[]{camera.getBlockData()};
    }
}

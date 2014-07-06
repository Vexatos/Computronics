package pl.asie.computronics.tile;

import cpw.mods.fml.common.Optional;
import openperipheral.api.Arg;
import openperipheral.api.LuaCallable;
import openperipheral.api.LuaType;
import dan200.computer.api.IComputerAccess;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.SimpleComponent;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.block.BlockCamera;
import pl.asie.computronics.util.Camera;
import pl.asie.computronics.util.CollisionFinder;
import pl.asie.lib.block.TileEntityBase;

public class TileCamera extends TileEntityPeripheralBase {
	private static final int CALL_LIMIT = 20;
	private final Camera camera = new Camera();
	private final Camera cameraRedstone = new Camera();
	private int tick;

	public TileCamera() {
		super("camera");
	}
	
	@Override
	public boolean canUpdate() { return true; }
	
	@Override
	public int requestCurrentRedstoneValue(int side) {
		ForgeDirection dir = Computronics.instance.camera.getFacingDirection(worldObj, xCoord, yCoord, zCoord);
		cameraRedstone.reset();
		cameraRedstone.setRayDirection(worldObj, xCoord, yCoord, zCoord, dir, 0.0f, 0.0f);
		double distance = cameraRedstone.getDistance();
		if(distance > 0.0) return 15 - (int)Math.min(15, Math.round(distance / 2));
		else return 0;
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		camera.reset();
		if(tick % 20 == 0 && Computronics.CAMERA_REDSTONE_REFRESH) {
			this.worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, this.worldObj.getBlock(xCoord, yCoord, zCoord));
		}
		tick++;
	}
	
	// OpenComputers
    @Callback(direct = true, limit = CALL_LIMIT)
    @Optional.Method(modid="OpenComputers")
    public Object[] setRayDirection(Context context, Arguments args) {
    	if(args.count() == 2) {
    		return new Object[]{
    			camera.setRayDirection(worldObj, xCoord, yCoord, zCoord,
    					Computronics.instance.camera.getFacingDirection(worldObj, xCoord, yCoord, zCoord),
    					(float)args.checkDouble(0), (float)args.checkDouble(1))
    		};
    	}
    	return null;
    }
    
    @Callback(direct = true, limit = CALL_LIMIT)
    @Optional.Method(modid="OpenComputers")
    public Object[] distance(Context context, Arguments args) {
    	setRayDirection(context, args);
    	return new Object[]{camera.getDistance()};
    }
    
    @Callback(direct = true, limit = CALL_LIMIT / 2)
    @Optional.Method(modid="OpenComputers")
    public Object[] block(Context context, Arguments args) {
    	setRayDirection(context, args);
    	return new Object[]{camera.getBlockData()};
    }
}

package pl.asie.computronics.tile;

import openperipheral.api.Arg;
import openperipheral.api.LuaCallable;
import openperipheral.api.LuaType;
import dan200.computer.api.IComputerAccess;
import net.minecraftforge.common.ForgeDirection;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.SimpleComponent;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.block.BlockCamera;
import pl.asie.computronics.util.CollisionFinder;
import pl.asie.lib.block.TileEntityBase;

public class TileCamera extends TileEntityBase implements SimpleComponent {
	private static final int CALL_LIMIT = 20;
	private CollisionFinder cf;
	private Object hit;
	
	public boolean setRayDirection(float x, float y) {
		if(x < -1.0F || x > 1.0F || y < -1.0F || y > 1.0F) return false;
		ForgeDirection dir = Computronics.instance.camera.getFacingDirection(worldObj, xCoord, yCoord, zCoord);
		if(dir != null) {
			float xInc = 0.0F;
			float yInc = y;
			float zInc = 0.0F;
			switch(dir) {
				case EAST: { xInc = 1.0F; zInc = x; } break;
				case NORTH: { zInc = -1.0F; xInc = x; } break;
				case SOUTH: { zInc = 1.0F; xInc = x; } break;
				case WEST: { xInc = -1.0F; zInc = x; } break;
				case DOWN: { yInc = -1.0F; xInc = x; zInc = y; } break;
				case UP: { yInc = 1.0F; xInc = x; zInc = y; } break;
				case UNKNOWN: return false;
				default: return false;
			}
			synchronized(cf) {
				if(cf != null && cf.xDirection() == xInc && cf.yDirection() == yInc && cf.zDirection() == zInc)
					return true;
				
				cf = new CollisionFinder(this.worldObj, xCoord, yCoord, zCoord, xInc, yInc, zInc);
				hit = cf.nextCollision(32);
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean canUpdate() { return true; }
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		synchronized(cf) {
			cf = null; // Reset ray every tick to make sure new blocks get hit.
		}
	}
	
	// OpenComputers
    @Callback(direct = true, limit = CALL_LIMIT)
    public Object[] setRayDirection(Context context, Arguments args) {
    	if(args.count() == 2) {
    		return new Object[]{setRayDirection((float)args.checkDouble(0), (float)args.checkDouble(1))};
    	}
    	return null;
    }
    
    @Callback(direct = true, limit = CALL_LIMIT)
    public Object[] distance(Context context, Arguments args) {
    	setRayDirection(context, args);
    	synchronized(cf) {
    		if(cf != null) {
    			if(hit != null) return new Object[]{(double)cf.distance()};
    		}
    	}
    	return new Object[]{(double)-1.0F};
    }
    
    @Callback(direct = true, limit = CALL_LIMIT / 2)
    public Object[] block(Context context, Arguments args) {
    	setRayDirection(context, args);
    	synchronized(cf) {
    		if(cf != null) {
    			if(hit != null) return new Object[]{cf.blockData()};
    		}
    	}
    	return null;
    }
 
	@Override
	public String getComponentName() {
		return "camera";
	}
	
	// OpenPeripheral
	
    @LuaCallable(description = "Gets the distance for a specified direction.", returnTypes = {LuaType.NUMBER})
	public Float distance(
		IComputerAccess computer,
		@Arg(name = "x", type = LuaType.NUMBER, description = "The X direction (-1.0 to 1.0)") Float x,
		@Arg(name = "y", type = LuaType.NUMBER, description = "The Y direction (-1.0 to 1.0)") Float y
	) {
    	setRayDirection(x, y);
    	synchronized(cf) {
    		if(cf != null && hit != null) return cf.distance();
    		else return -1.0F;
    	}
    }
}

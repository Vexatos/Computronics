package pl.asie.computronics.tile;

import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
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
	
	private ForgeDirection getFacingDirection() {
		return Computronics.instance.camera.getFacingDirection(worldObj, xCoord, yCoord, zCoord);
	}
	
	@Override
	public int requestCurrentRedstoneValue(int side) {
		double distance = cameraRedstone.getDistance();
		if(distance > 0.0) return 15 - (int)Math.min(15, Math.round(distance / 2));
		else return 0;
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if(tick % 20 == 0 && Computronics.REDSTONE_REFRESH) {
			cameraRedstone.ray(worldObj, xCoord, yCoord, zCoord, getFacingDirection(), 0.0f, 0.0f);
			this.worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, this.blockType);
		}
		tick++;
	}
	
	// OpenComputers
    @Callback(direct = true, limit = CALL_LIMIT)
    @Optional.Method(modid="OpenComputers")
    public Object[] distance(Context context, Arguments args) {
    	if(camera != null && args.count() == 2) {
    		camera.ray(worldObj, xCoord, yCoord, zCoord, getFacingDirection(),
    				(float)args.checkDouble(0), (float)args.checkDouble(1));
    	}
    	return new Object[]{camera.getDistance()};
    }

	@Override
    @Optional.Method(modid="ComputerCraft")
	public String[] getMethodNames() {
		return new String[]{"distance"};
	}

	@Override
    @Optional.Method(modid="ComputerCraft")
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
			InterruptedException {
		if(camera == null) return null;
		Object[] rayDir = null;
    	if(arguments.length == 2 && arguments[0] instanceof Double && arguments[1] instanceof Double) {
    		rayDir = new Object[]{
    			camera.ray(worldObj, xCoord, yCoord, zCoord, getFacingDirection(),
    					((Double)arguments[0]).floatValue(), ((Double)arguments[1]).floatValue())
    		};
    	}
		switch(method) {
			case 0: { // distance
				return new Object[]{new Double(camera.getDistance())};
			}
		}
		return null;
	}
	
	private float _nedo_xDir, _nedo_yDir;

	@Override
    @Optional.Method(modid="nedocomputers")
	public short busRead(int addr) {
		switch((addr & 0xFFFE)) {
		case 4: return ((short)(camera.getDistance() * 64));
		}
		return 0;
	}

	@Override
    @Optional.Method(modid="nedocomputers")
	public void busWrite(int addr, short data) {
		float dataAsDir = (data / 32768.0F);
		switch((addr & 0xFFFE)) {
		case 0: _nedo_xDir = dataAsDir; break;
		case 2: _nedo_yDir = dataAsDir; break;
		}
		if(addr < 4)
			camera.ray(worldObj, xCoord, yCoord, zCoord, getFacingDirection(), _nedo_xDir, _nedo_yDir);
	}
}

package pl.asie.computronics.tile;

import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.Camera;

public class TileCamera extends TileEntityPeripheralBase {
	private static final int CALL_LIMIT = 20;
	private final Camera camera = new Camera();
	private final Camera cameraRedstone = new Camera();
	private int tick;

	public TileCamera() {
		super("camera");
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	private ForgeDirection getFacingDirection() {
		return Computronics.camera.getFacingDirection(worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public int requestCurrentRedstoneValue(int side) {
		double distance = cameraRedstone.getDistance();
		if(distance > 0.0) {
			return 15 - (int) Math.min(15, Math.round(distance / 2));
		} else {
			return 0;
		}
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(tick % 20 == 0 && Config.REDSTONE_REFRESH) {
			cameraRedstone.ray(worldObj, xCoord, yCoord, zCoord, getFacingDirection(), 0.0f, 0.0f);
			this.worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, this.blockType);
		}
		tick++;
	}

	// OpenComputers
    @Callback(doc = "function([x:number, y:number]):number; "
		+ "Returns the distance to the block the ray is shot at with the specified x-y offset, "
		+ "or of the block directly in front", direct = true, limit = CALL_LIMIT)
    @Optional.Method(modid= Mods.OpenComputers)
    public Object[] distance(Context context, Arguments args) {
		float x = 0.0f;
		float y = 0.0f;
    	if(args.count() == 2) {
			x = (float)args.checkDouble(0);
			y = (float)args.checkDouble(1);
    	}
		camera.ray(worldObj, xCoord, yCoord, zCoord, getFacingDirection(), x, y);
    	return new Object[]{camera.getDistance()};
    }

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String[] getMethodNames() {
		return new String[] { "distance" };
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
			InterruptedException {
		if(camera == null) return null;
		//Object[] rayDir = null;
		switch(method) {
			case 0: { // distance
				float x = 0.0f;
				float y = 0.0f;
				if(arguments.length == 2 && arguments[0] instanceof Double && arguments[1] instanceof Double) {
					//rayDir = new Object[]{
					x = ((Double)arguments[0]).floatValue();
					y = ((Double)arguments[1]).floatValue();
					//};
				}
				camera.ray(worldObj, xCoord, yCoord, zCoord, getFacingDirection(), x, y);
				return new Object[]{ camera.getDistance() };
			}
		}
		return null;
	}

	private float _nedo_xDir, _nedo_yDir;

	@Override
	@Optional.Method(modid = Mods.NedoComputers)
	public short busRead(int addr) {
		switch((addr & 0xFFFE)) {
			case 4:
				return ((short) (camera.getDistance() * 64));
		}
		return 0;
	}

	@Override
	@Optional.Method(modid = Mods.NedoComputers)
	public void busWrite(int addr, short data) {
		float dataAsDir = (data / 32768.0F);
		switch((addr & 0xFFFE)) {
			case 0:
				_nedo_xDir = dataAsDir;
				break;
			case 2:
				_nedo_yDir = dataAsDir;
				break;
		}
		if(addr < 4) {
			camera.ray(worldObj, xCoord, yCoord, zCoord, getFacingDirection(), _nedo_xDir, _nedo_yDir);
		}
	}
}

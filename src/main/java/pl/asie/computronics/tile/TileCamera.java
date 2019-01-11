package pl.asie.computronics.tile;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.ILuaTask;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.Camera;
import pl.asie.computronics.util.OCUtils;

import javax.annotation.Nullable;

public class TileCamera extends TileEntityPeripheralBase implements ITickable {

	private static final int CALL_LIMIT = 20;
	private final Camera camera = new Camera();
	private final Camera cameraRedstone = new Camera();
	private int tick;

	public TileCamera() {
		super("camera");
	}

	private EnumFacing getFacingDirection() {
		return Computronics.camera.getFacingDirection(world, getPos());
	}

	@Override
	public int requestCurrentRedstoneValue(@Nullable EnumFacing side) {
		double distance = cameraRedstone.getDistance();
		if(distance > 0.0) {
			return 15 - (int) Math.min(15, Math.round(distance / 2D));
		} else {
			return 0;
		}
	}

	@Override
	public void update() {
		super.update();
		if(tick % 20 == 0 && Config.REDSTONE_REFRESH) {
			BlockPos pos = getPos();
			cameraRedstone.ray(world, pos.getX(), pos.getY(), pos.getZ(), getFacingDirection(), 0.0f, 0.0f);
			this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
		}
		tick++;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	protected OCUtils.Device deviceInfo() {
		return new OCUtils.Device(
			DeviceClass.Multimedia,
			"Rangefinder",
			OCUtils.Vendors.Siekierka,
			"Simple Spatiometer 1"
		);
	}

	// OpenComputers
	@Callback(doc = "function([x:number, y:number]):number; "
		+ "Returns the distance to the block the ray is shot at with the specified x-y offset, "
		+ "or of the block directly in front", direct = true, limit = CALL_LIMIT)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] distance(Context context, Arguments args) {
		float x = 0.0f;
		float y = 0.0f;
		if(args.count() == 2) {
			x = (float) args.checkDouble(0);
			y = (float) args.checkDouble(1);
		}
		BlockPos pos = getPos();
		camera.ray(world, pos.getX(), pos.getY(), pos.getZ(), getFacingDirection(), x, y);
		return new Object[] { camera.getDistance() };
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
		if(camera == null) {
			return new Object[] {};
		}
		//Object[] rayDir = null;
		switch(method) {
			case 0: { // distance
				float x = 0.0f;
				float y = 0.0f;
				if(arguments.length == 2 && arguments[0] instanceof Double && arguments[1] instanceof Double) {
					//rayDir = new Object[]{
					x = ((Double) arguments[0]).floatValue();
					y = ((Double) arguments[1]).floatValue();
					//};
				}
				float fx = x;
				float fy = y;
				return context.executeMainThreadTask(new ILuaTask() {
					@Nullable
					@Override
					public Object[] execute() throws LuaException {
						BlockPos pos = getPos();
						camera.ray(world, pos.getX(), pos.getY(), pos.getZ(), getFacingDirection(), fx, fy);
						return new Object[] { camera.getDistance() };
					}
				});
			}
		}
		return new Object[] {};
	}
}

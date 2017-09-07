package pl.asie.computronics.cc;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.Camera;

/**
 * @author Vexatos
 */
public class CameraTurtleUpgrade extends TurtleUpgradeBase {

	private static class CameraTurtlePeripheral extends TurtlePeripheralBase {

		private final Camera camera = new Camera();

		public CameraTurtlePeripheral(ITurtleAccess access) {
			super(access);
		}

		@Override
		public String getType() {
			return "camera";
		}

		@Override
		@Optional.Method(modid = Mods.ComputerCraft)
		public String[] getMethodNames() {
			return new String[] { "distance", "distanceUp", "distanceDown" };
		}

		@Override
		@Optional.Method(modid = Mods.ComputerCraft)
		public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
			InterruptedException {
			//Object[] rayDir = null;
			float x = 0.0f;
			float y = 0.0f;
			if(arguments.length == 2 && arguments[0] instanceof Double && arguments[1] instanceof Double) {
				//rayDir = new Object[]{
				x = ((Double) arguments[0]).floatValue();
				y = ((Double) arguments[1]).floatValue();
				//};
			}
			BlockPos pos = access.getPosition();
			EnumFacing dir = null;
			switch(method) {
				case 0: { // distance
					dir = access.getDirection();
					break;
				}
				case 1: {
					dir = EnumFacing.UP;
					break;
				}
				case 2: {
					dir = EnumFacing.DOWN;
					break;
				}
			}
			if(dir != null) {
				camera.ray(access.getWorld(), pos.getX(), pos.getY(), pos.getZ(),
					dir, x, y);
				return new Object[] { camera.getDistance() };
			}
			return null;
		}
	}

	public CameraTurtleUpgrade(String id) {
		super(id);
	}

	@Override
	public String getUnlocalisedAdjective() {
		return "Camera";
	}

	@Override
	public ItemStack getCraftingItem() {
		return new ItemStack(Computronics.camera, 1, 0);
	}

	@Override
	public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
		return new CameraTurtlePeripheral(turtle);
	}
}

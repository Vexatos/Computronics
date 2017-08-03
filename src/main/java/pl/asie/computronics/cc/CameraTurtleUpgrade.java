package pl.asie.computronics.cc;

import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
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
			ChunkCoordinates pos = access.getPosition();
			ForgeDirection dir = null;
			switch(method) {
				case 0: { // distance
					dir = ForgeDirection.getOrientation(access.getDirection());
					break;
				}
				case 1: {
					dir = ForgeDirection.UP;
					break;
				}
				case 2: {
					dir = ForgeDirection.DOWN;
					break;
				}
			}
			if(dir != null) {
				camera.ray(access.getWorld(), pos.posX, pos.posY, pos.posZ,
					dir, x, y);
				return new Object[] { camera.getDistance() };
			}
			return null;
		}
	}

	public CameraTurtleUpgrade(int id) {
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

	@Override
	public IIcon getIcon(ITurtleAccess turtle, TurtleSide side) {
		return Computronics.camera.getAbsoluteSideIcon(2, 0);
	}
}

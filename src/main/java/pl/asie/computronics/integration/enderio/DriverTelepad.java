package pl.asie.computronics.integration.enderio;

import com.enderio.core.common.util.BlockCoord;
import crazypants.enderio.api.teleport.ITelePad;
import crazypants.enderio.config.Config;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;
import pl.asie.computronics.reference.Names;

/**
 * @author Vexatos
 */
public class DriverTelepad {

	private static void checkTelepad(ITelePad tile) {
		if(tile == null || !tile.inNetwork()) {
			throw new IllegalArgumentException("telepad is not a valid structure");
		}
	}

	private static Object[] notEnabled() {
		return new Object[] { null, "not enabled in config" };
	}

	public static class OCDriver extends DriverSidedTileEntity {

		public class InternalManagedEnvironment extends ManagedEnvironmentOCTile<ITelePad> {
			public InternalManagedEnvironment(ITelePad tile) {
				super(tile, Names.EnderIO_Telepad);
			}

			@Override
			public int priority() {
				return 4;
			}

			@Callback(doc = "function():number; Returns the x coordinate the telepad is set to")
			public Object[] getX(Context c, Arguments a) {
				checkTelepad(tile);
				return new Object[] { tile.getX() };
			}

			@Callback(doc = "function():number; Returns the y coordinate the telepad is set to")
			public Object[] getY(Context c, Arguments a) {
				checkTelepad(tile);
				return new Object[] { tile.getY() };
			}

			@Callback(doc = "function():number; Returns the z coordinate the telepad is set to")
			public Object[] getZ(Context c, Arguments a) {
				checkTelepad(tile);
				return new Object[] { tile.getZ() };
			}

			@Callback(doc = "function():number, number, number; Returns the coordinates the telepad is set to")
			public Object[] getCoords(Context c, Arguments a) {
				checkTelepad(tile);
				return new Object[] { tile.getX(), tile.getY(), tile.getZ() };
			}

			@Callback(doc = "function():number; Returns the dimension ID the telepad is set to")
			public Object[] getDimension(Context c, Arguments a) {
				checkTelepad(tile);
				return new Object[] { tile.getTargetDim() };
			}

			@Callback(doc = "function(xCoord:number):number; Changes the x coordinate the telepad is set to; Returns the new x coordinate")
			public Object[] setX(Context c, Arguments a) {
				checkTelepad(tile);
				if(Config.telepadLockCoords) {
					return notEnabled();
				}
				tile.setX(a.checkInteger(0));
				return new Object[] { tile.getX() };
			}

			@Callback(doc = "function(yCoord:number):number; Changes the y coordinate the telepad is set to; Returns the new y coordinate")
			public Object[] setY(Context c, Arguments a) {
				checkTelepad(tile);
				if(Config.telepadLockCoords) {
					return notEnabled();
				}
				tile.setY(a.checkInteger(0));
				return new Object[] { tile.getY() };
			}

			@Callback(doc = "function(zCoord:number):number; Changes the z coordinate the telepad is set to; Returns the new z coordinate")
			public Object[] setZ(Context c, Arguments a) {
				checkTelepad(tile);
				if(Config.telepadLockCoords) {
					return notEnabled();
				}
				tile.setZ(a.checkInteger(0));
				return new Object[] { tile.getZ() };
			}

			@Callback(doc = "function(xCoord:number, yCoord:number, zCoord:number):number, number, number; Changes the coordinates the telepad is set to; Returns the new coordinates")
			public Object[] setCoords(Context c, Arguments a) {
				checkTelepad(tile);
				if(Config.telepadLockCoords) {
					return notEnabled();
				}
				tile.setCoords(new BlockCoord(a.checkInteger(0), a.checkInteger(1), a.checkInteger(2)));
				return new Object[] { tile.getX(), tile.getY(), tile.getZ() };
			}

			@Callback(doc = "function(dimension:number):number; Changes the dimension the telepad is set to; Returns the new dimension")
			public Object[] setDimension(Context c, Arguments a) {
				checkTelepad(tile);
				if(Config.telepadLockDimension) {
					return notEnabled();
				}
				tile.setTargetDim(a.checkInteger(0));
				return new Object[] { tile.getTargetDim() };
			}

			@Callback(doc = "function(); Activates the telepad")
			public Object[] teleport(Context c, Arguments a) {
				checkTelepad(tile);
				tile.teleportAll();
				return new Object[] {};
			}

			@Callback(doc = "function():boolean; Returns true if the telepad is a valid multiblock")
			public Object[] isValid(Context c, Arguments a) {
				return new Object[] { tile.inNetwork() };
			}
		}

		@Override
		public Class<?> getTileEntityClass() {
			return ITelePad.class;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
			return new InternalManagedEnvironment(((ITelePad) world.getTileEntity(x, y, z)));
		}
	}

	public static class CCDriver extends CCMultiPeripheral<ITelePad> {

		public CCDriver() {
		}

		public CCDriver(ITelePad tile, World world, int x, int y, int z) {
			super(tile, Names.EnderIO_Telepad, world, x, y, z);
		}

		@Override
		public int peripheralPriority() {
			return 4;
		}

		@Override
		public CCMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof ITelePad) {
				return new CCDriver((ITelePad) te, world, x, y, z);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getX", "getY", "getZ", "getCoords", "getDimension",
				"setX", "setY", "setZ", "setCoords", "setDimension", "teleport", "isValid" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			try {
				if(method != 10) {
					checkTelepad(tile);
				}
				switch(method) {
					case 0: {
						return new Object[] { tile.getX() };
					}
					case 1: {
						return new Object[] { tile.getY() };
					}
					case 2: {
						return new Object[] { tile.getZ() };
					}
					case 3: {
						return new Object[] { tile.getX(), tile.getY(), tile.getZ() };
					}
					case 4: {
						return new Object[] { tile.getTargetDim() };
					}
					case 5: {
						if(arguments.length < 1 || !(arguments[0] instanceof Double)) {
							throw new LuaException("first argument needs to be a number");
						}
						if(Config.telepadLockCoords) {
							return notEnabled();
						}
						tile.setX(((Double) arguments[0]).intValue());
						return new Object[] { tile.getX() };
					}
					case 6: {
						if(arguments.length < 1 || !(arguments[0] instanceof Double)) {
							throw new LuaException("first argument needs to be a number");
						}
						if(Config.telepadLockCoords) {
							return notEnabled();
						}
						tile.setY(((Double) arguments[0]).intValue());
						return new Object[] { tile.getY() };
					}
					case 7: {
						if(arguments.length < 1 || !(arguments[0] instanceof Double)) {
							throw new LuaException("first argument needs to be a number");
						}
						if(Config.telepadLockCoords) {
							return notEnabled();
						}
						tile.setZ(((Double) arguments[0]).intValue());
						return new Object[] { tile.getZ() };
					}
					case 8: {
						if(arguments.length < 1 || !(arguments[0] instanceof Double)) {
							throw new LuaException("first argument needs to be a number");
						} else if(arguments.length < 2 || !(arguments[1] instanceof Double)) {
							throw new LuaException("second argument needs to be a number");
						} else if(arguments.length < 3 || !(arguments[2] instanceof Double)) {
							throw new LuaException("third argument needs to be a number");
						}
						if(Config.telepadLockCoords) {
							return notEnabled();
						}
						tile.setCoords(new BlockCoord(
							((Double) arguments[0]).intValue(),
							((Double) arguments[1]).intValue(),
							((Double) arguments[2]).intValue()));
						return new Object[] { tile.getX(), tile.getY(), tile.getZ() };
					}
					case 9: {
						if(arguments.length < 1 || !(arguments[0] instanceof Double)) {
							throw new LuaException("first argument needs to be a number");
						}
						if(Config.telepadLockDimension) {
							return notEnabled();
						}
						tile.setTargetDim(((Double) arguments[0]).intValue());
						return new Object[] { tile.getTargetDim() };
					}
					case 10: {
						tile.teleportAll();
						return new Object[] {};
					}
					case 11: {
						return new Object[] { tile.inNetwork() };
					}
				}
			} catch(Exception e) {
				throw new LuaException(e.getMessage());
			}
			return null;
		}
	}
}

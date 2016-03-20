package pl.asie.computronics.integration.armourersworkshop;

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
import riskyken.armourersWorkshop.common.data.BipedRotations.BipedPart;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

public class DriverMannequin {

	private static final HashMap<String, Part> parts = new HashMap<String, Part>();

	private enum Part {
		head,
		chest,
		left_arm,
		right_arm,
		left_leg,
		right_leg;

		private static final Part[] VALUES = values();

		Part() {
			parts.put(this.name().toLowerCase(Locale.ENGLISH), this);
		}

		private static Part from(String name) {
			return parts.get(name.toLowerCase(Locale.ENGLISH));
		}
	}

	private static BipedPart getPart(TileEntityMannequin tile, String s) {
		Part part = Part.from(s);
		if(part != null) {
			if(tile.getBipedRotations() != null) {
				switch(part) {
					case head: {
						return tile.getBipedRotations().head;
					}
					case chest: {
						return tile.getBipedRotations().chest;
					}
					case left_arm: {
						return tile.getBipedRotations().leftArm;
					}
					case right_arm: {
						return tile.getBipedRotations().rightArm;
					}
					case left_leg: {
						return tile.getBipedRotations().leftLeg;
					}
					case right_leg: {
						return tile.getBipedRotations().rightLeg;
					}
				}
			}
			return null;
		}
		throw new IllegalArgumentException("invalid mannequin part");
	}

	private static void updateMannequin(TileEntityMannequin tile) {
		tile.markDirty();
		tile.getWorldObj().markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
	}

	private static float check(double d) {
		if(d < -180 || d > 180) {
			throw new IllegalArgumentException("rotation must be between -180 and 180");
		}
		return (float) Math.toRadians(d);
	}

	public static Object[] setRotation(TileEntityMannequin tile, String partName, Double rotationX, Double rotationY, Double rotationZ) {
		BipedPart part = getPart(tile, partName);
		if(part != null) {
			part.rotationX = rotationX != null ? check(rotationX) : part.rotationX;
			part.rotationY = rotationY != null ? check(rotationY) : part.rotationY;
			part.rotationZ = rotationZ != null ? check(rotationZ) : part.rotationZ;
			updateMannequin(tile);
		}
		return new Object[] {};
	}

	public static Object[] setRotationX(TileEntityMannequin tile, String partName, double rotation) {
		BipedPart part = getPart(tile, partName);
		if(part != null) {
			part.rotationX = check(rotation);
			updateMannequin(tile);
		}
		return new Object[] {};
	}

	public static Object[] setRotationY(TileEntityMannequin tile, String partName, double rotation) {
		BipedPart part = getPart(tile, partName);
		if(part != null) {
			part.rotationY = check(rotation);
			updateMannequin(tile);
		}
		return new Object[] {};
	}

	public static Object[] setRotationZ(TileEntityMannequin tile, String partName, double rotation) {
		BipedPart part = getPart(tile, partName);
		if(part != null) {
			part.rotationZ = check(rotation);
			updateMannequin(tile);
		}
		return new Object[] {};
	}

	public static Object[] getRotation(TileEntityMannequin tile, String partName) {
		BipedPart part = getPart(tile, partName);
		if(part != null) {
			return new Object[] { Math.toDegrees(part.rotationX), Math.toDegrees(part.rotationY), Math.toDegrees(part.rotationZ) };
		}
		return new Object[] { 0.0, 0.0, 0.0 };
	}

	public static Object[] getRotationX(TileEntityMannequin tile, String partName) {
		BipedPart part = getPart(tile, partName);
		if(part != null) {
			return new Object[] { Math.toDegrees(part.rotationX) };
		}
		return new Object[] { 0.0 };
	}

	public static Object[] getRotationY(TileEntityMannequin tile, String partName) {
		BipedPart part = getPart(tile, partName);
		if(part != null) {
			return new Object[] { Math.toDegrees(part.rotationY) };
		}
		return new Object[] { 0.0, 0.0, 0.0 };
	}

	public static Object[] getRotationZ(TileEntityMannequin tile, String partName) {
		BipedPart part = getPart(tile, partName);
		if(part != null) {
			return new Object[] { Math.toDegrees(part.rotationZ) };
		}
		return new Object[] { 0.0, 0.0, 0.0 };
	}

	private static Object[] parts() {
		LinkedHashMap<Integer, String> modes = new LinkedHashMap<Integer, String>();
		int i = 1;
		for(Part mode : Part.VALUES) {
			modes.put(i++, mode.name());
		}
		return new Object[] { modes };
	}

	public static class OCDriver extends DriverSidedTileEntity {

		public static class InternalManagedEnvironment extends ManagedEnvironmentOCTile<TileEntityMannequin> {

			public InternalManagedEnvironment(TileEntityMannequin tile) {
				super(tile, Names.AW_Mannequin);
			}

			@Override
			public int priority() {
				return 4;
			}

			@Callback(doc = "function(part:string, x:number or nil , y:number or nil , z:number or nil); Sets the rotation (in degrees) of the mannequin.")
			public Object[] setRotation(Context c, Arguments a) {
				Double x = null, y = null, z = null;
				if(a.isDouble(1)) {
					x = a.checkDouble(1);
				}
				if(a.isDouble(2)) {
					y = a.checkDouble(2);
				}
				if(a.isDouble(3)) {
					z = a.checkDouble(3);
				}
				return DriverMannequin.setRotation(tile, a.checkString(0), x, y, z);
			}

			@Callback(doc = "function(part:string, x:number); Sets the X rotation (in degrees) of the mannequin.")
			public Object[] setRotationX(Context c, Arguments a) {
				return DriverMannequin.setRotationX(tile, a.checkString(0), a.checkDouble(1));
			}

			@Callback(doc = "function(part:string, y:number); Sets the Y rotation (in degrees) of the mannequin.")
			public Object[] setRotationY(Context c, Arguments a) {
				return DriverMannequin.setRotationY(tile, a.checkString(0), a.checkDouble(1));
			}

			@Callback(doc = "function(part:string, z:number); Sets the Z rotation (in degrees) of the mannequin.")
			public Object[] setRotationZ(Context c, Arguments a) {
				return DriverMannequin.setRotationZ(tile, a.checkString(0), a.checkDouble(1));
			}

			@Callback(doc = "function(part:string):number, number, number; Returns the rotation of the mannequin, in degrees.", direct = true)
			public Object[] getRotation(Context c, Arguments a) {
				return DriverMannequin.getRotation(tile, a.checkString(0));
			}

			@Callback(doc = "function(part:string):number; Returns the X rotation of the mannequin, in degrees.", direct = true)
			public Object[] getRotationX(Context c, Arguments a) {
				return DriverMannequin.getRotationX(tile, a.checkString(0));
			}

			@Callback(doc = "function(part:string):number; Returns the Y rotation of the mannequin, in degrees.", direct = true)
			public Object[] getRotationY(Context c, Arguments a) {
				return DriverMannequin.getRotationY(tile, a.checkString(0));
			}

			@Callback(doc = "function(part:string):number; Returns the Z rotation of the mannequin, in degrees.", direct = true)
			public Object[] getRotationZ(Context c, Arguments a) {
				return DriverMannequin.getRotationZ(tile, a.checkString(0));
			}

			@Callback(doc = "This is a table containing all valid mannequin part names.", getter = true, direct = true)
			public Object[] parts(Context c, Arguments a) {
				return DriverMannequin.parts();
			}
		}

		@Override
		public Class<?> getTileEntityClass() {
			return TileEntityMannequin.class;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
			return new InternalManagedEnvironment((TileEntityMannequin) world.getTileEntity(x, y, z));
		}
	}

	public static class CCDriver extends CCMultiPeripheral<TileEntityMannequin> {

		public CCDriver() {
		}

		public CCDriver(TileEntityMannequin tile, World world, int x, int y, int z) {
			super(tile, Names.AW_Mannequin, world, x, y, z);
		}

		@Override
		public int peripheralPriority() {
			return 4;
		}

		@Override
		public CCMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof TileEntityMannequin) {
				return new CCDriver((TileEntityMannequin) te, world, x, y, z);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "setRotation", "setRotationX", "setRotationY", "setRotationZ",
				"getRotation", "getRotationX", "getRotationY", "getRotationZ", "parts" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			try {
				if(method != 8 && (arguments.length < 1 || !(arguments[0] instanceof String))) {
					throw new LuaException("first argument needs to be a string");
				}
				switch(method) {
					case 0: {
						Double x = null, y = null, z = null;
						if(arguments.length > 1 && arguments[1] instanceof Double) {
							x = ((Double) arguments[1]);
						}
						if(arguments.length > 2 && arguments[2] instanceof Double) {
							y = ((Double) arguments[2]);
						}
						if(arguments.length > 3 && arguments[3] instanceof Double) {
							z = ((Double) arguments[3]);
						}
						return DriverMannequin.setRotation(tile, ((String) arguments[0]), x, y, z);
					}
					case 1: {
						if(arguments.length < 2 || !(arguments[1] instanceof Double)) {
							throw new LuaException("second argument needs to be a number");
						}
						return DriverMannequin.setRotationX(tile, ((String) arguments[0]), ((Double) arguments[1]));
					}
					case 2: {
						if(arguments.length < 2 || !(arguments[1] instanceof Double)) {
							throw new LuaException("second argument needs to be a number");
						}
						return DriverMannequin.setRotationY(tile, ((String) arguments[0]), ((Double) arguments[1]));
					}
					case 3: {
						if(arguments.length < 2 || !(arguments[1] instanceof Double)) {
							throw new LuaException("second argument needs to be a number");
						}
						return DriverMannequin.setRotationZ(tile, ((String) arguments[0]), ((Double) arguments[1]));
					}
					case 4: {
						return DriverMannequin.getRotation(tile, ((String) arguments[0]));
					}
					case 5: {
						return DriverMannequin.getRotationX(tile, ((String) arguments[0]));
					}
					case 6: {
						return DriverMannequin.getRotationY(tile, ((String) arguments[0]));
					}
					case 7: {
						return DriverMannequin.getRotationZ(tile, ((String) arguments[0]));
					}
					case 8: {
						return DriverMannequin.parts();
					}
				}
				return null;
			} catch(LuaException le) {
				throw le;
			} catch(Exception e) {
				throw new LuaException(e.getMessage());
			}
		}
	}
}

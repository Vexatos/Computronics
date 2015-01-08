package pl.asie.computronics.integration.openblocks;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.prefab.DriverTileEntity;
import li.cil.oc.api.prefab.ManagedEnvironment;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openblocks.common.tileentity.TileEntityGuide;
import openblocks.shapes.GuideShape;
import org.apache.commons.lang3.StringUtils;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheral;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;
import pl.asie.computronics.reference.Names;

import java.util.LinkedHashMap;
import java.util.Locale;

/**
 * @author Vexatos
 */
public class DriverBuildingGuide {

	private static Object[] setShape(TileEntityGuide tile, String mode) {
		try {
			tile.setShape(GuideShape.valueOf(StringUtils.capitalize(mode)));
		} catch(IllegalArgumentException e) {
			throw new IllegalArgumentException("No valid shape given");
		}
		return new Object[] { };
	}

	private static void setWidth(TileEntityGuide tile, int width) {
		tile.setWidth(width);
	}

	private static void setHeight(TileEntityGuide tile, int height) {
		tile.setHeight(height);
	}

	private static void setDepth(TileEntityGuide tile, int depth) {
		tile.setDepth(depth);
	}

	private static Object[] shapes() {
		LinkedHashMap<Integer, String> shapes = new LinkedHashMap<Integer, String>();
		GuideShape[] values = GuideShape.values();
		for(int i = 0; i < values.length; i++) {
			shapes.put(i, values[i].name().toLowerCase(Locale.ENGLISH));
		}
		return new Object[] { shapes };
	}

	public static class OCDriver extends DriverTileEntity {

		public class InternalManagedEnvironment extends ManagedEnvironmentOCTile<TileEntityGuide> {

			public InternalManagedEnvironment(TileEntityGuide tile) {
				super(tile, Names.OpenBlocks_BuildingGuide);
			}

			@Override
			public int priority() {
				return 1;
			}

			@Callback(doc = "function():number; Returns current shape")
			public Object[] getShape(Context c, Arguments a) {
				return new Object[] { tile.getCurrentMode().name() };
			}

			@Callback(doc = "function(shape:string); Sets the building shape")
			public Object[] setShape(Context c, Arguments a) {
				return DriverBuildingGuide.setShape(tile, a.checkString(0));
			}

			@Callback(doc = "function():number; Returns the current width")
			public Object[] getWidth(Context c, Arguments a) {
				return new Object[] { tile.getWidth() };
			}

			@Callback(doc = "function(width:number); Sets the width")
			public Object[] setWidth(Context c, Arguments a) {
				DriverBuildingGuide.setWidth(tile, a.checkInteger(0));
				return new Object[] { };
			}

			@Callback(doc = "function():number; Returns the current height")
			public Object[] getHeight(Context c, Arguments a) {
				return new Object[] { tile.getHeight() };
			}

			@Callback(doc = "function(height:number); Sets the height")
			public Object[] setHeight(Context c, Arguments a) {
				DriverBuildingGuide.setHeight(tile, a.checkInteger(0));
				return new Object[] { };
			}

			@Callback(doc = "function():number; Returns the current depth")
			public Object[] getDepth(Context c, Arguments a) {
				return new Object[] { tile.getDepth() };
			}

			@Callback(doc = "function(depth:number); Sets the depth")
			public Object[] setDepth(Context c, Arguments a) {
				DriverBuildingGuide.setDepth(tile, a.checkInteger(0));
				return new Object[] { };
			}

			@Callback(doc = "function():number; Returns the block colour")
			public Object[] getColor(Context c, Arguments a) {
				return new Object[] { tile.getColor() };
			}

			@Callback(doc = "function(color:number); Sets the block colour")
			public Object[] setColor(Context c, Arguments a) {
				tile.setColor(a.checkInteger(0) & 0xFFFFFF);
				return new Object[] { };
			}

			@Callback(doc = "function(); Returns the block count of the shape")
			public Object[] getCount(Context c, Arguments a) {
				return new Object[] { tile.getCount() };
			}

			@Callback(doc = "This is a table of every Guide shape available", getter = true)
			public Object[] guide_shapes(Context c, Arguments a) {
				return DriverBuildingGuide.shapes();
			}
		}

		@Override
		public Class<?> getTileEntityClass() {
			return TileEntityGuide.class;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
			return new InternalManagedEnvironment((TileEntityGuide) world.getTileEntity(x, y, z));
		}

	}

	public static class CCDriver extends CCMultiPeripheral<TileEntityGuide> {
		public CCDriver() {
			super();
		}

		public CCDriver(TileEntityGuide tile, World world, int x, int y, int z) {
			super(tile, Names.OpenBlocks_BuildingGuide, world, x, y, z);
		}

		@Override
		public int peripheralPriority() {
			return 1;
		}

		@Override
		public IMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof TileEntityGuide) {
				return new CCDriver((TileEntityGuide) te, world, x, y, z);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getShape", "setShape", "getWidth", "setWidth", "getHeight",
				"setHeight", "getDepth", "setDepth", "getColor", "setColor", "getCount", "getShapeTable" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			switch(method){
				case 0:{
					return new Object[] { tile.getCurrentMode().name() };
				}
				case 1:{
					if(arguments.length < 1 || !(arguments[0] instanceof String)) {
						throw new LuaException("first argument needs to be a string");
					}
					try {
						return DriverBuildingGuide.setShape(tile, (String) arguments[0]);
					} catch(IllegalArgumentException e) {
						throw new LuaException(e.getMessage());
					}
				}
				case 2:{
					return new Object[] { tile.getWidth() };
				}
				case 3:{
					if(arguments.length < 1 || !(arguments[0] instanceof Number)) {
						throw new LuaException("first argument needs to be a number");
					}
					try {
						DriverBuildingGuide.setWidth(tile, ((Number) arguments[0]).intValue());
					} catch(IllegalArgumentException e) {
						throw new LuaException(e.getMessage());
					}
					return new Object[] { };
				}
				case 4:{
					return new Object[] { tile.getHeight() };
				}
				case 5:{
					if(arguments.length < 1 || !(arguments[0] instanceof Number)) {
						throw new LuaException("first argument needs to be a number");
					}
					try {
						DriverBuildingGuide.setHeight(tile, ((Number) arguments[0]).intValue());
					} catch(IllegalArgumentException e) {
						throw new LuaException(e.getMessage());
					}
					return new Object[] { };
				}
				case 6:{
					return new Object[] { tile.getDepth() };
				}
				case 7:{
					if(arguments.length < 1 || !(arguments[0] instanceof Number)) {
						throw new LuaException("first argument needs to be a number");
					}
					try {
						DriverBuildingGuide.setDepth(tile, ((Number) arguments[0]).intValue());
					} catch(IllegalArgumentException e) {
						throw new LuaException(e.getMessage());
					}
					return new Object[] { };
				}
				case 8:{
					return new Object[] { tile.getColor() };
				}
				case 9:{
					if(arguments.length < 1 || !(arguments[0] instanceof Number)) {
						throw new LuaException("first argument needs to be a number");
					}
					tile.setColor(((Number) arguments[0]).intValue() & 0xFFFFFF);
					return new Object[] { };
				}
				case 10:{
					return new Object[] { tile.getCount() };
				}
				case 11:{
					return DriverBuildingGuide.shapes();
				}
			}
			return null;
		}
	}
}

package pl.asie.computronics.integration.enderio;

import crazypants.enderio.base.xp.IHaveExperience;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.integration.DriverSpecificTileEntity;
import pl.asie.computronics.integration.NamedManagedEnvironment;
import pl.asie.computronics.reference.Names;

/**
 * @author Vexatos
 */
public class DriverHasExperience {

	public static class OCDriver extends DriverSpecificTileEntity<IHaveExperience> {

		public static class InternalManagedEnvironment extends NamedManagedEnvironment<IHaveExperience> {

			public InternalManagedEnvironment(IHaveExperience tile) {
				super(tile, Names.EnderIO_ExperienceTile);
			}

			@Override
			public int priority() {
				return 3;
			}

			@Callback(doc = "function():number; Returns the current amount of EXP in the tile")
			public Object[] getExperience(Context c, Arguments a) {
				return new Object[] { tile.getContainer().getExperienceTotal() };
			}

			@Callback(doc = "function():number; Returns the current amount of experience levels in the tile")
			public Object[] getExperienceLevels(Context c, Arguments a) {
				return new Object[] { tile.getContainer().getExperienceLevel() };
			}

			@Callback(doc = "function():number; Returns the maximum amount of EXP the tile can store")
			public Object[] getMaxExperience(Context c, Arguments a) {
				return new Object[] { tile.getContainer().getMaximumExperiance() };
			}
		}

		public OCDriver() {
			super(IHaveExperience.class);
		}

		@Override
		public InternalManagedEnvironment createEnvironment(World world, BlockPos pos, EnumFacing side, IHaveExperience tile) {
			return new InternalManagedEnvironment(tile);
		}
	}

	public static class CCDriver extends CCMultiPeripheral<IHaveExperience> {

		public CCDriver() {
		}

		public CCDriver(IHaveExperience tile, World world, BlockPos pos) {
			super(tile, Names.EnderIO_ExperienceTile, world, pos);
		}

		@Override
		public int peripheralPriority() {
			return 3;
		}

		@Override
		public CCMultiPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
			TileEntity te = world.getTileEntity(pos);
			if(te != null && te instanceof IHaveExperience) {
				return new CCDriver((IHaveExperience) te, world, pos);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getExperience", "getExperienceLevels", "getMaxExperience" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			switch(method) {
				case 0: {
					return new Object[] { tile.getContainer().getExperienceTotal() };
				}
				case 1: {
					return new Object[] { tile.getContainer().getExperienceLevel() };
				}
				case 2: {
					return new Object[] { tile.getContainer().getMaximumExperiance() };
				}
			}
			return new Object[] {};
		}
	}
}

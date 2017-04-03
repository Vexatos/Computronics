package pl.asie.computronics.integration.redlogic;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.Network;
import li.cil.oc.api.driver.DriverBlock;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Visibility;
import mods.immibis.redlogic.api.misc.ILampBlock;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheral;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.reference.Names;

public class DriverLamp {

	public static class OCDriver implements DriverBlock {

		public static class InternalManagedEnvironment extends li.cil.oc.api.prefab.AbstractManagedEnvironment {

			private ILampBlock block;
			private IBlockAccess w;
			private int x, y, z;

			public InternalManagedEnvironment(ILampBlock block, IBlockAccess w, int x, int y, int z) {
				this.block = block;
				this.w = w;
				this.x = x;
				this.y = y;
				this.z = z;
				this.setNode(Network.newNode(this, Visibility.Network).withComponent(Names.RedLogic_Lamp, Visibility.Network).create());
			}

			@Callback(doc = "function():string; Returns the type of this lamp", direct = true)
			public Object[] getLampType(Context c, Arguments a) {
				return new Object[] { block.getType().name() };
			}

			@Callback(doc = "function():number; Returns the color of this lamp", direct = true)
			public Object[] getLampColor(Context c, Arguments a) {
				return new Object[] { block.getColourRGB(w, x, y, z) };
			}

			@Callback(doc = "function():boolean; Returns whether this lamp is powered", direct = true)
			public Object[] isLampPowered(Context c, Arguments a) {
				return new Object[] { block.isPowered() };
			}
		}

		@Override
		public boolean worksWith(final World world, final int x, final int y, final int z, ForgeDirection side) {
			return world.getBlock(x, y, z) instanceof ILampBlock;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
			Block block = world.getBlock(x, y, z);
			if(block instanceof ILampBlock) {
				return new InternalManagedEnvironment((ILampBlock) block, world, x, y, z);
			} else {
				return null;
			}
		}
	}

	public static class CCDriver extends CCMultiPeripheral<ILampBlock> {

		public CCDriver() {
		}

		public CCDriver(ILampBlock block, World world, int x, int y, int z) {
			super(block, Names.RedLogic_Lamp, world, x, y, z);
		}

		@Override
		public IMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			Block block = world.getBlock(x, y, z);
			if(block instanceof ILampBlock) {
				return new CCDriver((ILampBlock) block, world, x, y, z);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getLampColor", "isLampPowered", "getLampType" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
			InterruptedException {
			switch(method) {
				case 0:
					return new Object[] { tile.getColourRGB(w, x, y, z) };
				case 1:
					return new Object[] { tile.isPowered() };
				case 2:
					return new Object[] { tile.getType().name() };
			}
			return null;
		}
	}
}

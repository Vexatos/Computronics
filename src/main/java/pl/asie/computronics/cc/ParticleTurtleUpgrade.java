package pl.asie.computronics.cc;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.Optional;
import org.apache.commons.lang3.tuple.Pair;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.ParticleUtils;

import javax.vecmath.Matrix4f;
import java.util.Random;

public class ParticleTurtleUpgrade extends TurtleUpgradeBase {

	private static class ParticleTurtlePeripheral extends TurtlePeripheralBase {

		public ParticleTurtlePeripheral(ITurtleAccess access) {
			super(access);
		}

		@Override
		public String getType() {
			return "particle";
		}

		@Override
		@Optional.Method(modid = Mods.ComputerCraft)
		public String[] getMethodNames() {
			return new String[] { "spawn" };
		}

		@Override
		@Optional.Method(modid = Mods.ComputerCraft)
		public Object[] callMethod(IComputerAccess computer,
			ILuaContext context, int method, Object[] arguments)
			throws LuaException, InterruptedException {
			// check argument type validity
			if(arguments.length < 4 || !(arguments[0] instanceof String)) {
				return new Object[] { false, "invalid arguments" };
			}
			for(int i = 1; i < arguments.length; i++) {
				if(!(arguments[i] instanceof Double)) {
					return new Object[] { false, "invalid argument " + i };
				}
			}

			String name = (String) arguments[0];

			if(name.length() > Short.MAX_VALUE) {
				return new Object[] { false, "name too long" };
			}
			EnumParticleTypes particle = ParticleUtils.getParticleType(name);
			if(particle == null) {
				return new Object[] { false, "invalid particle type" };
			}

			double xOffset = MathHelper.clamp_double((Double) arguments[1], -65536D, 65536D);
			double yOffset = MathHelper.clamp_double((Double) arguments[2], -65536D, 65536D);
			double zOffset = MathHelper.clamp_double((Double) arguments[3], -65536D, 65536D);
			if(Config.FX_RANGE >= 0 && xOffset * xOffset + yOffset * yOffset + zOffset * zOffset > Config.FX_RANGE * Config.FX_RANGE) {
				return new Object[] { false, "out of range" };
			}

			Random rng = access.getWorld().rand;
			double x = access.getPosition().getX() + 0.5 + xOffset;
			double y = access.getPosition().getY() + 0.5 + yOffset;
			double z = access.getPosition().getZ() + 0.5 + zOffset;
			double defaultv = (rng.nextDouble() * 0.1);
			if(arguments.length >= 5) {
				defaultv = (Double) arguments[4];
			}
			double vx = defaultv * rng.nextGaussian();
			double vy = defaultv * rng.nextGaussian();
			double vz = defaultv * rng.nextGaussian();
			if(arguments.length >= 7) {
				vx = (Double) arguments[4];
				vy = (Double) arguments[5];
				vz = (Double) arguments[6];
			}
			ParticleUtils.sendParticlePacket(particle, access.getWorld(), x, y, z, vx, vy, vz);
			return new Object[] { true };
		}

	}

	public ParticleTurtleUpgrade(String id) {
		super(id);
	}

	@Override
	public String getUnlocalisedAdjective() {
		return "Particle";
	}

	@Override
	public ItemStack getCraftingItem() {
		return new ItemStack(Items.BLAZE_POWDER, 1, 0);
	}

	@Override
	public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
		return new ParticleTurtlePeripheral(turtle);
	}

	@Override
	public Pair<IBakedModel, Matrix4f> getModel(ITurtleAccess turtle, TurtleSide side) {
		ItemStack stack;
		if(turtle != null) {
			int dyeColour = 15 - turtle.getDyeColour();
			stack = dyeColour >= 0 && dyeColour < 16 ? new ItemStack(Blocks.STAINED_GLASS, 1, dyeColour) : new ItemStack(Blocks.GLASS, 1, 0);
		} else {
			stack = new ItemStack(Blocks.GLASS, 1, 0);
		}
		return Pair.of(Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack), getStandardBlockMatrixForSide(side));
	}
}

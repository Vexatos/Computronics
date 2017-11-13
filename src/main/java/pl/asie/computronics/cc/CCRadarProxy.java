package pl.asie.computronics.cc;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.RadarUtils;
import pl.asie.computronics.util.TableUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CCRadarProxy {

	private static AxisAlignedBB getBounds(BlockPos pos, int d) {
		int distance = Math.min(d, Config.RADAR_RANGE);
		if(distance < 1) {
			distance = 1;
		}
		return new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).
			grow(distance, distance, distance);
	}

	@Optional.Method(modid = Mods.ComputerCraft)
	public static String[] getMethodNames() {
		return new String[] { "getEntities", "getPlayers", "getMobs", "getItems" };
	}

	@Optional.Method(modid = Mods.ComputerCraft)
	public static Object[] callMethod(World world, BlockPos pos, IComputerAccess computer, ILuaContext context,
		int method, Object[] arguments, Object powerProvider) throws LuaException,
		InterruptedException {
		int distance = Config.RADAR_RANGE;
		if(arguments.length >= 1 && (arguments[0] instanceof Double)) {
			distance = ((Double) arguments[0]).intValue();
			if(distance <= 0 || distance > Config.RADAR_RANGE) {
				distance = Config.RADAR_RANGE;
			}
		}
		double energyNeeded = (Config.RADAR_ENERGY_COST_OC * distance);
		if(method == 0) {
			energyNeeded *= 1.75;
		} else if(method == 3) {
			energyNeeded *= 2.0;
		}

		if(powerProvider instanceof ITurtleAccess
			&& ((ITurtleAccess) powerProvider).isFuelNeeded()
			&& !((ITurtleAccess) powerProvider).consumeFuel(
			(int) Math.ceil(energyNeeded)
		)) {
			return null;
		}

		AxisAlignedBB bounds = getBounds(pos, distance);
		Set<Map> entities = new HashSet<Map>();
		if(method == 0 || method == 1) {
			entities.addAll(RadarUtils.getEntities(world, pos, bounds, EntityPlayer.class));
		}
		if(method == 0 || method == 2) {
			entities.addAll(RadarUtils.getEntities(world, pos, bounds, EntityLiving.class));
		}
		if(method == 3) {
			entities.addAll(RadarUtils.getItems(world, pos, bounds, EntityItem.class));
		}

		return new Object[] { TableUtils.convertSetToMap(entities) };
	}
}

package pl.asie.computronics.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pl.asie.computronics.reference.Config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RadarUtils {

	public static Set<Map<String, Object>> getEntities(World world, BlockPos pos, AxisAlignedBB bounds, Class<? extends EntityLivingBase> eClass) {
		return getEntities(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, bounds, eClass);
	}

	public static Set<Map<String, Object>> getEntities(World world, double xCoord, double yCoord, double zCoord, AxisAlignedBB bounds, Class<? extends EntityLivingBase> eClass) {
		Set<Map<String, Object>> entities = new HashSet<Map<String, Object>>();
		for(EntityLivingBase entity : world.getEntitiesWithinAABB(eClass, bounds)) {
			double dx = entity.posX - xCoord;
			double dy = entity.posY - yCoord;
			double dz = entity.posZ - zCoord;
			if(Math.sqrt(dx * dx + dy * dy + dz * dz) < Config.RADAR_RANGE) {
				// Maps are converted to tables on the Lua side.
				Map<String, Object> entry = new HashMap<String, Object>();
				entry.put("name", entity.getName());
				if(!Config.RADAR_ONLY_DISTANCE) {
					entry.put("x", (int) dx);
					entry.put("y", (int) dy);
					entry.put("z", (int) dz);
				}
				entry.put("distance", Math.sqrt(dx * dx + dy * dy + dz * dz));
				entities.add(entry);
			}
		}
		return entities;
	}

	public static Set<Map<String, Object>> getItems(World world, BlockPos pos, AxisAlignedBB bounds, Class<? extends EntityItem> eClass) {
		return getItems(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, bounds, eClass);
	}

	public static Set<Map<String, Object>> getItems(World world, double xCoord, double yCoord, double zCoord, AxisAlignedBB bounds, Class<? extends EntityItem> eClass) {
		Set<Map<String, Object>> entities = new HashSet<Map<String, Object>>();
		for(EntityItem entity : world.getEntitiesWithinAABB(eClass, bounds)) {
			double dx = entity.posX - xCoord;
			double dy = entity.posY - yCoord;
			double dz = entity.posZ - zCoord;
			if(Math.sqrt(dx * dx + dy * dy + dz * dz) < Config.RADAR_RANGE) {
				// Maps are converted to tables on the Lua side.
				Map<String, Object> entry = new HashMap<String, Object>();
				ItemStack stack = entity.getItem();
				entry.put("name", Item.REGISTRY.getNameForObject(stack.getItem()));
				entry.put("damage", stack.getItemDamage());
				entry.put("hasTag", stack.hasTagCompound());
				entry.put("size", stack.getCount());
				entry.put("label", stack.getDisplayName());

				if(!Config.RADAR_ONLY_DISTANCE) {
					entry.put("x", (int) dx);
					entry.put("y", (int) dy);
					entry.put("z", (int) dz);
				}
				entry.put("distance", Math.sqrt(dx * dx + dy * dy + dz * dz));
				entities.add(entry);
			}
		}
		return entities;
	}

}

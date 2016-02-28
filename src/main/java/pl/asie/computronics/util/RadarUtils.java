package pl.asie.computronics.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import pl.asie.computronics.reference.Config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RadarUtils {

	public static Set<Map<String, Object>> getEntities(World world, BlockPos pos, AxisAlignedBB bounds, Class<? extends EntityLivingBase> eClass){
		return getEntities(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, bounds, eClass);
	}

	public static Set<Map<String, Object>> getEntities(World world, double x, double y, double z, AxisAlignedBB bounds, Class<? extends EntityLivingBase> eClass) {
		Set<Map<String, Object>> entities = new HashSet<Map<String, Object>>();
		for(Object obj : world.getEntitiesWithinAABB(eClass, bounds)) {
			EntityLivingBase entity = (EntityLivingBase) obj;
			double dx = entity.posX - x;
			double dy = entity.posY - y;
			double dz = entity.posZ - z;
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

	public static Set<Map<String, Object>> getItems(World world, BlockPos pos, AxisAlignedBB bounds, Class<? extends EntityItem> eClass){
		return getItems(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, bounds, eClass);
	}

	public static Set<Map<String, Object>> getItems(World world, double x, double y, double z, AxisAlignedBB bounds, Class<? extends EntityItem> eClass) {
		Set<Map<String, Object>> entities = new HashSet<Map<String, Object>>();
		for(Object obj : world.getEntitiesWithinAABB(eClass, bounds)) {
			EntityItem entity = (EntityItem) obj;
			double dx = entity.posX - x;
			double dy = entity.posY - y;
			double dz = entity.posZ - z;
			if(Math.sqrt(dx * dx + dy * dy + dz * dz) < Config.RADAR_RANGE) {
				// Maps are converted to tables on the Lua side.
				Map<String, Object> entry = new HashMap<String, Object>();
				ItemStack stack = entity.getEntityItem();
				entry.put("name", Item.itemRegistry.getNameForObject(stack.getItem()));
				entry.put("damage", stack.getItemDamage());
				entry.put("hasTag", stack.hasTagCompound());
				entry.put("size", stack.stackSize);
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

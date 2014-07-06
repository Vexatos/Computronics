package pl.asie.computronics.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import li.cil.oc.api.machine.Robot;
import pl.asie.computronics.Computronics;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class RadarUtils {
	public static Set<Map> getEntities(World world, int xCoord, int yCoord ,int zCoord, AxisAlignedBB bounds, Class eClass) {
		Set<Map> entities = new HashSet<Map>();
		for (Object obj : world.getEntitiesWithinAABB(eClass, bounds)) {
            EntityLivingBase entity = (EntityLivingBase)obj;
            double dx = entity.posX - (xCoord + 0.5);
			double dy = entity.posY - (yCoord + 0.5);
            double dz = entity.posZ - (zCoord + 0.5);
            // Check if the entity is actually in range. I forget, is there a cubed root function?
            if (Math.sqrt(dx * dx + dz * dz) < Computronics.RADAR_RANGE && Math.sqrt(dx * dx + dy * dy) < Computronics.RADAR_RANGE && Math.sqrt(dy * dy + dz * dz) < Computronics.RADAR_RANGE) {
                // Maps are converted to tables on the Lua side.
                Map<String, Object> entry = new HashMap<String, Object>();
                if(entity instanceof EntityPlayer) {
                    entry.put("name", ((EntityPlayer)entity).getDisplayName());
                } else if (entity instanceof EntityLiving && ((EntityLiving)entity).hasCustomNameTag()) {
                    entry.put("name", ((EntityLiving)entity).getCustomNameTag());
                } else {
                    entry.put("name", entity.getCommandSenderName());
                }
                if(!Computronics.RADAR_ONLY_DISTANCE) {
                	entry.put("x", (int) dx);
                	entry.put("y", (int) dy);
                	entry.put("z", (int) dz);
                }
                entry.put("distance", Math.sqrt(dx*dx + dy*dy + dz*dz));
                entities.add(entry);
            }
        }
		return entities;
	}
}

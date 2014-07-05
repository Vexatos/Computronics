package pl.asie.computronics.tile;

import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.AxisAlignedBB;
import pl.asie.lib.block.TileEntityBase;
import pl.asie.computronics.Computronics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileRadar extends TileEntityBase implements SimpleComponent {
    public static final double RadarRange = Computronics.RADAR_RANGE;

    @Override
    public String getComponentName() {
        return "radar";
    }

    @Callback
    public Object[] getEntities(Context context, Arguments args) {
        List<Map> entities = new ArrayList<Map>();
        // Get a initial list of entities near the tile entity.
        AxisAlignedBB bounds = AxisAlignedBB.
                getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1).
                expand(RadarRange, RadarRange, RadarRange);
        for (Object obj : getWorldObj().getEntitiesWithinAABB(EntityLiving.class, bounds)) {
            EntityLiving entity = (EntityLiving) obj;
            double dx = entity.posX - (xCoord + 0.5);
			double dy = entity.posY - (yCoord + 0.5);
            double dz = entity.posZ - (zCoord + 0.5);
            // Check if the entity is actually in range. I forget, is there a cubed root function?
            if (Math.sqrt(dx * dx + dz * dz) < RadarRange && Math.sqrt(dx * dx + dy * dy) < RadarRange && Math.sqrt(dy * dy + dz * dz) < RadarRange) {
                // Maps are converted to tables on the Lua side.
                Map<String, Object> entry = new HashMap<String, Object>();
                if (entity.hasCustomNameTag()) {
                    entry.put("name", entity.getCustomNameTag());
                }
                else {
                    entry.put("name", entity.getCommandSenderName());
                }
                entry.put("x", (int) dx);
				entry.put("y", (int) dy);
                entry.put("z", (int) dz);
                entities.add(entry);
            }
        }
        context.pause(0.5);

        // The returned array is treated as a tuple, meaning if we return the
        // entities as an array directly, we'd end up with each entity as an
        // individual result value (i.e. in Lua we'd have to write
        //   result = {radar.getEntities()}
        // and we'd be limited in the number of entities, due to the limit of
        // return values. So we wrap it in an array to return it as a list.
        return new Object[]{entities.toArray()};
    }
}

package pl.asie.computronics.tile;

import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.SimpleComponent;
import li.cil.oc.api.machine.Robot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import pl.asie.lib.block.TileEntityBase;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.util.RadarUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileRadar extends TileEntityBase implements SimpleComponent {

    @Override
    public String getComponentName() {
        return "radar";
    }

    private AxisAlignedBB getBounds() {
    	return AxisAlignedBB.
                getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1).
                expand(Computronics.RADAR_RANGE, Computronics.RADAR_RANGE, Computronics.RADAR_RANGE);
    }
    @Callback
    public Object[] getEntities(Context context, Arguments args) {
        List<Map> entities = new ArrayList<Map>();
        AxisAlignedBB bounds = getBounds();
        entities.addAll(RadarUtils.getEntities(getWorldObj(), xCoord, yCoord, zCoord, bounds, EntityPlayer.class));
        entities.addAll(RadarUtils.getEntities(getWorldObj(), xCoord, yCoord, zCoord, bounds, Robot.class));
        entities.addAll(RadarUtils.getEntities(getWorldObj(), xCoord, yCoord, zCoord, bounds, EntityLiving.class));
        context.pause(0.5);

        // The returned array is treated as a tuple, meaning if we return the
        // entities as an array directly, we'd end up with each entity as an
        // individual result value (i.e. in Lua we'd have to write
        //   result = {radar.getEntities()}
        // and we'd be limited in the number of entities, due to the limit of
        // return values. So we wrap it in an array to return it as a list.
        return new Object[]{entities.toArray()};
    }
	
	@Callback
    public Object[] getPlayers(Context context, Arguments args) {
        List<Map> entities = new ArrayList<Map>();
        AxisAlignedBB bounds = getBounds();
        entities.addAll(RadarUtils.getEntities(getWorldObj(), xCoord, yCoord, zCoord, bounds, EntityPlayer.class));
        context.pause(0.5);
        
        return new Object[]{entities.toArray()};
    }
	
	@Callback
    public Object[] getRobots(Context context, Arguments args) {
        List<Map> entities = new ArrayList<Map>();
        AxisAlignedBB bounds = getBounds();
        entities.addAll(RadarUtils.getEntities(getWorldObj(), xCoord, yCoord, zCoord, bounds, Robot.class));
        context.pause(0.5);

        return new Object[]{entities.toArray()};
    }
	
	@Callback
    public Object[] getMobs(Context context, Arguments args) {
        List<Map> entities = new ArrayList<Map>();
        AxisAlignedBB bounds = getBounds();
        entities.addAll(RadarUtils.getEntities(getWorldObj(), xCoord, yCoord, zCoord, bounds, EntityLiving.class));
        context.pause(0.5);
        
        return new Object[]{entities.toArray()};
    }
}

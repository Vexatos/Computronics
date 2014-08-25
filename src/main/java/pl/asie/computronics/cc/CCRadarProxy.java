package pl.asie.computronics.cc;

import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.util.RadarUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CCRadarProxy {
    private static AxisAlignedBB getBounds(int xCoord, int yCoord, int zCoord, int d) {
    	int distance = Math.min(d, Computronics.RADAR_RANGE);
    	if(distance < 1) distance = 1;
    	return AxisAlignedBB.
                getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1).
                expand(distance, distance, distance);
    }

    @Optional.Method(modid="ComputerCraft")
	public static String[] getMethodNames() {
		return new String[]{"getEntities", "getPlayers", "getMobs"};
	}

	private static class RadarEnqueuerCC implements Runnable {
		private IComputerAccess computer;
		private int distance, i;
		private Map[] entities;
		
		public RadarEnqueuerCC(int distance, Set<Map> entities, IComputerAccess computer) {
			this.computer = computer;
			this.distance = distance;
			this.entities = entities.toArray(new Map[entities.size()]);
			this.i = 0;
		}
		
		@Override
		public void run() {
			try {
				while(i < distance) {
					Thread.sleep((long)(Computronics.RADAR_CC_TIME * 1000));
					i++;
					for(Map m: entities) {
						int entityD = ((Integer)m.get("distance")).intValue();
						if(entityD >= (i - 1) && entityD < i) {
							if(Computronics.RADAR_ONLY_DISTANCE) {
								computer.queueEvent("entity", new Object[]{m.get("name"), entityD});
							} else {
								computer.queueEvent("entity", new Object[]{m.get("name"), entityD, m.get("x"), m.get("y"), m.get("z")});
							}
						}
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
    @Optional.Method(modid="ComputerCraft")
	public static Object[] callMethod(World worldObj, int xCoord, int yCoord, int zCoord, IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
			InterruptedException {
		int distance = Computronics.RADAR_RANGE;
		if(arguments.length >= 1 && (arguments[0] instanceof Double)) {
			distance = ((Double)arguments[0]).intValue();
			if(distance <= 0 || distance > Computronics.RADAR_RANGE) distance = Computronics.RADAR_RANGE;
		}
		AxisAlignedBB bounds = getBounds(xCoord, yCoord, zCoord, distance);
    	Set<Map> entities = new HashSet<Map>();
    	if(method == 0 || method == 1) entities.addAll(RadarUtils.getEntities(worldObj, xCoord, yCoord, zCoord, bounds, EntityPlayer.class));
    	if(method == 0 || method == 2) entities.addAll(RadarUtils.getEntities(worldObj, xCoord, yCoord, zCoord, bounds, EntityLiving.class));
    	RadarEnqueuerCC enqueuer = new RadarEnqueuerCC(distance, entities, computer);
    	new Thread(enqueuer).run();
		return null;
	}
}

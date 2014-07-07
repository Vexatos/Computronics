package pl.asie.computronics.tile;

import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.SimpleComponent;
import li.cil.oc.api.network.Connector;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.Network;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import pl.asie.lib.block.TileEntityBase;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.util.RadarUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;

@Optional.Interface(iface = "li.cil.li.oc.network.Environment", modid = "OpenComputers")
public class TileRadar extends TileEntityPeripheralBase implements Environment {

	protected boolean hasEnergy;
	
	public TileRadar() {
		super("radar");
	}
   
    private int getDistance(Arguments args) {
    	if(args.isInteger(0)) {
    		return args.checkInteger(0);
    	} else return Computronics.RADAR_RANGE;
    }
    
    private AxisAlignedBB getBounds(int d) {
    	int distance = Math.min(d, Computronics.RADAR_RANGE);
    	if(distance < 1) distance = 1;
    	return AxisAlignedBB.
                getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1).
                expand(distance, distance, distance);
    }

    @Callback
    @Optional.Method(modid="OpenComputers")
    public Object[] getEntities(Context context, Arguments args) {
		List<Map> entities = new ArrayList<Map>();
		if(hasEnergy) {
			int distance = getDistance(args);
			AxisAlignedBB bounds = getBounds(distance);
			entities.addAll(RadarUtils.getEntities(getWorldObj(), xCoord, yCoord, zCoord, bounds, EntityPlayer.class));
			entities.addAll(RadarUtils.getEntities(getWorldObj(), xCoord, yCoord, zCoord, bounds, EntityLiving.class));
			context.pause(0.5);
			
			// Suck some power
			hasEnergy = ((Connector) node).tryChangeBuffer(0 - (Computronics.RADAR_OC_ENERGY_COST * distance * 2));
		}
		// The returned array is treated as a tuple, meaning if we return the
		// entities as an array directly, we'd end up with each entity as an
		// individual result value (i.e. in Lua we'd have to write
		//   result = {radar.getEntities()}
		// and we'd be limited in the number of entities, due to the limit of
		// return values. So we wrap it in an array to return it as a list.
		return new Object[]{entities.toArray()};
    }
	
	@Callback
    @Optional.Method(modid="OpenComputers")
    public Object[] getPlayers(Context context, Arguments args) {
        List<Map> entities = new ArrayList<Map>();
		if(hasEnergy) {
			int distance = getDistance(args);
			AxisAlignedBB bounds = getBounds(distance);
			entities.addAll(RadarUtils.getEntities(getWorldObj(), xCoord, yCoord, zCoord, bounds, EntityPlayer.class));
			context.pause(0.5);
			// Suck some power
			hasEnergy = ((Connector) node).tryChangeBuffer(0 - (Computronics.RADAR_OC_ENERGY_COST * distance));
		}
        return new Object[]{entities.toArray()};
    }
	
	@Callback
    @Optional.Method(modid="OpenComputers")
    public Object[] getMobs(Context context, Arguments args) {
        List<Map> entities = new ArrayList<Map>();
		if(hasEnergy) {
			int distance = getDistance(args);
			AxisAlignedBB bounds = getBounds(distance);
			entities.addAll(RadarUtils.getEntities(getWorldObj(), xCoord, yCoord, zCoord, bounds, EntityLiving.class));
			context.pause(0.5);
			// Suck some power
			hasEnergy = ((Connector) node).tryChangeBuffer(0 - (Computronics.RADAR_OC_ENERGY_COST * distance));
		}
        return new Object[]{entities.toArray()};
    }

	@Override
    @Optional.Method(modid="ComputerCraft")
	public String[] getMethodNames() {
		return new String[]{"getEntities", "getPlayers", "getMobs"};
	}

	private class RadarEnqueuerCC implements Runnable {
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
	
	@Override
    @Optional.Method(modid="ComputerCraft")
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
			InterruptedException {
		int distance = Computronics.RADAR_RANGE;
		if(arguments.length >= 1 && (arguments[0] instanceof Integer)) {
			distance = ((Integer)arguments[0]).intValue();
			if(distance <= 0 || distance > Computronics.RADAR_RANGE) distance = Computronics.RADAR_RANGE;
		}
		AxisAlignedBB bounds = getBounds(distance);
    	Set<Map> entities = new HashSet<Map>();
    	if(method == 0 || method == 1) entities.addAll(RadarUtils.getEntities(getWorldObj(), xCoord, yCoord, zCoord, bounds, EntityPlayer.class));
    	if(method == 0 || method == 2) entities.addAll(RadarUtils.getEntities(getWorldObj(), xCoord, yCoord, zCoord, bounds, EntityLiving.class));
    	RadarEnqueuerCC enqueuer = new RadarEnqueuerCC(distance, entities, computer);
    	new Thread(enqueuer).run();
		return null;
	}

	@Override
    @Optional.Method(modid="nedocomputers")
	public short busRead(int addr) {
		return 0;
	}

	@Override
    @Optional.Method(modid="nedocomputers")
	public void busWrite(int addr, short data) {
	}
}

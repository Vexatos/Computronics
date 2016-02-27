package pl.asie.computronics.tile;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Connector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.cc.CCRadarProxy;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.RadarUtils;
import pl.asie.lib.api.tile.IBatteryProvider;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TileRadar extends TileEntityPeripheralBase implements IBatteryProvider {

	public TileRadar() {
		super("radar", Config.RADAR_ENERGY_COST_OC * Config.RADAR_RANGE * 3.5);
	}

	private int getDistance(Arguments args) {
		if(args.isInteger(0)) {
			return args.checkInteger(0);
		} else {
			return Config.RADAR_RANGE;
		}
	}

	private AxisAlignedBB getBounds(int d) {
		int distance = Math.min(d, Config.RADAR_RANGE);
		if(distance < 1) {
			distance = 1;
		}
		final BlockPos pos = getPos();
		return AxisAlignedBB.
			fromBounds(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).
			expand(distance, distance, distance);
	}

	@Callback(doc = "function([distance:number]):table; Returns a list of all entities detected within the specified or the maximum range")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getEntities(Context context, Arguments args) {
		Set<Map> entities = new HashSet<Map>();
		int distance = getDistance(args);
		double energyNeeded = (Config.RADAR_ENERGY_COST_OC * distance * 1.75);
		if(((Connector) node()).tryChangeBuffer(0 - energyNeeded)) {
			AxisAlignedBB bounds = getBounds(distance);
			entities.addAll(RadarUtils.getEntities(worldObj, getPos(), bounds, EntityPlayer.class));
			entities.addAll(RadarUtils.getEntities(worldObj, getPos(), bounds, EntityLiving.class));
			context.pause(0.5);
		}
		// The returned array is treated as a tuple, meaning if we return the
		// entities as an array directly, we'd end up with each entity as an
		// individual result value (i.e. in Lua we'd have to write
		//   result = {radar.getEntities()}
		// and we'd be limited in the number of entities, due to the limit of
		// return values. So we wrap it in an array to return it as a list.
		return new Object[] { RadarUtils.convertSetToMap(entities) };
	}

	@Callback(doc = "function([distance:number]):table; Returns a list of all players detected within the specified or the maximum range")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getPlayers(Context context, Arguments args) {
		Set<Map> entities = new HashSet<Map>();
		int distance = getDistance(args);
		double energyNeeded = (Config.RADAR_ENERGY_COST_OC * distance * 1.0);
		if(((Connector) node()).tryChangeBuffer(0 - energyNeeded)) {
			AxisAlignedBB bounds = getBounds(distance);
			entities.addAll(RadarUtils.getEntities(worldObj, getPos(), bounds, EntityPlayer.class));
			context.pause(0.5);
		}
		return new Object[] { RadarUtils.convertSetToMap(entities) };
	}

	@Callback(doc = "function([distance:number]):table; Returns a list of all mobs detected within the specified or the maximum range")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getMobs(Context context, Arguments args) {
		Set<Map> entities = new HashSet<Map>();
		int distance = getDistance(args);
		double energyNeeded = (Config.RADAR_ENERGY_COST_OC * distance * 1.0);
		if(((Connector) node()).tryChangeBuffer(0 - energyNeeded)) {
			AxisAlignedBB bounds = getBounds(distance);
			entities.addAll(RadarUtils.getEntities(worldObj, getPos(), bounds, EntityLiving.class));
			context.pause(0.5);
		}
		return new Object[] { RadarUtils.convertSetToMap(entities) };
	}

	@Callback(doc = "function([distance:number]):table; Returns a list of all items detected within the specified or the maximum range")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getItems(Context context, Arguments args) {
		Set<Map> entities = new HashSet<Map>();
		int distance = getDistance(args);
		double energyNeeded = (Config.RADAR_ENERGY_COST_OC * distance * 2.0);
		if(((Connector) node()).tryChangeBuffer(0 - energyNeeded)) {
			AxisAlignedBB bounds = getBounds(distance);
			entities.addAll(RadarUtils.getItems(worldObj, getPos(), bounds, EntityItem.class));
			context.pause(0.5);
		}
		return new Object[] { RadarUtils.convertSetToMap(entities) };
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String[] getMethodNames() {
		return CCRadarProxy.getMethodNames();
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
		int method, Object[] arguments) throws LuaException,
		InterruptedException {
		return CCRadarProxy.callMethod(worldObj, getPos(), computer, context, method, arguments, this);
	}
}

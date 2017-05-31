package pl.asie.computronics.tile;

import cpw.mods.fml.common.Optional;
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
import pl.asie.computronics.cc.CCRadarProxy;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.OCUtils;
import pl.asie.computronics.util.RadarUtils;
import pl.asie.computronics.util.TableUtils;
import pl.asie.lib.api.tile.IBatteryProvider;
import pl.asie.lib.tile.BatteryBasic;
import pl.asie.lib.util.EnergyConverter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TileRadar extends TileEntityPeripheralBase implements IBatteryProvider {

	public TileRadar() {
		super("radar", 0);
		this.registerBattery(new BatteryBasic(EnergyConverter.convertEnergy(Config.RADAR_ENERGY_COST_OC * Config.RADAR_RANGE * 3.5, "OC", "RF")));
	}

	@Override
	public boolean canUpdate() {
		return Config.MUST_UPDATE_TILE_ENTITIES;
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
		return AxisAlignedBB.
			getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1).
			expand(distance, distance, distance);
	}

	public boolean extractFromBattery(double amount) {
		double amt = EnergyConverter.convertEnergy(amount, "OC", "RF");
		if(this.getBatteryProvider().getEnergyStored() < amt) {
			return false;
		}
		this.getBatteryProvider().extract(-1, amt, false);
		return true;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	protected OCUtils.Device deviceInfo() {
		return new OCUtils.Device(
			DeviceClass.Multimedia,
			"Radar",
			OCUtils.Vendors.Trumbour,
			"Detectotron M1"
		);
	}

	@Callback(doc = "function([distance:number]):table; Returns a list of all entities detected within the specified or the maximum range")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getEntities(Context context, Arguments args) {
		Set<Map> entities = new HashSet<Map>();
		int distance = getDistance(args);
		double energyNeeded = (Config.RADAR_ENERGY_COST_OC * distance * 1.75);
		if(((Connector) node()).tryChangeBuffer(0 - energyNeeded)
			|| extractFromBattery(energyNeeded)) {
			AxisAlignedBB bounds = getBounds(distance);
			entities.addAll(RadarUtils.getEntities(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, bounds, EntityPlayer.class));
			entities.addAll(RadarUtils.getEntities(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, bounds, EntityLiving.class));
			context.pause(0.5);
		}
		// The returned array is treated as a tuple, meaning if we return the
		// entities as an array directly, we'd end up with each entity as an
		// individual result value (i.e. in Lua we'd have to write
		//   result = {radar.getEntities()}
		// and we'd be limited in the number of entities, due to the limit of
		// return values. So we wrap it in an array to return it as a list.
		return new Object[] { TableUtils.convertSetToMap(entities) };
	}

	@Callback(doc = "function([distance:number]):table; Returns a list of all players detected within the specified or the maximum range")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getPlayers(Context context, Arguments args) {
		Set<Map> entities = new HashSet<Map>();
		int distance = getDistance(args);
		double energyNeeded = (Config.RADAR_ENERGY_COST_OC * distance * 1.0);
		if(((Connector) node()).tryChangeBuffer(0 - energyNeeded)
			|| extractFromBattery(energyNeeded)) {
			AxisAlignedBB bounds = getBounds(distance);
			entities.addAll(RadarUtils.getEntities(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, bounds, EntityPlayer.class));
			context.pause(0.5);
		}
		return new Object[] { TableUtils.convertSetToMap(entities) };
	}

	@Callback(doc = "function([distance:number]):table; Returns a list of all mobs detected within the specified or the maximum range")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getMobs(Context context, Arguments args) {
		Set<Map> entities = new HashSet<Map>();
		int distance = getDistance(args);
		double energyNeeded = (Config.RADAR_ENERGY_COST_OC * distance * 1.0);
		if(((Connector) node()).tryChangeBuffer(0 - energyNeeded)
			|| extractFromBattery(energyNeeded)) {
			AxisAlignedBB bounds = getBounds(distance);
			entities.addAll(RadarUtils.getEntities(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, bounds, EntityLiving.class));
			context.pause(0.5);
		}
		return new Object[] { TableUtils.convertSetToMap(entities) };
	}

	@Callback(doc = "function([distance:number]):table; Returns a list of all items detected within the specified or the maximum range")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getItems(Context context, Arguments args) {
		Set<Map> entities = new HashSet<Map>();
		int distance = getDistance(args);
		double energyNeeded = (Config.RADAR_ENERGY_COST_OC * distance * 2.0);
		if(((Connector) node()).tryChangeBuffer(0 - energyNeeded)
			|| extractFromBattery(energyNeeded)) {
			AxisAlignedBB bounds = getBounds(distance);
			entities.addAll(RadarUtils.getItems(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, bounds, EntityItem.class));
			context.pause(0.5);
		}
		return new Object[] { TableUtils.convertSetToMap(entities) };
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
		return CCRadarProxy.callMethod(worldObj, xCoord, yCoord, zCoord, computer, context, method, arguments, this);
	}
}

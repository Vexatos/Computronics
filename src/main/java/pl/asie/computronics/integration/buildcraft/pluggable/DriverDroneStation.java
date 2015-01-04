package pl.asie.computronics.integration.buildcraft.pluggable;

import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.pluggable.PipePluggable;
import li.cil.oc.api.Network;
import li.cil.oc.api.internal.Drone;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.ManagedEnvironment;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author Vexatos
 */
public class DriverDroneStation extends ManagedEnvironment {

	protected final Drone drone;
	protected boolean isDocking = false;

	public DriverDroneStation(Drone drone) {
		this.drone = drone;
		this.setNode(Network.newNode(this, Visibility.Neighbors).
			withComponent("docking").
			create());
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void update() {

	}

	@Callback(doc="function(slot:number,maxAmount:number):number; drops items into the attached pipe if docked; Returns the amount of items dropped on success, 0 and an error message otherwise")
	public Object[] dropItem(Context context, Arguments args) {
		return new Object[] { false };
	}

	@Callback(doc = "function():boolean; Makes the drone start docking with a docking station; Always tries to dock with a station below it first")
	public Object[] dock(Context context, Arguments args) {
		int x = (int) Math.floor(drone.xPosition());
		int y = (int) Math.floor(drone.yPosition());
		int z = (int) Math.floor(drone.zPosition());
		double targetY = y;
		World world = drone.world();
		DroneStationPluggable station = tryGetStation(world, x, y - 1, z, ForgeDirection.UP);
		if(station != null) {
			targetY = y - 1 + station.getBoundingBox(ForgeDirection.UP).maxY;
		} else {
			station = tryGetStation(world, x, y + 1, z, ForgeDirection.DOWN);
			if(station != null) {
				targetY = y + 1 + station.getBoundingBox(ForgeDirection.DOWN).minY;
			}
		}
		if(station != null && drone instanceof li.cil.oc.common.entity.Drone) {
			li.cil.oc.common.entity.Drone droneEntity = (li.cil.oc.common.entity.Drone) drone;
			if(droneEntity.motionX != 0 || droneEntity.motionY != 0 || droneEntity.motionZ != 0) {
				return new Object[] { false, "drone is still moving" };
			}
			droneEntity.targetX_$eq(x);
			droneEntity.targetY_$eq((float) targetY);
			droneEntity.targetZ_$eq(z);
			return new Object[] { true };
		}
		return new Object[] { false };
	}

	private DroneStationPluggable tryGetStation(World world, int x, int y, int z, ForgeDirection side) {
		TileEntity tile = world.getTileEntity(x, y - 1, z);
		if(tile != null && tile instanceof IPipeTile) {
			PipePluggable pluggable = ((IPipeTile) tile).getPipePluggable(ForgeDirection.UP);
			if(pluggable != null && pluggable instanceof DroneStationPluggable) {
				return (DroneStationPluggable) pluggable;
			}
		}
		return null;
	}

	@Override
	public void save(NBTTagCompound nbt) {
		super.save(nbt);
		nbt.setBoolean("drone:docking", isDocking);
	}

	@Override
	public void load(NBTTagCompound nbt) {
		super.load(nbt);
		isDocking = nbt.getBoolean("drone:docking");
	}
}

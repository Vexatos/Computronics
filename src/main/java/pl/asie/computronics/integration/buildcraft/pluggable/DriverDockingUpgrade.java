package pl.asie.computronics.integration.buildcraft.pluggable;

import buildcraft.api.core.EnumColor;
import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.pluggable.PipePluggable;
import li.cil.oc.api.Network;
import li.cil.oc.api.internal.Drone;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.ManagedEnvironment;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.integration.buildcraft.pluggable.DroneStationPluggable.DroneStationState;

/**
 * @author Vexatos
 */
public class DriverDockingUpgrade extends ManagedEnvironment {

	protected final Drone drone;

	protected boolean isDocking = false;
	protected boolean isDocked = false;
	protected IPipeTile pipe;
	protected final ForgeDirection side = ForgeDirection.UP;
	private int[] pipevec;

	private float targetX, targetY, targetZ;

	public DriverDockingUpgrade(Drone drone) {
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
		if((isDocking || isDocked) && pipe == null && pipevec != null) {
			TileEntity tile = drone.world().getTileEntity(pipevec[0], pipevec[1], pipevec[2]);
			if(tile instanceof IPipeTile) {
				pipe = (IPipeTile) tile;
				if(pipe.getPipePluggable(side) instanceof DroneStationPluggable) {
					DroneStationPluggable station = (DroneStationPluggable) pipe.getPipePluggable(side);
					station.setDrone(drone);
				} else {
					isDocked = false;
					isDocking = false;
					pipe = null;
				}
			} else {
				isDocked = false;
				isDocking = false;
			}
		}
		Vec3 velocity = drone.getVelocity();
		if(isDocking && pipe != null && velocity.xCoord == 0 && velocity.yCoord == 0 && velocity.zCoord == 0) {
			isDocking = false;
			isDocked = true;
			DroneStationPluggable station = (DroneStationPluggable) pipe.getPipePluggable(side);
			station.setDrone(drone);
		}
		if(isDocked) {
			Vec3 target = drone.getTarget();
			Vec3 realTarget = Vec3.createVectorHelper(targetX, targetY, targetZ);
			if(target.distanceTo(realTarget) > 0) {
				Entity droneEntity = (Entity) drone;
				droneEntity.motionX = 0;
				droneEntity.motionY = 0;
				droneEntity.motionZ = 0;
				drone.setTarget(Vec3.createVectorHelper(targetX, targetY, targetZ));
				droneEntity.posX = targetX;
				droneEntity.posY = targetY;
				droneEntity.posZ = targetZ;
			}
		}
	}

	@Callback(doc = "function(slot:number[,maxAmount:number[,color:number]]):number; drops an item into the attached pipe if docked; Returns the amount of items dropped on success; Allows coloring the item if the drone is tier 2")
	public Object[] dropItem(Context context, Arguments args) {
		if(!isDocked || pipe == null) {
			if(isDocking) {
				return new Object[] { 0, "drone is still docking" };
			}
			return new Object[] { 0, "drone is not docked" };
		}
		if(!(pipe.getPipeType() == IPipeTile.PipeType.ITEM)) {
			return new Object[] { 0, "pipe is not an item pipe" };
		}
		int count = args.count() > 1 ? Math.max(0, Math.min(64, args.checkInteger(1))) : 64;
		int slot = Math.max(0, args.checkInteger(0) - 1);
		ItemStack stack = drone.inventory().getStackInSlot(slot);
		if(stack != null && stack.getItem() != null) {
			stack = drone.inventory().decrStackSize(args.checkInteger(0), count);
			pipe.injectItem(stack, true, side, args.count() > 2 && drone.tier() > 0 ? EnumColor.fromId(args.checkInteger(2)) : null);
			return new Object[] { stack.stackSize };
		}
		return new Object[] { 0, "invalid/empty slot" };
	}

	@Callback(doc = "function():boolean; Makes the drone start docking with a docking station; Always tries to dock with a station below it first")
	public Object[] dock(Context context, Arguments args) {
		int x = (int) Math.floor(drone.xPosition());
		int y = (int) Math.floor(drone.yPosition());
		int z = (int) Math.floor(drone.zPosition());
		World world = drone.world();
		DroneStationPluggable station = tryGetStation(world, x, y - 1, z, side);
		if(station != null && station.getState() != DroneStationState.Used) {
			double targetY = pipe.y() + 1;
			Vec3 velocity = drone.getVelocity();
			if(velocity.xCoord != 0 || velocity.yCoord != 0 || velocity.zCoord != 0) {
				return new Object[] { false, "drone is still moving" };
			}
			Vec3 target = drone.getTarget();
			target.yCoord = (float) targetY;
			drone.setTarget(target);
			target = drone.getTarget();
			this.targetX = (float) target.xCoord;
			this.targetY = (float) target.yCoord;
			this.targetZ = (float) target.zCoord;
			this.isDocking = true;
			return new Object[] { true };
		}
		return new Object[] { false, "no non-occupied station found" };
	}

	@Callback(doc = "function():boolean; Releases the drone if docked")
	public Object[] release(Context context, Arguments args) {
		if(!isDocked || pipe == null || !(pipe.getPipePluggable(side) instanceof DroneStationPluggable)) {
			if(isDocking) {
				return new Object[] { 0, "drone is still docking" };
			}
			return new Object[] { 0, "drone is not docked" };
		}

		double targetY = pipe.y() + 1.5;
		Vec3 target = drone.getTarget();
		target.yCoord = (float) targetY;
		drone.setTarget(target);
		((DroneStationPluggable) pipe.getPipePluggable(side)).setDrone(null);
		isDocking = false;
		isDocked = false;
		pipe = null;
		return new Object[] { true };
	}

	private DroneStationPluggable tryGetStation(World world, int x, int y, int z, ForgeDirection side) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile != null && tile instanceof IPipeTile) {
			PipePluggable pluggable = ((IPipeTile) tile).getPipePluggable(side);
			if(pluggable != null && pluggable instanceof DroneStationPluggable) {
				this.pipe = (IPipeTile) tile;
				return (DroneStationPluggable) pluggable;
			}
		}
		return null;
	}

	@Override
	public void save(NBTTagCompound nbt) {
		super.save(nbt);
		nbt.setBoolean("drone:docking", isDocking);
		nbt.setBoolean("drone:docked", isDocked);
		if((isDocked || isDocking) && pipe != null) {
			nbt.setInteger("drone:dockX", pipe.x());
			nbt.setInteger("drone:dockY", pipe.y());
			nbt.setInteger("drone:dockZ", pipe.z());
			nbt.setFloat("drone:targetX", targetX);
			nbt.setFloat("drone:targetY", targetY);
			nbt.setFloat("drone:targetZ", targetZ);
		}
	}

	@Override
	public void load(NBTTagCompound nbt) {
		super.load(nbt);
		isDocking = nbt.getBoolean("drone:docking");
		isDocked = nbt.getBoolean("drone:docked");
		if((isDocked || isDocking)) {
			pipevec = new int[] {
				nbt.getInteger("drone:dockX"),
				nbt.getInteger("drone:dockY"),
				nbt.getInteger("drone:dockZ") };
			targetX = nbt.getFloat("drone:targetX");
			targetY = nbt.getFloat("drone:targetY");
			targetZ = nbt.getFloat("drone:targetZ");
		}
	}
}

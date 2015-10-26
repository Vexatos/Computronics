package pl.asie.computronics.integration.buildcraft.pluggable;

import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.pluggable.IPipePluggableRenderer;
import buildcraft.api.transport.pluggable.PipePluggable;
import buildcraft.core.lib.utils.MatrixTranformations;
import cofh.api.energy.IEnergyReceiver;
import io.netty.buffer.ByteBuf;
import li.cil.oc.Settings;
import li.cil.oc.api.internal.Drone;
import li.cil.oc.api.network.Connector;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.util.ParticleUtils;
import pl.asie.lib.util.EnergyConverter;

/**
 * @author Vexatos
 */
public class DroneStationPluggable extends PipePluggable implements IEnergyReceiver {

	public enum DroneStationState {
		Available,
		Used;

		public static final DroneStationState[] VALUES = values();
	}

	private DroneStationState state = DroneStationState.Available;
	private Drone drone;

	public Drone getDrone() {
		return drone;
	}

	public void setDrone(Drone drone) {
		this.drone = drone;
		if(this.drone == null) {
			this.state = DroneStationState.Available;
		} else {
			this.state = DroneStationState.Used;
		}
	}

	public DroneStationState getState() {
		return state;
	}

	public boolean isConnected(Drone drone, ForgeDirection side) {
		return this.drone == drone;
	}

	@Override
	public ItemStack[] getDropItems(IPipeTile pipe) {
		return new ItemStack[] { new ItemStack(Computronics.buildcraft.droneStationItem, 1, 0) };
	}

	@Override
	public boolean isBlocking(IPipeTile pipe, ForgeDirection direction) {
		return true;
	}

	@Override
	public void update(IPipeTile pipe, ForgeDirection direction) {
		super.update(pipe, direction);
		if(getState() == DroneStationState.Used && drone == null) {
			state = DroneStationState.Available;
		}
		if(drone != null && (drone.world() != pipe.getWorld()
			|| drone instanceof Entity && ((Entity) drone).getDistanceSq(pipe.x(), pipe.y(), pipe.z()) >= 4)) {
			setDrone(null);
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox(ForgeDirection side) {
		float[][] bounds = new float[3][2];
		// X START - END
		bounds[0][0] = 0.25F;
		bounds[0][1] = 0.75F;
		// Y START - END
		bounds[1][0] = 0.125F;
		bounds[1][1] = 0.251F;
		// Z START - END
		bounds[2][0] = 0.25F;
		bounds[2][1] = 0.75F;

		MatrixTranformations.transform(bounds, side);
		return AxisAlignedBB.getBoundingBox(bounds[0][0], bounds[1][0], bounds[2][0], bounds[0][1], bounds[1][1], bounds[2][1]);
	}

	@Override
	public IPipePluggableRenderer getRenderer() {
		return new DroneStationRenderer();
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		//state = DroneStationState.VALUES[tag.getInteger("drone:state") % DroneStationState.VALUES.length];
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		//tag.setInteger("drone:state", state.ordinal());
	}

	@Override
	public void writeData(ByteBuf data) {
		//data.writeByte(state.ordinal());
	}

	@Override
	public void readData(ByteBuf data) {
		//this.state = DroneStationState.values()[data.readUnsignedByte()];
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		if(drone == null || state == DroneStationState.Available || drone.world() == null) {
			return 0;
		}
		World world = drone.world();
		if(!world.isRemote) {
			Connector node = (Connector) drone.machine().node();
			double change = Math.min(Settings.get().chargeRateExternal(), node.globalBufferSize() - node.globalBuffer());
			if(change > 10) {
				double newPower = Math.min(EnergyConverter.convertEnergy(maxReceive, "RF", "OC"), change);
				if(newPower > 0) {
					if(simulate) {
						// We cannot simulate node buffer changes
						return (int) Math.ceil(EnergyConverter.convertEnergy(newPower, "OC", "RF"));
					}
					double remainingPower = node.changeBuffer(newPower);
					if(remainingPower < newPower
						&& world.getWorldInfo().getWorldTotalTime() % 10 == 0) {
						double theta = world.rand.nextDouble() * Math.PI;
						double phi = world.rand.nextDouble() * Math.PI * 2;
						double dx = 0.45 * Math.sin(theta) * Math.cos(phi);
						double dy = 0.45 * Math.sin(theta) * Math.sin(phi);
						double dz = 0.45 * Math.cos(theta);
						ParticleUtils.sendParticlePacket("happyVillager", drone.world(), drone.xPosition() + dx, drone.yPosition() + dz, drone.zPosition() + dy, 0, 0, 0);
					}
					return (int) Math.ceil(EnergyConverter.convertEnergy(newPower - remainingPower, "OC", "RF"));
				}
			}
		}
		return 0;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return 0;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return true;
	}
}

package pl.asie.computronics.integration.buildcraft.pluggable;

import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.pluggable.IPipePluggableRenderer;
import buildcraft.api.transport.pluggable.PipePluggable;
import buildcraft.core.utils.MatrixTranformations;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportPower;
import io.netty.buffer.ByteBuf;
import li.cil.oc.Settings;
import li.cil.oc.api.internal.Drone;
import li.cil.oc.api.network.Connector;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.Computronics;

/**
 * @author Vexatos
 */
public class DroneStationPluggable extends PipePluggable {

	public static enum DroneStationState {
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

		if(pipe != null && pipe.getPipe() != null && drone != null
			&& pipe.getPipe() instanceof Pipe
			&& ((Pipe) pipe.getPipe()).transport != null
			&& ((Pipe) pipe.getPipe()).transport instanceof PipeTransportPower) {

			PipeTransportPower powerPipe = (PipeTransportPower) ((Pipe) pipe.getPipe()).transport;
			World world = pipe.getWorldObj();
			if(!world.isRemote && world.getWorldInfo().getWorldTotalTime() % Settings.get().tickFrequency() == 0) {
				Connector node = (Connector) drone.machine().node();
				double charge = Settings.get().chargeRateExternal() * Settings.get().tickFrequency();
				double change = Math.min(charge, node.globalBufferSize() - node.globalBuffer());
				int amount = (int) Math.floor(change * 10D);
				powerPipe.requestEnergy(direction, amount);
				node.changeBuffer(powerPipe.consumePower(direction, amount));
			}
			if(world.isRemote && world.getWorldInfo().getWorldTotalTime() % 10 == 0) {
				double theta = world.rand.nextDouble() * Math.PI;
				double phi = world.rand.nextDouble() * Math.PI * 2;
				double dx = 0.45 * Math.sin(theta) * Math.cos(phi);
				double dy = 0.45 * Math.sin(theta) * Math.sin(phi);
				double dz = 0.45 * Math.cos(theta);
				world.spawnParticle("happyVillager", drone.xPosition() + dx, drone.yPosition() + dz, drone.zPosition() + dy, 0, 0, 0);
			}
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
		state = DroneStationState.VALUES[tag.getInteger("drone:state") % DroneStationState.VALUES.length];
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("drone:state", state.ordinal());
	}

	@Override
	public void writeData(ByteBuf data) {
		data.writeByte(state.ordinal());
	}

	@Override
	public void readData(ByteBuf data) {
		this.state = DroneStationState.values()[data.readUnsignedByte()];
	}
}

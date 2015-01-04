package pl.asie.computronics.integration.buildcraft.pluggable;

import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.pluggable.IPipePluggableRenderer;
import buildcraft.api.transport.pluggable.PipePluggable;
import buildcraft.core.utils.MatrixTranformations;
import io.netty.buffer.ByteBuf;
import li.cil.oc.common.entity.Drone;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.Computronics;

/**
 * @author Vexatos
 */
public class DroneStationPluggable extends PipePluggable {

	public static enum DroneStationState {
		Available,
		Used
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
		return this.drone == drone || drone.getBoundingBox().intersectsWith(this.getBoundingBox(side));
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
	}

	@Override
	public AxisAlignedBB getBoundingBox(ForgeDirection side) {
		float[][] bounds = new float[3][2];
		// X START - END
		bounds[0][0] = 0.25F;
		bounds[0][1] = 0.75F;
		// Y START - END
		bounds[1][0] = 0F;
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

	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {

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

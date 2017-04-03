package pl.asie.computronics.integration.tis3d.module;

import li.cil.tis3d.api.machine.Casing;
import li.cil.tis3d.api.machine.Face;
import li.cil.tis3d.api.module.traits.BlockChangeAware;
import li.cil.tis3d.api.prefab.module.AbstractModule;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

/**
 * @author Vexatos
 */
public class ComputronicsModule extends AbstractModule implements BlockChangeAware {

	public ComputronicsModule(Casing casing, Face face) {
		super(casing, face);
	}

	@Override
	public void onData(NBTTagCompound nbt) {
		super.onData(nbt);
		this.readFromNBT(nbt);
	}

	protected void sendDataToClient() {
		if(!isVisible()) {
			return;
		}
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		this.getCasing().sendData(this.getFace(), nbt, (byte) 0);
	}

	@Override
	public void onNeighborBlockChange(BlockPos blockPos, boolean b) {
		if(isVisible()) {
			sendDataToClient();
		}
	}
}

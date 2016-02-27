package pl.asie.computronics.integration.railcraft.tile;

import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import mods.railcraft.api.signals.IControllerTile;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.api.signals.SignalController;
import mods.railcraft.common.blocks.signals.ISignalTileDefinition;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.integration.railcraft.SignalTypes;
import pl.asie.computronics.integration.railcraft.signalling.MassiveSignalController;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.reference.Names;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author CovertJaguar, Vexatos
 */
@Optional.InterfaceList({
	@Optional.Interface(iface = "mods.railcraft.api.signals.IControllerTile", modid = Mods.Railcraft)
})
public class TileDigitalControllerBox extends TileDigitalBoxBase implements IControllerTile {

	private boolean prevBlinkState;
	private final MassiveSignalController controller = new MassiveSignalController(getName(), this);

	public TileDigitalControllerBox() {
		super(Names.Railcraft_DigitalControllerBox);
	}

	public void updateEntity() {
		super.updateEntity();
		if(Game.isNotHost(this.worldObj)) {
			this.controller.tickClient();
			if(this.controller.getVisualAspect().isBlinkAspect() && this.prevBlinkState != SignalAspect.isBlinkOn()) {
				this.prevBlinkState = SignalAspect.isBlinkOn();
				this.markBlockForUpdate();
			}

		} else {
			this.controller.tickServer();
			SignalAspect prevAspect = this.controller.getVisualAspect();
			if(this.controller.isBeingPaired()) {
				this.controller.setVisualAspect(SignalAspect.BLINK_YELLOW);
			} else if(this.controller.isPaired()) {
				this.controller.setVisualAspect(this.controller.getMostRestrictiveAspect());
			} else {
				this.controller.setVisualAspect(SignalAspect.BLINK_RED);
			}

			if(prevAspect != this.controller.getVisualAspect()) {
				this.sendUpdateToClient();
			}

		}
	}

	@Override
	public SignalController getController() {
		return this.controller;
	}

	@Override
	public boolean isConnected(ForgeDirection side) {
		return false;
	}

	@Override
	public SignalAspect getBoxSignalAspect(ForgeDirection side) {
		return this.controller.getVisualAspect();
	}

	@Override
	public ISignalTileDefinition getSignalType() {
		return SignalTypes.DigitalController;
	}

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		this.controller.writeToNBT(data);
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		this.controller.readFromNBT(data);
	}

	@Override
	public void writePacketData(DataOutputStream data) throws IOException {
		super.writePacketData(data);
		this.controller.writePacketData(data);
	}

	@Override
	public void readPacketData(DataInputStream data) throws IOException {
		super.readPacketData(data);
		this.controller.readPacketData(data);
		markBlockForUpdate();
	}

	// TODO Computer stuff

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String[] getMethodNames() {
		return new String[0];
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		return new Object[0];
	}
}

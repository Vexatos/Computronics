package pl.asie.computronics.integration.railcraft.tile;

import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import mods.railcraft.api.signals.IReceiverTile;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.api.signals.SignalController;
import mods.railcraft.api.signals.SimpleSignalReceiver;
import mods.railcraft.common.blocks.signals.ISignalTileDefinition;
import mods.railcraft.common.blocks.signals.TileBoxBase;
import mods.railcraft.common.plugins.buildcraft.triggers.IAspectProvider;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.integration.railcraft.SignalTypes;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.reference.Names;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Locale;

/**
 * @author CovertJaguar, Vexatos
 */
@Optional.InterfaceList({
	@Optional.Interface(iface = "mods.railcraft.api.signals.IReceiverTile", modid = Mods.Railcraft),
	@Optional.Interface(iface = "mods.railcraft.common.plugins.buildcraft.triggers.IAspectProvider", modid = Mods.Railcraft)
})
public class TileDigitalReceiverBox extends TileDigitalBoxBase implements IReceiverTile, IAspectProvider {

	private boolean prevBlinkState;
	private final SimpleSignalReceiver receiver = new SimpleSignalReceiver(getName(), this);

	@Override
	public void updateEntity() {
		super.updateEntity();

		if(worldObj.isRemote) {
			this.receiver.tickClient();
			if((this.receiver.getAspect().isBlinkAspect()) && (this.prevBlinkState != SignalAspect.isBlinkOn())) {
				this.prevBlinkState = SignalAspect.isBlinkOn();
				markBlockForUpdate();
			}
			return;
		}
		this.receiver.tickServer();
		SignalAspect prevAspect = this.receiver.getAspect();
		if(this.receiver.isBeingPaired()) {
			this.receiver.setAspect(SignalAspect.BLINK_YELLOW);
		} else if(!this.receiver.isPaired()) {
			this.receiver.setAspect(SignalAspect.BLINK_RED);
		}
		if(prevAspect != this.receiver.getAspect()) {
			updateNeighbors();
			sendUpdateToClient();
		}
	}

	@Override
	public void onControllerAspectChange(SignalController con, SignalAspect aspect) {
		if(Mods.isLoaded(Mods.OpenComputers)) {
			eventOC(aspect);
		}
		if(Mods.isLoaded(Mods.ComputerCraft)) {
			eventCC(aspect);
		}
		updateNeighbors();
		sendUpdateToClient();
	}

	private void updateNeighbors() {
		notifyBlocksOfNeighborChange();
	}

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		this.receiver.writeToNBT(data);
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		this.receiver.readFromNBT(data);
	}

	@Override
	public void writePacketData(DataOutputStream data)
		throws IOException {
		super.writePacketData(data);
		this.receiver.writePacketData(data);
	}

	@Override
	public void readPacketData(DataInputStream data)
		throws IOException {
		super.readPacketData(data);
		this.receiver.readPacketData(data);
		markBlockForUpdate();
	}

	public SignalAspect getBoxSignalAspect(ForgeDirection side) {
		return this.receiver.getAspect();
	}

	public boolean canTransferAspect() {
		return true;
	}

	@Override
	public SimpleSignalReceiver getReceiver() {
		return this.receiver;
	}

	@Override
	public SignalAspect getTriggerAspect() {
		return getBoxSignalAspect(null);
	}

	@Override
	public boolean isConnected(ForgeDirection side) {
		TileEntity tile = this.tileCache.getTileOnSide(side);
		return tile != null && tile instanceof TileBoxBase && ((TileBoxBase) tile).canReceiveAspect();
	}

	@Override
	public ISignalTileDefinition getSignalType() {
		return SignalTypes.DigitalReceiver;
	}

	// Computer Stuff //

	public TileDigitalReceiverBox() {
		super(Names.Railcraft_DigitalReceiverBox);
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public void eventOC(SignalAspect aspect) {
		if(node() != null) {
			node().sendToReachable("computer.signal", "aspect_changed", aspect.ordinal());
		}
	}

	@Optional.Method(modid = Mods.ComputerCraft)
	public void eventCC(SignalAspect aspect) {
		if(attachedComputersCC != null) {
			for(IComputerAccess computer : attachedComputersCC) {
				computer.queueEvent("aspect_changed", new Object[] {
					computer.getAttachmentName(),
					aspect.ordinal()
				});
			}
		}
	}

	private Object[] getSignal() {
		return new Object[] { this.getTriggerAspect().ordinal() };
	}

	private static Object[] aspects() {
		LinkedHashMap<String, Integer> aspectMap = new LinkedHashMap<String, Integer>();
		for(SignalAspect aspect : SignalAspect.VALUES) {
			aspectMap.put(aspect.name().toLowerCase(Locale.ENGLISH), aspect.ordinal());
		}
		return new Object[] { aspectMap };
	}

	@Callback(doc = "function():number; Returns the currently received aspect that triggers the receiver box", direct = true, limit = 16)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getSignal(Context context, Arguments args) {
		return getSignal();
	}

	@Callback(doc = "This is a list of every available Signal Aspect in Railcraft", getter = true, direct = true, limit = 16)
	public Object[] aspects(Context c, Arguments a) {
		return aspects();
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String[] getMethodNames() {
		return new String[] { "getSignal", "aspects" };
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		if(method < getMethodNames().length) {
			switch(method) {
				case 0: {
					return getSignal();
				}
				case 1: {
					return aspects();
				}
			}
		}
		return null;
	}

}

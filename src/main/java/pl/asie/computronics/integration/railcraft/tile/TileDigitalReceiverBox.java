package pl.asie.computronics.integration.railcraft.tile;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import mods.railcraft.api.signals.IReceiverTile;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.api.signals.SignalController;
import mods.railcraft.api.signals.SignalReceiver;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.wayobjects.boxes.TileBoxBase;
import mods.railcraft.common.plugins.buildcraft.triggers.IAspectProvider;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.integration.railcraft.SignalTypes;
import pl.asie.computronics.integration.railcraft.signalling.MassiveSignalReceiver;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.reference.Names;
import pl.asie.computronics.util.OCUtils;
import pl.asie.computronics.util.TableUtils;

import java.io.IOException;
import java.util.Collection;
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
	private final MassiveSignalReceiver receiver = new MassiveSignalReceiver(getName(), this);

	@Override
	public void update() {
		super.update();

		if(world.isRemote) {
			this.receiver.tickClient();
			if((this.receiver.getVisualAspect().isBlinkAspect()) && (this.prevBlinkState != SignalAspect.isBlinkOn())) {
				this.prevBlinkState = SignalAspect.isBlinkOn();
				markBlockForUpdate();
			}
			return;
		}
		this.receiver.tickServer();
		SignalAspect prevAspect = this.receiver.getVisualAspect();
		if(this.receiver.isBeingPaired()) {
			this.receiver.setVisualAspect(SignalAspect.BLINK_YELLOW);
		} else if(this.receiver.isPaired()) {
			this.receiver.setVisualAspect(this.receiver.getMostRestrictiveAspect());
		} else {
			this.receiver.setVisualAspect(SignalAspect.BLINK_RED);
		}
		if(prevAspect != this.receiver.getVisualAspect()) {
			updateNeighbors();
			sendUpdateToClient();
		}
	}

	@Override
	public void onControllerAspectChange(SignalController con, SignalAspect aspect) {
		String name = this.receiver.getNameFor(con);
		if(Mods.isLoaded(Mods.OpenComputers)) {
			eventOC(name, aspect);
		}
		if(Mods.isLoaded(Mods.ComputerCraft)) {
			eventCC(name, aspect);
		}
		updateNeighbors();
		sendUpdateToClient();
	}

	private void updateNeighbors() {
		notifyBlocksOfNeighborChange();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		data = super.writeToNBT(data);
		this.receiver.writeToNBT(data);
		return data;
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		this.receiver.readFromNBT(data);
	}

	@Override
	public void writePacketData(RailcraftOutputStream data) throws IOException {
		super.writePacketData(data);
		this.receiver.writePacketData(data);
	}

	@Override
	public void readPacketData(RailcraftInputStream data) throws IOException {
		super.readPacketData(data);
		this.receiver.readPacketData(data);
		markBlockForUpdate();
	}

	@Override
	public SignalAspect getBoxSignalAspect(EnumFacing side) {
		return this.receiver.getVisualAspect();
	}

	@Override
	public boolean canTransferAspect() {
		return true;
	}

	@Override
	public SignalReceiver getReceiver() {
		return this.receiver;
	}

	@Override
	public SignalAspect getTriggerAspect() {
		return getBoxSignalAspect(null);
	}

	@Override
	public boolean isConnected(EnumFacing side) {
		TileEntity tile = this.tileCache.getTileOnSide(side);
		return tile instanceof TileBoxBase && ((TileBoxBase) tile).canReceiveAspect();
	}

	@Override
	public IEnumMachine<SignalTypes> getMachineType() {
		return SignalTypes.DigitalReceiver;
	}

	// Computer Stuff //

	public TileDigitalReceiverBox() {
		super(Names.Railcraft_DigitalReceiverBox);
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public void eventOC(String name, SignalAspect aspect) {
		if(node() != null) {
			node().sendToReachable("computer.signal", "aspect_changed", name, aspect.ordinal() + 1);
		}
	}

	@Optional.Method(modid = Mods.ComputerCraft)
	public void eventCC(String name, SignalAspect aspect) {
		if(attachedComputersCC != null) {
			for(IComputerAccess computer : attachedComputersCC) {
				computer.queueEvent("aspect_changed", new Object[] {
					computer.getAttachmentName(),
					name, aspect.ordinal() + 1
				});
			}
		}
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	protected OCUtils.Device deviceInfo() {
		return new OCUtils.Device(
			DeviceClass.Communication,
			"Signal receiver",
			OCUtils.Vendors.Railcraft,
			"Digitized Signal Receiver X3"
		);
	}

	private Object[] getAspect(String name) {
		SignalAspect aspect = this.receiver.getMostRestrictiveAspectFor(name);
		if(aspect != null) {
			return new Object[] { aspect.ordinal() + 1 };
		} else {
			return new Object[] { null, "no valid signal found" };
		}
	}

	private Object[] removeSignal(String name) {
		Collection<BlockPos> coords = this.receiver.getCoordsFor(name);
		if(!coords.isEmpty()) {
			for(BlockPos coord : coords) {
				this.receiver.clearPairing(coord);
			}
			return new Object[] { true };
		}
		return new Object[] { false, "no valid signal found" };
	}

	private Object[] getSignalNames() {
		return new Object[] { TableUtils.convertSetToMap(this.receiver.getSignalNames()) };
	}

	private static LinkedHashMap<Object, Object> aspectMap;

	private static Object[] aspects() {
		if(aspectMap == null) {
			LinkedHashMap<Object, Object> newMap = new LinkedHashMap<Object, Object>();
			for(int i = 0; i < SignalAspect.VALUES.length - 1; i++) {
				SignalAspect aspect = SignalAspect.VALUES[i];
				String name = aspect.name().toLowerCase(Locale.ENGLISH);
				newMap.put(name, aspect.ordinal() + 1);
				newMap.put(aspect.ordinal() + 1, name);
			}
			aspectMap = newMap;
		}
		return new Object[] { aspectMap };
	}

	@Callback(doc = "function(name:string):number; Returns the aspect currently received from a connected signal with the specified name. Returns nil and an error message on failure.", direct = true, limit = 32)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getAspect(Context context, Arguments args) {
		return getAspect(args.checkString(0));
	}

	@Callback(doc = "function():number; Returns the most restrictive aspect currently received from connected signals", direct = true, limit = 32)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getMostRestrictiveAspect(Context context, Arguments args) {
		return new Object[] { this.receiver.getMostRestrictiveAspect().ordinal() + 1 };
	}

	@Callback(doc = "function(name:string):number; Tries to remove any pairing to a signal with the specified name. Returns true on success.", direct = true, limit = 32)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] unpair(Context context, Arguments args) {
		return removeSignal(args.checkString(0));
	}

	@Callback(doc = "function():table; Returns a list containing the name of every paired controller.", direct = true, limit = 32)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getSignalNames(Context c, Arguments a) {
		return getSignalNames();
	}

	@Callback(doc = "This is a list of every available Signal Aspect in Railcraft", getter = true, direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] aspects(Context c, Arguments a) {
		return aspects();
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String[] getMethodNames() {
		return new String[] { "getAspect", "getMostRestrictiveAspect", "unpair", "getSignalNames", "aspects" };
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		if(method < getMethodNames().length) {
			switch(method) {
				case 0: {
					if(arguments.length < 1 || !(arguments[0] instanceof String)) {
						throw new LuaException("first argument needs to be a string");
					}
					return getAspect((String) arguments[0]);
				}
				case 1: {
					return new Object[] { this.receiver.getMostRestrictiveAspect().ordinal() + 1 };
				}
				case 2: {
					if(arguments.length < 1 || !(arguments[0] instanceof String)) {
						throw new LuaException("first argument needs to be a string");
					}
					return removeSignal((String) arguments[0]);
				}
				case 3: {
					return getSignalNames();
				}
				case 4: {
					return aspects();
				}
			}
		}
		return null;
	}

}

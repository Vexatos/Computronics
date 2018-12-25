package pl.asie.computronics.integration.railcraft.tile;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import mods.railcraft.api.signals.IControllerTile;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.api.signals.SignalController;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.integration.railcraft.SignalTypes;
import pl.asie.computronics.integration.railcraft.signalling.MassiveSignalController;
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
	@Optional.Interface(iface = "mods.railcraft.api.signals.IControllerTile", modid = Mods.Railcraft)
})
public class TileDigitalControllerBox extends TileDigitalBoxBase implements IControllerTile {

	private boolean prevBlinkState;
	private final MassiveSignalController controller = new MassiveSignalController(getName(), this);

	public TileDigitalControllerBox() {
		super(Names.Railcraft_DigitalControllerBox);
	}

	@Override
	public void update() {
		super.update();
		if(Game.isClient(this.world)) {
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
	public boolean isConnected(EnumFacing side) {
		return false;
	}

	@Override
	public SignalAspect getBoxSignalAspect(EnumFacing side) {
		return this.controller.getVisualAspect();
	}

	@Override
	public IEnumMachine<?> getMachineType() {
		return SignalTypes.DigitalController;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		data = super.writeToNBT(data);
		this.controller.writeToNBT(data);
		return data;
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		this.controller.readFromNBT(data);
	}

	@Override
	public void writePacketData(RailcraftOutputStream data) throws IOException {
		super.writePacketData(data);
		this.controller.writePacketData(data);
	}

	@Override
	public void readPacketData(RailcraftInputStream data) throws IOException {
		super.readPacketData(data);
		this.controller.readPacketData(data);
		markBlockForUpdate();
	}

	// Computer stuff //

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	protected OCUtils.Device deviceInfo() {
		return new OCUtils.Device(
			DeviceClass.Communication,
			"Signal controller",
			OCUtils.Vendors.Railcraft,
			"Digitized Signal Sender X3"
		);
	}

	private Object[] setAspect(String name, int aspectIndex) {
		if(aspectIndex > 0 && aspectIndex < SignalAspect.VALUES.length) {
			SignalAspect aspect = SignalAspect.fromOrdinal(aspectIndex - 1);
			boolean success = this.controller.setAspectFor(name, aspect);
			if(success) {
				return new Object[] { true };
			} else {
				return new Object[] { false, "no valid signal found" };
			}
		}
		throw new IllegalArgumentException("invalid aspect: " + aspectIndex);
	}

	private Object[] setEveryAspect(int aspectIndex) {
		if(aspectIndex > 0 && aspectIndex < SignalAspect.VALUES.length) {
			SignalAspect aspect = SignalAspect.fromOrdinal(aspectIndex - 1);
			this.controller.setAspectForAll(aspect);
			return new Object[] { true };
		}
		throw new IllegalArgumentException("invalid aspect" + aspectIndex);
	}

	private Object[] removeSignal(String name) {
		Collection<BlockPos> coords = this.controller.getCoordsFor(name);
		if(!coords.isEmpty()) {
			for(BlockPos coord : coords) {
				this.controller.clearPairing(coord);
			}
			return new Object[] { true };
		}
		return new Object[] { false, "no valid signal found" };
	}

	private Object[] getSignalNames() {
		return new Object[] { TableUtils.convertSetToMap(this.controller.getSignalNames()) };
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

	@Callback(doc = "function(name:string, aspect:number):boolean; Tries to set the aspect for any paired signal with the specified name. Returns true on success.", direct = true, limit = 32)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setAspect(Context context, Arguments args) {
		return setAspect(args.checkString(0), args.checkInteger(1));
	}

	@Callback(doc = "function(aspect:number):boolean; Sets the aspect for every paired signal to the specified value. Returns true on success.", direct = true, limit = 32)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setEveryAspect(Context context, Arguments args) {
		return setEveryAspect(args.checkInteger(0));
	}

	@Callback(doc = "function(name:string):boolean; Tries to remove any pairing to a signal with the specified name. Returns true on success.", direct = true, limit = 32)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] unpair(Context context, Arguments args) {
		return removeSignal(args.checkString(0));
	}

	@Callback(doc = "function():table; Returns a list containing the name of every paired receiver.", direct = true, limit = 32)
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
		return new String[] { "setAspect", "setEveryAspect", "unpair", "getSignalNames", "aspects" };
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		try {
			if(method < getMethodNames().length) {
				switch(method) {
					case 0: {
						if(arguments.length < 1 || !(arguments[0] instanceof String)) {
							throw new LuaException("first argument needs to be a string");
						}
						if(arguments.length < 2 || !(arguments[1] instanceof Number)) {
							throw new LuaException("second argument needs to be a number");
						}
						return setAspect((String) arguments[0], ((Number) arguments[1]).intValue());
					}
					case 1: {
						if(arguments.length < 1 || !(arguments[0] instanceof Number)) {
							throw new LuaException("first argument needs to be a number");
						}
						return setEveryAspect(((Number) arguments[1]).intValue());
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
		} catch(Exception e) {
			throw new LuaException(e.getMessage());
		}
	}
}

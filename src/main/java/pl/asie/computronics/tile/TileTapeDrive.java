package pl.asie.computronics.tile;

//import java.nio.file.FileSystem;

import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Component;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.tape.IItemTapeStorage;
import pl.asie.computronics.item.ItemTape;
import pl.asie.computronics.network.Packets;
import pl.asie.computronics.network.Packets.Types;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TapeDriveState.State;
import pl.asie.computronics.util.internal.ITapeDrive;
import pl.asie.lib.api.tile.IInventoryProvider;
import pl.asie.lib.network.Packet;

public class TileTapeDrive extends TileEntityPeripheralBase implements IInventoryProvider, ITapeDrive {
	private String storageName = "";
	private TapeDriveState state;

	public TileTapeDrive() {
		super("tape_drive");
		this.createInventory(1);
		this.state = new TapeDriveState();
		if(Mods.isLoaded(Mods.OpenComputers) && node() != null) {
			initOCFilesystem();
		}
	}

	private ManagedEnvironment oc_fs;

	@Optional.Method(modid = Mods.OpenComputers)
	private void initOCFilesystem() {
		oc_fs = li.cil.oc.api.FileSystem.asManagedEnvironment(li.cil.oc.api.FileSystem.fromClass(Computronics.class, Mods.Computronics, "lua/component/tape_drive"),
			"tape_drive");
		((Component) oc_fs.node()).setVisibility(Visibility.Network);
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public void onConnect(final Node node) {
		if(node.host() instanceof Context) {
			node.connect(oc_fs.node());
		}
	}
	// GUI/State

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public void onDisconnect(final Node node) {
		if(node.host() instanceof Context) {
			// Remove our file systems when we get disconnected from a
			// computer.
			node.disconnect(oc_fs.node());
		} else if(node == this.node()) {
			// Remove the file system if we are disconnected, because in that
			// case this method is only called once.
			oc_fs.node().remove();
		}
	}

	protected void sendState() {
		if(worldObj.isRemote) {
			return;
		}
		try {
			Packet packet = Computronics.packet.create(Packets.PACKET_TAPE_GUI_STATE)
				.writeInt(Types.TileEntity)
				.writeTileLocation(this)
				.writeByte((byte) state.getState().ordinal());
			//.writeByte((byte)soundVolume);
			Computronics.packet.sendToAllAround(packet, this, 64.0D);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	// Logic
	public State getEnumState() {
		return this.state.getState();
	}

	@Override
	public void switchState(State s) {
		//System.out.println("Switchy switch to " + s.name());
		if(this.getEnumState() != s) {
			this.state.switchState(worldObj, xCoord, yCoord, zCoord, s);
			this.sendState();
		}
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		State st = getEnumState();
		Packet pkt = state.update(worldObj, xCoord, yCoord, zCoord);
		if(pkt != null) {
			Computronics.packet.sendToAllAround(pkt, this, Config.TAPEDRIVE_DISTANCE * 2);
		}
		if(!worldObj.isRemote && st != getEnumState()) {
			sendState();
		}
	}

	// Minecraft boilerplate

	private void setLabel(String label) {
		ItemStack stack = this.getStackInSlot(0);
		if(stack != null && stack.getTagCompound() != null) {
			if(label.length() == 0 && stack.getTagCompound().hasKey("label")) {
				stack.getTagCompound().removeTag("label");
			} else if(label.length() > 0) {
				stack.getTagCompound().setString("label", label);
			}
			storageName = label;
		}
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void openInventory() {
		super.openInventory();
		sendState();
	}

	@Override
	public void onBlockDestroy() {
		super.onBlockDestroy();
		unloadStorage();
	}

	@Override
	public void invalidate() {
		unloadStorage();
		super.invalidate();
	}

	@Override
	public void onRedstoneSignal(int signal) {
		this.switchState(signal > 0 ? State.PLAYING : State.STOPPED);
	}

	@Override
	public boolean shouldPlaySound() {
		switch(getEnumState()) {
			case REWINDING:
			case FORWARDING:
				return true;
			default:
				return false;
		}
	}

	@Override
	public String getSoundName() {
		return "tape_rewind";
	}

	// Storage handling

	private void loadStorage() {
		if(worldObj != null && worldObj.isRemote) {
			return;
		}

		if(state.getStorage() != null) {
			unloadStorage();
		}
		ItemStack stack = this.getStackInSlot(0);
		if(stack != null) {
			// Get Storage.
			Item item = stack.getItem();
			if(item instanceof IItemTapeStorage) {
				state.setStorage(((IItemTapeStorage) item).getStorage(stack));
			}

			// Get possible label.
			if(stack.getTagCompound() != null) {
				NBTTagCompound tag = stack.getTagCompound();
				storageName = tag.hasKey("label") ? tag.getString("label") : "";
			} else {
				storageName = "";
			}
		}
	}

	public void saveStorage() {
		unloadStorage();
	}

	private void unloadStorage() {
		if(worldObj.isRemote || state.getStorage() == null) {
			return;
		}

		switchState(State.STOPPED);
		try {
			state.getStorage().onStorageUnload();
		} catch(Exception e) {
			e.printStackTrace();
		}
		state.setStorage(null);
	}

	@Override
	public void onSlotUpdate(int slot) {
		if(this.getStackInSlot(0) == null) {
			if(state.getStorage() != null) { // Tape was inserted
				// Play eject sound
				worldObj.playSoundEffect(xCoord, yCoord, zCoord, "computronics:tape_eject", 1, 0);
			}
			unloadStorage();
		} else {
			loadStorage();
			if(this.getStackInSlot(0).getItem() instanceof IItemTapeStorage) {
				// Play insert sound
				worldObj.playSoundEffect(xCoord, yCoord, zCoord, "computronics:tape_insert", 1, 0);
			}
		}
	}

	@Override
	public void onChunkUnload() {
		unloadStorage();
		super.onChunkUnload();
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if(tag.hasKey("state")) {
			this.state.setState(State.values()[tag.getByte("state")]);
		}
		if(tag.hasKey("sp")) {
			this.state.packetSize = tag.getShort("sp");
		}
		if(tag.hasKey("vo")) {
			this.state.soundVolume = tag.getByte("vo");
		} else {
			this.state.soundVolume = 127;
		}
		loadStorage();
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setShort("sp", (short) this.state.packetSize);
		tag.setByte("state", (byte) this.state.getState().ordinal());
		if(this.state.soundVolume != 127) {
			tag.setByte("vo", (byte) this.state.soundVolume);
		}
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public void readFromNBT_OC(NBTTagCompound tag) {
		super.readFromNBT_OC(tag);
		if(oc_fs != null && oc_fs.node() != null) {
			oc_fs.node().load(tag.getCompoundTag("oc:fs"));
		}
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public void writeToNBT_OC(NBTTagCompound tag) {
		super.writeToNBT_OC(tag);
		if(oc_fs != null && oc_fs.node() != null) {
			final NBTTagCompound fsNbt = new NBTTagCompound();
			oc_fs.node().save(fsNbt);
			tag.setTag("oc:fs", fsNbt);
		}
	}

	@Override
	public void writeToRemoteNBT(NBTTagCompound tag) {
		super.writeToRemoteNBT(tag);
		tag.setByte("state", (byte) this.state.getState().ordinal());
	}

	@Override
	public void removeFromNBTForTransfer(NBTTagCompound data) {
		super.removeFromNBTForTransfer(data);
		data.removeTag("oc:fs");
		data.removeTag("state");
	}

	@Override
	public void readFromRemoteNBT(NBTTagCompound tag) {
		super.readFromRemoteNBT(tag);
		if(tag.hasKey("state")) {
			this.state.setState(State.values()[tag.getByte("state")]);
		}
	}

	// OpenComputers

	@Callback(doc = "function():boolean; Returns true if the tape drive is empty or the inserted tape has reached its end", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] isEnd(Context context, Arguments args) {
		if(state.getStorage() != null) {
			return new Object[] { state.getStorage().getPosition() + state.packetSize > state.getStorage().getSize() };
		} else {
			return new Object[] { true };
		}
	}

	@Callback(doc = "function():boolean; Returns true if there is a tape inserted", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] isReady(Context context, Arguments args) {
		return new Object[] { state.getStorage() != null };
	}

	@Callback(doc = "function():number; Returns the size of the tape, in bytes", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getSize(Context context, Arguments args) {
		return new Object[] { (state.getStorage() != null ? state.getStorage().getSize() : 0) };
	}

	@Callback(doc = "function(label:string):string; Sets the label of the tape. "
		+ "Returns the new label, or nil if there is no tape inserted")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setLabel(Context context, Arguments args) {
		setLabel(args.checkString(0));
		return new Object[] { (state.getStorage() != null ? storageName : null) };
	}

	@Callback(doc = "function():string; Returns the current label of the tape, or nil if there is no tape inserted")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getLabel(Context context, Arguments args) {
		return new Object[] { (state.getStorage() != null ? storageName : null) };
	}

	@Callback(doc = "function(length:number):number; Seeks the specified amount of bytes on the tape. "
		+ "Negative values for rewinding. Returns the amount of bytes sought, or nil if there is no tape inserted")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] seek(Context context, Arguments args) {
		if(state.getStorage() != null) {
			return new Object[] { state.getStorage().seek(args.checkInteger(0)) };
		}
		return null;
	}

	@Callback(doc = "function([length:number]):string; "
		+ "Reads and returns the specified amount of bytes or a single byte from the tape. "
		+ "Returns nil if there is no tape inserted")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] read(Context context, Arguments args) {
		if(state.getStorage() != null) {
			if(args.count() >= 1 && args.isInteger(0) && (args.checkInteger(0) >= 0)) {
				byte[] data = new byte[args.checkInteger(0)];
				state.getStorage().read(data, false);
				return new Object[] { data };
			} else {
				return new Object[] { state.getStorage().read(false) & 0xFF };
			}
		} else {
			return null;
		}
	}

	@Callback(doc = "function(data:number or string); Writes the specified data to the tape if there is one inserted")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] write(Context context, Arguments args) {
		if(state.getStorage() != null && args.count() >= 1) {
			if(args.isInteger(0)) {
				state.getStorage().write((byte) args.checkInteger(0));
			} else if(args.isByteArray(0)) {
				state.getStorage().write(args.checkByteArray(0));
			} else {
				throw new IllegalArgumentException("bad arguments #1 (number or string expected)");
			}
		}
		return null;
	}

	@Callback(doc = "function():boolean; Make the Tape Drive start playing the tape. Returns true on success")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] play(Context context, Arguments args) {
		switchState(State.PLAYING);
		return new Object[] { state.getStorage() != null && this.getEnumState() == State.PLAYING };
	}

	@Callback(doc = "function():boolean; Make the Tape Drive stop playing the tape. Returns true on success")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] stop(Context context, Arguments args) {
		switchState(State.STOPPED);
		return new Object[] { state.getStorage() != null && this.getEnumState() == State.STOPPED };
	}

	@Callback(doc = "function(speed:number):boolean; Sets the speed of the tape drive. Needs to be beween 0.25 and 2. Returns true on success")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setSpeed(Context context, Arguments args) {
		return new Object[] { this.state.setSpeed((float) args.checkDouble(0)) };
	}

	@Callback(doc = "function(speed:number); Sets the volume of the tape drive. Needs to be beween 0 and 1")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setVolume(Context context, Arguments args) {
		this.state.setVolume((float) args.checkDouble(0));
		return null;
	}

	@Callback(doc = "function():string; Returns the current state of the tape drive", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getState(Context context, Arguments args) {
		return new Object[] { state.getState().toString() };
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String[] getMethodNames() {
		return new String[] { "isEnd", "isReady", "getSize", "getLabel", "getState", "setLabel", "setSpeed", "setVolume", "seek", "read", "write", "play", "stop" };
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
		int method, Object[] arguments) throws LuaException,
		InterruptedException {

		// methods which don't take any arguments
		switch(method) {
			case 0: { // isEnd
				if(state.getStorage() != null) {
					return new Object[] { state.getStorage().getPosition() + state.packetSize > state.getStorage().getSize() };
				} else {
					return new Object[] { true };
				}
			}
			case 1: { // isReady
				return new Object[] { state.getStorage() != null };
			}
			case 2: { // getSize
				return new Object[] { (state.getStorage() != null ? state.getStorage().getSize() : 0) };
			}
			case 3: { // getLabel
				return new Object[] { (state.getStorage() != null ? storageName : null) };
			}
			case 4: { // getState
				return new Object[] { state.getState().toString() };
			}
			case 9: { // read
				if(arguments.length < 1) {
					if(state.getStorage() != null) {
						return new Object[] { state.getStorage().read(false) & 0xFF };
					} else {
						return null;
					}
				}
				break;
			}
			case 11: { // play
				switchState(State.PLAYING);
				return new Object[] { state.getStorage() != null && this.getEnumState() == State.PLAYING };
			}
			case 12: { // stop
				switchState(State.STOPPED);
				return new Object[] { state.getStorage() != null && this.getEnumState() == State.STOPPED };
			}
		}

		// Argument type check
		switch(method) {
			case 5: { // setLabel
				if(arguments.length < 1 || !(arguments[0] instanceof String)) {
					throw new LuaException("first argument needs to be a string");
				}
				break;
			}
			case 6:
			case 7:
			case 8: { // setSpeed, setVolume, seek
				if(arguments.length < 1 || !(arguments[0] instanceof Number)) {
					throw new LuaException("first argument needs to be a number");
				}
				break;
			}
			case 9: { // read
				if(arguments.length < 1 || !(arguments[0] instanceof Number)) {
					throw new LuaException("first argument needs to be a number or non-existant");
				}
				break;
			}
			case 10: { // write
				if(arguments.length < 1 || (!(arguments[0] instanceof String) && !(arguments[0] instanceof Number))) {
					throw new LuaException("first argument needs to be a number or string");
				}
				break;
			}
		}

		if(arguments.length < 1) {
			throw new LuaException("no first argument found");
		}

		// Execution
		if((arguments[0] instanceof String)) {
			// methods which take strings and do something
			switch(method) {
				case 5: // setLabel
					setLabel((String) arguments[0]);
					return new Object[] { (state.getStorage() != null ? storageName : null) };
				case 10: // write
					if(state.getStorage() != null) {
						return new Object[] { state.getStorage().write(((String) arguments[0]).getBytes()) };
					}
					break;
			}
		} else if(arguments[0] instanceof Number) {
			// methods which take floats and do something
			switch(method) {
				case 6: { // setSpeed
					return new Object[] { this.state.setSpeed(((Number) arguments[0]).floatValue()) };
				}
				case 7: { // setVolume
					this.state.setVolume(((Number) arguments[0]).floatValue());
					return null;
				}
				case 8: { // seek
					if(state.getStorage() != null) {
						return new Object[] { state.getStorage().seek(((Number) arguments[0]).intValue()) };
					}
					break;
				}
				case 9: { // read
					int i = ((Number) arguments[0]).intValue();
					if(state.getStorage() != null) {
						if(i >= 256) {
							i = 256;
						}
						byte[] data = new byte[i];
						state.getStorage().read(data, false);
						return new Object[] { new String(data) };
					} else {
						return null;
					}
				}
				case 10: { // write
					if(state.getStorage() != null) {
						state.getStorage().write((byte) ((Number) arguments[0]).intValue());
					}
					break;
				}
			}
		}

		// catch all other methods
		return null;
	}

	private int _nedo_lastSeek = 0;

	@Override
	@Optional.Method(modid = Mods.NedoComputers)
	public short busRead(int addr) {
		switch(addr & 0xFFFE) {
			case 0:
				return (short) state.getState().ordinal();
			case 2:
				return 0; // speed?
			case 4:
				return (short) state.soundVolume;
			case 6:
				return (state.getStorage() != null ? (short) (state.getStorage().getSize() / ItemTape.L_MINUTE) : 0);
			case 8:
				return (short) _nedo_lastSeek;
			case 10:
				return (state.getStorage() != null ? (short) state.getStorage().read(false) : 0);
		}
		return 0;
	}

	@Override
	@Optional.Method(modid = Mods.NedoComputers)
	public void busWrite(int addr, short data) {
		switch(addr & 0xFFFE) {
			case 0:
				switchState(State.values()[data % State.values().length]);
				break;
			case 2:
				break; // speed?
			case 4:
				state.soundVolume = Math.max(0, Math.min(data, 127));
				break;
			case 6:
				break; // tape size is read-only!
			case 8:
				_nedo_lastSeek = state.getStorage().seek(data);
				break;
			case 10:
				if(state.getStorage() != null) {
					state.getStorage().write((byte) (data & 0xFF));
				}
				break;
		}
	}
}

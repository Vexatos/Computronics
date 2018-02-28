package pl.asie.computronics.tile;

//import java.nio.file.FileSystem;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.filesystem.IMount;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Node;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.audio.AudioPacket;
import pl.asie.computronics.api.audio.IAudioReceiver;
import pl.asie.computronics.api.audio.IAudioSource;
import pl.asie.computronics.api.tape.IItemTapeStorage;
import pl.asie.computronics.audio.AudioUtils;
import pl.asie.computronics.cc.ComputronicsFileMount;
import pl.asie.computronics.network.PacketType;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TapeDriveState.State;
import pl.asie.computronics.util.OCUtils;
import pl.asie.lib.api.tile.IInventoryProvider;
import pl.asie.lib.network.Packet;
import pl.asie.lib.util.ColorUtils;
import pl.asie.lib.util.internal.IColorable;

import java.util.concurrent.ConcurrentHashMap;

public class TileTapeDrive extends TileEntityPeripheralBase implements IInventoryProvider, IAudioSource {

	private final IAudioReceiver internalSpeaker = new IAudioReceiver() {
		@Override
		public boolean connectsAudio(ForgeDirection side) {
			return true;
		}

		@Override
		public World getSoundWorld() {
			return worldObj;
		}

		@Override
		public Vec3 getSoundPos() {
			return Vec3.createVectorHelper(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D);
		}

		@Override
		public int getSoundDistance() {
			return Config.TAPEDRIVE_DISTANCE;
		}

		@Override
		public void receivePacket(AudioPacket packet, ForgeDirection direction) {
			packet.addReceiver(this);
		}

		@Override
		public String getID() {
			return AudioUtils.positionId(xCoord, yCoord, zCoord);
		}
	};

	private String storageName = "";
	private TapeDriveState state;

	public TileTapeDrive() {
		super("tape_drive");
		this.createInventory(1);
		this.state = new TapeDriveState();
	}

	private static Object cc_fs;
	private static Object cc_fs_autorun; // dan200, why?
	protected ConcurrentHashMap<IComputerAccess, String> computerMountPointsCC;
	protected ConcurrentHashMap<IComputerAccess, String> computerMountPointsCC_autorun;

	@Optional.Method(modid = Mods.ComputerCraft)
	protected IMount cc_fs() {
		return (IMount) cc_fs;
	}

	@Optional.Method(modid = Mods.ComputerCraft)
	protected IMount cc_autorun_fs() {
		return (IMount) cc_fs_autorun;
	}

	@Optional.Method(modid = Mods.ComputerCraft)
	public static void initCCFilesystem() {
		if(cc_fs == null) {
			cc_fs = ComputronicsFileMount.createMount(Computronics.class, Mods.Computronics, "lua/peripheral/tape_drive/programs/tape_drive");
			if(cc_fs == null) {
				Computronics.log.error("Unable to create ComputerCraft mount for tape drive programs.");
			}
		}
		if(cc_fs_autorun == null) {
			cc_fs_autorun = ComputronicsFileMount.createMount(Computronics.class, Mods.Computronics, "lua/peripheral/tape_drive/autorun/tape_drive");
			if(cc_fs == null) {
				Computronics.log.error("Unable to create ComputerCraft mount for tape drive autorun program.");
			}
		}
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public void attach(IComputerAccess computer) {
		super.attach(computer);
		if(computerMountPointsCC == null) {
			computerMountPointsCC = new ConcurrentHashMap<IComputerAccess, String>(2);
		}
		IMount mount = cc_fs();
		if(mount != null) {
			String mountPoint = computer.mount("rom/programs/tape_drive", mount);
			if(mountPoint != null) {
				computerMountPointsCC.put(computer, mountPoint);
			}
		}
		if(computerMountPointsCC_autorun == null) {
			computerMountPointsCC_autorun = new ConcurrentHashMap<IComputerAccess, String>(2);
		}
		mount = cc_autorun_fs();
		if(mount != null) {
			String mountPoint = computer.mount("rom/autorun/tape_drive", mount);
			if(mountPoint != null) {
				computerMountPointsCC_autorun.put(computer, mountPoint);
			}
		}
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public void detach(IComputerAccess computer) {
		super.detach(computer);
		if(computerMountPointsCC == null) {
			computerMountPointsCC = new ConcurrentHashMap<IComputerAccess, String>(2);
		}
		String mountPoint = computerMountPointsCC.remove(computer);
		if(mountPoint != null) {
			computer.unmount(mountPoint);
		}
		if(computerMountPointsCC_autorun == null) {
			computerMountPointsCC_autorun = new ConcurrentHashMap<IComputerAccess, String>(2);
		}
		mountPoint = computerMountPointsCC_autorun.remove(computer);
		if(mountPoint != null) {
			computer.unmount(mountPoint);
		}
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public void onConnect(final Node node) {
		super.onConnect(node);
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	protected void onChunkUnload_OC() {
		super.onChunkUnload_OC();
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	protected void invalidate_OC() {
		super.invalidate_OC();
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	protected OCUtils.Device deviceInfo() {
		return new OCUtils.Device(
			DeviceClass.Tape,
			"Tape drive",
			OCUtils.Vendors.Yanaki,
			"DFPWM 1"
		);
	}

	// GUI/State

	protected void sendState() {
		if(worldObj.isRemote) {
			return;
		}
		try {
			Packet packet = Computronics.packet.create(PacketType.TAPE_GUI_STATE.ordinal())
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

	public void switchState(State s) {
		//System.out.println("Switchy switch to " + s.name());
		if(this.getEnumState() != s) {
			this.state.switchState(worldObj, s);
			this.sendState();
		}
	}

	public void setSpeed(float speed) {
		this.state.setSpeed(speed);
	}

	public void setVolume(float vol) {
		this.state.setVolume(vol);
	}

	public boolean isEnd() {
		return state.getStorage() == null || state.getStorage().getPosition() + state.packetSize > state.getStorage().getSize();
	}

	public boolean isReady() {
		return state.getStorage() != null;
	}

	public int getSize() {
		return (state.getStorage() != null ? state.getStorage().getSize() : 0);
	}

	public int getPosition() {
		return state.getStorage() != null ? state.getStorage().getPosition() : 0;
	}

	public int seek(int bytes) {
		return state.getStorage() != null ? state.getStorage().seek(bytes) : 0;
	}

	public int read() {
		return read(false);
	}

	public int read(boolean simulate) {
		if(state.getStorage() != null) {
			return state.getStorage().read(simulate) & 0xFF;
		} else {
			return 0;
		}
	}

	public byte[] read(int amount) {
		if(state.getStorage() != null) {
			byte[] data = new byte[amount];
			state.getStorage().read(data, false);
			return data;
		}
		return null;
	}

	public void write(byte b) {
		state.getStorage().write(b);
	}

	public int write(byte[] bytes) {
		return state.getStorage().write(bytes);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		State st = getEnumState();
		AudioPacket pkt = state.update(this, worldObj);
		if(pkt != null) {
			int receivers = 0;
			for(int i = 0; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.getOrientation(i);
				TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
				if(tile instanceof IAudioReceiver) {
					if(tile instanceof IColorable && ((IColorable) tile).canBeColored()
						&& !ColorUtils.isSameOrDefault(this, (IColorable) tile)) {
						continue;
					}
					((IAudioReceiver) tile).receivePacket(pkt, dir.getOpposite());
					receivers++;
				}
			}

			if(receivers == 0) {
				internalSpeaker.receivePacket(pkt, ForgeDirection.UNKNOWN);
			}

			pkt.sendPacket();
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
			this.state.setState(State.VALUES[tag.getByte("state")]);
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
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public void writeToNBT_OC(NBTTagCompound tag) {
		super.writeToNBT_OC(tag);
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
			this.state.setState(State.VALUES[tag.getByte("state")]);
		}
	}

	// OpenComputers

	@Callback(doc = "function():boolean; Returns true if the tape drive is empty or the inserted tape has reached its end", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] isEnd(Context context, Arguments args) {
		return new Object[] { isEnd() };
	}

	@Callback(doc = "function():boolean; Returns true if there is a tape inserted", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] isReady(Context context, Arguments args) {
		return new Object[] { isReady() };
	}

	@Callback(doc = "function():number; Returns the size of the tape, in bytes", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getSize(Context context, Arguments args) {
		return new Object[] { getSize() };
	}

	@Callback(doc = "function():number; Returns the position of the tape, in bytes", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getPosition(Context context, Arguments args) {
		return new Object[] { getPosition() };
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
			return new Object[] { seek(args.checkInteger(0)) };
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
				return new Object[] { read(args.checkInteger(0)) };
			} else {
				return new Object[] { read() };
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
				write((byte) args.checkInteger(0));
			} else if(args.isByteArray(0)) {
				write(args.checkByteArray(0));
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
		return new String[] { "isEnd", "isReady", "getSize", "getLabel", "getState", "setLabel", "setSpeed", "setVolume", "seek", "read", "write", "play", "stop", "getPosition" };
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
		int method, Object[] arguments) throws LuaException,
		InterruptedException {

		// methods which don't take any arguments
		switch(method) {
			case 0: { // isEnd
				return new Object[] { isEnd() };
			}
			case 1: { // isReady
				return new Object[] { isReady() };
			}
			case 2: { // getSize
				return new Object[] { getSize() };
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
						return new Object[] { read() };
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
			case 13: { // getPosition
				return new Object[] { getPosition() };
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
						return new Object[] { write(((String) arguments[0]).getBytes(Charsets.UTF_8)) };
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
						return new Object[] { new String(read(i), Charsets.UTF_8) };
					} else {
						return null;
					}
				}
				case 10: { // write
					if(state.getStorage() != null) {
						write((byte) ((Number) arguments[0]).intValue());
					}
					break;
				}
			}
		}

		// catch all other methods
		return null;
	}

	@Override
	public int getSourceId() {
		return state.getId();
	}

	@Override
	public boolean connectsAudio(ForgeDirection side) {
		return Computronics.tapeReader.getFrontSide(getBlockMetadata()) != side.ordinal();
	}
}

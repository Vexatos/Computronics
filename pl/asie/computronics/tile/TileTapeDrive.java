package pl.asie.computronics.tile;

import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import openperipheral.api.Arg;
import openperipheral.api.LuaCallable;
import openperipheral.api.LuaType;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.Packets;
import pl.asie.computronics.api.IItemStorage;
import pl.asie.computronics.storage.Storage;
import pl.asie.lib.audio.DFPWM;
import pl.asie.lib.block.TileEntityInventory;
import pl.asie.lib.network.PacketInput;
import dan200.computer.api.IComputerAccess;

public class TileTapeDrive extends TileEntityInventory implements SimpleComponent {
	public enum State {
		STOPPED,
		PLAYING,
		REWINDING,
		FORWARDING
	}
	
	private State state = State.STOPPED;
	private Storage storage;
	private String storageName = "";
	private int codecId, codecTick, packetId;
	
	// Audio handling
	
	private void switchState(State newState) {
		if(!worldObj.isRemote) { // Server-side happening
			if(state == State.PLAYING) { // State is playing - stop playback
				Computronics.instance.audio.removePlayer(codecId);
				try {
					PacketInput pkt = Computronics.packet.create(Packets.PACKET_AUDIO_STOP)
						.writeInt(codecId);
					Computronics.packet.sendToAllPlayers(pkt);
				} catch(Exception e) { e.printStackTrace(); }
			}
			if(newState == State.PLAYING) { // Time to play again!
				codecId = Computronics.instance.audio.newPlayer();
				Computronics.instance.audio.getPlayer(codecId);
				codecTick = 0;
				packetId = 0;
			}
		}
		state = newState;
	}
	
	private final int MUSIC_PACKET_SIZE = 1024;
	
	private void sendMusicPacket() {
		byte[] packet = new byte[MUSIC_PACKET_SIZE];
		int amount = storage.read(packet, 0, false); // read data into packet array
		try {
			PacketInput pkt = Computronics.packet.create(Packets.PACKET_AUDIO_DATA)
				.writeTileLocation(this)
				.writeInt(packetId++)
				.writeInt(codecId)
				.writeByteArrayData(packet);
			Computronics.packet.sendToAllNear(pkt, this, 64.0D);
		} catch(Exception e) { e.printStackTrace(); }
		if(amount < MUSIC_PACKET_SIZE) switchState(State.STOPPED);
	}
	
	@Override
	public boolean canUpdate() { return true; }
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if(!worldObj.isRemote && state == State.PLAYING) {
			if(codecTick % 5 == 0) sendMusicPacket();
			codecTick++;
		}
	}
	
	@Override
	public void onBlockDestroy() {
		super.onBlockDestroy();
		unloadStorage();
	}
	
	@Override
	public void invalidate() {
		super.invalidate();
		unloadStorage();
	}
	
	// Storage handling
	
	private void loadStorage() {
		if(worldObj != null && worldObj.isRemote) return;
		
		if(storage != null) unloadStorage();
		ItemStack stack = this.getStackInSlot(0);
		if(stack != null) {
			// Get Storage.
			Item item = stack.getItem();
			if(item instanceof IItemStorage) {
				storage = ((IItemStorage)item).getStorage(stack);
			}
			
			// Get possible label.
			if(stack.getTagCompound() != null) {
				NBTTagCompound tag = stack.getTagCompound();
				storageName = tag.hasKey("label") ? tag.getString("label") : "";
			} else storageName = "";
		}
	}
    
    private void setLabel(String label) {
		if(storage != null) {
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
	}
	
	private void unloadStorage() {
		if(worldObj.isRemote || storage == null) return;
		
		switchState(State.STOPPED);
		try {
			storage.writeFileIfModified();
		} catch(Exception e) { e.printStackTrace(); }
		storage = null;
	}
	
	@Override
	public void onInventoryUpdate(int slot) {
		if(this.getStackInSlot(0) == null) {
			unloadStorage();
			if(slot == 0) {
				// Play eject sound
				worldObj.playSoundEffect(xCoord, yCoord, zCoord, "computronics:tape_eject", 1, 0);
			}
		} else loadStorage();
	}
	
	@Override
	public void onChunkUnload() {
		super.onWorldUnload();
		unloadStorage();
	}
	
	@Override
	public void onWorldUnload() {
		super.onWorldUnload();
		unloadStorage();
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		loadStorage();
	}
	
	public static final int END_SIZE = 1024;
	
	// OpenComputers
	@Callback(direct = true)
	public Object[] isEnd(Context context, Arguments args) {
	    return new Object[]{storage.getPosition() + END_SIZE <= storage.getSize()};
	}
	
    @Callback(direct = true)
    public Object[] isReady(Context context, Arguments args) {
    	return new Object[]{storage != null};
    }
    
    @Callback(direct = true)
    public Object[] getSize(Context context, Arguments args) {
    	return new Object[]{(storage != null ? storage.getSize() : 0)};
    }
    
    @Callback
    public Object[] setLabel(Context context, Arguments args) {
    	if(args.count() == 1) {
    		if(args.isString(0)) setLabel(args.checkString(0));
    	}
    	return new Object[]{(storage != null ? storageName : "")};
    }
    
    @Callback
    public Object[] getLabel(Context context, Arguments args) {
    	return new Object[]{(storage != null ? storageName : "")};
    }

	@Callback
    public Object[] seek(Context context, Arguments args) {
    	if(storage != null && args.count() >= 1 && args.isInteger(0)) {
    		return new Object[]{storage.seek(args.checkInteger(0))};
    	}
    	return null;
    }
    
    @Callback
    public Object[] read(Context context, Arguments args) {
    	if(storage != null) {
    		if(args.count() >= 1 && args.isInteger(0) && (args.checkInteger(0) >= 0)) {
    			byte[] data = new byte[args.checkInteger(0)];
    			storage.read(data, false);
    			return new Object[]{data};
    		} else return new Object[]{(int)storage.read() & 0xFF};
    	} else return null;
    }
    
    @Callback
    public Object[] write(Context context, Arguments args) {
    	if(storage != null && args.count() >= 1) {
    		if(args.isInteger(0))
    			storage.write((byte)args.checkInteger(0));
    		else if(args.isByteArray(0))
    			storage.write(args.checkByteArray(0));
    	}
    	return null;
    }
    
    @Callback
    public Object[] play(Context context, Arguments args) {
    	switchState(State.PLAYING);
    	return null;
    }

    @Callback
    public Object[] stop(Context context, Arguments args) {
    	switchState(State.STOPPED);
    	return null;
    }
    
	@Override
	public String getComponentName() {
		return "tape_drive";
	}
    
    // OpenPeripheral
    @LuaCallable(description = "Check if any tape is inserted.", returnTypes = {LuaType.BOOLEAN})
	public boolean isReady(IComputerAccess computer) {
    	return storage != null;
    }
    
    @LuaCallable(description = "Get the size of the inserted tape.", returnTypes = {LuaType.NUMBER})
	public int getSize(IComputerAccess computer) {
    	if(storage != null) return storage.getSize();
    	else return 0;
    }
    
    @LuaCallable(description = "Check if the tape is near its end.", returnTypes = {LuaType.BOOLEAN})
	public boolean isEnd(IComputerAccess computer) {
	    return storage.getPosition() + END_SIZE <= storage.getSize();
	}
    
    @LuaCallable(description = "Get the label of the inserted tape.", returnTypes = {LuaType.STRING})
	public String getLabel(IComputerAccess computer) {
    	if(storage != null) return storageName;
    	else return null;
    }
    
    @LuaCallable(description = "Set the label of the inserted tape.")
	public void setLabel(
		IComputerAccess computer,
		@Arg(name = "label", type = LuaType.STRING, description = "The new label.") String label
	) {
    	if(storage != null) setLabel(label);
    }
    
    @LuaCallable(description = "Seeks on the tape, returns number of bytes actually seeked.", returnTypes = {LuaType.NUMBER})
	public int seek(
		IComputerAccess computer,
		@Arg(name = "amount", type = LuaType.NUMBER, description = "The amount, negative values go backwards") int amount
	) {
    	if(storage != null) return storage.seek(amount);
    	else return 0;
    }
    
    @LuaCallable(description = "Reads a value (0-255) from the tape and advances the counter.", returnTypes = {LuaType.NUMBER})
	public int read(IComputerAccess computer) {
    	if(storage != null) return (int)storage.read() & 0xFF;
    	else return 0;
    }
    
    @LuaCallable(description = "Writes a value on the tape and advanced the counter.")
	public void write(
		IComputerAccess computer,
		@Arg(name = "value", type = LuaType.NUMBER, description = "The value (0-255)") int value
	) {
    	if(storage != null) storage.write((byte)value);
    }
    
    @LuaCallable(description = "Starts playing music.")
	public void play(IComputerAccess computer) {
    	switchState(State.PLAYING);
    }
    
    @LuaCallable(description = "Stops playing music.")
	public void stop(IComputerAccess computer) {
    	switchState(State.STOPPED);
    }
}

package pl.asie.computronics.tile;

import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import openperipheral.api.Arg;
import openperipheral.api.Freeform;
import openperipheral.api.LuaCallable;
import openperipheral.api.LuaType;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.Packets;
import pl.asie.computronics.api.IItemStorage;
import pl.asie.computronics.item.ItemTape;
import pl.asie.computronics.storage.Storage;
import pl.asie.lib.audio.DFPWM;
import pl.asie.lib.block.TileEntityInventory;
import pl.asie.lib.network.PacketInput;
import dan200.computer.api.IComputerAccess;

@Freeform
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
	private int packetSize = 1024;
	private int soundVolume = 127;
	
	private boolean setSpeed(float speed) {
		if(speed < 0.25F || speed > 2.0F) return false;
		this.packetSize = Math.round(1024*speed);
		return true;
	}
	
	private void setVolume(float volume) {
		if(volume < 0.0F) volume = 0.0F;
		if(volume > 1.0F) volume = 1.0F;
		this.soundVolume = (int)Math.floor(volume*127);
	}
	// GUI/State
	
	private void sendState() {
		if(worldObj.isRemote) return;
		try {
			PacketInput packet = Computronics.packet.create(Packets.PACKET_TAPE_GUI_STATE)
					.writeTileLocation(this)
					.writeByte((byte)state.ordinal())
					.writeByte((byte)soundVolume);
			Computronics.packet.sendToAllNear(packet, this, 64.0D);
		} catch(Exception e) { e.printStackTrace(); }
	}
	
	private String rewindSound = "";
	
	private void stopRewindSound() {
		if(worldObj.isRemote) {
			SoundManager sound = Minecraft.getMinecraft().sndManager;
			sound.sndSystem.stop(rewindSound);
			rewindSound = "";
		}
	}
	
	public void switchState(State newState) {
		if(worldObj.isRemote) { // Client-side happening
			if(newState == state) return;
		}
		if(!worldObj.isRemote) { // Server-side happening
			if(this.storage == null) newState = State.STOPPED;
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
		sendState();
		if(worldObj.isRemote) {
			SoundManager sound = Minecraft.getMinecraft().sndManager;
			if((newState == State.REWINDING || newState == State.FORWARDING)
					&& (state == State.REWINDING || state == State.FORWARDING)) return;
			if(newState == State.REWINDING || newState == State.FORWARDING) {
				rewindSound = "computronics_"+xCoord+"_"+yCoord+"_"+zCoord+"_"+Math.floor(Math.random()*10000);
				// Initialize rewind sound
				SoundPoolEntry spe = sound.soundPoolSounds.getRandomSoundFromSoundPool("computronics:tape_rewind");
				sound.sndSystem.newSource(false, rewindSound, spe.getSoundUrl(), spe.getSoundName(), false, xCoord, yCoord, zCoord, 2, 16.0F);
				sound.sndSystem.setLooping(rewindSound, true);
				sound.sndSystem.setVolume(rewindSound, Minecraft.getMinecraft().gameSettings.soundVolume);
				sound.sndSystem.play(rewindSound);
			} else stopRewindSound();
		}
	}
	
	public State getState() {
		return state;
	}

	// Packet handling
	
	private void sendMusicPacket() {
		byte[] packet = new byte[packetSize];
		int amount = storage.read(packet, 0, false); // read data into packet array
		try {
			PacketInput pkt = Computronics.packet.create(Packets.PACKET_AUDIO_DATA)
				.writeTileLocation(this)
				.writeInt(packetId++)
				.writeInt(codecId)
				.writeShort((short)packetSize)
				.writeByte((byte)soundVolume)
				.writeByteArrayData(packet);
			Computronics.packet.sendToAllNear(pkt, this, 64.0D);
		} catch(Exception e) { e.printStackTrace(); }
		if(amount < packetSize) switchState(State.STOPPED);
	}
	
	// Logic
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if(!worldObj.isRemote) {
			switch(state) {
				case PLAYING: {
					if(codecTick % 5 == 0) sendMusicPacket();
					codecTick++;
				} break;
				case REWINDING: {
					int seeked = storage.seek(-2048);
					if(seeked > -2048) switchState(State.STOPPED);
				} break;
				case FORWARDING: {
					int seeked = storage.seek(2048);
					if(seeked < 2048) switchState(State.STOPPED);
				} break;	
				case STOPPED: {
				} break;
			}
		}
	}
	
	// Minecraft boilerplate
	
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
	
	@Override
	public boolean canUpdate() { return true; }
	
	@Override
	public void openChest() {
		super.openChest();
		sendState();
	}
	
	@Override
	public void onBlockDestroy() {
		super.onBlockDestroy();
		stopRewindSound();
		unloadStorage();
	}
	
	@Override
	public void invalidate() {
		super.invalidate();
		stopRewindSound();
		unloadStorage();
	}
	
	@Override
	public void onRedstoneSignal(int signal) {
		this.switchState(signal > 0 ? State.PLAYING : State.STOPPED);
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
			if(slot == 0 && storage != null) { // Tape was inserted
				// Play eject sound
				worldObj.playSoundEffect(xCoord, yCoord, zCoord, "computronics:tape_eject", 1, 0);
			}
			unloadStorage();
		} else {
			loadStorage();
			if(slot == 0 && this.getStackInSlot(0).getItem() instanceof IItemStorage) {
				// Play insert sound
				worldObj.playSoundEffect(xCoord, yCoord, zCoord, "computronics:tape_insert", 1, 0);
			}
		}
	}
	
	@Override
	public void onChunkUnload() {
		super.onWorldUnload();
		stopRewindSound();
		unloadStorage();
	}
	
	@Override
	public void onWorldUnload() {
		super.onWorldUnload();
		stopRewindSound();
		unloadStorage();
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if(tag.hasKey("state")) this.state = State.values()[tag.getByte("state")];
		if(tag.hasKey("sp")) this.packetSize = tag.getShort("sp");
		if(tag.hasKey("vo")) this.soundVolume = tag.getByte("vo"); else this.soundVolume = 127;
		loadStorage();
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setShort("sp", (short)this.packetSize);
		tag.setByte("state", (byte)this.state.ordinal());
		if(this.soundVolume != 127) tag.setByte("vo", (byte)this.soundVolume);
	}
	
	// OpenComputers
	@Callback(direct = true)
	public Object[] isEnd(Context context, Arguments args) {
	    return new Object[]{storage.getPosition() + packetSize <= storage.getSize()};
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
    
    @Callback
    public Object[] setSpeed(Context context, Arguments args) {
    	if(args.count() > 0 && args.isDouble(0)) return new Object[]{this.setSpeed((float)args.checkDouble(0))};
    	else return null;
    }
    
    @Callback
    public Object[] setVolume(Context context, Arguments args) {
    	if(args.count() > 0 && args.isDouble(0)) this.setVolume((float)args.checkDouble(0));
    	return null;
    }
    
    @Callback
    public Object[] getState(Context context, Arguments args) {
    	return new Object[]{state.toString()};
    }
    
    
	@Override
	public String getComponentName() {
		return "tape_drive";
	}
    
    // OpenPeripheral
    @LuaCallable(description = "Check if any tape is inserted.", returnTypes = {LuaType.BOOLEAN})
	public boolean isReady() {
    	return storage != null;
    }
    
    @LuaCallable(description = "Get the size of the inserted tape.", returnTypes = {LuaType.NUMBER})
	public int getSize() {
    	if(storage != null) return storage.getSize();
    	else return 0;
    }
    
    @LuaCallable(description = "Check if the tape is near its end.", returnTypes = {LuaType.BOOLEAN})
	public boolean isEnd() {
	    return storage.getPosition() + packetSize <= storage.getSize();
	}
    
    @LuaCallable(description = "Get the label of the inserted tape.", returnTypes = {LuaType.STRING})
	public String getLabel() {
    	if(storage != null) return storageName;
    	else return null;
    }
    
    @LuaCallable(name = "setLabel", description = "Set the label of the inserted tape.")
	public void setLabelOP(
		@Arg(name = "label", type = LuaType.STRING, description = "The new label.") String label
	) {
    	if(storage != null) setLabel(label);
    }
    
    @LuaCallable(description = "Seeks on the tape, returns number of bytes actually seeked.", returnTypes = {LuaType.NUMBER})
	public int seek(
		@Arg(name = "amount", type = LuaType.NUMBER, description = "The amount, negative values go backwards") int amount
	) {
    	if(storage != null) return storage.seek(amount);
    	else return 0;
    }
    
    @LuaCallable(description = "Reads a value (0-255) from the tape and advances the counter.", returnTypes = {LuaType.NUMBER})
	public int read() {
    	if(storage != null) return (int)storage.read() & 0xFF;
    	else return 0;
    }
    
    @LuaCallable(description = "Writes a value on the tape and advanced the counter.")
	public void write(
		@Arg(name = "value", type = LuaType.NUMBER, description = "The value (0-255)") int value
	) {
    	if(storage != null) storage.write((byte)value);
    }
    
    @LuaCallable(description = "Sets the speed of tape playback - between 0.25 and 2.0.", returnTypes = {LuaType.BOOLEAN})
	public boolean setSpeed(
		@Arg(name = "speed", type = LuaType.NUMBER, description = "The speed.")double speed
	) {
    	return this.setSpeed((float)speed);
    }
    
    @LuaCallable(description = "Sets the tape volume - between 0.0 and 1.0.")
	public void setVolume(
		@Arg(name = "volume", type = LuaType.NUMBER, description = "The volume.")double volume
	) {
    	this.setVolume((float)volume);
    }
    
    @LuaCallable(description = "Starts playing music.")
	public void play() {
    	switchState(State.PLAYING);
    }
    
    @LuaCallable(name = "getState", description = "Get the current state of the player.", returnTypes = {LuaType.STRING})
 	public String getStateOP() {
     	return state.toString();
     }
    
    @LuaCallable(description = "Stops playing music.")
	public void stop() {
    	switchState(State.STOPPED);
    }
}

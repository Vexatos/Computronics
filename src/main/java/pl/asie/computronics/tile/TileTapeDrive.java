package pl.asie.computronics.tile;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
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
import pl.asie.computronics.Computronics;
import pl.asie.computronics.Packets;
import pl.asie.computronics.api.IItemStorage;
import pl.asie.computronics.item.ItemTape;
import pl.asie.computronics.storage.Storage;
import pl.asie.lib.audio.DFPWM;
import pl.asie.lib.block.TileEntityInventory;
import pl.asie.lib.network.Packet;

public class TileTapeDrive extends TileEntityPeripheralInventory {
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
	
	public TileTapeDrive() {
		super("tape_drive");
	}
	
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
			Packet packet = Computronics.packet.create(Packets.PACKET_TAPE_GUI_STATE)
					.writeTileLocation(this)
					.writeByte((byte)state.ordinal());
					//.writeByte((byte)soundVolume);
			Computronics.packet.sendToAllAround(packet, this, 64.0D);
		} catch(Exception e) { e.printStackTrace(); }
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
					Packet pkt = Computronics.packet.create(Packets.PACKET_AUDIO_STOP)
						.writeInt(codecId);
					Computronics.packet.sendToAll(pkt);
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
	}
	
	public State getState() {
		return state;
	}

	// Packet handling
	
	private void sendMusicPacket() {
		byte[] packet = new byte[packetSize];
		int amount = storage.read(packet, 0, false); // read data into packet array
		try {
			Packet pkt = Computronics.packet.create(Packets.PACKET_AUDIO_DATA)
				.writeTileLocation(this)
				.writeInt(packetId++)
				.writeInt(codecId)
				.writeShort((short)packetSize)
				.writeByte((byte)soundVolume)
				.writeByteArrayData(packet);
			Computronics.packet.sendToAllAround(pkt, this, 64.0D);
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
		super.invalidate();
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
    @Optional.Method(modid="OpenComputers")
	public Object[] isEnd(Context context, Arguments args) {
	    return new Object[]{storage.getPosition() + packetSize <= storage.getSize()};
	}
	
    @Callback(direct = true)
    @Optional.Method(modid="OpenComputers")
    public Object[] isReady(Context context, Arguments args) {
    	return new Object[]{storage != null};
    }
    
    @Callback(direct = true)
    @Optional.Method(modid="OpenComputers")
    public Object[] getSize(Context context, Arguments args) {
    	return new Object[]{(storage != null ? storage.getSize() : 0)};
    }
    
    @Callback
    @Optional.Method(modid="OpenComputers")
    public Object[] setLabel(Context context, Arguments args) {
    	if(args.count() == 1) {
    		if(args.isString(0)) setLabel(args.checkString(0));
    	}
    	return new Object[]{(storage != null ? storageName : "")};
    }
    
    @Callback
    @Optional.Method(modid="OpenComputers")
    public Object[] getLabel(Context context, Arguments args) {
    	return new Object[]{(storage != null ? storageName : "")};
    }

	@Callback
    @Optional.Method(modid="OpenComputers")
    public Object[] seek(Context context, Arguments args) {
    	if(storage != null && args.count() >= 1 && args.isInteger(0)) {
    		return new Object[]{storage.seek(args.checkInteger(0))};
    	}
    	return null;
    }
    
    @Callback
    @Optional.Method(modid="OpenComputers")
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
    @Optional.Method(modid="OpenComputers")
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
    @Optional.Method(modid="OpenComputers")
    public Object[] play(Context context, Arguments args) {
    	switchState(State.PLAYING);
    	return null;
    }

    @Callback
    @Optional.Method(modid="OpenComputers")
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
    @Optional.Method(modid="OpenComputers")
    public Object[] getState(Context context, Arguments args) {
    	return new Object[]{state.toString()};
    }
    
	@Override
    @Optional.Method(modid="ComputerCraft")
	public String[] getMethodNames() {
		// TODO Auto-generated method stub
		return new String[]{};
	}

	@Override
    @Optional.Method(modid="ComputerCraft")
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
			InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}
}

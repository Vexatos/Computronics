package pl.asie.computronics.tile;

import java.nio.file.FileSystem;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Node;
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
import pl.asie.computronics.tile.TapeDriveState.State;
import pl.asie.lib.audio.DFPWM;
import pl.asie.lib.block.TileEntityInventory;
import pl.asie.lib.network.Packet;

public class TileTapeDrive extends TileEntityPeripheralInventory {
	private String storageName = "";
	private TapeDriveState state;
	
	public TileTapeDrive() {
		super("tape_drive");
		this.state = new TapeDriveState();
		if(Loader.isModLoaded("OpenComputers")) {
			initOCFilesystem();
		}
	}
	
	private ManagedEnvironment oc_fs;
	
	@Optional.Method(modid="OpenComputers")
	private void initOCFilesystem() {
		oc_fs = li.cil.oc.api.FileSystem.asManagedEnvironment(li.cil.oc.api.FileSystem.fromClass(Computronics.class, "computronics", "ocfs/tape"),
				"tape_drive");
	}
	
	// GUI/State

	protected void sendState() {
		if(worldObj.isRemote) return;
		try {
			Packet packet = Computronics.packet.create(Packets.PACKET_TAPE_GUI_STATE)
					.writeTileLocation(this)
					.writeByte((byte)state.getState().ordinal());
					//.writeByte((byte)soundVolume);
			Computronics.packet.sendToAllAround(packet, this, 64.0D);
		} catch(Exception e) { e.printStackTrace(); }
	}
	
	// Logic
	public State getEnumState() { return this.state.getState(); }
	
	public void switchState(State s) {
		System.out.println("Switchy switch to " + s.name());
		this.state.switchState(worldObj, xCoord, yCoord, zCoord, s);
		this.sendState();
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		Packet pkt = state.update(worldObj, xCoord, yCoord, zCoord);
		if(pkt != null) {
			Computronics.packet.sendToAllAround(pkt, this, Computronics.TAPEDRIVE_DISTANCE * 2);
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
		
		if(state.getStorage() != null) unloadStorage();
		ItemStack stack = this.getStackInSlot(0);
		if(stack != null) {
			// Get Storage.
			Item item = stack.getItem();
			if(item instanceof IItemStorage) {
				state.setStorage(((IItemStorage)item).getStorage(stack));
			}
			
			// Get possible label.
			if(stack.getTagCompound() != null) {
				NBTTagCompound tag = stack.getTagCompound();
				storageName = tag.hasKey("label") ? tag.getString("label") : "";
			} else storageName = "";
		}
	}
    
	private void unloadStorage() {
		if(worldObj.isRemote || state.getStorage() == null) return;
		
		switchState(State.STOPPED);
		try {
			state.getStorage().writeFileIfModified();
		} catch(Exception e) { e.printStackTrace(); }
		state.setStorage(null);
	}
	
	@Override
	public void onInventoryUpdate(int slot) {
		if(this.getStackInSlot(0) == null) {
			if(slot == 0 && state.getStorage() != null) { // Tape was inserted
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
		if(tag.hasKey("state")) this.state.setState(State.values()[tag.getByte("state")]);
		if(tag.hasKey("sp")) this.state.packetSize = tag.getShort("sp");
		if(tag.hasKey("vo")) this.state.soundVolume = tag.getByte("vo"); else this.state.soundVolume = 127;
		loadStorage();
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setShort("sp", (short)this.state.packetSize);
		tag.setByte("state", (byte)this.state.getState().ordinal());
		if(this.state.soundVolume != 127) tag.setByte("vo", (byte)this.state.soundVolume);
	}
	
	// OpenComputers
	@Callback(direct = true)
    @Optional.Method(modid="OpenComputers")
	public Object[] isEnd(Context context, Arguments args) {
		if(state.getStorage() != null) return new Object[]{state.getStorage().getPosition() + state.packetSize <= state.getStorage().getSize()};
		else return new Object[]{true};
	}
	
    @Callback(direct = true)
    @Optional.Method(modid="OpenComputers")
    public Object[] isReady(Context context, Arguments args) {
    	return new Object[]{state.getStorage() != null};
    }
    
    @Callback(direct = true)
    @Optional.Method(modid="OpenComputers")
    public Object[] getSize(Context context, Arguments args) {
    	return new Object[]{(state.getStorage() != null ? state.getStorage().getSize() : 0)};
    }
    
    @Callback
    @Optional.Method(modid="OpenComputers")
    public Object[] setLabel(Context context, Arguments args) {
    	if(args.count() == 1) {
    		if(args.isString(0)) setLabel(args.checkString(0));
    	}
    	return new Object[]{(state.getStorage() != null ? storageName : "")};
    }
    
    @Callback
    @Optional.Method(modid="OpenComputers")
    public Object[] getLabel(Context context, Arguments args) {
    	return new Object[]{(state.getStorage() != null ? storageName : "")};
    }

	@Callback
    @Optional.Method(modid="OpenComputers")
    public Object[] seek(Context context, Arguments args) {
    	if(state.getStorage() != null && args.count() >= 1 && args.isInteger(0)) {
    		return new Object[]{state.getStorage().seek(args.checkInteger(0))};
    	}
    	return null;
    }
    
    @Callback
    @Optional.Method(modid="OpenComputers")
    public Object[] read(Context context, Arguments args) {
    	if(state.getStorage() != null) {
    		if(args.count() >= 1 && args.isInteger(0) && (args.checkInteger(0) >= 0)) {
    			byte[] data = new byte[args.checkInteger(0)];
    			state.getStorage().read(data, false);
    			return new Object[]{data};
    		} else return new Object[]{(int)state.getStorage().read() & 0xFF};
    	} else return null;
    }
    
    @Callback
    @Optional.Method(modid="OpenComputers")
    public Object[] write(Context context, Arguments args) {
    	if(state.getStorage() != null && args.count() >= 1) {
    		if(args.isInteger(0))
    			state.getStorage().write((byte)args.checkInteger(0));
    		else if(args.isByteArray(0))
    			state.getStorage().write(args.checkByteArray(0));
    	}
    	return null;
    }
    
    @Callback
    @Optional.Method(modid="OpenComputers")
    public Object[] play(Context context, Arguments args) {
    	switchState(State.PLAYING);
    	return new Object[]{state.getStorage() != null};
    }

    @Callback
    @Optional.Method(modid="OpenComputers")
    public Object[] stop(Context context, Arguments args) {
    	switchState(State.STOPPED);
    	return new Object[]{state.getStorage() != null};
    }
    
    @Callback
    public Object[] setSpeed(Context context, Arguments args) {
    	if(args.count() > 0 && args.isDouble(0)) return new Object[]{this.state.setSpeed((float)args.checkDouble(0))};
    	else return null;
    }
    
    @Callback
    public Object[] setVolume(Context context, Arguments args) {
    	if(args.count() > 0 && args.isDouble(0)) this.state.setVolume((float)args.checkDouble(0));
    	return null;
    }
    
    @Callback
    @Optional.Method(modid="OpenComputers")
    public Object[] getState(Context context, Arguments args) {
    	return new Object[]{state.toString()};
    }
    
    @Override
	@Optional.Method(modid="OpenComputers")
    public void onConnect(final Node node) {
    	super.onConnect(node);
    	node.connect(oc_fs.node());
    }

    @Override
	@Optional.Method(modid="OpenComputers")
    public void onDisconnect(final Node node) {
    	super.onDisconnect(node);
    	node.disconnect(oc_fs.node());
    }
    
	@Override
    @Optional.Method(modid="ComputerCraft")
	public String[] getMethodNames() {
		// TODO Auto-generated method stub
		return new String[]{"isEnd", "isReady", "getSize", "getLabel", "getState", "setLabel", "setSpeed", "setVolume", "seek", "read", "write", "play", "stop"};
	}

	@Override
    @Optional.Method(modid="ComputerCraft")
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
			InterruptedException {
		// methods which take strings and do something
		if(arguments.length == 1 && (arguments[0] instanceof String)) {
			switch(method) {
			case 5: setLabel((String)arguments[0]); break;
			case 10: if(state.getStorage() != null) return new Object[]{state.getStorage().write(((String)arguments[0]).getBytes())}; break;
			}
		}
		
		// methods which take floats and do something
		if(arguments.length == 1 && (arguments[0] instanceof Double)) {
			float f = ((Double)arguments[0]).floatValue();
			int i = ((Double)arguments[0]).intValue();
			switch(method) {
			case 6: return new Object[]{this.state.setSpeed(f)};
			case 7: this.state.setVolume(f); return null;
			case 8: if(state.getStorage() != null) return new Object[]{state.getStorage().seek(i)};
			case 10: if(state.getStorage() != null) state.getStorage().write((byte)i); break;
			case 9: if(state.getStorage() != null) {
				if(i >= 256) i = 256;
				byte[] data = new byte[i];
				state.getStorage().read(data, false);
				return new Object[]{new String(data)};
			} break;
			}
		}

		// methods which don't take any arguments and do something
		switch(method) {
		case 11: switchState(State.PLAYING);
		case 12: switchState(State.STOPPED);
		}
		
		// returns for the methods which didn't return something before
		switch(method) {
		case 0: if(state.getStorage() != null) return new Object[]{state.getStorage().getPosition() + state.packetSize <= state.getStorage().getSize()};
		
		case 1:  // isReady, play, stop
		case 11:
		case 12: return new Object[]{state.getStorage() != null};
		
		case 2: return new Object[]{(state.getStorage() != null ? state.getStorage().getSize() : 0)};
		
		case 3: // getLabel, setLabel
		case 5: return new Object[]{(state.getStorage() != null ? storageName : "")};
		
		case 4: return new Object[]{state.toString()};
		
		case 9: if(state.getStorage() != null) return new Object[]{(int)state.getStorage().read() & 0xFF};
		}
		
		// catch all other methods
		return null;
	}
	
	private int _nedo_lastSeek = 0;

	@Override
    @Optional.Method(modid="nedocomputers")
	public short busRead(int addr) {
		switch(addr & 0xFFFE) {
		case 0: return (short)state.getState().ordinal();
		case 2: return 0; // speed?
		case 4: return (short)state.soundVolume;
		case 6: return (state.getStorage() != null ? (short)(state.getStorage().getSize() / ItemTape.L_MINUTE) : 0);
		case 8: return (short)_nedo_lastSeek;
		case 10: return (state.getStorage() != null ? (short)state.getStorage().read() : 0);
		}
		return 0;
	}

	@Override
    @Optional.Method(modid="nedocomputers")
	public void busWrite(int addr, short data) {
		switch(addr & 0xFFFE) {
		case 0: state.setState(State.values()[data % State.values().length]); break;
		case 2: break; // speed?
		case 4:state. soundVolume = Math.max(0, Math.min(data, 127)); break;
		case 6: break; // tape size is read-only!
		case 8: _nedo_lastSeek = state.getStorage().seek(data); break;
		case 10: if(state.getStorage() != null) state.getStorage().write((byte)(data & 0xFF)); break;
		}
	}
}

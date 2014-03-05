package pl.asie.computronics.tile;

import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.SimpleComponent;
import openperipheral.api.Arg;
import openperipheral.api.LuaCallable;
import openperipheral.api.LuaType;
import dan200.computer.api.IComputerAccess;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import pl.asie.computronics.api.IItemStorage;
import pl.asie.computronics.storage.Storage;
import pl.asie.lib.block.TileEntityInventory;

public class TileTapeReader extends TileEntityInventory implements SimpleComponent {
	private Storage storage;
	private String storageName = "";
	
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
		try {
			storage.writeFileIfModified();
		} catch(Exception e) { e.printStackTrace(); }
		storage = null;
	}
	
	@Override
	public void onInventoryUpdate(int slot) {
		if(this.getStackInSlot(0) == null) unloadStorage();
		else loadStorage();
	}
	
	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
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
	
	// OpenComputers
    @Callback
    public Object[] isReady(Context context, Arguments args) {
    	return new Object[]{storage != null};
    }
    
    @Callback
    public Object[] size(Context context, Arguments args) {
    	return new Object[]{(storage != null ? storage.getSize() : 0)};
    }
    
    @Callback
    public Object[] label(Context context, Arguments args) {
    	if(args.count() == 1) {
    		if(args.isString(0)) setLabel(args.checkString(0));
    	}
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
    			storage.read(data);
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

	@Override
	public String getComponentName() {
		return "tape_reader";
	}
    
    // OpenPeripheral
    @LuaCallable(description = "Checks if any tape is inserted.", returnTypes = {LuaType.BOOLEAN})
	public boolean isReady(IComputerAccess computer) {
    	return storage != null;
    }
    
    @LuaCallable(description = "Get the size of the inserted tape.", returnTypes = {LuaType.NUMBER})
	public int getSize(IComputerAccess computer) {
    	if(storage != null) return storage.getSize();
    	else return 0;
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
}

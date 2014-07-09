package pl.asie.computronics.tile;

import net.minecraft.item.ItemStack;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;

public class TileEEPROMReader extends TileEntityPeripheralInventory {
	public TileEEPROMReader() {
		super("eeprom");
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public void onInventoryUpdate(int arg0) { }

	public boolean isReady() {
    	ItemStack is = this.getStackInSlot(0);
    	return is != null && is.stackSize > 0 && is.hasTagCompound() && is.getTagCompound().hasKey("ram");
	}
	
	public byte[] getData() {
    	ItemStack is = this.getStackInSlot(0);
		if(!isReady()) return new byte[0];
		else return is.getTagCompound().getByteArray("ram");
	}
	
	public int getSize() { return getData().length; }
	
	public void setData(byte[] data) {
    	ItemStack is = this.getStackInSlot(0);
		if(!isReady()) return;
		else is.getTagCompound().setByteArray("ram", data);
	}
	
    @Callback(direct = true)
    @Optional.Method(modid="OpenComputers")
    public Object[] isReady(Context context, Arguments args) {
		return new Object[]{ isReady() };
    }
    
    @Callback(direct = true)
    @Optional.Method(modid="OpenComputers")
    public Object[] getSize(Context context, Arguments args) {
		return new Object[]{ getSize() };
    }
    
    @Callback(direct = true)
    @Optional.Method(modid="OpenComputers")
    public Object[] read(Context context, Arguments args) {
    	byte[] data = getData();
		if(args.count() >= 1 && args.isInteger(0)) {
			int pos = args.checkInteger(0);
			if(pos < 0 || pos >= data.length) return null;
			if(args.count() >= 2 && args.isInteger(1)) {
				int len = args.checkInteger(1);
				if(pos+len >= data.length) return null;
				byte[] out = new byte[len];
				System.arraycopy(data, pos, out, 0, len);
				return new Object[]{out};
			} else return new Object[]{(int)data[pos]};
		} else return null;
    }

    @Callback(direct = true)
    @Optional.Method(modid="OpenComputers")
    public Object[] write(Context context, Arguments args) {
    	byte[] data = getData();
    	if(args.count() == 2 && args.isInteger(0)) {
			int pos = args.checkInteger(0);
			if(pos < 0 || pos >= data.length) return new Object[]{false};
    		if(args.isByteArray(1)) {
    			byte[] inject = args.checkByteArray(1);
    			if(pos+inject.length >= data.length) return new Object[]{false};
    			System.arraycopy(inject, 0, data, pos, inject.length);
    		} else if(args.isInteger(1)) {
    			data[pos] = (byte)args.checkInteger(1);
    		} else return new Object[]{false};
    		
    		setData(data);
    		return new Object[]{true};
    	} else return null;
    }
    
	@Override
    @Optional.Method(modid="ComputerCraft")
	public String[] getMethodNames() {
		return new String[]{"isReady", "getSize", "read", "write"};
	}

	@Override
    @Optional.Method(modid="ComputerCraft")
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
			InterruptedException {
		switch(method) {
		case 0: return new Object[] { isReady() };
		case 1: return new Object[] { getSize() };
		}
		// 2 or 3
		if(arguments.length == 0 || !(arguments[0] instanceof Double)) return null;
		byte[] data = getData();
		int pos = ((Double)arguments[0]).intValue();
		if(pos < 0 || pos >= data.length) return null;
		switch(method) {
		case 2: return new Object[] { (int)data[pos] };
		case 3: {
			if(arguments.length >= 2 && (arguments[1] instanceof Double)) {
				data[pos] = (byte)(((Double)arguments[1]).intValue());
				setData(data);
			}
		}
		}
		return null;
	}
	
	private short _nedo_addr;
	@Override
	public short busRead(int addr) {
		switch((addr & 0xFFFE)) {
		case 0: return (short)(getData().length >> 1);
		}
		return 0;
	}

	@Override
	public void busWrite(int addr, short data) {

	}
}

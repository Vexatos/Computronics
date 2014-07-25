package pl.asie.computronics.tile;

import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;

public class TileColorfulLamp extends TileEntityPeripheralBase {
	public TileColorfulLamp() {
		super("colorful_lamp");
	}

	private int color = 0;
	
	public int getLampColor() {
		return color;
	}
	
	public void setLampColor(int color) {
		this.color = color;
		this.markDirty();
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	@Override
	public String[] getMethodNames() {
		return new String[]{"getLampColor", "setLampColor"};
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
			InterruptedException {
		switch(method) {
		case 0:
		default: return new Object[]{this.color};
		case 1: {
			if(arguments.length > 0 && (arguments[0] instanceof Double)) {
				this.setLampColor(((Double)arguments[0]).intValue());
			}
		} break;
		}
		return null;
	}

	@Override
	public short busRead(int addr) {
		return (short)color;
	}

	@Override
	public void busWrite(int addr, short data) {
		this.setLampColor(((int)data) & 0x7FFF);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if(tag.hasKey("clc")) color = tag.getShort("clc");
		if(color < 0) color = 0;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setShort("clc", (short)(color & 32767));
	}
	
	@Override
    public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setShort("c", (short)(color & 32767));
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
    }
	
	@Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		NBTTagCompound tag = pkt.func_148857_g();
		if(tag != null && tag.hasKey("c")) {
			this.color = tag.getShort("c");
			System.out.println("client color is now " + this.color);
			this.worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
		}
    }
}

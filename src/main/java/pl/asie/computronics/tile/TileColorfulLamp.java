package pl.asie.computronics.tile;

import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import mods.immibis.redlogic.api.wiring.IBundledEmitter;
import mods.immibis.redlogic.api.wiring.IBundledUpdatable;
import mods.immibis.redlogic.api.wiring.IConnectable;
import mods.immibis.redlogic.api.wiring.IWire;
import mrtjp.projectred.api.IBundledTile;
import mrtjp.projectred.api.ProjectRedAPI;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

@Optional.InterfaceList({
	@Optional.Interface(iface = "mods.immibis.redlogic.api.wiring.IBundledUpdatable", modid = "RedLogic"),
	@Optional.Interface(iface = "mods.immibis.redlogic.api.wiring.IConnectable", modid = "RedLogic"),
	@Optional.Interface(iface = "mrtjp.projectred.api.IBundledTile", modid = "ProjRed|Core")
})
public class TileColorfulLamp extends TileEntityPeripheralBase implements IBundledTile, IBundledUpdatable, IConnectable {
	public TileColorfulLamp() {
		super("colorful_lamp");
	}

	private int color = 0;
	
	public int getLampColor() {
		return color;
	}
	
	public void setLampColor(int color) {
		this.color = color & 0x7FFF;
		this.markDirty();
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	@Callback(direct = true)
    @Optional.Method(modid="OpenComputers")
	public Object[] getLampColor(Context context, Arguments args) throws Exception {
		return new Object[]{this.getLampColor()};
	}
	
	@Callback()
    @Optional.Method(modid="OpenComputers")
	public Object[] setLampColor(Context context, Arguments args) throws Exception {
		if(args.count() >= 1 && args.isInteger(0))
			this.setLampColor(args.checkInteger(0));
		return null;
	}
	
	@Override
    @Optional.Method(modid="ComputerCraft")
	public String[] getMethodNames() {
		return new String[]{"getLampColor", "setLampColor"};
	}

	@Override
    @Optional.Method(modid="ComputerCraft")
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
	@Optional.Method(modid="nedocomputers")
	public short busRead(int addr) {
		return (short)color;
	}

	@Override
	@Optional.Method(modid="nedocomputers")
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
			this.worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
		}
    }
	
	private boolean parseBundledInput(byte[] data) {
		if(data != null) {
			int c = 0;
			for(int i = 0; i < 15; i++) {
				if(data[i] != 0)
					c |= (1 << i);
			}
			this.setLampColor(c);
			return true;
		} else return false;
	}

	@Override
	@Optional.Method(modid="ProjRed|Core")
	public byte[] getBundledSignal(int side) {
		return new byte[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	}

	@Override
	@Optional.Method(modid="ProjRed|Core")
	public boolean canConnectBundled(int side) { return true; }
	
	@Optional.Method(modid = "ProjRed|Core")
	public void onProjectRedBundledInputChanged() {
		for(int i = 0; i < 6; i++) {
			if(parseBundledInput(ProjectRedAPI.transmissionAPI.getBundledInput(worldObj, xCoord, yCoord, zCoord, i)))
				return;
		}
	}

	@Override
	@Optional.Method(modid = "RedLogic")
	public boolean connects(IWire wire, int blockFace, int fromDirection) { return (wire instanceof IBundledEmitter); }
	@Override
	@Optional.Method(modid = "RedLogic")
	public boolean connectsAroundCorner(IWire wire, int blockFace, int fromDirection) { return false; }
	@Override
	@Optional.Method(modid = "RedLogic")
	public void onBundledInputChanged() {
		for(int side = 0; side < 6; side++) {
			ForgeDirection dir = ForgeDirection.getOrientation(side);
			TileEntity input = worldObj.getTileEntity(xCoord+dir.offsetX, yCoord+dir.offsetY, zCoord+dir.offsetZ);
			if(!(input instanceof IBundledEmitter)) continue;
			for(int direction = -1; direction < 6; direction++) {
 				byte[] data = ((IBundledEmitter)input).getBundledCableStrength(direction, side ^ 1);
 				if(parseBundledInput(data)) return;
			}
		}
	}
}

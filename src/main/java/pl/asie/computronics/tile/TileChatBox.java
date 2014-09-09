package pl.asie.computronics.tile;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.ServerChatEvent;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.ChatBoxUtils;

//import dan200.computer.api.IComputerAccess;
//import dan200.computer.api.ILuaContext;
//import dan200.computer.api.IPeripheral;

public class TileChatBox extends TileEntityPeripheralBase {
	private int distance;
	private int ticksUntilOff = 0;
	private boolean mustRefresh = false;
	private String name = "";
	
	public TileChatBox() {
		super("chat_box");
		distance = Computronics.CHATBOX_DISTANCE;
	}
	
	@Override
	public int requestCurrentRedstoneValue(int side) {
		return (ticksUntilOff > 0) ? 15 : 0;
	}
	
	@Override
	public boolean canUpdate() { return Computronics.MUST_UPDATE_TILE_ENTITIES || Computronics.REDSTONE_REFRESH; }
	
	public boolean isCreative() {
		return Computronics.CHATBOX_CREATIVE && worldObj != null
			&& worldObj.getBlockMetadata(xCoord, yCoord, zCoord) >= 8;
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if(Computronics.REDSTONE_REFRESH && ticksUntilOff > 0) {
			ticksUntilOff--;
			if(ticksUntilOff == 0 || mustRefresh)
				this.worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, this.blockType);
		}
	}
	
	public int getDistance() { return distance; }
	
	public void setDistance(int dist) {
		if(dist > 32767) dist = 32767;
		
		this.distance = Math.min(Computronics.CHATBOX_DISTANCE, dist);
		if(this.distance < 0) this.distance = Computronics.CHATBOX_DISTANCE;
	}
	
	public void receiveChatMessage(ServerChatEvent event) {
		if(Computronics.REDSTONE_REFRESH) {
			ticksUntilOff = 5;
			mustRefresh = true;
		}
		if(Loader.isModLoaded(Mods.OpenComputers)) eventOC(event);
		if(Loader.isModLoaded(Mods.ComputerCraft)) eventCC(event);
	}
	
	@Optional.Method(modid=Mods.OpenComputers)
	public void eventOC(ServerChatEvent event) {
		node.sendToReachable("computer.signal", "chat_message", event.username, event.message);
	}
	
	@Optional.Method(modid=Mods.ComputerCraft)
	public void eventCC(ServerChatEvent event) {
		for(IComputerAccess computer: attachedComputersCC) {
			computer.queueEvent("chat_message", new Object[]{event.username, event.message});
		}
	}
	
	// OpenComputers API
	
	@Callback
	@Optional.Method(modid=Mods.OpenComputers)
	public Object[] say(Context context, Arguments args) {
		int d = distance;
		if(args.count() >= 1) {
			if(args.isInteger(1)) {
				d = Math.min(Computronics.CHATBOX_DISTANCE, args.checkInteger(1));
				if(d <= 0) d = distance;
			}
			if(args.isString(0)) ChatBoxUtils.sendChatMessage(this, d, name.length() > 0 ? name : Computronics.CHATBOX_PREFIX, args.checkString(0));
		}
		return null;
	}
	
	@Callback(direct = true)
	@Optional.Method(modid=Mods.OpenComputers)
	public Object[] getDistance(Context context, Arguments args) {
		return new Object[]{distance};
	}
	
	@Callback(direct = true)
	@Optional.Method(modid=Mods.OpenComputers)
	public Object[] setDistance(Context context, Arguments args) {
		if(args.count() == 1) {
			if(args.isInteger(0)) setDistance(args.checkInteger(0));
		}
		return null;
	}
	
	@Callback(direct = true)
	@Optional.Method(modid=Mods.OpenComputers)
	public Object[] getName(Context context, Arguments args) {
		return new Object[]{distance};
	}
	
	@Callback(direct = true)
	@Optional.Method(modid=Mods.OpenComputers)
	public Object[] setName(Context context, Arguments args) {
		if(args.count() == 1) {
			if(args.isString(0)) this.name = args.checkString(0);
		}
		return null;
	}

    @Override
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if(nbt.hasKey("d")) this.distance = nbt.getShort("d");
        if(nbt.hasKey("n")) this.name = nbt.getString("n");
    }

    @Override
    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setShort("d", (short)this.distance);
        if(name.length() > 0) nbt.setString("n", this.name);
    }

	@Override
    @Optional.Method(modid=Mods.ComputerCraft)
	public String[] getMethodNames() {
		return new String[]{"say", "getDistance", "setDistance", "getName", "setName"};
	}

	@Override
    @Optional.Method(modid=Mods.ComputerCraft)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
			InterruptedException {
		switch(method) {
		case 0: { // say
			if(arguments.length >= 1 && arguments[0] instanceof String) {
				int d = distance;
				if(arguments.length >= 2 && arguments[1] instanceof Double) {
					d = Math.min(Computronics.CHATBOX_DISTANCE, ((Double)arguments[1]).intValue());
					if(d <= 0) d = distance;
				}
				ChatBoxUtils.sendChatMessage(this, d, Computronics.CHATBOX_PREFIX, ((String)arguments[0]));
			}
		}
		case 1: { // getDistance
			return new Object[]{distance};
		}
		case 2: { // setDistance
			if(arguments.length == 1 && arguments[0] instanceof Double) {
				setDistance(((Double)arguments[0]).intValue());
			}
		}
		case 3: { // getName
			return new Object[]{name};
		}
		case 4: { // setName
			if(arguments.length == 1 && arguments[0] instanceof String) {
				this.name = (String)arguments[0];
			}
		}
		}
		return null;
	}

	@Override
    @Optional.Method(modid=Mods.NedoComputers)
	public short busRead(int addr) {
		return 0;
	}

	@Override
    @Optional.Method(modid=Mods.NedoComputers)
	public void busWrite(int addr, short data) {
		switch((addr & 0xFFFE)) {
		case 0: if(data > 0) distance = data; break;
		}
	}
}

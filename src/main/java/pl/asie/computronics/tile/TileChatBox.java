package pl.asie.computronics.tile;

import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.ServerChatEvent;
import pl.asie.computronics.api.chat.ChatAPI;
import pl.asie.computronics.api.chat.IChatListener;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.ChatBoxUtils;

public class TileChatBox extends TileEntityPeripheralBase implements IChatListener {
	private int distance;
	private int ticksUntilOff = 0;
	private boolean mustRefresh = false;
	private String name = "";

	public TileChatBox() {
		super("chat_box");
		distance = Config.CHATBOX_DISTANCE;
	}

	@Override
	public int requestCurrentRedstoneValue(int side) {
		return (ticksUntilOff > 0) ? 15 : 0;
	}

	@Override
	public boolean canUpdate() {
		return Config.MUST_UPDATE_TILE_ENTITIES || Config.REDSTONE_REFRESH;
	}

	public boolean isCreative() {
		return Config.CHATBOX_CREATIVE && worldObj != null
			&& worldObj.getBlockMetadata(xCoord, yCoord, zCoord) >= 8;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(Config.REDSTONE_REFRESH && ticksUntilOff > 0) {
			ticksUntilOff--;
			if(ticksUntilOff == 0 || mustRefresh) {
				this.worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, this.blockType);
			}
		}
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int dist) {
		if(dist > 32767) {
			dist = 32767;
		}

		if(!isCreative()) {
			this.distance = Math.min(Config.CHATBOX_DISTANCE, dist);
		}
		if(this.distance < 0) {
			this.distance = Config.CHATBOX_DISTANCE;
		}
	}

	public void receiveChatMessage(ServerChatEvent event) {
		if(!isCreative() && (event.player.worldObj != this.worldObj || event.player.getDistanceSq(xCoord, yCoord, zCoord) > distance * distance)) {
			return;
		}

		if(Config.REDSTONE_REFRESH) {
			ticksUntilOff = 5;
			mustRefresh = true;
		}
		if(Mods.isLoaded(Mods.OpenComputers)) {
			eventOC(event);
		}
		if(Mods.isLoaded(Mods.ComputerCraft)) {
			eventCC(event);
		}
	}

	@Override
	public void validate() {
		super.validate();
		ChatAPI.registry.registerChatListener(this);
	}

	@Override
	public void invalidate() {
		super.invalidate();
		ChatAPI.registry.unregisterChatListener(this);
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public void eventOC(ServerChatEvent event) {
		if(node() != null) {
			node().sendToReachable("computer.signal", "chat_message", event.username, event.message);
		}
	}

	@Optional.Method(modid = Mods.ComputerCraft)
	public void eventCC(ServerChatEvent event) {
		if(attachedComputersCC != null) {
			for(IComputerAccess computer : attachedComputersCC) {
				computer.queueEvent("chat_message", new Object[] {
					computer.getAttachmentName(),
					event.username, event.message
				});
			}
		}
	}

	// OpenComputers API

	@Callback(doc = "function(text:string [, distance:number]):boolean; "
		+ "Makes the chat box say some text with the currently set or the specified distance. Returns true on success")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] say(Context context, Arguments args) {
		int d = distance;
		if(args.count() >= 1) {
			if(args.isInteger(1)) {
				d = isCreative() ? args.checkInteger(1) : Math.min(Config.CHATBOX_DISTANCE, args.checkInteger(1));
				if(d <= 0) {
					d = distance;
				}
			}
			if(args.isString(0)) {
				ChatBoxUtils.sendChatMessage(this, d, name.length() > 0 ? name : Config.CHATBOX_PREFIX, args.checkString(0));
				return new Object[] { true };
			}
		}
		return new Object[] { false };
	}

	@Callback(doc = "function():number; Returns the chat distance the chat box is currently set to", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getDistance(Context context, Arguments args) {
		return new Object[] { distance };
	}

	@Callback(doc = "function(distance:number):number; Sets the distance of the chat box. Returns the new distance", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setDistance(Context context, Arguments args) {
		setDistance(args.checkInteger(0));
		return new Object[] { distance };
	}

	@Callback(doc = "function():string; Returns the name of the chat box", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getName(Context context, Arguments args) {
		return new Object[] { name };
	}

	@Callback(doc = "function(name:string):string; Sets the name of the chat box. Returns the new name", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setName(Context context, Arguments args) {
		this.name = args.checkString(0);
		return new Object[] { this.name };
	}

	@Override
	public void readFromNBT(final NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if(nbt.hasKey("d")) {
			this.distance = nbt.getShort("d");
		}
		if(nbt.hasKey("n")) {
			this.name = nbt.getString("n");
		}
	}

	@Override
	public void writeToNBT(final NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setShort("d", (short) this.distance);
		if(name.length() > 0) {
			nbt.setString("n", this.name);
		}
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String[] getMethodNames() {
		return new String[] { "say", "getDistance", "setDistance", "getName", "setName" };
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
		int method, Object[] arguments) throws LuaException,
		InterruptedException {
		switch(method) {
			case 0: { // say
				if(arguments.length >= 1 && arguments[0] instanceof String) {
					int d = distance;
					if(arguments.length >= 2 && arguments[1] instanceof Double) {
						d = isCreative() ? ((Double) arguments[1]).intValue() : Math.min(Config.CHATBOX_DISTANCE, ((Double) arguments[1]).intValue());
						if(d <= 0) {
							d = distance;
						}
					}
					ChatBoxUtils.sendChatMessage(this, d, name.length() > 0 ? name : Config.CHATBOX_PREFIX, ((String) arguments[0]));
					return new Object[] { true };
				}
				return new Object[] { false };
			}
			case 1: { // getDistance
				return new Object[] { distance };
			}
			case 2: { // setDistance
				if(arguments.length == 1 && arguments[0] instanceof Double) {
					setDistance(((Double) arguments[0]).intValue());
					return new Object[] { distance };
				}
				throw new LuaException("first argument needs to be a number");
			}
			case 3: { // getName
				return new Object[] { name };
			}
			case 4: { // setName
				if(arguments.length == 1 && arguments[0] instanceof String) {
					this.name = (String) arguments[0];
					return new Object[] { this.name };
				}
				throw new LuaException("first argument needs to be a string");
			}
		}
		return null;
	}

	@Override
	@Optional.Method(modid = Mods.NedoComputers)
	public short busRead(int addr) {
		return 0;
	}

	@Override
	@Optional.Method(modid = Mods.NedoComputers)
	public void busWrite(int addr, short data) {
		switch((addr & 0xFFFE)) {
			case 0:
				if(data > 0) {
					distance = data;
				}
				break;
		}
	}
}

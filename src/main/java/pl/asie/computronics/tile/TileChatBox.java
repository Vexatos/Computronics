package pl.asie.computronics.tile;

import java.util.HashSet;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
//import dan200.computer.api.IComputerAccess;
//import dan200.computer.api.ILuaContext;
//import dan200.computer.api.IPeripheral;
import li.cil.oc.api.Network;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.util.ChatBoxUtils;
import pl.asie.lib.block.TileEntityBase;
import pl.asie.lib.util.ChatUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.ServerChatEvent;

@Optional.InterfaceList({
	@Optional.Interface(iface = "li.cil.li.oc.network.SimpleComponent", modid = "OpenComputers"),
	//@Optional.Interface(iface = "dan200.computer.api.IPeripheral", modid = "ComputerCraft")
})
public class TileChatBox extends TileEntityBase implements Environment /*, IPeripheral*/ {
	private int distance;
	
	public TileChatBox() {
		distance = Computronics.CHATBOX_DISTANCE;
		if(Loader.isModLoaded("OpenComputers")) {
			initOC();
		}
	}
	
	@Optional.Method(modid="OpenComputers")
	public void initOC() {
		node = Network.newNode(this, Visibility.Network).withComponent("chat_box", Visibility.Network).create();
	}
	
	public boolean isCreative() {
		if(!Computronics.CHATBOX_CREATIVE || worldObj == null) return false;
		else return worldObj.getBlockMetadata(xCoord, yCoord, zCoord) >= 8;
	}
	
	public int getDistance() { return distance; }
	
	public void setDistance(int dist) {
		if(dist > 32767) dist = 32767;
		
		this.distance = Math.min(Computronics.CHATBOX_DISTANCE, dist);
		if(this.distance < 0) this.distance = Computronics.CHATBOX_DISTANCE;
	}
	
	public void receiveChatMessage(ServerChatEvent event) {
		if(Loader.isModLoaded("OpenComputers")) eventOC(event);
		//if(Loader.isModLoaded("ComputerCraft")) eventCC(event);
	}
	
	@Optional.Method(modid="OpenComputers")
	public void eventOC(ServerChatEvent event) {
		node.sendToReachable("computer.signal", "chat_message", event.username, event.message);
	}
	
	/*@Optional.Method(modid="ComputerCraft")
	public void eventCC(ServerChatEvent event) {
		for(IComputerAccess computer: ccComputers) {
			computer.queueEvent("chat_message", new Object[]{event.username, event.message});
		}
	}*/
	// OpenComputers API
	
	@Callback(direct = true, limit = 3)
	@Optional.Method(modid="OpenComputers")
	public Object[] say(Context context, Arguments args) {
		if(args.count() >= 1) {
			if(args.isString(0)) ChatBoxUtils.sendChatMessage(this, distance, "ChatBox", args.checkString(0));
		}
		return null;
	}
	
	@Callback(direct = true)
	@Optional.Method(modid="OpenComputers")
	public Object[] getDistance(Context context, Arguments args) {
		return new Object[]{distance};
	}
	
	@Callback(direct = true)
	@Optional.Method(modid="OpenComputers")
	public Object[] setDistance(Context context, Arguments args) {
		if(args.count() >= 1) {
			if(args.isInteger(0)) setDistance(args.checkInteger(0));
		}
		return null;
	}
	/*
	// ComputerCraft API

	@Override
	@Optional.Method(modid="ComputerCraft")
	public String getType() {
		return "chat_box";
	}

	@Override
	@Optional.Method(modid="ComputerCraft")
	public String[] getMethodNames() {
		return new String[]{"say", "getDistance", "setDistance"};
	}

	@Override
	@Optional.Method(modid="ComputerCraft")
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws Exception {
		if(method == 0) {
			if(arguments.length >= 1 && arguments[0] instanceof String) {
				this.sendChatMessage((String)arguments[0]);
			}
		} else if(method == 1) {
			return new Object[]{distance};
		} else if(method == 2) {
			if(arguments.length >= 1 && arguments[0] instanceof Integer) {
				this.setDistance(((Integer)arguments[0]).intValue());
			}
		}
		return null;
	}

	@Override
	@Optional.Method(modid="ComputerCraft")
	public boolean canAttachToSide(int side) {
		return true;
	}
	
	private HashSet<IComputerAccess> ccComputers;

	@Override
	@Optional.Method(modid="ComputerCraft")
	public void attach(IComputerAccess computer) {
		if(ccComputers == null) ccComputers = new HashSet<IComputerAccess>();
		ccComputers.add(computer);
	}

	@Override
	@Optional.Method(modid="ComputerCraft")
	public void detach(IComputerAccess computer) {
		if(ccComputers == null) ccComputers = new HashSet<IComputerAccess>();
		ccComputers.remove(computer);
	}
	*/

	// OpenComputers Environment boilerplate
	// From TileEntityEnvironment
	
    protected Node node;
    protected boolean addedToNetwork = false;
    
    @Override
	@Optional.Method(modid="OpenComputers")
    public Node node() {
        return node;
    }

    @Override
	@Optional.Method(modid="OpenComputers")
    public void onConnect(final Node node) {
    }

    @Override
	@Optional.Method(modid="OpenComputers")
    public void onDisconnect(final Node node) {
    }

    @Override
	@Optional.Method(modid="OpenComputers")
    public void onMessage(final Message message) {
    }

    @Override
	@Optional.Method(modid="OpenComputers")
    public void updateEntity() {
        super.updateEntity();
        if (!addedToNetwork) {
            addedToNetwork = true;
            Network.joinOrCreateNetwork(this);
        }
    }

    @Override
	@Optional.Method(modid="OpenComputers")
    public void onChunkUnload() {
        super.onChunkUnload();
        if (node != null) node.remove();
    }

    @Override
	@Optional.Method(modid="OpenComputers")
    public void invalidate() {
        super.invalidate();
        if (node != null) node.remove();
    }

    @Override
	@Optional.Method(modid="OpenComputers")
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if(nbt.hasKey("d")) this.distance = nbt.getShort("d");
        if (node != null && node.host() == this) {
            node.load(nbt.getCompoundTag("oc:node"));
        }
    }

    @Override
	@Optional.Method(modid="OpenComputers")
    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setShort("d", (short)this.distance);
        if (node != null && node.host() == this) {
            final NBTTagCompound nodeNbt = new NBTTagCompound();
            node.save(nodeNbt);
            nbt.setTag("oc:node", nodeNbt);
        }
    }
}

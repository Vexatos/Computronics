package pl.asie.computronics.tile;

import java.util.HashSet;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;
import dan200.computer.api.IPeripheral;
import li.cil.oc.api.Network;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import pl.asie.computronics.Computronics;
import pl.asie.lib.block.TileEntityBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.ServerChatEvent;

public class TileChatBox extends TileEntityBase implements Environment, IPeripheral {
	public TileChatBox() {
		node = Network.newNode(this, Visibility.Network).withComponent("chat_box", Visibility.Neighbors).create();
	}
	
	public void sendChatMessage(String string) {
		ChatMessageComponent chat = new ChatMessageComponent();
		chat.setColor(EnumChatFormatting.GRAY);
		chat.setItalic(true);
		chat.addText(EnumChatFormatting.ITALIC + "[ChatBox] ");
		chat.addText(EnumChatFormatting.RESET + "" + EnumChatFormatting.GRAY + string);
		for(Object o: this.worldObj.playerEntities) {
			if(!(o instanceof EntityPlayer)) continue;
			EntityPlayer player = (EntityPlayer)o;
			if(player.getDistance(this.xCoord, this.yCoord, this.zCoord) < Computronics.CHATBOX_DISTANCE) {
				player.sendChatToPlayer(chat);
			}
		}
	}
	
	public void receiveChatMessage(ServerChatEvent event) {
		// Send OC event
		node.sendToReachable("computer.signal", "chat_message", event.username, event.message);
		
		// Send CC event
		for(IComputerAccess computer: ccComputers) {
			computer.queueEvent("chat_message", new Object[]{event.username, event.message});
		}
	}
	
	// OpenComputers API
	
	@Callback(direct = true, limit = 3)
	public Object[] say(Context context, Arguments args) {
		if(args.count() >= 1) {
			if(args.isString(0)) sendChatMessage(args.checkString(0));
		}
		return null;
	}
	
	// ComputerCraft API

	@Override
	public String getType() {
		return "chat_box";
	}

	@Override
	public String[] getMethodNames() {
		return new String[]{"say"};
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws Exception {
		if(method == 0) {
			if(arguments.length >= 1 && arguments[0] instanceof String) {
				this.sendChatMessage((String)arguments[0]);
			}
		}
		return null;
	}

	@Override
	public boolean canAttachToSide(int side) {
		return true;
	}
	
	private final HashSet<IComputerAccess> ccComputers = new HashSet<IComputerAccess>();

	@Override
	public void attach(IComputerAccess computer) {
		ccComputers.add(computer);
	}

	@Override
	public void detach(IComputerAccess computer) {
		ccComputers.remove(computer);
	}

	// OpenComputers Environment boilerplate
	// From TileEntityEnvironment
	
    protected Node node;
    protected boolean addedToNetwork = false;
    
    @Override
    public Node node() {
        return node;
    }

    @Override
    public void onConnect(final Node node) {
    }

    @Override
    public void onDisconnect(final Node node) {
    }

    @Override
    public void onMessage(final Message message) {
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (!addedToNetwork) {
            addedToNetwork = true;
            Network.joinOrCreateNetwork(this);
        }
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        if (node != null) node.remove();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (node != null) node.remove();
    }

    @Override
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (node != null && node.host() == this) {
            node.load(nbt.getCompoundTag("oc:node"));
        }
    }

    @Override
    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if (node != null && node.host() == this) {
            final NBTTagCompound nodeNbt = new NBTTagCompound();
            node.save(nodeNbt);
            nbt.setCompoundTag("oc:node", nodeNbt);
        }
    }
}

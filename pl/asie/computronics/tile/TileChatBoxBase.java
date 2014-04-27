package pl.asie.computronics.tile;

import java.util.HashSet;

import cpw.mods.fml.common.Loader;
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
import pl.asie.lib.util.ChatUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.ServerChatEvent;

public abstract class TileChatBoxBase extends TileEntityBase implements Environment {
	protected int distance;

	public TileChatBoxBase() {
		if(Loader.isModLoaded("OpenComputers"))
			node = Network.newNode(this, Visibility.Network).withComponent("chat_box", Visibility.Neighbors).create();
	}
	
	public boolean isCreative() {
		if(!Computronics.CHATBOX_CREATIVE || worldObj == null) return false;
		else return worldObj.getBlockMetadata(xCoord, yCoord, zCoord) >= 8;
	}

	public int getDistance() { return distance; }

	public void setDistance(int dist) {
		if(dist > 32767) dist = 32767;

		if(isCreative()) { this.distance = dist; return; } else {
			this.distance = Math.min(Computronics.CHATBOX_DISTANCE, dist);
			if(this.distance < 0) this.distance = Computronics.CHATBOX_DISTANCE;
		}
	}

	public void sendChatMessage(String string) {
		ChatMessageComponent chat = new ChatMessageComponent();
		chat.setColor(EnumChatFormatting.GRAY);
		chat.setItalic(true);
		chat.addText(EnumChatFormatting.ITALIC + Computronics.CHATBOX_PREFIX + " ");
		chat.addText(EnumChatFormatting.RESET + "" + EnumChatFormatting.GRAY + ChatUtils.color(string));
		
		for(Object o: this.worldObj.playerEntities) {
			if(!(o instanceof EntityPlayer)) continue;
			EntityPlayer player = (EntityPlayer)o;
			if(player.getDistance(this.xCoord, this.yCoord, this.zCoord) < Computronics.CHATBOX_DISTANCE) {
				player.sendChatToPlayer(chat);
			}
		}
	}

	public abstract void receiveChatMessageCC(ServerChatEvent event);

	public void receiveChatMessage(ServerChatEvent event) {
		// Send OC event
		if(node != null)
			node.sendToReachable("computer.signal", "chat_message", event.username, event.message);

		receiveChatMessageCC(event);
	}

	// OpenComputers API

	@Callback(direct = true, limit = 3)
	public Object[] say(Context context, Arguments args) {
		if(args.count() >= 1) {
			if(args.isString(0)) sendChatMessage(args.checkString(0));
		}
		return null;
	}

	@Callback(direct = true)
	public Object[] getDistance(Context context, Arguments args) {
		return new Object[]{distance};
	}

	@Callback(direct = true)
	public Object[] setDistance(Context context, Arguments args) {
		if(args.count() >= 1) {
			if(args.isInteger(0)) setDistance(args.checkInteger(0));
		}
		return null;
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
		
		if(nbt.hasKey("d")) this.distance = nbt.getShort("d");
		else setDistance(32767);
		
		if (node != null && node.host() == this) {
			node.load(nbt.getCompoundTag("oc:node"));
		}
	}

	@Override
	public void writeToNBT(final NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setShort("d", (short)this.distance);
		if (node != null && node.host() == this) {
			final NBTTagCompound nodeNbt = new NBTTagCompound();
			node.save(nodeNbt);
			nbt.setCompoundTag("oc:node", nodeNbt);
		}
	}
}

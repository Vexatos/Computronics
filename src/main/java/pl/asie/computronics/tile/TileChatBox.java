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
	@Optional.Interface(iface = "li.cil.li.oc.network.Environment", modid = "OpenComputers"),
	//@Optional.Interface(iface = "dan200.computer.api.IPeripheral", modid = "ComputerCraft")
})
public class TileChatBox extends TileEntityPeripheralBase implements Environment /*, IPeripheral*/ {
	private int distance;
	
	public TileChatBox() {
		super("chat_box");
		distance = Computronics.CHATBOX_DISTANCE;
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
			if(args.isString(0)) ChatBoxUtils.sendChatMessage(this, distance, Computronics.CHATBOX_PREFIX, args.checkString(0));
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

    @Override
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if(nbt.hasKey("d")) this.distance = nbt.getShort("d");
    }

    @Override
    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setShort("d", (short)this.distance);
    }
}

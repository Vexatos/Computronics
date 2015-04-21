package pl.asie.computronics.tile;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import li.cil.oc.api.Network;
import li.cil.oc.api.network.BlacklistedPeripheral;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.SidedEnvironment;
import li.cil.oc.api.network.Visibility;
import mods.railcraft.api.carts.CartTools;
import mods.railcraft.common.carts.EntityLocomotive;
import mods.railcraft.common.carts.EnumCart;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;

import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheral;
import pl.asie.computronics.cc.ISidedPeripheral;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.reference.Names;
import pl.asie.computronics.util.internal.IComputronicsPeripheral;
import pl.asie.lib.block.TileEntityBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.mojang.authlib.GameProfile;

/**
 * @author CovertJaguar, Vexatos, marcin212, Kubuxu
 */
@Optional.InterfaceList({
	@Optional.Interface(iface = "li.cil.oc.api.network.Environment", modid = Mods.OpenComputers),
	@Optional.Interface(iface = "li.cil.oc.api.network.SidedEnvironment", modid = Mods.OpenComputers),
	@Optional.Interface(iface = "li.cil.oc.api.network.BlacklistedPeripheral", modid = Mods.OpenComputers),
	@Optional.Interface(iface = "pl.asie.computronics.api.multiperipheral.IMultiPeripheral", modid = Mods.ComputerCraft)
})
public class TileDigitalDetector extends TileEntityBase
	implements Environment, SidedEnvironment, IMultiPeripheral, IComputronicsPeripheral, ISidedPeripheral, BlacklistedPeripheral {
	
	public ForgeDirection direction;
	private boolean tested;
	private List<EntityMinecart> currentCarts = new ArrayList<EntityMinecart>();

	@Override
	public void updateEntity() {
		if(getWorldObj().isRemote) {
			return;
		}
		if(!this.tested) {
			this.tested = true;
			int meta = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
			if(meta != 0) {
				this.worldObj.removeTileEntity(this.xCoord, this.yCoord, this.yCoord);
				Block block = Computronics.railcraft.detector;
				if(block != null) {
					this.worldObj.setBlock(this.xCoord, this.yCoord, this.yCoord, block, 0, 3);
				}
			}
		}

		List<EntityMinecart> carts = CartTools.getMinecartsOnAllSides(this.worldObj, this.xCoord, this.yCoord, this.zCoord, 0.2F);
		
		for(EntityMinecart cart : carts){
			if (!this.currentCarts.contains(cart)) {
	        	ArrayList<Object> info = new ArrayList<Object>();
    			appendCartType(info, cart);
    			appendLocomotiveInformation(info, cart);
				if(Loader.isModLoaded(Mods.OpenComputers) && this.node() != null) {
						this.eventOC(info);
				}
				if(Loader.isModLoaded(Mods.ComputerCraft)) {
					this.eventCC(info);
				}
	        }
	    }
		
		currentCarts = carts;
		

		if(!addedToNetwork && Loader.isModLoaded(Mods.OpenComputers)) {
			addedToNetwork = true;
			Network.joinOrCreateNetwork(this);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		if(Loader.isModLoaded(Mods.OpenComputers)) {
			writeToNBT_OC(data);
		}
		data.setByte("direction", (byte) direction.ordinal());
	}

	
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		if(Loader.isModLoaded(Mods.OpenComputers)) {
			readFromNBT_OC(data);
		}
		direction = data.hasKey("direction") ? ForgeDirection.getOrientation(data.getByte("direction")) : ForgeDirection.UNKNOWN;
	}
	
	@Override
	public void writeToRemoteNBT(NBTTagCompound tag) {
		tag.setByte("direction", (byte) direction.ordinal());
	}
	
	@Override
	public void readFromRemoteNBT(NBTTagCompound tag) {
		direction = tag.hasKey("direction") ? ForgeDirection.getOrientation(tag.getByte("direction")) : ForgeDirection.UNKNOWN;
	}

	private void appendLocomotiveInformation(ArrayList<Object> info, EntityMinecart cart){
		if(cart instanceof EntityLocomotive){
			EntityLocomotive locomotive = (EntityLocomotive) cart;
			GameProfile owner = locomotive.getOwner();
			info.add(locomotive.getPrimaryColor());
			info.add(locomotive.getSecondaryColor());
			info.add(locomotive.getDestination());
			info.add(owner!=null?owner.getName():"");
		}
	}

	private void appendCartType(ArrayList<Object> info, EntityMinecart cart){
		EnumCart type = EnumCart.fromCart(cart);
		info.add(type != null ? type.name().toLowerCase(Locale.ENGLISH) : "unknow");
		String entityName = cart.func_95999_t();
		info.add(entityName != null ? entityName : "");
	}

	// Computer Stuff //

	protected String peripheralName;
	protected Node node;
	protected ArrayList<IComputerAccess> attachedComputersCC;
	protected boolean addedToNetwork = false;

	public TileDigitalDetector() {
		this(Names.Railcraft_DigitalDetector);
	}

	public TileDigitalDetector(String name) {
		super();
		this.peripheralName = name;
		this.direction = ForgeDirection.UNKNOWN;
		if(Loader.isModLoaded(Mods.OpenComputers)) {
			initOC();
		}
	}

	public TileDigitalDetector(String name, double bufferSize) {
		super();
		this.peripheralName = name;
		this.direction = ForgeDirection.UNKNOWN;
		if(Loader.isModLoaded(Mods.OpenComputers)) {
			initOC(bufferSize);
		}
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public void eventOC(ArrayList<Object> info) {
		ArrayList<Object> extendedInfo = new ArrayList<Object>();
		extendedInfo.add("minecart");
		extendedInfo.addAll(info);
		node().sendToReachable("computer.signal", extendedInfo.toArray());
	}

	@Optional.Method(modid = Mods.ComputerCraft)
	public void eventCC(ArrayList<Object> info) {
		if(attachedComputersCC != null) {
			for(IComputerAccess computer : attachedComputersCC) {
				ArrayList<Object> extendedInfo = new ArrayList<Object>();
				extendedInfo.add(computer.getAttachmentName());
				extendedInfo.addAll(info);
				computer.queueEvent("minecart", info.toArray());
			}
		}
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Node node() {
		return node;
	}

	@Override
	public Node sidedNode(ForgeDirection side) {
		return side == this.direction ? node : null;
	}

	@Override
	public boolean canConnect(ForgeDirection side) {
		return side == this.direction;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public void onConnect(Node node) {

	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public void onDisconnect(Node node) {

	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public void onMessage(Message message) {

	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public boolean isPeripheralBlacklisted() {
		return true;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public void onChunkUnload() {
		super.onChunkUnload();
		if(node != null) {
			node.remove();
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if(Loader.isModLoaded(Mods.OpenComputers) && node != null) {
			node.remove();
		}
	}

	@Optional.Method(modid = Mods.OpenComputers)
	private void initOC(double s) {
		node = Network.newNode(this, Visibility.Network).withComponent(this.peripheralName, Visibility.Network).withConnector(s).create();
	}

	@Optional.Method(modid = Mods.OpenComputers)
	private void initOC() {
		node = Network.newNode(this, Visibility.Network).withComponent(this.peripheralName, Visibility.Network).create();
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public void readFromNBT_OC(final NBTTagCompound nbt) {
		if(node != null && node.host() == this) {
			node.load(nbt.getCompoundTag("oc:node"));
		}
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public void writeToNBT_OC(final NBTTagCompound nbt) {
		if(node != null && node.host() == this) {
			final NBTTagCompound nodeNbt = new NBTTagCompound();
			node.save(nodeNbt);
			nbt.setTag("oc:node", nodeNbt);
		}
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String getType() {
		return peripheralName;
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String[] getMethodNames() {
		return new String[] { };
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		return null;
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public void attach(IComputerAccess computer) {
		if(attachedComputersCC == null) {
			attachedComputersCC = new ArrayList<IComputerAccess>(2);
		}
		attachedComputersCC.add(computer);
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public void detach(IComputerAccess computer) {
		if(attachedComputersCC != null) {
			attachedComputersCC.remove(computer);
		}
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public boolean equals(IPeripheral other) {
		if(other == null) {
			return false;
		}
		if(this == other) {
			return true;
		}
		if(other instanceof TileEntity) {
			TileEntity tother = (TileEntity) other;
			if(!tother.getWorldObj().equals(worldObj)) {
				return false;
			}
			if(tother.xCoord != this.xCoord || tother.yCoord != this.yCoord || tother.zCoord != this.zCoord) {
				return false;
			}
		}

		return true;
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public int peripheralPriority() {
		return 1;
	}

	@Override
	public boolean canConnectPeripheralOnSide(int side) {
		return ForgeDirection.getOrientation(side) == this.direction;
	}
}

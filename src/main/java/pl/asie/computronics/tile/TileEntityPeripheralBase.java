package pl.asie.computronics.tile;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import li.cil.oc.api.Network;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import nedocomputers.INedoPeripheral;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import pl.asie.computronics.cc.IComputronicsPeripheral;
import pl.asie.computronics.reference.Mods;
import pl.asie.lib.tile.TileMachine;

import java.util.ArrayList;

// #######################################################
//
// REMEMBER TO SYNC ME WITH TILEENTITYPERIPHERALINVENTORY!
//
// #######################################################

@Optional.InterfaceList({
	@Optional.Interface(iface = "li.cil.oc.api.network.Environment", modid = Mods.OpenComputers),
	@Optional.Interface(iface = "pl.asie.computronics.cc.IComputronicsPeripheral", modid = Mods.ComputerCraft),
	@Optional.Interface(iface = "nedocomputers.INedoPeripheral", modid = Mods.NedoComputers)
})
public abstract class TileEntityPeripheralBase extends TileMachine implements Environment, IComputronicsPeripheral, INedoPeripheral {
	protected String peripheralName;

	public TileEntityPeripheralBase(String name) {
		super();
		this.peripheralName = name;
		if(Loader.isModLoaded(Mods.OpenComputers)) {
			initOC();
		}
	}

	public TileEntityPeripheralBase(String name, double bufferSize) {
		super();
		this.peripheralName = name;
		if(Loader.isModLoaded(Mods.OpenComputers)) {
			initOC(bufferSize);
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

	// OpenComputers Environment boilerplate
	// From TileEntityEnvironment

	protected Node node;
	protected ArrayList<IComputerAccess> attachedComputersCC;
	protected boolean addedToNetwork = false;

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Node node() {
		return node;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public void onConnect(final Node node) {
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public void onDisconnect(final Node node) {
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public void onMessage(final Message message) {
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public void updateEntity() {
		super.updateEntity();
		if(!addedToNetwork) {
			addedToNetwork = true;
			Network.joinOrCreateNetwork(this);
			this.onOCNetworkCreation();
		}
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public void onOCNetworkCreation() {

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
	@Optional.Method(modid = Mods.OpenComputers)
	public void invalidate() {
		super.invalidate();
		if(node != null) {
			node.remove();
		}
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
			return tother.getWorldObj().equals(worldObj)
				&& tother.xCoord == this.xCoord && tother.yCoord == this.yCoord && tother.zCoord == this.zCoord;
		}

		return false;
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public int peripheralPriority() {
		return 1;
	}

	@Override
	@Optional.Method(modid = Mods.NedoComputers)
	public boolean Connectable(int side) {
		return true;
	}

	protected int nedoBusID = 0;

	@Override
	@Optional.Method(modid = Mods.NedoComputers)
	public int getBusId() {
		return nedoBusID;
	}

	@Override
	@Optional.Method(modid = Mods.NedoComputers)
	public void setBusId(int id) {
		nedoBusID = id;
	}

	@Optional.Method(modid = Mods.NedoComputers)
	public void readFromNBT_NC(final NBTTagCompound nbt) {
		if(nbt.hasKey("nc:bus")) {
			nedoBusID = nbt.getShort("nc:bus");
		}
	}

	@Optional.Method(modid = Mods.NedoComputers)
	public void writeToNBT_NC(final NBTTagCompound nbt) {
		if(nedoBusID != 0) {
			nbt.setShort("nc:bus", (short) nedoBusID);
		}
	}

	@Override
	public void readFromNBT(final NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if(Loader.isModLoaded(Mods.OpenComputers)) {
			readFromNBT_OC(nbt);
		}
		if(Loader.isModLoaded(Mods.NedoComputers)) {
			readFromNBT_NC(nbt);
		}
	}

	@Override
	public void writeToNBT(final NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if(Loader.isModLoaded(Mods.OpenComputers)) {
			writeToNBT_OC(nbt);
		}
		if(Loader.isModLoaded(Mods.NedoComputers)) {
			writeToNBT_NC(nbt);
		}
	}

	@Override
	public void writeToRemoteNBT(NBTTagCompound nbt) {
		super.writeToRemoteNBT(nbt);
		nbt.setInteger("x", this.xCoord);
		nbt.setInteger("y", this.yCoord);
		nbt.setInteger("z", this.zCoord);
		if(Loader.isModLoaded(Mods.OpenComputers)) {
			writeToNBT_OC(nbt);
		}
		if(Loader.isModLoaded(Mods.NedoComputers)) {
			writeToNBT_NC(nbt);
		}
	}
}

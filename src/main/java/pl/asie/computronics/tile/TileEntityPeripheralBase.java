package pl.asie.computronics.tile;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import li.cil.oc.api.Network;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import pl.asie.lib.block.TileEntityBase;

// #######################################################
//
// REMEMBER TO SYNC ME WITH TILEENTITYPERIPHERALINVENTORY!
//
// #######################################################

@Optional.InterfaceList({
	@Optional.Interface(iface = "li.cil.li.oc.network.Environment", modid = "OpenComputers"),
	@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = "ComputerCraft")
})
public abstract class TileEntityPeripheralBase extends TileEntityBase implements Environment, IPeripheral {
	protected String peripheralName;
	
	public TileEntityPeripheralBase(String name) {
		super();
		this.peripheralName = name;
		if(Loader.isModLoaded("OpenComputers")) {
			initOC();
		}
	}
	
	public TileEntityPeripheralBase(String name, double bufferSize) {
		super();
		this.peripheralName = name;
		if(Loader.isModLoaded("OpenComputers")) {
			initOC(bufferSize);
		}
	}
	
	@Optional.Method(modid="OpenComputers")
	private void initOC(double s) {
		node = Network.newNode(this, Visibility.Network).withComponent(this.peripheralName, Visibility.Network).withConnector(s).create();
	}
	
	@Optional.Method(modid="OpenComputers")
	private void initOC() {
		node = Network.newNode(this, Visibility.Network).withComponent(this.peripheralName, Visibility.Network).create();
	}
	
	// OpenComputers Environment boilerplate
	// From TileEntityEnvironment
	
    protected Node node;
    protected ArrayList<IComputerAccess> attachedComputersCC;
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
        if (node != null && node.host() == this) {
            node.load(nbt.getCompoundTag("oc:node"));
        }
    }

    @Override
	@Optional.Method(modid="OpenComputers")
    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if (node != null && node.host() == this) {
            final NBTTagCompound nodeNbt = new NBTTagCompound();
            node.save(nodeNbt);
            nbt.setTag("oc:node", nodeNbt);
        }
    }

	@Override
	@Optional.Method(modid="ComputerCraft")
	public String getType() {
		return peripheralName;
	}

	@Override
	@Optional.Method(modid="ComputerCraft")
	public void attach(IComputerAccess computer) {
		if(attachedComputersCC == null) attachedComputersCC = new ArrayList<IComputerAccess>(2);
		attachedComputersCC.add(computer);
	}

	@Override
	@Optional.Method(modid="ComputerCraft")
	public void detach(IComputerAccess computer) {
		if(attachedComputersCC != null) attachedComputersCC.remove(computer);
	}

	@Override
	@Optional.Method(modid="ComputerCraft")
	public boolean equals(IPeripheral other) {
		return ((other != null) && other.equals(this));
	}
}

package pl.asie.computronics.tile;

import net.minecraft.nbt.NBTTagCompound;
import li.cil.oc.api.Network;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import pl.asie.lib.block.TileEntityBase;

@Optional.InterfaceList({
	@Optional.Interface(iface = "li.cil.li.oc.network.Environment", modid = "OpenComputers")
})
public abstract class TileEntityPeripheralBase extends TileEntityBase implements Environment {
	protected String peripheralName;
	
	public TileEntityPeripheralBase(String name) {
		this.peripheralName = name;
		if(Loader.isModLoaded("OpenComputers")) {
			initOC();
		}
	}
	
	public TileEntityPeripheralBase(String name, double bufferSize) {
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
}

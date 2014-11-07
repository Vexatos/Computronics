package pl.asie.computronics.tile;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import li.cil.oc.api.Network;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.SidedEnvironment;
import li.cil.oc.api.network.Visibility;
import mods.railcraft.common.blocks.detector.TileDetector;
import mods.railcraft.common.carts.EnumCart;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.cc.IComputronicsPeripheral;
import pl.asie.computronics.cc.ISidedPeripheral;
import pl.asie.computronics.integration.railcraft.DetectorDigital;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.reference.Names;

import java.util.ArrayList;
import java.util.Locale;

/**
 * @author CovertJaguar, Vexatos
 */
@Optional.InterfaceList({
	@Optional.Interface(iface = "li.cil.oc.api.network.Environment", modid = Mods.OpenComputers),
	@Optional.Interface(iface = "li.cil.oc.api.network.SidedEnvironment", modid = Mods.OpenComputers),
	@Optional.Interface(iface = "pl.asie.computronics.cc.IComputronicsPeripheral", modid = Mods.ComputerCraft)
})
public class TileDigitalDetector extends TileDetector
	implements Environment, SidedEnvironment, IComputronicsPeripheral, ISidedPeripheral {

	private boolean tested;
	public DetectorDigital detector;

	@Override
	public void updateEntity() {
		if(getWorld().isRemote) {
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
		int newPowerState = this.detector.testCarts(getCarts());
		if(newPowerState != this.powerState) {
			this.powerState = newPowerState;
			sendUpdateToClient();
			this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, Computronics.railcraft.detector);
			WorldPlugin.notifyBlocksOfNeighborChangeOnSide(this.worldObj, this.xCoord, this.yCoord, this.zCoord, Computronics.railcraft.detector, this.direction);
		}

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
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		if(Loader.isModLoaded(Mods.OpenComputers)) {
			readFromNBT_OC(data);
		}
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		NBTTagCompound tag = pkt.func_148857_g();
		if(tag != null) {
			readFromNBT(tag);
		}
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
		this.detector = new DetectorDigital();
		this.detector.setTile(this);
		this.peripheralName = name;
		this.direction = ForgeDirection.UNKNOWN;
		if(Loader.isModLoaded(Mods.OpenComputers)) {
			initOC();
		}
	}

	public TileDigitalDetector(String name, double bufferSize) {
		super();
		this.detector = new DetectorDigital();
		this.detector.setTile(this);
		this.peripheralName = name;
		this.direction = ForgeDirection.UNKNOWN;
		if(Loader.isModLoaded(Mods.OpenComputers)) {
			initOC(bufferSize);
		}
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public void eventOC(EntityMinecart cart, EnumCart type) {
		node().sendToReachable("computer.signal", "minecart",
			type != null ? type.name().toLowerCase(Locale.ENGLISH) : null,
			cart.func_95999_t());
	}

	@Optional.Method(modid = Mods.ComputerCraft)
	public void eventCC(EntityMinecart cart, EnumCart type) {
		if(attachedComputersCC != null) {
			for(IComputerAccess computer : attachedComputersCC) {
				computer.queueEvent("minecart", new Object[] {
					computer.getAttachmentName(),
					type != null ? type.name().toLowerCase(Locale.ENGLISH) : null,
					cart.func_95999_t()
				});
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

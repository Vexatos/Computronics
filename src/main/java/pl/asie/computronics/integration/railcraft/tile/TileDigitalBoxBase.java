package pl.asie.computronics.integration.railcraft.tile;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import li.cil.oc.api.Network;
import li.cil.oc.api.driver.DeviceInfo;
import li.cil.oc.api.network.BlacklistedPeripheral;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.SidedEnvironment;
import li.cil.oc.api.network.Visibility;
import mods.railcraft.common.blocks.signals.TileBoxBase;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheral;
import pl.asie.computronics.cc.ISidedPeripheral;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.OCUtils;
import pl.asie.computronics.util.internal.IComputronicsPeripheral;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author CovertJaguar, Vexatos
 */
@Optional.InterfaceList({
	@Optional.Interface(iface = "li.cil.oc.api.network.Environment", modid = Mods.OpenComputers),
	@Optional.Interface(iface = "li.cil.oc.api.network.SidedEnvironment", modid = Mods.OpenComputers),
	@Optional.Interface(iface = "li.cil.oc.api.driver.DeviceInfo", modid = Mods.OpenComputers),
	@Optional.Interface(iface = "li.cil.oc.api.network.BlacklistedPeripheral", modid = Mods.OpenComputers),
	@Optional.Interface(iface = "pl.asie.computronics.api.multiperipheral.IMultiPeripheral", modid = Mods.ComputerCraft)
})
public abstract class TileDigitalBoxBase extends TileBoxBase
	implements Environment, SidedEnvironment, DeviceInfo, IMultiPeripheral, IComputronicsPeripheral,
	ISidedPeripheral, BlacklistedPeripheral {

	public TileDigitalBoxBase(String name) {
		super();
		this.peripheralName = name;
		if(Mods.isLoaded(Mods.OpenComputers)) {
			initOC();
		}
	}

	public TileDigitalBoxBase(String name, double bufferSize) {
		super();
		this.peripheralName = name;
		if(Mods.isLoaded(Mods.OpenComputers)) {
			initOC(bufferSize);
		}
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(!addedToNetwork && Mods.isLoaded(Mods.OpenComputers)) {
			addedToNetwork = true;
			Network.joinOrCreateNetwork(this);
		}
	}

	@Override
	public Block getBlockType() {
		if(this.blockType == null) {
			this.blockType = this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord);
		}
		return this.blockType;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int i, int j, int k, ForgeDirection side) {
		return side == ForgeDirection.UP;
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		NBTTagCompound tag = pkt.func_148857_g();
		if(tag != null) {
			readFromNBT(tag);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		if(Mods.isLoaded(Mods.OpenComputers)) {
			writeToNBT_OC(data);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		if(Mods.isLoaded(Mods.OpenComputers)) {
			readFromNBT_OC(data);
		}
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if(Mods.isLoaded(Mods.OpenComputers)) {
			onChunkUnload_OC();
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if(Mods.isLoaded(Mods.OpenComputers)) {
			invalidate_OC();
		}
	}

	// Computer Stuff //

	protected String peripheralName;
	// Has to be an Object for getDeclaredFields to not error when
	// called on this class without OpenComputers being present. Blame OpenPeripheral.
	protected Object node;
	protected CopyOnWriteArrayList<IComputerAccess> attachedComputersCC;
	protected boolean addedToNetwork = false;

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public boolean isPeripheralBlacklisted() {
		return true;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Node node() {
		return (Node) node;
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

	protected Map<String, String> deviceInfo;

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Map<String, String> getDeviceInfo() {
		if(deviceInfo == null) {
			OCUtils.Device device = deviceInfo();
			if(device != null) {
				return deviceInfo = device.deviceInfo();
			}
		}
		return deviceInfo;
	}

	@Optional.Method(modid = Mods.OpenComputers)
	protected abstract OCUtils.Device deviceInfo();

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Node sidedNode(ForgeDirection forgeDirection) {
		return forgeDirection == ForgeDirection.DOWN || forgeDirection == ForgeDirection.UP ? node() : null;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	@SideOnly(Side.CLIENT)
	public boolean canConnect(ForgeDirection forgeDirection) {
		return forgeDirection == ForgeDirection.DOWN || forgeDirection == ForgeDirection.UP;
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String getType() {
		return peripheralName;
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
		if(node() != null && node().host() == this) {
			node().load(nbt.getCompoundTag("oc:node"));
		}
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public void writeToNBT_OC(final NBTTagCompound nbt) {
		if(node() != null && node().host() == this) {
			final NBTTagCompound nodeNbt = new NBTTagCompound();
			node().save(nodeNbt);
			nbt.setTag("oc:node", nodeNbt);
		}
	}

	@Optional.Method(modid = Mods.OpenComputers)
	protected void onChunkUnload_OC() {
		if(node() != null) {
			node().remove();
		}
	}

	@Optional.Method(modid = Mods.OpenComputers)
	protected void invalidate_OC() {
		if(node() != null) {
			node().remove();
		}
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public void attach(IComputerAccess computer) {
		if(attachedComputersCC == null) {
			attachedComputersCC = new CopyOnWriteArrayList<IComputerAccess>();
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
		return 5;
	}

	@Override
	public boolean canConnectPeripheralOnSide(int side) {
		ForgeDirection forgeDirection = ForgeDirection.getOrientation(side);
		return forgeDirection == ForgeDirection.DOWN || forgeDirection == ForgeDirection.UP;
	}
}

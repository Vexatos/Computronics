package pl.asie.computronics.tile;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.SidedEnvironment;
import li.cil.oc.api.network.Visibility;
import mods.railcraft.api.signals.IReceiverTile;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.api.signals.SignalController;
import mods.railcraft.api.signals.SimpleSignalReceiver;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.plugins.buildcraft.triggers.IAspectProvider;
import mods.railcraft.common.util.misc.AdjacentTileCache;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.reference.Mods;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;

/**
 * @author CovertJaguar, Vexatos
 */
@Optional.InterfaceList({
	@Optional.Interface(iface = "li.cil.oc.api.network.Environment", modid = Mods.OpenComputers),
	@Optional.Interface(iface = "li.cil.oc.api.network.SidedEnvironment", modid = Mods.OpenComputers),
	@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = Mods.ComputerCraft)
})
public class TileDigitalReceiverBox extends RailcraftTileEntity
	implements IReceiverTile, IAspectProvider, Environment, SidedEnvironment, IPeripheral {

	private boolean prevBlinkState;
	private final SimpleSignalReceiver receiver = new SimpleSignalReceiver(getName(), this);

	public boolean blockActivated(int side, EntityPlayer player) {
		return false;
	}

	public void updateEntity() {
		super.updateEntity();

		if(!addedToNetwork) {
			addedToNetwork = true;
			Network.joinOrCreateNetwork(this);
		}

		if(worldObj.isRemote) {
			this.receiver.tickClient();
			if((this.receiver.getAspect().isBlinkAspect()) && (this.prevBlinkState != SignalAspect.isBlinkOn())) {
				this.prevBlinkState = SignalAspect.isBlinkOn();
				markBlockForUpdate();
			}
			return;
		}
		this.receiver.tickServer();
		SignalAspect prevAspect = this.receiver.getAspect();
		if(this.receiver.isBeingPaired()) {
			this.receiver.setAspect(SignalAspect.BLINK_YELLOW);
		} else if(!this.receiver.isPaired()) {
			this.receiver.setAspect(SignalAspect.BLINK_RED);
		}
		if(prevAspect != this.receiver.getAspect()) {
			updateNeighbors();
			sendUpdateToClient();
		}
	}

	public void onControllerAspectChange(SignalController con, SignalAspect aspect) {
		if(Loader.isModLoaded(Mods.OpenComputers)) {
			eventOC(aspect);
		}
		if(Loader.isModLoaded(Mods.ComputerCraft)) {
			eventCC(aspect);
		}
		updateNeighbors();
		sendUpdateToClient();
	}

	private void updateNeighbors() {
		notifyBlocksOfNeighborChange();
		updateNeighborBoxes();
	}

	public int getPowerOutput(int side) {
		return 0;
	}

	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		if(Loader.isModLoaded("OpenComputers")) {
			writeToNBT_OC(data);
		}
		this.receiver.writeToNBT(data);
	}

	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		if(Loader.isModLoaded("OpenComputers")) {
			readFromNBT_OC(data);
		}
		this.receiver.readFromNBT(data);
	}

	public void writePacketData(DataOutputStream data)
		throws IOException {
		super.writePacketData(data);
		this.receiver.writePacketData(data);
	}

	public void readPacketData(DataInputStream data)
		throws IOException {
		super.readPacketData(data);
		this.receiver.readPacketData(data);
		markBlockForUpdate();
	}

	public boolean isConnected(ForgeDirection side) {
		TileEntity tile = this.tileCache.getTileOnSide(side);
		return (tile instanceof TileDigitalReceiverBox) && ((TileDigitalReceiverBox) tile).canReceiveAspect();
	}

	public boolean isEmitingRedstone(ForgeDirection side) {
		return false;
	}

	public boolean canEmitingRedstone(ForgeDirection side) {
		return false;
	}

	public SignalAspect getBoxSignalAspect(ForgeDirection side) {
		return this.receiver.getAspect();
	}

	public boolean canTransferAspect() {
		return true;
	}

	public SimpleSignalReceiver getReceiver() {
		return this.receiver;
	}

	public SignalAspect getTriggerAspect() {
		return getBoxSignalAspect(null);
	}

	public Block getBlockType() {
		if(this.blockType == null) {
			this.blockType = this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord);
		}

		return this.blockType;
	}

	public float getHardness() {
		return worldObj.getBlock(xCoord, yCoord, zCoord).getBlockHardness(worldObj, xCoord, yCoord, zCoord);
	}

	public short getId() {
		return (short) 30;
	}

	public String getName() {
		return StatCollector.translateToLocal("tile.computronics.signalBox.name");
	}

	public boolean canConnectRedstone(int dir) {
		return false;
	}

	private static final float BOUND = 0.1F;

	public void setBlockBoundsBasedOnState(IBlockAccess world, int i, int j, int k) {
		getBlockType().setBlockBounds(0.1F, 0.0F, 0.1F, 0.9F, 0.95F, 0.9F);
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
		return AxisAlignedBB.getBoundingBox(i + 0.1F, j, k + 0.1F, i + 1 - 0.1F, j + 1 - 0.05F, k + 1 - 0.1F);
	}

	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int i, int j, int k) {
		return AxisAlignedBB.getBoundingBox(i + 0.1F, j, k + 0.1F, i + 1 - 0.1F, j + 1 - 0.05F, k + 1 - 0.1F);
	}

	public boolean canUpdate() {
		return true;
	}

	public boolean canReceiveAspect() {
		return false;
	}

	public void onNeighborStateChange(TileDigitalReceiverBox neighbor, ForgeDirection side) {
	}

	public final void updateNeighborBoxes() {
		for(int side = 2; side < 6; side++) {
			ForgeDirection forgeSide = ForgeDirection.getOrientation(side);
			TileEntity tile = this.tileCache.getTileOnSide(forgeSide);
			if((tile instanceof TileDigitalReceiverBox)) {
				TileDigitalReceiverBox box = (TileDigitalReceiverBox) tile;
				box.onNeighborStateChange(this, forgeSide);
			}
		}
	}

	public boolean isSideSolid(IBlockAccess world, int i, int j, int k, ForgeDirection side) {
		return side == ForgeDirection.UP;
	}

	protected AdjacentTileCache tileCache = new AdjacentTileCache(this);

	public boolean rotateBlock(ForgeDirection axis) {
		return false;
	}

	public ForgeDirection[] getValidRotations() {
		return ForgeDirection.VALID_DIRECTIONS;
	}

	public void onBlockPlaced() {
	}

	public void onBlockRemoval() {
	}

	public void onNeighborBlockChange(Block block) {
		this.tileCache.onNeighborChange();
	}

	public void validate() {
		this.tileCache.purge();
		super.validate();
	}

	public boolean needsSupport() {
		return true;
	}

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

	public TileDigitalReceiverBox() {
		super();
	}

	public TileDigitalReceiverBox(String name) {
		super();
		this.peripheralName = name;
		if(Loader.isModLoaded(Mods.OpenComputers)) {
			initOC();
		}
	}

	public TileDigitalReceiverBox(String name, double bufferSize) {
		super();
		this.peripheralName = name;
		if(Loader.isModLoaded(Mods.OpenComputers)) {
			initOC(bufferSize);
		}
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public void eventOC(SignalAspect aspect) {
		if(node != null) {
			node.sendToReachable("computer.signal", "aspect_changed", aspect.ordinal());
		}
	}

	@Optional.Method(modid = Mods.ComputerCraft)
	public void eventCC(SignalAspect aspect) {
		for(IComputerAccess computer : attachedComputersCC) {
			computer.queueEvent("aspect_changed", new Object[] { aspect.ordinal() });
		}
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Node node() {
		return node;
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

	private Object[] getSignal() {
		return new Object[] { this.getTriggerAspect().ordinal() };
	}

	private static Object[] aspects() {
		LinkedHashMap<String, Integer> aspectMap = new LinkedHashMap<String, Integer>();
		for(SignalAspect aspect : SignalAspect.VALUES) {
			aspectMap.put(aspect.name().toLowerCase(Locale.ENGLISH), aspect.ordinal());
		}
		return new Object[] { aspectMap };
	}

	@Callback(doc = "function():number; Returns the currently received aspect that triggers the receiver box")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getSignal(Context context, Arguments args) {
		return getSignal();
	}

	@Callback(doc = "This is a list of every available Signal Aspect in Railcraft", getter = true)
	public Object[] aspects(Context c, Arguments a) {
		return aspects();
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
		this.tileCache.purge();
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

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Node sidedNode(ForgeDirection forgeDirection) {
		return forgeDirection == ForgeDirection.DOWN || forgeDirection == ForgeDirection.UP ? node : null;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	@SideOnly(Side.CLIENT)
	public boolean canConnect(ForgeDirection forgeDirection) {
		return forgeDirection == ForgeDirection.DOWN || forgeDirection == ForgeDirection.UP;
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public void readFromNBT_OC(final NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if(node != null && node.host() == this) {
			node.load(nbt.getCompoundTag("oc:node"));
		}
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public void writeToNBT_OC(final NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if(node != null && node.host() == this) {
			final NBTTagCompound nodeNbt = new NBTTagCompound();
			node.save(nodeNbt);
			nbt.setTag("oc:node", nodeNbt);
		}
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public String getType() {
		return peripheralName;
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String[] getMethodNames() {
		return new String[] { "getSignal", "aspects" };
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		if(method < getMethodNames().length) {
			switch(method){
				case 0:{
					return getSignal();
				}
				case 1:{
					return aspects();
				}
			}
		}
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
}

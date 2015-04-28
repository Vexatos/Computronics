package pl.asie.computronics.tile;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import li.cil.oc.api.Network;
import li.cil.oc.api.network.BlacklistedPeripheral;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import nedocomputers.api.INedoPeripheral;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheral;
import pl.asie.computronics.audio.MachineSound;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.internal.IComputronicsPeripheral;
import pl.asie.lib.tile.TileMachine;

import java.util.ArrayList;

// #######################################################
//
// REMEMBER TO SYNC ME WITH TILEENTITYPERIPHERALINVENTORY!
//
// #######################################################

@Optional.InterfaceList({
	@Optional.Interface(iface = "li.cil.oc.api.network.Environment", modid = Mods.OpenComputers),
	@Optional.Interface(iface = "li.cil.oc.api.network.BlacklistedPeripheral", modid = Mods.OpenComputers),
	@Optional.Interface(iface = "pl.asie.computronics.api.multiperipheral.IMultiPeripheral", modid = Mods.ComputerCraft),
	@Optional.Interface(iface = "nedocomputers.api.INedoPeripheral", modid = Mods.NedoComputers)
})
public abstract class TileEntityPeripheralBase extends TileMachine implements Environment,
	IMultiPeripheral, IComputronicsPeripheral, INedoPeripheral, BlacklistedPeripheral {
	protected String peripheralName;

	public TileEntityPeripheralBase(String name) {
		super();
		this.peripheralName = name;
		if(Mods.isLoaded(Mods.OpenComputers)) {
			initOC();
		}
		soundRes = getSoundFor(getSoundName());
	}

	public TileEntityPeripheralBase(String name, double bufferSize) {
		super();
		this.peripheralName = name;
		if(Mods.isLoaded(Mods.OpenComputers)) {
			initOC(bufferSize);
		}
		soundRes = getSoundFor(getSoundName());
	}

	public boolean isValid() {
		return !isInvalid();
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
	public boolean isPeripheralBlacklisted() {
		return true;
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
		if(worldObj.isRemote && hasSound()) {
			updateSound();
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
		if(worldObj.isRemote && hasSound()) {
			updateSound();
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
	public boolean connectable(int side) {
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
		if(Mods.isLoaded(Mods.OpenComputers)) {
			readFromNBT_OC(nbt);
		}
		if(Mods.isLoaded(Mods.NedoComputers)) {
			readFromNBT_NC(nbt);
		}
	}

	@Override
	public void writeToNBT(final NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if(Mods.isLoaded(Mods.OpenComputers)) {
			writeToNBT_OC(nbt);
		}
		if(Mods.isLoaded(Mods.NedoComputers)) {
			writeToNBT_NC(nbt);
		}
	}

	// Sound related, thanks to EnderIO code for this!

	@SideOnly(Side.CLIENT)
	private MachineSound sound;

	private ResourceLocation soundRes;

	protected static ResourceLocation getSoundFor(String sound) {
		return sound == null ? null : new ResourceLocation(Mods.Computronics + ":" + sound);
	}

	public String getSoundName() {
		return null;
	}

	public ResourceLocation getSoundRes() {
		return soundRes;
	}

	public boolean shouldPlaySound() {
		return false;
	}

	public boolean hasSound() {
		return getSoundName() != null;
	}

	public float getVolume() {
		return 1.0f;
	}

	public float getPitch() {
		return 1.0f;
	}

	public boolean shouldRepeat() {
		return true;
	}

	@SideOnly(Side.CLIENT)
	private void updateSound() {
		if(hasSound()) {
			if(shouldPlaySound() && !isInvalid()) {
				if(sound == null) {
					sound = new MachineSound(getSoundRes(), xCoord + 0.5f, yCoord + 0.5f, zCoord + 0.5f, getVolume(), getPitch(), shouldRepeat());
					FMLClientHandler.instance().getClient().getSoundHandler().playSound(sound);
				}
			} else if(sound != null) {
				sound.endPlaying();
				sound = null;
			}
		}
	}
}

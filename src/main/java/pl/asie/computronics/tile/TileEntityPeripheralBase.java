package pl.asie.computronics.tile;

import cpw.mods.fml.client.FMLClientHandler;
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
import li.cil.oc.api.network.Visibility;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheral;
import pl.asie.computronics.audio.MachineSound;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.OCUtils;
import pl.asie.computronics.util.internal.IComputronicsPeripheral;
import pl.asie.lib.tile.TileMachine;
import pl.asie.lib.util.ColorUtils;
import pl.asie.lib.util.internal.IColorable;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

// #######################################################
//
// REMEMBER TO SYNC ME WITH TILEENTITYPERIPHERALINVENTORY!
//
// #######################################################

@Optional.InterfaceList({
	@Optional.Interface(iface = "li.cil.oc.api.network.Environment", modid = Mods.OpenComputers),
	@Optional.Interface(iface = "li.cil.oc.api.driver.DeviceInfo", modid = Mods.OpenComputers),
	@Optional.Interface(iface = "li.cil.oc.api.network.BlacklistedPeripheral", modid = Mods.OpenComputers),
	@Optional.Interface(iface = "pl.asie.computronics.api.multiperipheral.IMultiPeripheral", modid = Mods.ComputerCraft)
})
public abstract class TileEntityPeripheralBase extends TileMachine implements Environment, DeviceInfo,
	IMultiPeripheral, IComputronicsPeripheral, BlacklistedPeripheral, IColorable {

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
	protected void initOC(double s) {
		setNode(Network.newNode(this, Visibility.Network).withComponent(this.peripheralName, Visibility.Network).withConnector(s).create());
	}

	@Optional.Method(modid = Mods.OpenComputers)
	protected void initOC() {
		setNode(Network.newNode(this, Visibility.Network).withComponent(this.peripheralName, Visibility.Network).create());
	}

	// OpenComputers Environment boilerplate
	// From TileEntityEnvironment

	// Has to be an Object for getDeclaredFields to not error when
	// called on this class without OpenComputers being present. Blame OpenPeripheral.
	private Object node;
	protected CopyOnWriteArrayList<IComputerAccess> attachedComputersCC;
	protected boolean addedToNetwork = false;

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Node node() {
		return (Node) node;
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public void setNode(final Node node) {
		this.node = node;
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
	public void updateEntity() {
		super.updateEntity();
		if(!addedToNetwork) {
			addedToNetwork = true;
			if(Mods.isLoaded(Mods.OpenComputers)) {
				addToNetwork_OC();
			}
		}
		if(worldObj.isRemote && hasSound()) {
			updateSound();
		}
	}

	@Optional.Method(modid = Mods.OpenComputers)
	protected void addToNetwork_OC() {
		Network.joinOrCreateNetwork(this);
		this.onOCNetworkCreation();
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public void onOCNetworkCreation() {

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
		if(worldObj.isRemote && hasSound()) {
			updateSound();
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

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String getType() {
		return peripheralName;
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

	protected int overlayColor = getDefaultColor();

	@Override
	public int getColor() {
		return overlayColor;
	}

	@Override
	public int getDefaultColor() {
		return ColorUtils.Color.White.color;
	}

	@Override
	public void setColor(int color) {
		this.overlayColor = color;
		this.markDirty();
	}

	@Override
	public boolean canBeColored() {
		return true;
	}

	@Override
	public void readFromRemoteNBT(NBTTagCompound nbt) {
		super.readFromRemoteNBT(nbt);
		int oldColor = this.overlayColor;
		if(nbt.hasKey("computronics:color")) {
			overlayColor = nbt.getInteger("computronics:color");
		}
		if(this.overlayColor < 0) {
			this.overlayColor = getDefaultColor();
		}
		if(oldColor != this.overlayColor) {
			this.worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
		}
	}

	@Override
	public void writeToRemoteNBT(NBTTagCompound nbt) {
		super.writeToRemoteNBT(nbt);
		if(overlayColor != getDefaultColor()) {
			nbt.setInteger("computronics:color", overlayColor);
		}
	}

	@Override
	public void readFromNBT(final NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if(nbt.hasKey("computronics:color")) {
			overlayColor = nbt.getInteger("computronics:color");
		}
		if(this.overlayColor < 0) {
			this.overlayColor = getDefaultColor();
		}
		if(Mods.isLoaded(Mods.OpenComputers)) {
			readFromNBT_OC(nbt);
		}
	}

	@Override
	public void writeToNBT(final NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if(overlayColor != getDefaultColor()) {
			nbt.setInteger("computronics:color", overlayColor);
		}
		if(Mods.isLoaded(Mods.OpenComputers)) {
			writeToNBT_OC(nbt);
		}
	}

	@Override
	public void removeFromNBTForTransfer(NBTTagCompound data) {
		super.removeFromNBTForTransfer(data);
		data.removeTag("oc:node");
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

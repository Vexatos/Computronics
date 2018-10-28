package pl.asie.computronics.integration.railcraft.tile;

import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.SidedEnvironment;
import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.common.carts.EntityLocomotive;
import mods.railcraft.common.carts.IRailcraftCartContainer;
import mods.railcraft.common.carts.RailcraftCarts;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.cc.ISidedPeripheral;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.reference.Names;
import pl.asie.computronics.tile.TileEntityPeripheralBase;
import pl.asie.computronics.util.OCUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author CovertJaguar, Vexatos, marcin212, Kubuxu
 */
@Optional.InterfaceList({
	@Optional.Interface(iface = "li.cil.oc.api.network.SidedEnvironment", modid = Mods.OpenComputers)
})
public class TileDigitalDetector extends TileEntityPeripheralBase
	implements SidedEnvironment, ISidedPeripheral, ITickable {

	public EnumFacing direction;
	private List<EntityMinecart> currentCarts = new ArrayList<EntityMinecart>();

	public TileDigitalDetector() {
		super(Names.Railcraft_DigitalDetector);
		this.direction = EnumFacing.UP;
	}

	@Override
	public void update() {
		super.update();
		if(getWorld().isRemote) {
			return;
		}

		List<EntityMinecart> carts = CartToolsAPI.getMinecartsOnAllSides(this.world, this.getPos(), 0.2F);

		for(EntityMinecart cart : carts) {
			if(!this.currentCarts.contains(cart)) {
				ArrayList<Object> info = new ArrayList<Object>();
				appendCartType(info, cart);
				appendLocomotiveInformation(info, cart);
				if(Mods.isLoaded(Mods.OpenComputers)) {
					this.eventOC(info);
				}
				if(Mods.isLoaded(Mods.ComputerCraft)) {
					this.eventCC(info);
				}
			}
		}

		currentCarts.clear();
		currentCarts = carts;
	}

	@Override
	public boolean canBeColored() {
		return false;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		data = super.writeToNBT(data);
		data.setByte("direction", (byte) direction.ordinal());
		return data;
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		direction = data.hasKey("direction") ? EnumFacing.byIndex(data.getByte("direction")) : EnumFacing.UP;
	}

	@Override
	public NBTTagCompound writeToRemoteNBT(NBTTagCompound tag) {
		tag.setByte("direction", (byte) direction.ordinal());
		return tag;
	}

	@Override
	public void readFromRemoteNBT(NBTTagCompound tag) {
		EnumFacing oldDir = this.direction;
		direction = tag.hasKey("direction") ? EnumFacing.byIndex(tag.getByte("direction")) : EnumFacing.UP;
		if(oldDir != direction) {
			notifyBlockUpdate();
		}
	}

	private void appendCartType(ArrayList<Object> info, EntityMinecart cart) {
		IRailcraftCartContainer type = RailcraftCarts.fromCart(cart);
		info.add(type.getBaseTag().toLowerCase(Locale.ENGLISH));
		String entityName = cart.getName();
		info.add(entityName);
	}

	private void appendLocomotiveInformation(ArrayList<Object> info, EntityMinecart cart) {
		if(cart instanceof EntityLocomotive) {
			EntityLocomotive locomotive = (EntityLocomotive) cart;

			GameProfile owner = locomotive.getOwner();
			info.add(Math.max(15 - locomotive.getPrimaryColor().ordinal(), -1));
			info.add(Math.max(15 - locomotive.getSecondaryColor().ordinal(), -1));
			String destination = locomotive.getDestination();
			info.add(destination);
			info.add(owner.getName());
		}
	}

	// Computer Stuff //

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
				computer.queueEvent("minecart", extendedInfo.toArray());
			}
		}
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	protected OCUtils.Device deviceInfo() {
		return new OCUtils.Device(
			DeviceClass.Generic,
			"Cart detector",
			OCUtils.Vendors.Railcraft,
			"Digitized Detector A12"
		);
	}

	@Nullable
	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Node sidedNode(EnumFacing side) {
		return side == this.direction ? node() : null;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public boolean canConnect(EnumFacing side) {
		return side == this.direction;
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String[] getMethodNames() {
		return new String[] {};
	}

	@Nullable
	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		return null;
	}

	@Override
	public boolean canConnectPeripheralOnSide(EnumFacing side) {
		return side == this.direction;
	}
}

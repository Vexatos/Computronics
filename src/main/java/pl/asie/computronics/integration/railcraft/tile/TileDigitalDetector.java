package pl.asie.computronics.integration.railcraft.tile;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.SidedEnvironment;
import mods.railcraft.api.carts.CartTools;
import mods.railcraft.common.carts.EntityLocomotive;
import mods.railcraft.common.carts.EnumCart;
import mods.railcraft.common.carts.ICartType;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.cc.ISidedPeripheral;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.reference.Names;
import pl.asie.computronics.tile.TileEntityPeripheralBase;
import pl.asie.computronics.util.OCUtils;

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
	implements SidedEnvironment, ISidedPeripheral {

	public ForgeDirection direction;
	private boolean tested;
	private List<EntityMinecart> currentCarts = new ArrayList<EntityMinecart>();

	public TileDigitalDetector() {
		super(Names.Railcraft_DigitalDetector);
		this.direction = ForgeDirection.UP;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
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

		for(EntityMinecart cart : carts) {
			if(!this.currentCarts.contains(cart)) {
				ArrayList<Object> info = new ArrayList<Object>();
				appendCartType(info, cart);
				appendLocomotiveInformation(info, cart);
				if(Mods.isLoaded(Mods.OpenComputers) && this.node() != null) {
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
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		data.setByte("direction", (byte) direction.ordinal());
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		direction = data.hasKey("direction") ? ForgeDirection.getOrientation(data.getByte("direction")) : ForgeDirection.UP;
	}

	@Override
	public void writeToRemoteNBT(NBTTagCompound tag) {
		tag.setByte("direction", (byte) direction.ordinal());
	}

	@Override
	public void readFromRemoteNBT(NBTTagCompound tag) {
		ForgeDirection oldDir = this.direction;
		direction = tag.hasKey("direction") ? ForgeDirection.getOrientation(tag.getByte("direction")) : ForgeDirection.UP;
		if(oldDir != direction) {
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	private void appendCartType(ArrayList<Object> info, EntityMinecart cart) {
		ICartType type = EnumCart.fromCart(cart);
		info.add(type != null ? type.getTag().toLowerCase(Locale.ENGLISH) : "unknown");
		String entityName = cart.func_95999_t();
		info.add(entityName != null ? entityName : "");
	}

	private void appendLocomotiveInformation(ArrayList<Object> info, EntityMinecart cart) {
		if(cart instanceof EntityLocomotive) {
			EntityLocomotive locomotive = (EntityLocomotive) cart;

			GameProfile owner = locomotive.getOwner();
			info.add(Math.max(15 - locomotive.getPrimaryColor(), -1));
			info.add(Math.max(15 - locomotive.getSecondaryColor(), -1));
			String destination = locomotive.getDestination();
			info.add(destination != null ? destination : "");
			info.add(owner != null ? owner.getName() : "");
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

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Node sidedNode(ForgeDirection side) {
		return side == this.direction ? node() : null;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public boolean canConnect(ForgeDirection side) {
		return side == this.direction;
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String[] getMethodNames() {
		return new String[] {};
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		return null;
	}

	@Override
	public boolean canConnectPeripheralOnSide(int side) {
		return ForgeDirection.getOrientation(side) == this.direction;
	}
}

package pl.asie.computronics.tile;

import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import mods.railcraft.api.carts.CartTools;
import mods.railcraft.common.carts.EntityLocomotiveElectric;
import mods.railcraft.common.items.ItemTicket;
import mods.railcraft.common.items.ItemTicketGold;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;

import java.lang.ref.WeakReference;
import java.util.UUID;

/**
 * @author Vexatos
 */
public class TileLocomotiveRelay extends TileEntityPeripheralBase {

	private WeakReference<EntityLocomotiveElectric> locomotive;
	private boolean isInitialized = false, isBound = false;
	private int prevLocoticksExisted;

	private UUID uuid;

	public TileLocomotiveRelay() {
		super("locomotive_relay");
	}

	public void setLocomotive(EntityLocomotiveElectric loco) {
		//this.locomotive = new WeakReference<EntityLocomotiveElectric>(loco);
		this.isBound = true;
		this.uuid = loco.getUniqueID();
	}

	public EntityLocomotiveElectric getLocomotive() {
		//return this.locomotive != null ? this.locomotive.get() : null;
		if(uuid != null) {
			EntityMinecart cart = CartTools.getLinkageManager(worldObj).getCartFromUUID(uuid);
			if(cart != null && cart instanceof EntityLocomotiveElectric) {
				return (EntityLocomotiveElectric) cart;
			}
		}
		return null;
	}

	public boolean isBound() {
		return this.isBound;
	}

	public boolean unbind() {
		if(this.isBound) {
			this.isBound = false;
			this.locomotive = null;
			this.uuid = null;
			return true;
		}
		return false;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(worldObj.isRemote) {
			return;
		}
		if(!isInitialized && isBound && uuid != null) {
			this.tryFindLocomotive(this.uuid);
			isInitialized = true;
		}

		EntityLocomotiveElectric locomotive = getLocomotive();
		boolean b = true;
		if(locomotive != null && locomotive.dimension == worldObj.provider.dimensionId) {
			b = locomotive.worldObj.getChunkProvider().chunkExists(locomotive.chunkCoordX, locomotive.chunkCoordZ);
			if(!b) {
				return;
			}
		}
		if(locomotive != null && (locomotive.isDead || !isBound)) {
			this.locomotive = null;
			locomotive = null;
			return;
		}

		if(isBound && uuid == null) {
			isBound = false;
		}

		if(locomotive != null) {
			prevLocoticksExisted = locomotive.ticksExisted;
		}

		if(locomotive != null || !isBound) {
			return;
		}
		// Only check every second
		if(worldObj.getTotalWorldTime() % 20 == 0) {
			this.tryFindLocomotive(this.uuid);
		}
	}

	private void tryFindLocomotive(UUID uuid) {
		if(getLocomotive() != null) {
			return;
		}
		if(uuid != null) {
			EntityMinecart cart = CartTools.getLinkageManager(worldObj).getCartFromUUID(uuid);
			if(cart != null && cart instanceof EntityLocomotiveElectric) {
				this.setLocomotive((EntityLocomotiveElectric) cart);
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.isBound = nbt.getBoolean("bound");
		if(isBound) {
			this.uuid = MiscTools.readUUID(nbt, "locomotive");
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		EntityLocomotiveElectric locomotive = getLocomotive();
		if(isBound && locomotive != null) {
			MiscTools.writeUUID(nbt, "locomotive", locomotive.getPersistentID());
		}
		nbt.setBoolean("bound", isBound);
	}

	@Override
	public void writeToRemoteNBT(NBTTagCompound nbt) {
		super.writeToRemoteNBT(nbt);
		nbt.setBoolean("bound", isBound);
	}

	@Override
	public void readFromRemoteNBT(NBTTagCompound nbt) {
		super.readFromRemoteNBT(nbt);
		if(nbt.hasKey("bound")) {
			this.isBound = nbt.getBoolean("bound");
		}
	}

	private String cannotAccessLocomotive() {
		EntityLocomotiveElectric locomotive = getLocomotive();
		if(!isBound) {
			return "relay is not bound to a locomotive";
		}
		if(locomotive == null) {
			return "locomotive is currently not detectable";
		}
		if(locomotive.dimension != this.worldObj.provider.dimensionId) {
			return "relay and locomotive are in different dimensions";
		}
		if(!(locomotive.getDistance(xCoord, yCoord, zCoord) <= Config.LOCOMOTIVE_RELAY_RANGE)) {
			return "locomotive is too far away";
		}
		if(locomotive.isSecure()) {
			return "locomotive is locked";
		}
		return null;
	}

	//Computer stuff

	private static Object[] setDestination(EntityLocomotiveElectric locomotive, Object[] arguments) {
		ItemStack ticket = locomotive.getStackInSlot(0);
		if(ticket != null && ticket.getItem() instanceof ItemTicketGold) {
			ItemTicket.setTicketData(ticket, (String) arguments[0], (String) arguments[0],
				ItemTicketGold.getOwner(ticket));
			return new Object[] { locomotive.setDestination(ticket) };
		} else {
			return new Object[] { false, "there is no golden ticket inside the locomotive" };
		}
	}

	@Callback(doc = "function():string; gets the destination the locomotive is currently set to")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getDestination(Context context, Arguments args) {
		if(cannotAccessLocomotive() != null) {
			return new Object[] { null, cannotAccessLocomotive() };
		}
		return new Object[] { getLocomotive().getDestination() };
	}

	@Callback(doc = "function(destination:string):boolean; Sets the locomotive's destination; there needs to be a golden ticket inside the locomotive")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setDestination(Context c, Arguments a) {
		if(cannotAccessLocomotive() != null) {
			return new Object[] { null, cannotAccessLocomotive() };
		}
		a.checkString(0);
		return TileLocomotiveRelay.setDestination(getLocomotive(), a.toArray());
	}

	@Callback(doc = "function():number; gets the current charge of the locomotive")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getCharge(Context context, Arguments args) {
		if(cannotAccessLocomotive() != null) {
			return new Object[] { null, cannotAccessLocomotive() };
		}
		return new Object[] { getLocomotive().getChargeHandler().getCharge() };
	}

	@Callback(doc = "function():string; returns the current mode of the locomotive; can be RUNNING, IDLE or SHUTDOWN")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getMode(Context context, Arguments args) {
		if(cannotAccessLocomotive() != null) {
			return new Object[] { null, cannotAccessLocomotive() };
		}
		return new Object[] { getLocomotive().getMode().toString() };
	}

	@Callback(doc = "function():string; returns the current name of the locomotive")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getName(Context context, Arguments args) {
		if(cannotAccessLocomotive() != null) {
			return new Object[] { null, cannotAccessLocomotive() };
		}
		return new Object[] { getLocomotive().func_95999_t() != null ? getLocomotive().func_95999_t() : "" };
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String[] getMethodNames() {
		return new String[] { "getDestination", "setDestination", "getCharge", "getMode", "getName" };
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments)
		throws LuaException, InterruptedException {
		if(method < getMethodNames().length) {
			if(cannotAccessLocomotive() != null) {
				return new Object[] { null, cannotAccessLocomotive() };
			}
			switch(method) {
				case 0: {
					return new Object[] { getLocomotive().getDestination() };
				}
				case 1: {
					if(arguments.length < 1 || !(arguments[0] instanceof String)) {
						throw new LuaException("first argument needs to be a string");
					}

					return TileLocomotiveRelay.setDestination(getLocomotive(), arguments);
				}
				case 2: {
					return new Object[] { getLocomotive().getChargeHandler().getCharge() };
				}
				case 3: {
					return new Object[] { getLocomotive().getMode().toString() };
				}
				case 4: {
					return new Object[] { getLocomotive().func_95999_t() != null ? getLocomotive().func_95999_t() : "" };
				}
			}
		}
		return null;
	}

	@Override
	@Optional.Method(modid = Mods.NedoComputers)
	public boolean connectable(int side) {
		return false;
	}

	@Override
	@Optional.Method(modid = Mods.NedoComputers)
	public short busRead(int addr) {
		return 0;
	}

	@Override
	@Optional.Method(modid = Mods.NedoComputers)
	public void busWrite(int addr, short data) {

	}
}

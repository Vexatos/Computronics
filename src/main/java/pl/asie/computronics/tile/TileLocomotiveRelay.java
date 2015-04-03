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
import net.minecraft.util.AxisAlignedBB;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * @author Vexatos
 */
public class TileLocomotiveRelay extends TileEntityPeripheralBase {

	private EntityLocomotiveElectric locomotive;
	private double locomotiveX, locomotiveY, locomotiveZ;
	private boolean isInitialized = false, isBound = false;
	private UUID uuid;

	public TileLocomotiveRelay() {
		super("locomotive_relay");
	}

	public void setLocomotive(EntityLocomotiveElectric loco) {
		this.locomotive = loco;
		this.locomotiveX = this.locomotive.posX;
		this.locomotiveY = this.locomotive.posY;
		this.locomotiveZ = this.locomotive.posZ;
		this.isBound = true;
		this.uuid = this.locomotive.getUniqueID();
	}

	public EntityLocomotiveElectric getLocomotive() {
		return this.locomotive;
	}

	public boolean isBound() {
		return this.isBound;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(worldObj.isRemote) {
			return;
		}
		if(locomotive == null && !isInitialized && isBound) {
			this.tryFindLocomotive(this.uuid);
			if(locomotive == null) {
				this.tryFindLocomotiveExpensively();
				if(locomotive == null) {
					Computronics.log.error(String.format(Locale.ENGLISH,
						"Unable to find Electric Locomotive at (%s,%s,%s) bound to Locomotive Relay at (%s,%s,%s). It will not be bound anymore.",
						locomotiveX, locomotiveY, locomotiveZ, xCoord, yCoord, zCoord));
					if(uuid != null) {
						Computronics.log.error("The Locomotive's UUID was: " + this.uuid);
					}
				}
			}
			isInitialized = true;
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
		if(locomotive == null) {
			return;
		}
		if(uuid != null) {
			EntityMinecart cart = CartTools.getLinkageManager(worldObj).getCartFromUUID(uuid);
			if(cart != null && cart instanceof EntityLocomotiveElectric) {
				this.setLocomotive((EntityLocomotiveElectric) cart);
			}
		}
	}

	private void tryFindLocomotiveExpensively() {
		if(locomotive == null) {
			return;
		}
		List locos = worldObj.getEntitiesWithinAABB(EntityLocomotiveElectric.class, AxisAlignedBB.getBoundingBox(locomotiveX - 0.5, locomotiveY - 0.5, locomotiveZ - 0.5,
			locomotiveX + 0.5, locomotiveY + 0.5, locomotiveZ + 0.5));

		EntityLocomotiveElectric checkLoco = null;
		for(Object loco : locos) {
			if(loco instanceof EntityLocomotiveElectric) {
				if(checkLoco == null) {
					checkLoco = (EntityLocomotiveElectric) loco;
					continue;
				}
				EntityLocomotiveElectric newLoco = (EntityLocomotiveElectric) loco;
				if(newLoco.getDistanceSq(locomotiveX, locomotiveY, locomotiveZ)
					< checkLoco.getDistanceSq(locomotiveX, locomotiveY, locomotiveZ)) {
					checkLoco = newLoco;
				}
			}
		}
		if(checkLoco != null) {
			this.setLocomotive(checkLoco);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if(this.locomotive == null) {
			this.locomotiveX = nbt.getDouble("locomotiveX");
			this.locomotiveY = nbt.getDouble("locomotiveY");
			this.locomotiveZ = nbt.getDouble("locomotiveZ");
			this.uuid = MiscTools.readUUID(nbt, "locomotive");
			this.isBound = nbt.getBoolean("bound");
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if(this.locomotive != null) {
			nbt.setDouble("locomotiveX", this.locomotive.posX);
			nbt.setDouble("locomotiveY", this.locomotive.posY);
			nbt.setDouble("locomotiveZ", this.locomotive.posZ);
			MiscTools.writeUUID(nbt, "locomotive", this.locomotive.getPersistentID());
		}
		nbt.setBoolean("bound", isBound);
	}

	@Override
	public void writeToRemoteNBT(NBTTagCompound nbt) {
		super.writeToRemoteNBT(nbt);
		if(this.locomotive != null) {
			nbt.setBoolean("bound", isBound);
		}
	}

	@Override
	public void readFromRemoteNBT(NBTTagCompound nbt) {
		super.readFromRemoteNBT(nbt);
		if(nbt.hasKey("bound")) {
			this.isBound = nbt.getBoolean("bound");
		}
	}

	private String cannotAccessLocomotive() {
		if(this.locomotive == null) {
			return "relay is not bound to a locomotive";
		}
		if(this.locomotive.dimension != this.worldObj.provider.dimensionId) {
			return "relay and locomotive are in different dimensions";
		}
		if(!(this.locomotive.getDistance(xCoord, yCoord, zCoord) <= Config.LOCOMOTIVE_RELAY_RANGE)) {
			return "locomotive is too far away";
		}
		if(this.locomotive.isSecure()) {
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
		return new Object[] { this.locomotive.getDestination() };
	}

	@Callback(doc = "function(destination:string):boolean; Sets the locomotive's destination; there needs to be a golden ticket inside the locomotive")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setDestination(Context c, Arguments a) {
		if(cannotAccessLocomotive() != null) {
			return new Object[] { null, cannotAccessLocomotive() };
		}
		a.checkString(0);
		return TileLocomotiveRelay.setDestination(this.locomotive, a.toArray());
	}

	@Callback(doc = "function():number; gets the current charge of the locomotive")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getCharge(Context context, Arguments args) {
		if(cannotAccessLocomotive() != null) {
			return new Object[] { null, cannotAccessLocomotive() };
		}
		return new Object[] { this.locomotive.getChargeHandler().getCharge() };
	}

	@Callback(doc = "function():string; returns the current mode of the locomotive; can be RUNNING, IDLE or SHUTDOWN")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getMode(Context context, Arguments args) {
		if(cannotAccessLocomotive() != null) {
			return new Object[] { null, cannotAccessLocomotive() };
		}
		return new Object[] { this.locomotive.getMode().toString() };
	}

	@Callback(doc = "function():string; returns the current name of the locomotive")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getName(Context context, Arguments args) {
		if(cannotAccessLocomotive() != null) {
			return new Object[] { null, cannotAccessLocomotive() };
		}
		return new Object[] { this.locomotive.func_95999_t() != null ? this.locomotive.func_95999_t() : "" };
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
					return new Object[] { this.locomotive.getDestination() };
				}
				case 1: {
					if(arguments.length < 1 || !(arguments[0] instanceof String)) {
						throw new LuaException("first argument needs to be a string");
					}

					return TileLocomotiveRelay.setDestination(this.locomotive, arguments);
				}
				case 2: {
					return new Object[] { this.locomotive.getChargeHandler().getCharge() };
				}
				case 3: {
					return new Object[] { this.locomotive.getMode().toString() };
				}
				case 4: {
					return new Object[] { this.locomotive.func_95999_t() != null ? this.locomotive.func_95999_t() : "" };
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

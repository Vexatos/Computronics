package pl.asie.computronics.tile;

import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import mods.railcraft.common.carts.EntityLocomotiveElectric;
import mods.railcraft.common.items.ItemTicket;
import mods.railcraft.common.items.ItemTicketGold;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import pl.asie.computronics.Computronics;

/**
 * @author Vexatos
 */
public class TileLocomotiveRelay extends TileEntityPeripheralBase {

	private EntityLocomotiveElectric locomotive;

	public TileLocomotiveRelay() {
		super("locomotive_relay");
	}

	public void setLocomotive(EntityLocomotiveElectric loco) {
		this.locomotive = loco;
	}

	public EntityLocomotiveElectric getLocomotive() {
		return this.locomotive;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		int eid = nbt.getInteger("locomotive");
		if(worldObj.getEntityByID(eid) instanceof EntityLocomotiveElectric) {
			this.setLocomotive((EntityLocomotiveElectric) worldObj.getEntityByID(eid));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if(this.locomotive != null) {
			nbt.setInteger("locomotive", this.locomotive.getEntityId());
		}
	}

	private String cannotAccessLocomotive() {
		if(this.locomotive == null) {
			return "relay is not bound to a locomotive";
		}
		if(this.locomotive.dimension != this.worldObj.provider.dimensionId) {
			return "relay and locomotive are in different dimensions";
		}
		if(!(this.locomotive.getDistance(xCoord, yCoord, zCoord) <= Computronics.LOCOMOTIVE_RELAY_RANGE)) {
			return "locomotive is too far away";
		}
		if(this.locomotive.isSecure()) {
			return "locomotive is locked";
		}
		return null;
	}

	//Computer stuff

	@Callback(doc = "function():String; gets the destination the locomotive is currently set to")
	@Optional.Method(modid = "OpenComputers")
	public Object[] getDestination(Context context, Arguments args) {
		if(cannotAccessLocomotive() != null) {
			return new Object[] { null, cannotAccessLocomotive() };
		}
		return new Object[] { this.locomotive.getDestination() };
	}

	@Callback(doc = "function(destination:String):boolean; Sets the locomotive's destination; there needs to be a golden ticket inside the locomotive")
	public Object[] setDestination(Context c, Arguments a) {
		if(cannotAccessLocomotive() != null) {
			return new Object[] { null, cannotAccessLocomotive() };
		}

		ItemStack ticket = this.locomotive.getStackInSlot(0);
		if(ticket == null || !(ticket.getItem() instanceof ItemTicketGold)) {
			return new Object[] { false, "there is no golden ticket inside the locomotive" };
		}
		ItemTicket.setTicketData(ticket, a.checkString(0), a.checkString(0), ItemTicketGold.getOwner(ticket));
		return new Object[] { this.locomotive.setDestination(ticket) };
	}

	@Callback(doc = "function():number; gets the current charge of the locomotive")
	@Optional.Method(modid = "OpenComputers")
	public Object[] getCharge(Context context, Arguments args) {
		if(cannotAccessLocomotive() != null) {
			return new Object[] { null, cannotAccessLocomotive() };
		}
		return new Object[] { this.locomotive.getCharge() };
	}

	@Callback(doc = "function():string; returns the current mode of the locomotive; can be RUNNING, IDLE or SHUTDOWN")
	@Optional.Method(modid = "OpenComputers")
	public Object[] getMode(Context context, Arguments args) {
		if(cannotAccessLocomotive() != null) {
			return new Object[] { null, cannotAccessLocomotive() };
		}
		return new Object[] { this.locomotive.getMode().toString() };
	}

	@Override
	public String[] getMethodNames() {
		return new String[] { "getDestination", "setDestination", "getCharge", "getMode" };
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments)
		throws LuaException, InterruptedException {
		if(method < 4) {
			if(cannotAccessLocomotive() != null) {
				return new Object[] { null, cannotAccessLocomotive() };
			}
			switch(method){
				case 0:{
					return new Object[] { this.locomotive.getMode().toString() };
				}
				case 1:{
					if(arguments.length < 1 || !(arguments[0] instanceof String)) {
						throw new LuaException("first argument needs to be a string");
					}

					ItemStack ticket = this.locomotive.getStackInSlot(0);
					if(ticket != null && ticket.getItem() instanceof ItemTicketGold) {
						ItemTicket.setTicketData(ticket, (String) arguments[0], (String) arguments[0],
							ItemTicketGold.getOwner(ticket));
						return new Object[] { this.locomotive.setDestination(ticket) };
					} else {
						return new Object[] { false, "there is no golden ticket inside the locomotive" };
					}
				}
				case 2:{
					return new Object[] { this.locomotive.getCharge() };
				}
				case 3:{
					return new Object[] { this.locomotive.getMode().toString() };
				}
			}
		}
		return null;
	}

	@Override
	@Optional.Method(modid = "nedocomputers")
	public boolean Connectable(int side) {
		return false;
	}

	@Override
	public short busRead(int addr) {
		return 0;
	}

	@Override
	public void busWrite(int addr, short data) {

	}
}

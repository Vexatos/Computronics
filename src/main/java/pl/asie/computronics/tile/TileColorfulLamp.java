package pl.asie.computronics.tile;

import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.ILuaTask;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import mods.immibis.redlogic.api.wiring.IBundledEmitter;
import mods.immibis.redlogic.api.wiring.IBundledUpdatable;
import mods.immibis.redlogic.api.wiring.IConnectable;
import mods.immibis.redlogic.api.wiring.IWire;
import mrtjp.projectred.api.IBundledTile;
import mrtjp.projectred.api.ProjectRedAPI;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.block.BlockColorfulLamp;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.LampUtil;
import pl.asie.computronics.util.OCUtils;

@Optional.InterfaceList({
	@Optional.Interface(iface = "mods.immibis.redlogic.api.wiring.IBundledUpdatable", modid = Mods.RedLogic),
	@Optional.Interface(iface = "mods.immibis.redlogic.api.wiring.IConnectable", modid = Mods.RedLogic),
	@Optional.Interface(iface = "mrtjp.projectred.api.IBundledTile", modid = Mods.ProjectRed)
})
public class TileColorfulLamp extends TileEntityPeripheralBase implements IBundledTile, IBundledUpdatable, IConnectable {

	public TileColorfulLamp() {
		super("colorful_lamp");
	}

	private int color = 0x6318;

	private boolean initialized = false;

	@Override
	public void updateEntity() {
		super.updateEntity();
		Block block = worldObj.getBlock(xCoord, yCoord, zCoord);
		if(!initialized && block instanceof BlockColorfulLamp) {
			if(LampUtil.shouldColorLight()) {
				((BlockColorfulLamp) block).setLightValue(color);
			} else {
				((BlockColorfulLamp) block).setLightValue(color == 0 ? 0 : 15);
			}
			initialized = true;
		}
	}

	public int getLampColor() {
		return color;
	}

	public void setLampColor(int color) {
		this.color = color & 0x7FFF;
		if(worldObj.getBlock(xCoord, yCoord, zCoord) instanceof BlockColorfulLamp) {
			if(LampUtil.shouldColorLight()) {
				((BlockColorfulLamp) worldObj.getBlock(xCoord, yCoord, zCoord)).setLightValue(this.color);
			} else {
				((BlockColorfulLamp) worldObj.getBlock(xCoord, yCoord, zCoord)).setLightValue(color == 0 ? 0 : 15);
			}
		}
		this.markDirty();
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Callback(doc = "function():number; Returns the current lamp color", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getLampColor(Context context, Arguments args) throws Exception {
		return new Object[] { this.getLampColor() };
	}

	@Callback(doc = "function(color:number):boolean; Sets the lamp color; Set to 0 to turn the off the lamp; Returns true on success")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setLampColor(Context context, Arguments args) throws Exception {
		if(args.checkInteger(0) >= 0 && args.checkInteger(0) <= 0xFFFF) {
			this.setLampColor(args.checkInteger(0));
			return new Object[] { true };
		}
		return new Object[] { false, "number must be between 0 and 32767" };
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	protected OCUtils.Device deviceInfo() {
		return new OCUtils.Device(
			DeviceClass.Display,
			"Colored Lamp",
			OCUtils.Vendors.Lumiose,
			"LED-4"
		);
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String[] getMethodNames() {
		return new String[] { "getLampColor", "setLampColor" };
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
		int method, final Object[] arguments) throws LuaException,
		InterruptedException {
		switch(method) {
			case 0:
			default:
				return new Object[] { this.color };
			case 1: {
				if(arguments.length > 0 && (arguments[0] instanceof Double)) {
					context.executeMainThreadTask(new ILuaTask() {
						@Override
						public Object[] execute() throws LuaException {
							TileColorfulLamp.this.setLampColor(((Double) arguments[0]).intValue());
							return null;
						}
					});
				}
			}
			break;
		}
		return null;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if(tag.hasKey("clc")) {
			color = tag.getShort("clc");
		}
		if(color < 0) {
			color = 0;
		}
		if(tag.hasKey("binaryMode")) {
			this.binaryMode = tag.getBoolean("binaryMode");
		}
	}

	@Override
	public boolean canBeColored() {
		return false;
	}

	private boolean binaryMode = false;

	public boolean isBinaryMode() {
		return this.binaryMode;
	}

	public void setBinaryMode(boolean mode) {
		this.binaryMode = mode;
		this.markDirty();
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		this.worldObj.notifyBlockChange(xCoord, yCoord, zCoord, getBlockType());
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setShort("clc", (short) (color & 32767));
		tag.setBoolean("binaryMode", this.binaryMode);
	}

	@Override
	public void writeToRemoteNBT(NBTTagCompound tag) {
		super.writeToRemoteNBT(tag);
		tag.setShort("clc", (short) (color & 32767));
	}

	@Override
	public void readFromRemoteNBT(NBTTagCompound tag) {
		super.readFromRemoteNBT(tag);
		int oldColor = this.color;
		if(tag.hasKey("clc")) {
			this.color = tag.getShort("clc");
		}
		if(this.color < 0) {
			this.color = 0;
		}
		if(oldColor != this.color) {
			this.worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
		}
	}

	private boolean parseBundledInput(byte[] data) {
		if(data != null) {
			int c = 0;
			for(int i = 0; i < 15; i++) {
				if(data[i] != 0) {
					c |= (1 << i);
				}
			}
			this.setLampColor(c);
			return true;
		} else {
			return false;
		}
	}

	@Override
	@Optional.Method(modid = Mods.ProjectRed)
	public byte[] getBundledSignal(int side) {
		return null;
	}

	@Override
	@Optional.Method(modid = Mods.ProjectRed)
	public boolean canConnectBundled(int side) {
		return true;
	}

	@Override
	@Optional.Method(modid = Mods.ProjectRed)
	public void onProjectRedBundledInputChanged() {
		for(int i = 0; i < 6; i++) {
			if(parseBundledInput(ProjectRedAPI.transmissionAPI.getBundledInput(worldObj, xCoord, yCoord, zCoord, i))) {
				return;
			}
		}
	}

	@Override
	@Optional.Method(modid = Mods.RedLogic)
	public boolean connects(IWire wire, int blockFace, int fromDirection) {
		return (wire instanceof IBundledEmitter);
	}

	@Override
	@Optional.Method(modid = Mods.RedLogic)
	public boolean connectsAroundCorner(IWire wire, int blockFace, int fromDirection) {
		return false;
	}

	@Override
	@Optional.Method(modid = Mods.RedLogic)
	public void onBundledInputChanged() {
		for(int side = 0; side < 6; side++) {
			ForgeDirection dir = ForgeDirection.getOrientation(side);
			TileEntity input = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
			if(!(input instanceof IBundledEmitter)) {
				continue;
			}
			for(int direction = -1; direction < 6; direction++) {
				byte[] data = ((IBundledEmitter) input).getBundledCableStrength(direction, side ^ 1);
				if(parseBundledInput(data)) {
					return;
				}
			}
		}
	}
}

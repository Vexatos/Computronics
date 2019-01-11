package pl.asie.computronics.tile;

import com.elytradev.mirage.lighting.IColoredLight;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.ILuaTask;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import elucent.albedo.lighting.ILightProvider;
import elucent.albedo.lighting.Light;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.block.BlockColorfulLamp;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.LampUtil;
import pl.asie.computronics.util.OCUtils;
import pl.asie.lib.api.tile.IBundledRedstoneProvider;

import javax.annotation.Nullable;

import static pl.asie.computronics.block.BlockColorfulLamp.BRIGHTNESS;

//import mods.immibis.redlogic.api.wiring.IBundledEmitter;
//import mods.immibis.redlogic.api.wiring.IBundledUpdatable;
//import mods.immibis.redlogic.api.wiring.IConnectable;
//import mods.immibis.redlogic.api.wiring.IWire;
//import mrtjp.projectred.api.IBundledTile;
//import mrtjp.projectred.api.ProjectRedAPI;

@Optional.InterfaceList({
	//@Optional.Interface(iface = "mods.immibis.redlogic.api.wiring.IBundledUpdatable", modid = Mods.RedLogic),
	//@Optional.Interface(iface = "mods.immibis.redlogic.api.wiring.IConnectable", modid = Mods.RedLogic),
	//@Optional.Interface(iface = "mrtjp.projectred.api.IBundledTile", modid = Mods.ProjectRed)
	@Optional.Interface(iface = "elucent.albedo.lighting.ILightProvider", modid = Mods.Albedo),
	@Optional.Interface(iface = "com.elytradev.mirage.lighting.IColoredLight", modid = Mods.Mirage)
})
public class TileColorfulLamp extends TileEntityPeripheralBase implements IBundledRedstoneProvider, ILightProvider, IColoredLight/*IBundledTile, IBundledUpdatable, IConnectable*/ {

	public TileColorfulLamp() {
		super("colorful_lamp");
	}

	private int color = 0x6318;

	@Override
	public void onLoad() {
		super.onLoad();
		Computronics.serverTickHandler.schedule(new Runnable() {
			@Override
			public void run() {
				IBlockState state = world.getBlockState(getPos());
				if(state.getBlock() instanceof BlockColorfulLamp) {
					if(LampUtil.shouldColorLight()) {
						setLightValue(state, color);
					} else {
						setLightValue(state, LampUtil.toBrightness(color));
					}
				}
			}
		});
	}

	public int getLampColor() {
		return color;
	}

	public void setLightValue(IBlockState state, int value) {
		if(LampUtil.shouldColorLight()) {
			//Bit-shift all the things!
			int r = (((value & 0x7C00) >>> 10) / 2),
				g = (((value & 0x03E0) >>> 5) / 2),
				b = ((value & 0x001F) / 2);
			r = value > 0x7FFF ? 15 : r < 0 ? 0 : r > 15 ? 15 : r;
			g = value > 0x7FFF ? 15 : g < 0 ? 0 : g > 15 ? 15 : g;
			b = value > 0x7FFF ? 15 : b < 0 ? 0 : b > 15 ? 15 : b;
			int brightness = Math.max(Math.max(r, g), b);
			world.setBlockState(getPos(), state.withProperty(BRIGHTNESS, brightness | ((b << 15) + (g << 10) + (r << 5))));
		} else {
			world.setBlockState(getPos(), state.withProperty(BRIGHTNESS, value));
		}
	}

	public void setLampColor(int color) {
		this.color = color & 0x7FFF;
		if(world.getBlockState(getPos()).getBlock() instanceof BlockColorfulLamp) {
			if(LampUtil.shouldColorLight()) {
				setLightValue(world.getBlockState(getPos()), this.color);
			} else {
				setLightValue(world.getBlockState(getPos()), LampUtil.toBrightness(color));
			}
		}
		this.markDirty();
		notifyBlockUpdate();
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
	@SideOnly(Side.CLIENT)
	@Optional.Method(modid = Mods.Albedo)
	public Light provideLight() {
		return Light.builder()
			.pos(getPos())
			.color((color & (0x1F << 10)) << 9 | (color & (0x1F << 5)) << 6 | (color & 0x1F) << 3, false)
			.radius(LampUtil.brightness(color) * 15F)
			.build();
	}

	@Nullable
	@Override
	@SideOnly(Side.CLIENT)
	@Optional.Method(modid = Mods.Mirage)
	public com.elytradev.mirage.lighting.Light getColoredLight() {
		return com.elytradev.mirage.lighting.Light.builder()
			.pos(getPos())
			.color((color & (0x1F << 10)) << 9 | (color & (0x1F << 5)) << 6 | (color & 0x1F) << 3, false)
			.radius(LampUtil.brightness(color) * 15F)
			.build();
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

	@Nullable
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
		/*if(tag.hasKey("binaryMode")) {
			this.binaryMode = tag.getBoolean("binaryMode");
		}*/
	}

	@Override
	public boolean canBeColored() {
		return false;
	}

	/*private boolean binaryMode = false;

	public boolean isBinaryMode() {
		return this.binaryMode;
	}

	public void setBinaryMode(boolean mode) {
		this.binaryMode = mode;
		this.markDirty();
		this.world.markBlockForUpdate(getPos());
		this.world.notifyBlockOfStateChange(getPos(), getBlockType());
	}*/

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setShort("clc", (short) (color & 32767));
		//tag.setBoolean("binaryMode", this.binaryMode);
		return tag;
	}

	@Override
	public NBTTagCompound writeToRemoteNBT(NBTTagCompound tag) {
		super.writeToRemoteNBT(tag);
		tag.setShort("clc", (short) (color & 32767));
		return tag;
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
			this.world.markBlockRangeForRenderUpdate(getPos(), getPos());
		}
	}

	private boolean parseBundledInput(@Nullable byte[] data) {
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
	public boolean canBundledConnectToInput(@Nullable EnumFacing side) {
		return true;
	}

	@Override
	public boolean canBundledConnectToOutput(@Nullable EnumFacing side) {
		return false;
	}

	@Override
	public byte[] getBundledOutput(@Nullable EnumFacing side) {
		return new byte[16];
	}

	@Override
	public void onBundledInputChange(@Nullable EnumFacing side, @Nullable byte[] data) {
		parseBundledInput(data);
	}

	/*@Override
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
			if(parseBundledInput(ProjectRedAPI.transmissionAPI.getBundledInput(world, xCoord, yCoord, zCoord, i))) {
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
			TileEntity input = world.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
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
	}*/
}

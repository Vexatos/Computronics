package pl.asie.computronics.tile;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.NoteUtils;
import pl.asie.computronics.util.OCUtils;
import pl.asie.lib.api.tile.IBundledRedstoneProvider;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

//import mods.immibis.redlogic.api.wiring.IBundledEmitter;
//import mods.immibis.redlogic.api.wiring.IBundledUpdatable;
//import mods.immibis.redlogic.api.wiring.IConnectable;
//import mods.immibis.redlogic.api.wiring.IWire;
//import mrtjp.projectred.api.IBundledTile;
//import mrtjp.projectred.api.ProjectRedAPI;

/*@Optional.InterfaceList({
	@Optional.Interface(iface = "mods.immibis.redlogic.api.wiring.IBundledUpdatable", modid = Mods.RedLogic),
	@Optional.Interface(iface = "mods.immibis.redlogic.api.wiring.IConnectable", modid = Mods.RedLogic),
	@Optional.Interface(iface = "mrtjp.projectred.api.IBundledTile", modid = Mods.ProjectRed)
})*/
public class TileIronNote extends TileEntityPeripheralBase implements IBundledRedstoneProvider, ITickable /*implements IBundledTile, IBundledUpdatable, IConnectable*/ {

	public TileIronNote() {
		super("iron_noteblock");
	}

	protected final List<NoteUtils.NoteTask> noteBuffer = new ArrayList<NoteUtils.NoteTask>();

	@Override
	public void update() {
		super.update();
		synchronized(noteBuffer) {
			if(!noteBuffer.isEmpty()) {
				for(NoteUtils.NoteTask task : noteBuffer) {
					task.play(world, getPos());
				}
				noteBuffer.clear();
			}
		}
	}
	// OpenComputers

	@Callback(direct = true, limit = 10, doc = "function([instrument:number or string,] note:number [, volume:number]); "
		+ "Plays the specified note with the specified instrument or the default one; volume may be a number between 0 and 1")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] playNote(Context context, Arguments args) {
		NoteUtils.NoteTask task = null;
		if(args.count() >= 1) {
			if(args.count() >= 2 && args.isInteger(1)) {
				if(args.isInteger(0)) {
					task = NoteUtils.playNote(world, getPos(), args.checkInteger(0), args.checkInteger(1), NoteUtils.toVolume(3, args.optDouble(2, 1.0D)));
				} else if(args.isString(0)) {
					task = NoteUtils.playNote(world, getPos(), args.checkString(0), args.checkInteger(1), NoteUtils.toVolume(3, args.optDouble(2, 1.0D)));
				} else if(args.checkAny(0) == null) {
					task = NoteUtils.playNote(world, getPos(), -1, args.checkInteger(1), NoteUtils.toVolume(3, args.optDouble(2, 1.0D)));
				}
			} else if(args.isInteger(0)) {
				task = NoteUtils.playNote(world, getPos(), -1, args.checkInteger(0), NoteUtils.toVolume(2, args.optDouble(1, 1.0D)));
			}
		}
		if(task != null) {
			synchronized(noteBuffer) {
				noteBuffer.add(task);
			}
		}
		return null;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	protected OCUtils.Device deviceInfo() {
		return new OCUtils.Device(
			DeviceClass.Multimedia,
			"Music note emission device",
			OCUtils.Vendors.Yanaki,
			"Vanilla 1"
		);
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String[] getMethodNames() {
		return new String[] { "playNote" };
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
		int method, Object[] arguments) throws LuaException,
		InterruptedException {
		try {
			NoteUtils.NoteTask task = null;
			if(arguments.length >= 1) {
				if(arguments.length >= 2 && (arguments[1] instanceof Double)) {
					if(arguments[0] != null) {
						if(arguments[0] instanceof Double) {
							task = NoteUtils.playNote(world, getPos(), ((Double) arguments[0]).intValue(), ((Double) arguments[1]).intValue(), NoteUtils.toVolume(arguments, 2));
						} else if(arguments[0] instanceof String) {
							task = NoteUtils.playNote(world, getPos(), (String) arguments[0], ((Double) arguments[1]).intValue(), NoteUtils.toVolume(arguments, 2));
						}
					} else {
						task = NoteUtils.playNote(world, getPos(), -1, ((Double) arguments[1]).intValue(), NoteUtils.toVolume(arguments, 2));
					}
				} else if((arguments[0] instanceof Double)) {
					task = NoteUtils.playNote(world, getPos(), -1, ((Double) arguments[0]).intValue(), NoteUtils.toVolume(arguments, 1));
				}
			}
			if(task != null) {
				synchronized(noteBuffer) {
					noteBuffer.add(task);
				}
			}
		} catch(IllegalArgumentException e) {
			throw new LuaException(e.getMessage());
		}
		return null;
	}

	private void parseBundledInput(@Nullable byte[] data) {
		int baseNote = 4;
		if(data != null) {
			for(int i = 0; i < 16; i++) {
				if(data[i] != 0) {
					NoteUtils.NoteTask task = NoteUtils.playNote(world, getPos(), -1, baseNote + i);
					if(task != null) {
						task.play(world, getPos());
					}
				}
			}
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
			parseBundledInput(ProjectRedAPI.transmissionAPI.getBundledInput(world, xCoord, yCoord, zCoord, i));
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
				if(data != null) {
					parseBundledInput(data);
				}
			}
		}
	}*/
}

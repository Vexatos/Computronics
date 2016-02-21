package pl.asie.computronics.cc;

import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.NoteUtils;

import java.util.ArrayList;
import java.util.List;

public class MusicalTurtleUpgrade extends TurtleUpgradeBase {

	private class MusicalTurtlePeripheral extends TurtlePeripheralBase {

		public MusicalTurtlePeripheral(ITurtleAccess access) {
			super(access);
		}

		@Override
		public String getType() {
			return "iron_noteblock";
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
								task = NoteUtils.playNote(access.getWorld(), access.getPosition().posX, access.getPosition().posY, access.getPosition().posZ, ((Double) arguments[0]).intValue(), ((Double) arguments[1]).intValue(), NoteUtils.toVolume(arguments, 2));
							} else if(arguments[0] instanceof String) {
								task = NoteUtils.playNote(access.getWorld(), access.getPosition().posX, access.getPosition().posY, access.getPosition().posZ, (String) arguments[0], ((Double) arguments[1]).intValue(), NoteUtils.toVolume(arguments, 2));
							}
						} else {
							task = NoteUtils.playNote(access.getWorld(), access.getPosition().posX, access.getPosition().posY, access.getPosition().posZ, -1, ((Double) arguments[1]).intValue(), NoteUtils.toVolume(arguments, 2));
						}
					} else if((arguments[0] instanceof Double)) {
						task = NoteUtils.playNote(access.getWorld(), access.getPosition().posX, access.getPosition().posY, access.getPosition().posZ, -1, ((Double) arguments[0]).intValue(), NoteUtils.toVolume(arguments, 1));
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
	}

	public MusicalTurtleUpgrade(int id) {
		super(id);
	}

	protected final List<NoteUtils.NoteTask> noteBuffer = new ArrayList<NoteUtils.NoteTask>();

	@Override
	public void update(ITurtleAccess turtle, TurtleSide side) {
		super.update(turtle, side);
		synchronized(noteBuffer) {
			if(!noteBuffer.isEmpty()) {
				for(NoteUtils.NoteTask task : noteBuffer) {
					task.play(turtle.getWorld(), turtle.getPosition().posX, turtle.getPosition().posY, turtle.getPosition().posZ);
				}
				noteBuffer.clear();
			}
		}
	}

	@Override
	public String getUnlocalisedAdjective() {
		return "Musical";
	}

	@Override
	public ItemStack getCraftingItem() {
		return new ItemStack(Computronics.ironNote, 1, 0);
	}

	@Override
	public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
		return new MusicalTurtlePeripheral(turtle);
	}

	@Override
	public IIcon getIcon(ITurtleAccess turtle, TurtleSide side) {
		return Computronics.ironNote.getIcon(2, 0);
	}
}

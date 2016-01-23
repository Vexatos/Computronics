package pl.asie.computronics.cc;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.NoteUtils;

public class MusicalTurtleUpgrade extends TurtleUpgradeBase {

	private static class MusicalTurtlePeripheral extends TurtlePeripheralBase {

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
				if(arguments.length >= 1) {
					if(arguments.length >= 2 && (arguments[1] instanceof Double)) {
						if(arguments[0] != null) {
							if(arguments[0] instanceof Double) {
								NoteUtils.playNote(access.getWorld(), access.getPosition(), ((Double) arguments[0]).intValue(), ((Double) arguments[1]).intValue(), NoteUtils.toVolume(arguments, 2));
							} else if(arguments[0] instanceof String) {
								NoteUtils.playNote(access.getWorld(), access.getPosition(), (String) arguments[0], ((Double) arguments[1]).intValue(), NoteUtils.toVolume(arguments, 2));
							}
						} else {
							NoteUtils.playNote(access.getWorld(), access.getPosition(), -1, ((Double) arguments[1]).intValue(), NoteUtils.toVolume(arguments, 2));
						}
					} else if((arguments[0] instanceof Double)) {
						NoteUtils.playNote(access.getWorld(), access.getPosition(), -1, ((Double) arguments[0]).intValue(), NoteUtils.toVolume(arguments, 1));
					}
				}
			} catch(IllegalArgumentException e) {
				throw new LuaException(e.getMessage());
			}
			return null;
		}
	}

	public MusicalTurtleUpgrade(String id) {
		super(id);
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
}

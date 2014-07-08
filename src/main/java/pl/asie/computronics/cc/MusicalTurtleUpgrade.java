package pl.asie.computronics.cc;

import cpw.mods.fml.common.Optional;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.util.ChatBoxUtils;
import pl.asie.computronics.util.NoteUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;

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
	    @Optional.Method(modid="ComputerCraft")
		public String[] getMethodNames() {
			return new String[]{"playNote"};
		}

		@Override
	    @Optional.Method(modid="ComputerCraft")
		public Object[] callMethod(IComputerAccess computer, ILuaContext context,
				int method, Object[] arguments) throws LuaException,
				InterruptedException {
			if(arguments.length == 1 && (arguments[0] instanceof Double)) {
				NoteUtils.playNote(access.getWorld(), access.getPosition().posX, access.getPosition().posY, access.getPosition().posZ, 0, ((Double)arguments[0]).intValue());
			} else if(arguments.length == 2 && (arguments[1] instanceof Double)) {
				if(arguments[0] instanceof Double) {
					NoteUtils.playNote(access.getWorld(), access.getPosition().posX, access.getPosition().posY, access.getPosition().posZ, ((Double)arguments[0]).intValue(), ((Double)arguments[1]).intValue());
				} else if(arguments[0] instanceof String) {
					NoteUtils.playNote(access.getWorld(), access.getPosition().posX, access.getPosition().posY, access.getPosition().posZ, (String)arguments[0], ((Double)arguments[1]).intValue());
				}
			}
			return null;
		}
	}
	public MusicalTurtleUpgrade(int id) {
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

	@Override
	public IIcon getIcon(ITurtleAccess turtle, TurtleSide side) {
		return Computronics.ironNote.getIcon(2, 0);
	}
}

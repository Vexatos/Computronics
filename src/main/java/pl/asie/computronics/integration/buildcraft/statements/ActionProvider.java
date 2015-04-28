package pl.asie.computronics.integration.buildcraft.statements;

import buildcraft.api.statements.IActionExternal;
import buildcraft.api.statements.IActionInternal;
import buildcraft.api.statements.IActionProvider;
import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.StatementManager;
import li.cil.oc.api.internal.Case;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.integration.buildcraft.statements.actions.Actions;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileColorfulLamp;
import pl.asie.computronics.tile.TileTapeDrive;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author Vexatos
 */
public class ActionProvider implements IActionProvider {

	public static void initialize() {
		Actions.initialize();
		StatementManager.registerActionProvider(new ActionProvider());
	}

	@Override
	public Collection<IActionInternal> getInternalActions(IStatementContainer iStatementContainer) {
		return null;
	}

	@Override
	public Collection<IActionExternal> getExternalActions(ForgeDirection side, TileEntity tile) {
		LinkedList<IActionExternal> actions = new LinkedList<IActionExternal>();
		if(tile != null) {
			if(Mods.isLoaded(Mods.OpenComputers) && tile instanceof Case) {
				actions.add(Actions.Computer_Start);
				actions.add(Actions.Computer_Stop);
			}
			if(tile instanceof TileTapeDrive) {
				actions.add(Actions.TapeDrive_Start);
				actions.add(Actions.TapeDrive_Stop);
				actions.add(Actions.TapeDrive_Rewind);
				actions.add(Actions.TapeDrive_Forward);
			}
			if(tile instanceof TileColorfulLamp) {
				actions.add(Actions.Lamp_SetColor);
				actions.add(Actions.Lamp_ResetColor);
			}
		}
		return actions;
	}
}

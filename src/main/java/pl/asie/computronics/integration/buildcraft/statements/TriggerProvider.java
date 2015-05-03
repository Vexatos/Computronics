package pl.asie.computronics.integration.buildcraft.statements;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.ITriggerExternal;
import buildcraft.api.statements.ITriggerInternal;
import buildcraft.api.statements.ITriggerProvider;
import buildcraft.api.statements.StatementManager;
import li.cil.oc.api.internal.Case;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.integration.buildcraft.statements.triggers.Triggers;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileTapeDrive;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author Vexatos
 */
public class TriggerProvider implements ITriggerProvider {

	public static void initialize() {
		Triggers.initialize();
		StatementManager.registerTriggerProvider(new TriggerProvider());
	}

	@Override
	public Collection<ITriggerInternal> getInternalTriggers(IStatementContainer iStatementContainer) {
		return null;
	}

	@Override
	public Collection<ITriggerExternal> getExternalTriggers(ForgeDirection side, TileEntity tile) {
		LinkedList<ITriggerExternal> triggers = new LinkedList<ITriggerExternal>();
		if(tile != null) {
			if(Mods.isLoaded(Mods.OpenComputers) && tile instanceof Case) {
				triggers.add(Triggers.Computer_Running);
				triggers.add(Triggers.Computer_Stopped);
			}
			if(tile instanceof TileTapeDrive) {
				triggers.add(Triggers.TapeDrive_Playing);
				triggers.add(Triggers.TapeDrive_Stopped);
				triggers.add(Triggers.TapeDrive_Rewinding);
				triggers.add(Triggers.TapeDrive_Forwarding);
			}
		}
		return triggers;
	}
}

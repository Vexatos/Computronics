package pl.asie.computronics.integration.buildcraft.statements.triggers;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.tile.TapeDriveState.State;
import pl.asie.computronics.tile.TileTapeDrive;

/**
 * @author Vexatos
 */
public class TriggerTapeDrive implements IComputronicsTrigger {

	private State state;

	public TriggerTapeDrive(State state) {
		this.state = state;
	}

	@Override
	public boolean isTriggerActive(TileEntity tile, ForgeDirection side, IStatementContainer container, IStatementParameter[] statements) {
		return tile != null && tile instanceof TileTapeDrive
			&& ((TileTapeDrive) tile).getEnumState() == state;
	}
}

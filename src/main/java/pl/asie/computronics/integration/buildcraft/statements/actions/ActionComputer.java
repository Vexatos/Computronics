package pl.asie.computronics.integration.buildcraft.statements.actions;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;
import li.cil.oc.api.internal.Case;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 * @author Vexatos
 */
public class ActionComputer {

	public static class Start implements IComputronicsAction {

		@Override
		public void actionActivate(TileEntity tile, EnumFacing side, IStatementContainer container, IStatementParameter[] parameters) {
			if(tile != null && tile instanceof Case && ((Case) tile).machine() != null
				&& !((Case) tile).machine().isRunning()) {

				((Case) tile).machine().start();
			}
		}
	}

	public static class Stop implements IComputronicsAction {

		@Override
		public void actionActivate(TileEntity tile, EnumFacing side, IStatementContainer container, IStatementParameter[] parameters) {
			if(tile != null && tile instanceof Case && ((Case) tile).machine() != null
				&& ((Case) tile).machine().isRunning()) {

				((Case) tile).machine().stop();
			}
		}
	}
}

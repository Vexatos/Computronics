package pl.asie.computronics.integration.buildcraft.statements.actions;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 * @author Vexatos
 */
public interface IComputronicsAction {

	void actionActivate(TileEntity tile, EnumFacing side, IStatementContainer container, IStatementParameter[] parameters);

}

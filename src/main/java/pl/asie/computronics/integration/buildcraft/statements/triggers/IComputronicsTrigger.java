package pl.asie.computronics.integration.buildcraft.statements.triggers;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 * @author Vexatos
 */
public interface IComputronicsTrigger {

	boolean isTriggerActive(TileEntity tile, EnumFacing side, IStatementContainer container, IStatementParameter[] statements);

}

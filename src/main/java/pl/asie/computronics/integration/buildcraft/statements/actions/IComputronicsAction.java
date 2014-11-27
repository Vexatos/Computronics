package pl.asie.computronics.integration.buildcraft.statements.actions;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author Vexatos
 */
public interface IComputronicsAction {

	public void actionActivate(TileEntity tile, ForgeDirection side, IStatementContainer container, IStatementParameter[] parameters);

}

package pl.asie.computronics.integration.buildcraft.actions;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.integration.buildcraft.parameters.ActionParameterLampColor;
import pl.asie.computronics.tile.TileColorfulLamp;

/**
 * @author Vexatos
 */
public class ActionLamp {

	public static class Set implements IComputronicsParameterAction {

		@Override
		public void actionActivate(TileEntity tile, ForgeDirection side, IStatementContainer container, IStatementParameter[] parameters) {
			if(tile != null && tile instanceof TileColorfulLamp) {

				TileColorfulLamp lamp = (TileColorfulLamp) tile;
				int color = 0x6318;

				if(parameters != null) {
					int red = 0, green = 0, blue = 0;

					if(parameters.length >= 1 && parameters[0] != null) {
						ItemStack stackRed = parameters[0].getItemStack();
						red = stackRed != null ? stackRed.stackSize : 0;
					}
					if(parameters.length >= 2 && parameters[1] != null) {
						ItemStack stackGreen = parameters[1].getItemStack();
						green = stackGreen != null ? stackGreen.stackSize : 0;
					}
					if(parameters.length >= 3 && parameters[2] != null) {
						ItemStack stackBlue = parameters[2].getItemStack();
						blue = stackBlue != null ? stackBlue.stackSize : 0;
					}
					color = (red << 10) | (green << 5) | blue;

				}

				if(lamp.getLampColor() != color) {
					lamp.setLampColor(color);
				}
			}
		}

		@Override
		public IStatementParameter createParameter(int index) {
			return new ActionParameterLampColor(index);
		}
	}

	public static class Reset implements IComputronicsAction {

		@Override
		public void actionActivate(TileEntity tile, ForgeDirection side, IStatementContainer container, IStatementParameter[] parameters) {
			if(tile != null && tile instanceof TileColorfulLamp) {

				TileColorfulLamp lamp = (TileColorfulLamp) tile;
				int color = 0x6318;
				if(lamp.getLampColor() != color) {
					lamp.setLampColor(color);
				}
			}
		}
	}
}
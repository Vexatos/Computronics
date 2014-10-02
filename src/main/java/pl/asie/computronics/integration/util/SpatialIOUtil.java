package pl.asie.computronics.integration.util;

import appeng.api.implementations.items.ISpatialStorageCell;
import net.minecraft.item.ItemStack;

/**
 * @author Vexatos
 */
public class SpatialIOUtil {
	public static String getCause(ItemStack input, ItemStack output, boolean revert) {
		if(revert) {
			if(input != null) {
				return "input slot is not empty";
			}
			if(output == null) {
				return "output slot is empty";
			}
			if(!isSpatialCell(output)) {
				return "output slot does not contain a spatial cell";
			}
		} else {
			if(input == null) {
				return "input slot is empty";
			}
			if(output != null) {
				return "output slot is not empty";
			}
			if(!isSpatialCell(input)) {
				return "input slot does not contain a spatial cell";
			}
		}
		return null;
	}

	/**
	 * Stolen from {@link appeng.tile.spatial.TileSpatialIOPort#isSpatialCell(net.minecraft.item.ItemStack)}
	 * @param cell the ItemStack to check
	 * @return Whether the item is a spatial storage cell
	 */
	@SuppressWarnings("ConstantConditions")
	public static boolean isSpatialCell(ItemStack cell) {
		if((cell != null) && ((cell.getItem() instanceof ISpatialStorageCell))) {
			ISpatialStorageCell sc = (ISpatialStorageCell) cell.getItem();
			return (sc != null) && (sc.isSpatialStorage(cell));
		}
		return false;
	}

	/**
	 * @return the spatial cell or <code>null</code> if the ItemStack does not contain a spacial cell
	 * @see #isSpatialCell(net.minecraft.item.ItemStack)
	 */
	@SuppressWarnings("ConstantConditions")
	public static ISpatialStorageCell getSpatialCell(ItemStack cell) {
		if((cell != null) && ((cell.getItem() instanceof ISpatialStorageCell))) {
			ISpatialStorageCell sc = (ISpatialStorageCell) cell.getItem();
			return ((sc != null) && (sc.isSpatialStorage(cell))) ? sc : null;
		}
		return null;
	}
}

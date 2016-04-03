package pl.asie.computronics.item;

import pl.asie.computronics.Computronics;
import pl.asie.lib.item.ItemMultiple;

/**
 * @author Vexatos
 */
public class ItemMultipleComputronics extends ItemMultiple {

	public ItemMultipleComputronics(String mod, String[] parts) {
		super(mod, parts);
	}

	public void registerItemModels() {
		if(!Computronics.proxy.isClient()) {
			return;
		}

		for(int i = 0; i < parts.length; i++) {
			registerItemModel(i);
		}
	}

	protected void registerItemModel(int meta) {
		Computronics.proxy.registerItemModel(this, meta, "computronics:" + parts[meta]);
	}
}

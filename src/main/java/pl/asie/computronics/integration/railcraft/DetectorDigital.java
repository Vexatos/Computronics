package pl.asie.computronics.integration.railcraft;

import cpw.mods.fml.common.Loader;
import mods.railcraft.common.blocks.detector.Detector;
import mods.railcraft.common.carts.EnumCart;
import net.minecraft.entity.item.EntityMinecart;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileDigitalDetector;

import java.util.List;

/**
 * @author CovertJaguar, Vexatos
 */
public class DetectorDigital extends Detector {

	@Override
	public int testCarts(List<EntityMinecart> carts) {
		if(this.getTile() instanceof TileDigitalDetector
			&& ((TileDigitalDetector) this.getTile()).node() != null) {

			for(EntityMinecart cart : carts) {
				EnumCart type = EnumCart.fromCart(cart);
				if(Loader.isModLoaded(Mods.OpenComputers)) {
					((TileDigitalDetector) this.getTile()).eventOC(cart, type);
				}
				if(Loader.isModLoaded(Mods.ComputerCraft)) {
					((TileDigitalDetector) this.getTile()).eventCC(cart, type);
				}
			}
		}
		return 0;
	}
}

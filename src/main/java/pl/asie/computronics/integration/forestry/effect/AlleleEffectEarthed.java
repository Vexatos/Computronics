package pl.asie.computronics.integration.forestry.effect;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import ic2.api.item.ElectricItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * @author Vexatos
 */
public class AlleleEffectEarthed extends AlleleEffectThrottled {

	public AlleleEffectEarthed() {
		super(10, "effectEarthed", "earthed");
	}

	@Override
	protected IEffectData doEffectThrottled(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		List<EntityPlayer> entities = getEntitiesInRange(genome, housing, EntityPlayer.class);
		for(EntityPlayer player : entities) {
			if(BeeManager.armorApiaristHelper.wearsItems((EntityLivingBase) player, getUID(), true) == 4) {
				continue;
			}
			InventoryPlayer inventory = player.inventory;
			boolean charged = false;
			for(ItemStack stack : inventory.armorInventory) {
				double energy = ElectricItem.manager.getCharge(stack);
				if(energy > 0) {
					charged = charged || ElectricItem.manager.discharge(stack, energy / 10D, Integer.MAX_VALUE, true, false, false) > 0;
				}
			}
			// TODO increase health.
		}
		return storedData;
	}
}

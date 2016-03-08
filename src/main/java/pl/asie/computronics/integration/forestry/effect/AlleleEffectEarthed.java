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
			InventoryPlayer inventory = player.inventory;
			boolean charged = false;
			for(ItemStack stack : inventory.armorInventory) {
				if(stack == null) {
					continue;
				}
				if(BeeManager.armorApiaristHelper.isArmorApiarist(stack, ((EntityLivingBase) player), getUID(), true)) {
					continue;
				}
				final double energy = ElectricItem.manager.getCharge(stack);
				final double maxEnergy = energy + ElectricItem.manager.charge(stack, Double.POSITIVE_INFINITY, Integer.MAX_VALUE, true, true);
				final double currentEnergy = maxEnergy / 10D;
				double energyToDrain = currentEnergy;
				if(energy > 0) {
					for(int i = 0; energyToDrain > 0 && i < 10; i++) {
						double discharged = ElectricItem.manager.discharge(stack, energy / 10D, Integer.MAX_VALUE, true, false, false);
						if(discharged <= 0) {
							break;
						}
						energyToDrain -= discharged;
					}
					charged = charged || currentEnergy > energyToDrain;
				}
			}
			// TODO increase health.
		}
		return storedData;
	}
}

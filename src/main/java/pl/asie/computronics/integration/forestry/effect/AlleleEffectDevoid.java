package pl.asie.computronics.integration.forestry.effect;

import cofh.api.energy.IEnergyContainerItem;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

import java.util.List;

/**
 * @author Vexatos
 */
public class AlleleEffectDevoid extends AlleleEffectThrottled {

	public AlleleEffectDevoid() {
		super(10, "effectDevoid", "devoid");
	}

	@Override
	protected IEffectData doEffectThrottled(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		List<EntityPlayer> entities = getEntitiesInRange(genome, housing, EntityPlayer.class);
		for(EntityPlayer player : entities) {
			InventoryPlayer inventory = player.inventory;
			boolean charged = false;
			for(ItemStack stack : inventory.armorInventory) {
				if(BeeManager.armorApiaristHelper.isArmorApiarist(stack, ((EntityLivingBase) player), getUID(), true)) {
					continue;
				}
				if(stack.getItem() instanceof IEnergyContainerItem) {
					IEnergyContainerItem item = (IEnergyContainerItem) stack.getItem();
					final int maxEnergy = item.getMaxEnergyStored(stack);
					final int currentEnergy = MathHelper.ceiling_double_int(maxEnergy / 10D);
					int energyToDrain = currentEnergy;
					if(currentEnergy > 0) {
						for(int i = 0; energyToDrain > 0 && i < 20; i++) {
							energyToDrain -= item.extractEnergy(stack, energyToDrain, false);
						}
					}
					charged = charged || currentEnergy > energyToDrain;
				}
			}
			// TODO increase health.
		}
		return storedData;
	}
}

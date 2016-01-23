package pl.asie.lib.tweak.enchantment;

import net.minecraft.enchantment.EnchantmentDamage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

/**
 * @author Vexatos
 */
public class EnchantmentBetterBane extends EnchantmentDamage {

	public EnchantmentBetterBane(int p_i1923_1_) {
		super(p_i1923_1_, new ResourceLocation("bane_of_arthropods_better"), 2, 2);
	}

	@Override
	public String getTranslatedName(int p_77316_1_) {
		String s = StatCollector.translateToLocal(this.getName());
		return s + " \u2468" /*+ StatCollector.translateToLocal("enchantment.asielib.level.9")*/;
	}

	@Override
	public void onEntityDamaged(EntityLivingBase attacker, Entity target, int level) {
		super.onEntityDamaged(attacker, target, 5);
	}

	@Override
	public boolean canApply(ItemStack p_92089_1_) {
		return false;
	}

	@Override
	public int getMaxLevel() {
		return 9;
	}

	@Override
	public int getMinLevel() {
		return 9;
	}

	@Override
	public int getMinEnchantability(int p_77321_1_) {
		//Nope.
		return 100;
	}

	@Override
	public int getMaxEnchantability(int p_77317_1_) {
		return 100;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		return false;
	}

	@Override
	public boolean isAllowedOnBooks() {
		return false;
	}
}

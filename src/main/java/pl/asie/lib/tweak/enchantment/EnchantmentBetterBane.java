package pl.asie.lib.tweak.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDamage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import pl.asie.computronics.util.StringUtil;

/**
 * @author Vexatos
 */
public class EnchantmentBetterBane extends EnchantmentDamage {

	public EnchantmentBetterBane() {
		super(Enchantment.Rarity.VERY_RARE, 2, EntityEquipmentSlot.MAINHAND);
	}

	@Override
	public String getTranslatedName(int p_77316_1_) {
		String s = StringUtil.localize(this.getName());
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

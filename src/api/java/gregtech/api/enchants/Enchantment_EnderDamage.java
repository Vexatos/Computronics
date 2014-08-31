package gregtech.api.enchants;

import gregtech.api.enums.ConfigCategories;
import gregtech.api.enums.Materials;
import gregtech.api.util.GT_Config;
import gregtech.api.util.GT_LanguageManager;
import net.minecraft.enchantment.EnchantmentDamage;
import net.minecraft.entity.EntityLivingBase;

public class Enchantment_EnderDamage extends EnchantmentDamage {
	public static Enchantment_EnderDamage INSTANCE;
	
	public Enchantment_EnderDamage() {
		super(GT_Config.addIDConfig(ConfigCategories.IDs.enchantments, "Disjunction", 15), 2, -1);
		GT_LanguageManager.addStringLocalization(getName(), "Disjunction");
		Materials.Silver			.setEnchantmentForTools(this, 2);
		Materials.Mercury			.setEnchantmentForTools(this, 3);
		Materials.Electrum			.setEnchantmentForTools(this, 3);
		Materials.SterlingSilver	.setEnchantmentForTools(this, 4);
		Materials.AstralSilver		.setEnchantmentForTools(this, 5);
		INSTANCE = this;
	}
	
    @Override
	public int getMinEnchantability(int aLevel) {
        return 5 + (aLevel - 1) * 8;
    }
    
    @Override
	public int getMaxEnchantability(int aLevel) {
        return this.getMinEnchantability(aLevel) + 20;
    }
    
    @Override
	public int getMaxLevel() {
        return 5;
    }

	public float calcModifierLiving(int aLevel, EntityLivingBase aEntity) {
        return aEntity.getClass().getName().indexOf(".") >= 0 && aEntity.getClass().getName().substring(aEntity.getClass().getName().lastIndexOf(".")).contains("Ender") ? aLevel * 2.5F : 0.0F;
    }
    
    @Override
	public String getName() {
        return "enchantment.damage.endermen";
    }
}

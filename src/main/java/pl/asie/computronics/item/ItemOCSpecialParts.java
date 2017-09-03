package pl.asie.computronics.item;

import li.cil.oc.api.driver.EnvironmentProvider;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.oc.driver.DriverMagicalMemory;
import pl.asie.computronics.oc.manual.IItemWithDocumentation;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.OCUtils;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Vexatos
 */
public class ItemOCSpecialParts extends ItemMultipleComputronics implements IItemWithDocumentation, EnvironmentProvider {

	public ItemOCSpecialParts() {
		super(Mods.Computronics, new String[] {
			"magical_memory"
		});
		this.setCreativeTab(Computronics.tab);
	}

	@Override
	public String getDocumentationName(ItemStack stack) {
		switch(stack.getItemDamage()) {
			case 0:
				return "magical_memory";
			default:
				return "index";
		}
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		switch(stack.getItemDamage()) {
			case 0:
				return EnumRarity.EPIC;
			default:
				return OCUtils.getRarityByTier(stack);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		OCUtils.addTooltip(stack, tooltip, flag);
	}

	@Nullable
	@Override
	public Class<?> getEnvironment(ItemStack stack) {
		if(!stack.getItem().equals(this)) {
			return null;
		}
		switch(stack.getItemDamage()) {
			case 0:
				return DriverMagicalMemory.class;
			default:
				return null;
		}
	}

	@Override
	public void registerItemModels() {
		if(!Computronics.proxy.isClient()) {
			return;
		}
		if(Config.OC_MAGICAL_MEMORY) {
			registerItemModel(0);
		}
	}
}

package gregtech.api.interfaces;

import gregtech.api.items.GT_MetaGenerated_Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;

public interface IFoodStat {
	public int getFoodLevel(GT_MetaGenerated_Item aItem, ItemStack aStack, EntityPlayer aPlayer);
	public float getSaturation(GT_MetaGenerated_Item aItem, ItemStack aStack, EntityPlayer aPlayer);
	public void onEaten(GT_MetaGenerated_Item aItem, ItemStack aStack, EntityPlayer aPlayer);
	public boolean alwaysEdible(GT_MetaGenerated_Item aItem, ItemStack aStack, EntityPlayer aPlayer);
	public EnumAction getFoodAction(GT_MetaGenerated_Item aItem, ItemStack aStack);
}
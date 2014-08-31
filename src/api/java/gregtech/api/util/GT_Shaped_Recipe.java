package gregtech.api.util;

import gregtech.api.GregTech_API;
import gregtech.api.items.GT_MetaGenerated_Tool;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class GT_Shaped_Recipe extends ShapedOreRecipe {
	public final boolean mDismantleable;
	
	public GT_Shaped_Recipe(ItemStack aResult, boolean aDismantleAble, Object... aRecipe) {
		super(aResult, aRecipe);
		int tDamage = aResult.getItemDamage();
		mDismantleable = aDismantleAble || (GT_Utility.getBlockFromStack(aResult) == GregTech_API.sBlockMachines && tDamage > 0 && tDamage < GregTech_API.METATILEENTITIES.length && GregTech_API.METATILEENTITIES[tDamage] != null && GregTech_API.METATILEENTITIES[tDamage].getTileEntityBaseType() < 4);
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting aGrid) {
		ItemStack rStack = super.getCraftingResult(aGrid);
		if (rStack != null) {
			// Charge Values
			if (GT_ModHandler.isElectricItem(rStack)) {
				int tCharge = 0;
				for (int i = 0; i < aGrid.getSizeInventory(); i++) tCharge += GT_ModHandler.dischargeElectricItem(aGrid.getStackInSlot(i), Integer.MAX_VALUE, Integer.MAX_VALUE, true, true, true);
				if (tCharge > 0) GT_ModHandler.chargeElectricItem(rStack, tCharge, Integer.MAX_VALUE, true, false);
			}
			
			// Saving Ingredients inside the Item.
			if (mDismantleable) {
				NBTTagCompound rNBT = rStack.getTagCompound(), tNBT = new NBTTagCompound();
				if (rNBT == null) rNBT = new NBTTagCompound();
				for (int i = 0; i < 9; i++) {
					ItemStack tStack = aGrid.getStackInSlot(i);
					if (tStack != null && GT_Utility.getContainerItem(tStack) == null && !(tStack.getItem() instanceof GT_MetaGenerated_Tool)) {
						tStack = GT_Utility.copyAmount(1, tStack);
						GT_ModHandler.dischargeElectricItem(tStack, Integer.MAX_VALUE, Integer.MAX_VALUE, true, false, true);
						tNBT.setTag("Ingredient."+i, tStack.writeToNBT(new NBTTagCompound()));
					}
				}
				rNBT.setTag("GT.CraftingComponents", tNBT);
				rStack.setTagCompound(rNBT);
			}
		}
		return rStack;
	}
}
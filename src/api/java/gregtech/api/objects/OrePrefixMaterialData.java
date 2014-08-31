package gregtech.api.objects;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import net.minecraft.item.ItemStack;

public class OrePrefixMaterialData {
	public boolean mBlackListed = false;
	public ItemStack mUnificationTarget = null;
	
	public OrePrefixes mPrefix;
	public Materials mMaterial;
	
	public OrePrefixMaterialData(OrePrefixes aPrefix, Materials aMaterial, boolean aBlackListed) {
		mPrefix = aPrefix;
		mMaterial = aMaterial;
		mBlackListed = aBlackListed;
	}
	
	public OrePrefixMaterialData(OrePrefixes aPrefix, Materials aMaterial) {
		this(aPrefix, aMaterial, false);
	}
	
	@Override
	public String toString() {
		return mPrefix.name() + mMaterial.name();
	}
}
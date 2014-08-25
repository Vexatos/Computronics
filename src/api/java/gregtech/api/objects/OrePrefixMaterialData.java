package gregtech.api.objects;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;

public class OrePrefixMaterialData {
	public OrePrefixes mPrefix;
	public Materials mMaterial;
	
	public OrePrefixMaterialData(OrePrefixes aPrefix, Materials aMaterial) {
		mPrefix = aPrefix;
		mMaterial = aMaterial;
	}
	
	@Override
	public String toString() {
		return mPrefix.name() + mMaterial.name();
	}
}
package gregtech.api.metatileentity.implementations;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import gregtech.api.GregTech_API;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.util.GT_Utility;
import gregtech.api.util.GT_Recipe.GT_Recipe_Map;

/**
 * NEVER INCLUDE THIS FILE IN YOUR MOD!!!
 * 
 * This is the main construct for my Basic Machines such as the Automatic Extractor
 * Extend this class to make a simple Machine
 */
public class GT_MetaTileEntity_BasicMachine_GT_Recipe extends GT_MetaTileEntity_BasicMachine {
	private final GT_Recipe_Map mRecipes;
	private final int mTankCapacity;
	private final String mSound;
	
	public GT_MetaTileEntity_BasicMachine_GT_Recipe(int aID, String aName, String aNameRegional, int aTier, String aDescription, GT_Recipe_Map aRecipes, int aInputSlots, int aOutputSlots, int aTankCapacity, String aGUIName, String aSound, ITexture... aOverlays) {
		super(aID, aName, aNameRegional, aTier, aDescription, aInputSlots, aOutputSlots, aGUIName, aRecipes.mUnlocalizedName, aOverlays);
		mTankCapacity = aTankCapacity;
		mRecipes = aRecipes;
		mSound = aSound;
	}
	
	public GT_MetaTileEntity_BasicMachine_GT_Recipe(String aName, int aTier, String aDescription, GT_Recipe_Map aRecipes, int aInputSlots, int aOutputSlots, int aTankCapacity, ITexture[][][] aTextures, String aGUIName, String aNEIName, String aSound) {
		super(aName, aTier, aDescription, aTextures, aInputSlots, aOutputSlots, aGUIName, aNEIName);
		mTankCapacity = aTankCapacity;
		mRecipes = aRecipes;
		mSound = aSound;
	}
	
	@Override
	public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
		return new GT_MetaTileEntity_BasicMachine_GT_Recipe(mName, mTier, mDescription, mRecipes, mInputSlotCount, mOutputItems==null?0:mOutputItems.length, mTankCapacity, mTextures, mGUIName, mNEIName, mSound);
	}
	
	@Override
	public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
		if (!super.allowPutStack(aBaseMetaTileEntity, aIndex, aSide, aStack)) return false;
		switch (mInputSlotCount) {
		case  0: return false;
		case  2: return ((getInputAt(0)!=null&&getInputAt(1)!=null) || (getInputAt(0)==null&&getInputAt(1)==null?getRecipeList().containsInput(aStack):null!=getRecipeList().findRecipe(mLastRecipe, true, GregTech_API.VOLTAGES[mTier], new FluidStack[] {getFillableStack()}, aIndex == getInputSlot() ? new ItemStack[] {aStack, getInputAt(1)} : new ItemStack[] {getInputAt(0), aStack})));
		default: return getRecipeList().containsInput(aStack);
		}
	}
	
	@Override
	public GT_Recipe_Map getRecipeList() {
		return mRecipes;
	}
	
	@Override
	public int getCapacity() {
		return mTankCapacity;
	}
	
	@Override
	public void startSoundLoop(byte aIndex, double aX, double aY, double aZ) {
		super.startSoundLoop(aIndex, aX, aY, aZ);
		if (aIndex == 1 && GT_Utility.isStringValid(mSound)) GT_Utility.doSoundAtClient(mSound, 100, 1.0F, aX, aY, aZ);
	}
	
	@Override
	public void startProcess() {
		if (GT_Utility.isStringValid(mSound)) sendLoopStart((byte)1);
	}
}
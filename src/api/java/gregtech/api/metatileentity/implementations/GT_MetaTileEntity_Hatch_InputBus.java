package gregtech.api.metatileentity.implementations;

import gregtech.api.enums.Textures;
import gregtech.api.gui.*;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_Recipe.GT_Recipe_Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class GT_MetaTileEntity_Hatch_InputBus extends GT_MetaTileEntity_Hatch {
	public GT_Recipe_Map mRecipeMap = null;
	
	public GT_MetaTileEntity_Hatch_InputBus(int aID, String aName, String aNameRegional, int aTier) {
		super(aID, aName, aNameRegional, aTier, aTier<1?1:aTier==1?4:aTier==2?9:16, "Better Input Hatch for more complex Multiblocks");
	}
	
	public GT_MetaTileEntity_Hatch_InputBus(String aName, int aTier, String aDescription, ITexture[][][] aTextures) {
		super(aName, aTier, aTier<1?1:aTier==1?4:aTier==2?9:16, aDescription, aTextures);
	}
	
	@Override
	public ITexture[] getTexturesActive(ITexture aBaseTexture) {
		return new ITexture[] {aBaseTexture, new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_PIPE_IN)};
	}
	
	@Override
	public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
		return new ITexture[] {aBaseTexture, new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_PIPE_IN)};
	}
	
	@Override public boolean isSimpleMachine()						{return true;}
	@Override public boolean isFacingValid(byte aFacing)			{return true;}
	@Override public boolean isAccessAllowed(EntityPlayer aPlayer)	{return true;}
	@Override public boolean isValidSlot(int aIndex)				{return true;}
	
	@Override
	public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
		return new GT_MetaTileEntity_Hatch_InputBus(mName, mTier, mDescription, mTextures);
	}
	
	@Override
	public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
		if (aBaseMetaTileEntity.isClientSide()) return true;
		aBaseMetaTileEntity.openGUI(aPlayer);
		return true;
	}
	
	@Override
    public Object getServerGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
		switch (mTier) {
		case  0: return new GT_Container_1by1(aPlayerInventory, aBaseMetaTileEntity);
		case  1: return new GT_Container_2by2(aPlayerInventory, aBaseMetaTileEntity);
		case  2: return new GT_Container_3by3(aPlayerInventory, aBaseMetaTileEntity);
		default: return new GT_Container_4by4(aPlayerInventory, aBaseMetaTileEntity);
		}
	}
	
	@Override
    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
		switch (mTier) {
		case  0: return new GT_GUIContainer_1by1(aPlayerInventory, aBaseMetaTileEntity, "Input Bus");
		case  1: return new GT_GUIContainer_2by2(aPlayerInventory, aBaseMetaTileEntity, "Input Bus");
		case  2: return new GT_GUIContainer_3by3(aPlayerInventory, aBaseMetaTileEntity, "Input Bus");
		case  3: return new GT_GUIContainer_4by4(aPlayerInventory, aBaseMetaTileEntity, "Input Bus");
		default: return new GT_GUIContainer_4by4(aPlayerInventory, aBaseMetaTileEntity, "Input Bus");
		}
	}
	
	public void updateSlots() {
		for (int i = 0; i < mInventory.length; i++) if (mInventory[i] != null && mInventory[i].stackSize <= 0) mInventory[i] = null;
	}
	
	@Override
	public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
		return false;
	}
	
	@Override
	public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
		return aSide == getBaseMetaTileEntity().getFrontFacing() && (mRecipeMap == null || mRecipeMap.containsInput(aStack));
	}
}

package gregtech.api.metatileentity.implementations;

import gregtech.api.GregTech_API;
import gregtech.api.gui.GT_Container_MultiMachine;
import gregtech.api.gui.GT_GUIContainer_MultiMachine;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.objects.GT_ItemStack;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_Recipe.GT_Recipe_Map;
import gregtech.api.util.GT_Utility;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

public abstract class GT_MetaTileEntity_MultiBlockBase extends MetaTileEntity {
	
	public boolean mMachine = false, mWrench = false, mScrewdriver = false, mSoftHammer = false, mHardHammer = false, mSolderingTool = false, mCrowbar = false;
	public int mPollution = 0, mProgresstime = 0, mMaxProgresstime = 0, mEUt = 0, mEfficiencyIncrease = 0, mUpdate = 0, mStartUpCheck = 100, mRuntime = 0, mEfficiency = 0;
	public ItemStack[] mOutputItems = null;
	
	public ArrayList<GT_MetaTileEntity_Hatch_Input> mInputHatches = new ArrayList<GT_MetaTileEntity_Hatch_Input>();
	public ArrayList<GT_MetaTileEntity_Hatch_Output> mOutputHatches = new ArrayList<GT_MetaTileEntity_Hatch_Output>();
	public ArrayList<GT_MetaTileEntity_Hatch_InputBus> mInputBusses = new ArrayList<GT_MetaTileEntity_Hatch_InputBus>();
	public ArrayList<GT_MetaTileEntity_Hatch_OutputBus> mOutputBusses = new ArrayList<GT_MetaTileEntity_Hatch_OutputBus>();
	public ArrayList<GT_MetaTileEntity_Hatch_Dynamo> mDynamoHatches = new ArrayList<GT_MetaTileEntity_Hatch_Dynamo>();
	public ArrayList<GT_MetaTileEntity_Hatch_Muffler> mMufflerHatches = new ArrayList<GT_MetaTileEntity_Hatch_Muffler>();
	public ArrayList<GT_MetaTileEntity_Hatch_Energy> mEnergyHatches = new ArrayList<GT_MetaTileEntity_Hatch_Energy>();
	public ArrayList<GT_MetaTileEntity_Hatch_Maintenance> mMaintenanceHatches = new ArrayList<GT_MetaTileEntity_Hatch_Maintenance>();
	
	public GT_MetaTileEntity_MultiBlockBase(int aID, String aName, String aNameRegional) {
		super(aID, aName, aNameRegional, 2);
	}
	
	public GT_MetaTileEntity_MultiBlockBase(String aName) {
		super(aName, 2);
	}
	
	@Override
	public boolean allowCoverOnSide(byte aSide, GT_ItemStack aCoverID) {
		return aSide != getBaseMetaTileEntity().getFrontFacing();
	}
	
	@Override public boolean isSimpleMachine()						{return false;}
	@Override public boolean isFacingValid(byte aFacing)			{return true;}
	@Override public boolean isAccessAllowed(EntityPlayer aPlayer)	{return true;}
	@Override public boolean isValidSlot(int aIndex)				{return aIndex > 0;}
	@Override public int getProgresstime()							{return mProgresstime;}
	@Override public int maxProgresstime()							{return mMaxProgresstime;}
	@Override public int increaseProgress(int aProgress)			{return aProgress;}
	
	@Override
	public void saveNBTData(NBTTagCompound aNBT) {
    	aNBT.setInteger("mEUt", mEUt);
    	aNBT.setInteger("mProgresstime", mProgresstime);
    	aNBT.setInteger("mMaxProgresstime", mMaxProgresstime);
    	aNBT.setInteger("mEfficiencyIncrease", mEfficiencyIncrease);
    	aNBT.setInteger("mEfficiency", mEfficiency);
    	aNBT.setInteger("mPollution", mPollution);
    	aNBT.setInteger("mRuntime", mRuntime);
    	
    	if (mOutputItems != null) for (int i = 0; i < mOutputItems.length; i++) if (mOutputItems[i] != null) {
            NBTTagCompound tNBT = new NBTTagCompound();
        	mOutputItems[i].writeToNBT(tNBT);
            aNBT.setTag("mOutputItem"+i, tNBT);
    	}
    	
		aNBT.setBoolean("mWrench", mWrench);
		aNBT.setBoolean("mScrewdriver", mScrewdriver);
		aNBT.setBoolean("mSoftHammer", mSoftHammer);
		aNBT.setBoolean("mHardHammer", mHardHammer);
		aNBT.setBoolean("mSolderingTool", mSolderingTool);
		aNBT.setBoolean("mCrowbar", mCrowbar);
	}
	
	@Override
	public void loadNBTData(NBTTagCompound aNBT) {
    	mEUt = aNBT.getInteger("mEUt");
    	mProgresstime = aNBT.getInteger("mProgresstime");
    	mMaxProgresstime = aNBT.getInteger("mMaxProgresstime");
    	mEfficiencyIncrease = aNBT.getInteger("mEfficiencyIncrease");
    	mEfficiency = aNBT.getInteger("mEfficiency");
    	mPollution = aNBT.getInteger("mPollution");
    	mRuntime = aNBT.getInteger("mRuntime");
    	mOutputItems = new ItemStack[getAmountOfOutputs()];
    	for (int i = 0; i < mOutputItems.length; i++) {
        	NBTTagCompound tNBT = (NBTTagCompound)aNBT.getTag("mOutputItem" + i);
        	if (tNBT != null) mOutputItems[i] = GT_Utility.loadItem(tNBT);
    	}
		mWrench = aNBT.getBoolean("mWrench");
		mScrewdriver = aNBT.getBoolean("mScrewdriver");
		mSoftHammer = aNBT.getBoolean("mSoftHammer");
		mHardHammer = aNBT.getBoolean("mHardHammer");
		mSolderingTool = aNBT.getBoolean("mSolderingTool");
		mCrowbar = aNBT.getBoolean("mCrowbar");
	}
	
	@Override
	public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
		if (aBaseMetaTileEntity.isClientSide()) return true;
		aBaseMetaTileEntity.openGUI(aPlayer);
		return true;
	}
	
	@Override
    public Object getServerGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
		return new GT_Container_MultiMachine(aPlayerInventory, aBaseMetaTileEntity);
	}
	
	@Override
    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
		return new GT_GUIContainer_MultiMachine(aPlayerInventory, aBaseMetaTileEntity, getLocalName(), "MultiblockDisplay.png");
	}
	
	@Override
	public byte getTileEntityBaseType() {
		return 2;
	}
	
    @Override
    public void onMachineBlockUpdate() {
    	mUpdate = 50;
    }
    
	@Override
	public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
		if (aBaseMetaTileEntity.isServerSide()) {
    		if (mEfficiency < 0) mEfficiency = 0;
			if (--mUpdate==0 || --mStartUpCheck==0) {
				mInputHatches.clear();
				mInputBusses.clear();
				mOutputHatches.clear();
				mOutputBusses.clear();
				mDynamoHatches.clear();
				mEnergyHatches.clear();
				mMufflerHatches.clear();
				mMaintenanceHatches.clear();
				mMachine = checkMachine(aBaseMetaTileEntity, mInventory[1]);
			}
			if (mStartUpCheck < 0) {
				if (mMachine) {
					for (GT_MetaTileEntity_Hatch_Maintenance tHatch : mMaintenanceHatches) {
						if (isValidMetaTileEntity(tHatch)) {
							if (tHatch.mWrench) mWrench = true;
							if (tHatch.mScrewdriver) mScrewdriver = true;
							if (tHatch.mSoftHammer) mSoftHammer = true;
							if (tHatch.mHardHammer) mHardHammer = true;
							if (tHatch.mSolderingTool) mSolderingTool = true;
							if (tHatch.mCrowbar) mCrowbar = true;
							
							tHatch.mWrench = false;
							tHatch.mScrewdriver = false;
							tHatch.mSoftHammer = false;
							tHatch.mHardHammer = false;
							tHatch.mSolderingTool = false;
							tHatch.mCrowbar = false;
						}
					}
					
					if (getRepairStatus() > 0) {
				    	if (mMaxProgresstime > 0 && doRandomMaintenanceDamage()) {
				    		if (onRunningTick(mInventory[1])) {
					    		if (!polluteEnvironment(getPollutionPerTick(mInventory[1]))) {
					    			stopMachine();
					    		}
					    		
						    	if (mMaxProgresstime > 0 && ++mProgresstime>=mMaxProgresstime) {
						    		if (mOutputItems != null) for (ItemStack tStack : mOutputItems) if (tStack != null) addOutput(tStack);
						    		mEfficiency = Math.max(0, Math.min(mEfficiency + mEfficiencyIncrease, getMaxEfficiency(mInventory[1]) - ((getIdealStatus() - getRepairStatus()) * 1000)));
						    		mOutputItems = null;
						    		mProgresstime = 0;
						    		mMaxProgresstime = 0;
						    		mEfficiencyIncrease = 0;
						    		if (aBaseMetaTileEntity.isAllowedToWork()) checkRecipe(mInventory[1]);
						    	}
				    		}
				    	} else {
				    		if (aTick % 100 == 0 || aBaseMetaTileEntity.hasWorkJustBeenEnabled() || aBaseMetaTileEntity.hasInventoryBeenModified()) {
					    		if (aBaseMetaTileEntity.isAllowedToWork()) checkRecipe(mInventory[1]);
					    		if (mMaxProgresstime <= 0) mEfficiency = Math.max(0, mEfficiency - 1000);
				    		}
				    	}
					} else {
						stopMachine();
					}
				} else {
					stopMachine();
				}
			}
			aBaseMetaTileEntity.setErrorDisplayID((aBaseMetaTileEntity.getErrorDisplayID()&~127)|(mWrench?0:1)|(mScrewdriver?0:2)|(mSoftHammer?0:4)|(mHardHammer?0:8)|(mSolderingTool?0:16)|(mCrowbar?0:32)|(mMachine?0:64));
			aBaseMetaTileEntity.setActive(mMaxProgresstime>0);
		}
	}
	
	public boolean polluteEnvironment(int aPollutionLevel) {
		mPollution += aPollutionLevel;
		for (GT_MetaTileEntity_Hatch_Muffler tHatch : mMufflerHatches) {
			if (isValidMetaTileEntity(tHatch)) {
				if (mPollution >= 10000) {
					if (tHatch.polluteEnvironment()) {
			    		mPollution -= 10000;
					}
				} else {
					break;
				}
			}
		}
		return mPollution < 10000;
	}
	
	/**
	 * Called every tick the Machine runs
	 */
	public boolean onRunningTick(ItemStack aStack) {
		if (mEUt > 0) {
			addEnergyOutput(((long)mEUt * mEfficiency) / 10000);
			return true;
		}
		if (mEUt < 0) {
			if (!drainEnergyInput(((long)-mEUt * 10000) / Math.max(1000, mEfficiency))) {
				stopMachine();
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if this is a Correct Machine Part for this kind of Machine (Turbine Rotor for example)
	 */
	public abstract boolean isCorrectMachinePart(ItemStack aStack);
	
	/**
	 * Checks the Recipe
	 */
	public abstract boolean checkRecipe(ItemStack aStack);
	
	/**
	 * Checks the Machine. You have to assign the MetaTileEntities for the Hatches here.
	 */
	public abstract boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack);
	
	/**
	 * Gets the maximum Efficiency that spare Part can get (0 - 10000)
	 */
	public abstract int getMaxEfficiency(ItemStack aStack);
	
	/**
	 * Gets the pollution this Device outputs to a Muffler per tick (10000 = one Pullution Block)
	 */
	public abstract int getPollutionPerTick(ItemStack aStack);
	
	/**
	 * Gets the damage to the ItemStack, usually 0 or 1.
	 */
	public abstract int getDamageToComponent(ItemStack aStack);
	
	/**
	 * Gets the Amount of possibly outputted Items for loading the Output Stack Array from NBT.
	 * This should be the largest Amount that can ever happen legitimately.
	 */
	public abstract int getAmountOfOutputs();
	
	/**
	 * If it explodes when the Component has to be replaced.
	 */
	public abstract boolean explodesOnComponentBreak(ItemStack aStack);
	
	public void stopMachine() {
		mOutputItems = null;
    	mEUt = 0;
    	mEfficiency = 0;
		mProgresstime = 0;
		mMaxProgresstime = 0;
		mEfficiencyIncrease = 0;
		getBaseMetaTileEntity().disableWorking();
	}
	
	public int getRepairStatus() {
		return (mWrench?1:0) + (mScrewdriver?1:0) + (mSoftHammer?1:0) + (mHardHammer?1:0) + (mSolderingTool?1:0) + (mCrowbar?1:0);
	}
	
	public int getIdealStatus() {
		return 6;
	}
	
	public static boolean isValidMetaTileEntity(MetaTileEntity aMetaTileEntity) {
		return aMetaTileEntity.getBaseMetaTileEntity() != null && aMetaTileEntity.getBaseMetaTileEntity().getMetaTileEntity() == aMetaTileEntity && !aMetaTileEntity.getBaseMetaTileEntity().isDead();
	}
	
	public boolean doRandomMaintenanceDamage() {
		if (!isCorrectMachinePart(mInventory[1]) || getRepairStatus() == 0) {
			stopMachine();
			return false;
		}
		if (mRuntime++>1000) {
			mRuntime = 0;
			if (getBaseMetaTileEntity().getRandomNumber(6000) == 0) {
				switch (getBaseMetaTileEntity().getRandomNumber(6)) {
				case  0: mWrench = false; break;
				case  1: mScrewdriver = false; break;
				case  2: mSoftHammer = false; break;
				case  3: mHardHammer = false; break;
				case  4: mSolderingTool = false; break;
				case  5: mCrowbar = false; break;
				}
			}
			if (mInventory[1] != null && getBaseMetaTileEntity().getRandomNumber(2) == 0) {
				mInventory[1].setItemDamage(mInventory[1].getItemDamage() + getDamageToComponent(mInventory[1]));
				if (mInventory[1].getItemDamage() >= mInventory[1].getMaxDamage()) {
					if (explodesOnComponentBreak(mInventory[1])) {
						explodeMultiblock();
					} else {
						mInventory[1] = null;
					}
					return false;
				}
			}
		}
		return true;
	}
	
	public void explodeMultiblock() {
		mInventory[1] = null;
		for (MetaTileEntity tTileEntity : mInputBusses) tTileEntity.getBaseMetaTileEntity().doExplosion(GregTech_API.VOLTAGE_ULTIMATE);
		for (MetaTileEntity tTileEntity : mOutputBusses) tTileEntity.getBaseMetaTileEntity().doExplosion(GregTech_API.VOLTAGE_ULTIMATE);
		for (MetaTileEntity tTileEntity : mInputHatches) tTileEntity.getBaseMetaTileEntity().doExplosion(GregTech_API.VOLTAGE_ULTIMATE);
		for (MetaTileEntity tTileEntity : mOutputHatches) tTileEntity.getBaseMetaTileEntity().doExplosion(GregTech_API.VOLTAGE_ULTIMATE);
		for (MetaTileEntity tTileEntity : mDynamoHatches) tTileEntity.getBaseMetaTileEntity().doExplosion(GregTech_API.VOLTAGE_ULTIMATE);
		for (MetaTileEntity tTileEntity : mMufflerHatches) tTileEntity.getBaseMetaTileEntity().doExplosion(GregTech_API.VOLTAGE_ULTIMATE);
		for (MetaTileEntity tTileEntity : mEnergyHatches) tTileEntity.getBaseMetaTileEntity().doExplosion(GregTech_API.VOLTAGE_ULTIMATE);
		for (MetaTileEntity tTileEntity : mMaintenanceHatches) tTileEntity.getBaseMetaTileEntity().doExplosion(GregTech_API.VOLTAGE_ULTIMATE);
		getBaseMetaTileEntity().doExplosion(GregTech_API.VOLTAGE_ULTIMATE);
	}
	
	public boolean addEnergyOutput(long aEU) {
		if (aEU <= 0) return true;
		for (GT_MetaTileEntity_Hatch_Dynamo tHatch : mDynamoHatches) {
			if (isValidMetaTileEntity(tHatch)) {
				if (tHatch.getBaseMetaTileEntity().increaseStoredEnergyUnits(aEU, false)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public long getMaxInputVoltage() {
		long rVoltage = 0;
		for (GT_MetaTileEntity_Hatch_Energy tHatch : mEnergyHatches) if (isValidMetaTileEntity(tHatch)) rVoltage += tHatch.getBaseMetaTileEntity().getInputVoltage();
		return rVoltage;
	}
	
	public boolean drainEnergyInput(long aEU) {
		if (aEU <= 0) return true;
		for (GT_MetaTileEntity_Hatch_Energy tHatch : mEnergyHatches) if (isValidMetaTileEntity(tHatch)) {
			if (tHatch.getBaseMetaTileEntity().decreaseStoredEnergyUnits(aEU, false)) return true;
		}
		return false;
	}
	
	public boolean addOutput(FluidStack aLiquid) {
		if (aLiquid == null) return false;
		for (GT_MetaTileEntity_Hatch_Output tHatch : mOutputHatches) {
			if (isValidMetaTileEntity(tHatch) && aLiquid.isFluidEqual(GT_ModHandler.getSteam(1))?tHatch.outputsSteam():tHatch.outputsLiquids()) {
				int tAmount = tHatch.fill(aLiquid, false);
				if (tAmount >= aLiquid.amount) {
					return tHatch.fill(aLiquid, true) >= aLiquid.amount;
				}
			}
		}
		return false;
	}
	
	public boolean depleteInput(FluidStack aLiquid) {
		if (aLiquid == null) return false;
		for (GT_MetaTileEntity_Hatch_Input tHatch : mInputHatches) {
			tHatch.mRecipeMap = getRecipeMap();
			if (isValidMetaTileEntity(tHatch)) {
				FluidStack tLiquid = tHatch.getFluid();
				if (tLiquid != null && tLiquid.isFluidEqual(aLiquid)) {
					tLiquid = tHatch.drain(aLiquid.amount, false);
					if (tLiquid != null && tLiquid.amount >= aLiquid.amount) {
						tLiquid = tHatch.drain(aLiquid.amount, true);
						return tLiquid != null && tLiquid.amount >= aLiquid.amount;
					}
				}
			}
		}
		return false;
	}
	
	public boolean addOutput(ItemStack aStack) {
		if (GT_Utility.isStackInvalid(aStack)) return false;
		aStack = GT_Utility.copy(aStack);
		FluidStack aLiquid = GT_Utility.getFluidForFilledItem(aStack);
		if (aLiquid == null) {
			for (GT_MetaTileEntity_Hatch_OutputBus tHatch : mOutputBusses) {
				if (isValidMetaTileEntity(tHatch)) {
					for (int i = tHatch.getSizeInventory() - 1; i >= 0; i--) {
						if (tHatch.getBaseMetaTileEntity().addStackToSlot(i, aStack)) return true;
					}
				}
			}
			for (GT_MetaTileEntity_Hatch_Output tHatch : mOutputHatches) {
				if (isValidMetaTileEntity(tHatch) && tHatch.outputsItems()) {
					if (tHatch.getBaseMetaTileEntity().addStackToSlot(1, aStack)) return true;
				}
			}
		} else {
			for (GT_MetaTileEntity_Hatch_Output tHatch : mOutputHatches) {
				if (isValidMetaTileEntity(tHatch) && aLiquid.isFluidEqual(GT_ModHandler.getSteam(1))?tHatch.outputsSteam():tHatch.outputsLiquids()) {
					int tAmount = tHatch.fill(aLiquid, false);
					if (tAmount >= aLiquid.amount) {
						return tHatch.fill(aLiquid, true) >= aLiquid.amount;
					}
				}
			}
		}
		return false;
	}
	
	public boolean depleteInput(ItemStack aStack) {
		if (GT_Utility.isStackInvalid(aStack)) return false;
		FluidStack aLiquid = GT_Utility.getFluidForFilledItem(aStack);
		if (aLiquid != null) return depleteInput(aLiquid);
		for (GT_MetaTileEntity_Hatch_Input tHatch : mInputHatches) {
			tHatch.mRecipeMap = getRecipeMap();
			if (isValidMetaTileEntity(tHatch)) {
				if (GT_Utility.areStacksEqual(aStack, tHatch.getBaseMetaTileEntity().getStackInSlot(0))) {
					if (tHatch.getBaseMetaTileEntity().getStackInSlot(0).stackSize >= aStack.stackSize) {
						tHatch.getBaseMetaTileEntity().decrStackSize(0, aStack.stackSize);
						return true;
					}
				}
			}
		}
		for (GT_MetaTileEntity_Hatch_InputBus tHatch : mInputBusses) {
			tHatch.mRecipeMap = getRecipeMap();
			if (isValidMetaTileEntity(tHatch)) {
				for (int i = tHatch.getBaseMetaTileEntity().getSizeInventory() - 1; i >= 0; i--) {
					if (GT_Utility.areStacksEqual(aStack, tHatch.getBaseMetaTileEntity().getStackInSlot(i))) {
						if (tHatch.getBaseMetaTileEntity().getStackInSlot(0).stackSize >= aStack.stackSize) {
							tHatch.getBaseMetaTileEntity().decrStackSize(0, aStack.stackSize);
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	public ArrayList<ItemStack> getStoredOutputs() {
		ArrayList<ItemStack> rList = new ArrayList<ItemStack>();
		for (GT_MetaTileEntity_Hatch_Output tHatch : mOutputHatches) {
			if (isValidMetaTileEntity(tHatch)) {
				rList.add(tHatch.getBaseMetaTileEntity().getStackInSlot(1));
			}
		}
		for (GT_MetaTileEntity_Hatch_OutputBus tHatch : mOutputBusses) {
			if (isValidMetaTileEntity(tHatch)) {
				for (int i = tHatch.getBaseMetaTileEntity().getSizeInventory() - 1; i >= 0; i--) {
					rList.add(tHatch.getBaseMetaTileEntity().getStackInSlot(i));
				}
			}
		}
		return rList;
	}
	
	public ArrayList<ItemStack> getStoredInputs() {
		ArrayList<ItemStack> rList = new ArrayList<ItemStack>();
		for (GT_MetaTileEntity_Hatch_Input tHatch : mInputHatches) {
			tHatch.mRecipeMap = getRecipeMap();
			if (isValidMetaTileEntity(tHatch) && tHatch.getBaseMetaTileEntity().getStackInSlot(0) != null) {
				rList.add(tHatch.getBaseMetaTileEntity().getStackInSlot(0));
			}
		}
		for (GT_MetaTileEntity_Hatch_InputBus tHatch : mInputBusses) {
			tHatch.mRecipeMap = getRecipeMap();
			if (isValidMetaTileEntity(tHatch)) {
				for (int i = tHatch.getBaseMetaTileEntity().getSizeInventory() - 1; i >= 0; i--) {
					if (tHatch.getBaseMetaTileEntity().getStackInSlot(i) != null) rList.add(tHatch.getBaseMetaTileEntity().getStackInSlot(i));
				}
			}
		}
		return rList;
	}
	
	public GT_Recipe_Map getRecipeMap() {
		return null;
	}
	
	public void updateSlots() {
		for (GT_MetaTileEntity_Hatch_Input tHatch : mInputHatches) if (isValidMetaTileEntity(tHatch)) tHatch.updateSlots();
		for (GT_MetaTileEntity_Hatch_InputBus tHatch : mInputBusses) if (isValidMetaTileEntity(tHatch)) tHatch.updateSlots();
	}
	
	public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
		if (aTileEntity == null) return false;
		IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
		if (aMetaTileEntity == null) return false;
		if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch) ((GT_MetaTileEntity_Hatch)aMetaTileEntity).mMachineBlock = (byte)aBaseCasingIndex;
		if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_Input)			return mInputHatches.add((GT_MetaTileEntity_Hatch_Input)aMetaTileEntity);
		if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_InputBus)		return mInputBusses.add((GT_MetaTileEntity_Hatch_InputBus)aMetaTileEntity);
		if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_Output)			return mOutputHatches.add((GT_MetaTileEntity_Hatch_Output)aMetaTileEntity);
		if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_OutputBus)		return mOutputBusses.add((GT_MetaTileEntity_Hatch_OutputBus)aMetaTileEntity);
		if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_Energy)			return mEnergyHatches.add((GT_MetaTileEntity_Hatch_Energy)aMetaTileEntity);
		if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_Dynamo)			return mDynamoHatches.add((GT_MetaTileEntity_Hatch_Dynamo)aMetaTileEntity);
		if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_Maintenance)		return mMaintenanceHatches.add((GT_MetaTileEntity_Hatch_Maintenance)aMetaTileEntity);
		if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_Muffler)			return mMufflerHatches.add((GT_MetaTileEntity_Hatch_Muffler)aMetaTileEntity);
		return false;
	}
	
	public boolean addMaintenanceToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
		if (aTileEntity == null) return false;
		IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
		if (aMetaTileEntity == null) return false;
		if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_Maintenance) {
			((GT_MetaTileEntity_Hatch)aMetaTileEntity).mMachineBlock = (byte)aBaseCasingIndex;
			return mMaintenanceHatches.add((GT_MetaTileEntity_Hatch_Maintenance)aMetaTileEntity);
		}
		return false;
	}
	
	public boolean addEnergyInputToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
		if (aTileEntity == null) return false;
		IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
		if (aMetaTileEntity == null) return false;
		if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_Energy) {
			((GT_MetaTileEntity_Hatch)aMetaTileEntity).mMachineBlock = (byte)aBaseCasingIndex;
			return mEnergyHatches.add((GT_MetaTileEntity_Hatch_Energy)aMetaTileEntity);
		}
		return false;
	}
	
	public boolean addDynamoToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
		if (aTileEntity == null) return false;
		IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
		if (aMetaTileEntity == null) return false;
		if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_Dynamo) {
			((GT_MetaTileEntity_Hatch)aMetaTileEntity).mMachineBlock = (byte)aBaseCasingIndex;
			return mDynamoHatches.add((GT_MetaTileEntity_Hatch_Dynamo)aMetaTileEntity);
		}
		return false;
	}
	
	public boolean addMufflerToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
		if (aTileEntity == null) return false;
		IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
		if (aMetaTileEntity == null) return false;
		if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_Muffler) {
			((GT_MetaTileEntity_Hatch)aMetaTileEntity).mMachineBlock = (byte)aBaseCasingIndex;
			return mMufflerHatches.add((GT_MetaTileEntity_Hatch_Muffler)aMetaTileEntity);
		}
		return false;
	}
	
	public boolean addInputToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
		if (aTileEntity == null) return false;
		IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
		if (aMetaTileEntity == null) return false;
		if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_Input) {
			((GT_MetaTileEntity_Hatch)aMetaTileEntity).mMachineBlock = (byte)aBaseCasingIndex;
			((GT_MetaTileEntity_Hatch_Input)aMetaTileEntity).mRecipeMap = getRecipeMap();
			return mInputHatches.add((GT_MetaTileEntity_Hatch_Input)aMetaTileEntity);
		}
		if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_InputBus) {
			((GT_MetaTileEntity_Hatch)aMetaTileEntity).mMachineBlock = (byte)aBaseCasingIndex;
			((GT_MetaTileEntity_Hatch_InputBus)aMetaTileEntity).mRecipeMap = getRecipeMap();
			return mInputBusses.add((GT_MetaTileEntity_Hatch_InputBus)aMetaTileEntity);
		}
		return false;
	}
	
	public boolean addOutputToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
		if (aTileEntity == null) return false;
		IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
		if (aMetaTileEntity == null) return false;
		if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_Output) {
			((GT_MetaTileEntity_Hatch)aMetaTileEntity).mMachineBlock = (byte)aBaseCasingIndex;
			return mOutputHatches.add((GT_MetaTileEntity_Hatch_Output)aMetaTileEntity);
		}
		if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_OutputBus) {
			((GT_MetaTileEntity_Hatch)aMetaTileEntity).mMachineBlock = (byte)aBaseCasingIndex;
			return mOutputBusses.add((GT_MetaTileEntity_Hatch_OutputBus)aMetaTileEntity);
		}
		return false;
	}
	
	@Override
	public String[] getInfoData() {
		return new String[] {"Progress:", (mProgresstime/20)+"secs", (mMaxProgresstime/20)+"secs", "Efficiency:", (mEfficiency/100.0F)+"%", "Problems:", ""+(getIdealStatus() - getRepairStatus())};
	}
	
	@Override
	public boolean isGivingInformation() {
		return true;
	}
	
	@Override
	public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
		return false;
	}
	
	@Override
	public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
		return false;
	}
}

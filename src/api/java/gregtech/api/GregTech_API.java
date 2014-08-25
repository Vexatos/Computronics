package gregtech.api;

import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IDamagableItem;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.internal.IGT_Mod;
import gregtech.api.interfaces.internal.IGT_RecipeAdder;
import gregtech.api.interfaces.internal.IThaumcraftCompat;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.items.GT_Tool_Item;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.net.IGT_NetworkHandler;
import gregtech.api.objects.GT_Cover_Default;
import gregtech.api.objects.GT_Cover_None;
import gregtech.api.objects.GT_ItemStack;
import gregtech.api.threads.GT_Runnable_MachineBlockUpdate;
import gregtech.api.util.*;
import gregtech.api.world.GT_Worldgen;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * This File contains the functions used to get Items and add Recipes. Please do not include this File in your Moddownload as it maybe ruins compatiblity, like with the IC2-API
 * You may just copy those Functions into your Code, or better call them via reflection.
 * 
 * The whole API is the basic construct of my Mod. Everything is dependent on it.
 * I change things quite often so please don't include any File inside your Mod, unless it's an Interface. Interfaces are not going to be changed during a MC-Version
 * Since some Authors were stupid enough to break this simple Rule, I added Version checks to enforce it.
 * 
 * In these Folders are many useful Functions. You can use them via reflection if you want.
 * I know not everything is compilable due to API's of other Mods, but these are easy to fix in your Setup.
 * 
 * You can use this to learn about Modding, but I would recommend simpler Mods.
 * You may even copypaste Code from these API-Files into your Mod, as I have nothing against that, but you should look exactly at what you are copying.
 * 
 * @author Gregorius Techneticies
 */
public class GregTech_API {
	/** For the API Version check */
	public static volatile int VERSION = 503;
	
	/** The Mod Object itself. That is the GT_Mod-Object. It's needed to open GUI's and similar. */
	public static IGT_Mod gregtech;
	
	/** For Internal Usage */
	public static IGT_NetworkHandler sNetworkHandler;
	
	/** Use this Object to add Recipes. All Recipe adding Functions have been moved to here! */
	public static IGT_RecipeAdder sRecipeAdder;
	
	/** Used to register Aspects to ThaumCraft, this Object might be null if ThaumCraft isn't installed */
	public static IThaumcraftCompat sThaumcraftCompat;
	
	/** These Lists are getting executed at their respective timings. Useful if you have to do things right before/after I do them, without having to control the load order. Add your "Commands" in the Constructor or in a static Code Block of your Mods Main Class. These are not Threaded, I just use a native Java Interface for their execution. Implement just the Method run() and everything should work */
	public static List<Runnable> sBeforeGTPreload = new ArrayList<Runnable>(), sAfterGTPreload = new ArrayList<Runnable>(), sBeforeGTLoad = new ArrayList<Runnable>(), sAfterGTLoad = new ArrayList<Runnable>(), sBeforeGTPostload = new ArrayList<Runnable>(), sAfterGTPostload = new ArrayList<Runnable>(), sBeforeGTServerstart = new ArrayList<Runnable>(), sAfterGTServerstart = new ArrayList<Runnable>(), sBeforeGTServerstop = new ArrayList<Runnable>(), sAfterGTServerstop = new ArrayList<Runnable>(), sGTBlockIconload = new ArrayList<Runnable>(), sGTItemIconload = new ArrayList<Runnable>();
	
	/** Fixes the HashMap Mappings for ItemStacks once the Server started */
	public static List<Map<GT_ItemStack, ?>> sItemStackMappings = new ArrayList<Map<GT_ItemStack, ?>>();
	
	/** The Icon Registers from Blocks and Items. They will get set right before the corresponding Icon Load Phase as executed in the Runnable List above. */
	@SideOnly(Side.CLIENT)
	public static IIconRegister sBlockIcons, sItemIcons;
	
	/** Configured Booleans */
	public static boolean DEBUG_MODE = false, SECONDARY_DEBUG_MODE = false, UE_ENERGY_COMPATIBILITY = true;
	
	/** The Configuration Objects */
	public static GT_Config sRecipeFile = null, sMachineFile = null, sWorldgenFile = null, sMaterialProperties = null, sUnification = null, sSpecialFile = null, sClientDataFile, sOPStuff = null;
	
	/** Because Minecraft changed it from -1 to that Value */
	public static final short ITEM_WILDCARD_DAMAGE = OreDictionary.WILDCARD_VALUE;
	
	/** The MetaTileEntity-ID-List-Length */
	public static final short MAXIMUM_METATILE_IDS = Short.MAX_VALUE - 1;
	
	/** My Creative Tab */
	public static final CreativeTabs TAB_GREGTECH = new GT_CreativeTab(), TAB_GREGTECH_MATERIALS = new GT_CreativeTab(), TAB_GREGTECH_ORES = new GT_CreativeTab();
	
	public static int TICKS_FOR_LAG_AVERAGING = 25, MILLISECOND_THRESHOLD_UNTIL_LAG_WARNING = 100;
	
	/**
	 * A List of all registered MetaTileEntities
	 * 
	 *      0 -  1199 are used by GregTech.
	 *   1200 -  2047 are used for GregTech Cables.
	 *   2048 -  2559 are reserved for OvermindDL.
	 *   2560 -  3071 are reserved for Immibis.
	 *   3072 -  3583 are reserved for LinusPhoenix.
	 *   3584 -  4095 are reserved for the next one who asks me.
	 *   4096 -  5095 are used for GregTech Frames.
	 *   5096 -  6099 are used for GregTech Pipes.
	 *   6100 -  8191 are used for GregTech Decoration Blocks.
	 *   8192 -  8703 are reserved for ZL123.
	 *   8704 -  9215 are reserved for Mr10Movie.
	 *   9216 -  9727 are used for GregTech Automation Machines.
	 *   9728 - 32766 are currently free.
	 *  
	 * Contact me if you need a free ID-Range, which doesn't conflict with other Addons.
	 * You could make an ID-Config, but we all know, what "stupid" customers think about conflicting ID's
	 */
	public static final IMetaTileEntity[] METATILEENTITIES = new IMetaTileEntity[MAXIMUM_METATILE_IDS];
	
	/** FilePaths and similar Strings */
    public static final String
      MOD_ID = "gregtech"
    , TEXTURE_FOLDER = "textures/"
    , TEXTURE_PATH_ITEM = MOD_ID + ":"
    , TEXTURE_PATH_BLOCK = MOD_ID + ":"
    , GUI_PATH = MOD_ID + ":" + TEXTURE_FOLDER + "gui/"

    , TC_MOD_ID = "Thaumcraft"
    
    , RC_MOD_ID = "Railcraft"

    , TE_MOD_ID = "ThermalExpansion"
    
    , FR_MOD_ID = "Forestry"
    
    , AE_MOD_ID = "appliedenergistics2"
    
    , PFAA_MOD_ID = "PFAAGeologica"
    
    , IC2_MOD_ID = "IC2"
    , IC2_TEXTURE_PATH = IC2_MOD_ID.toLowerCase() + ":"
    ;
    
	/** Initialized by the Block creation. */
	public static Block sBlockMachines;
	public static Block sBlockOres1;
	public static Block sBlockGranites, sBlockConcretes;
	public static Block sBlockCasings1, sBlockCasings2, sBlockCasings3, sBlockCasings4;
	
	/** Getting assigned by the Config */
	public static boolean sMultiThreadedSounds = false, sDoShowAllItemsInCreative = false, sColoredGUI = true, sConstantEnergy = true, sMachineExplosions = true, sMachineFlammable = true, sMachineNonWrenchExplosions = true, sMachineRainExplosions = true, sMachineThunderExplosions = true, sMachineFireExplosions = true, sMachineWireFire = true;
	
	/** Getting assigned by the Mod loading */
	public static boolean sPreloadStarted = false, sPreloadFinished = false, sLoadStarted = false, sLoadFinished = false, sPostloadStarted = false, sPostloadFinished = false;
	
	/** The Icon List for Covers */
	public static final Map<GT_ItemStack, ITexture> sCovers = new HashMap<GT_ItemStack, ITexture>();
	
	/** The List of Circuit Behaviors for the Redstone Circuit Block */
	public static final Map<Integer, GT_CircuitryBehavior> sCircuitryBehaviors = new HashMap<Integer, GT_CircuitryBehavior>();
	
	/** The List of Cover Behaviors for the Covers */
	public static final Map<GT_ItemStack, GT_CoverBehavior> sCoverBehaviors = new HashMap<GT_ItemStack, GT_CoverBehavior>();
	
	/** The List of Blocks, which can conduct Machine Block Updates */
    public static final Map<Block, Integer> sMachineIDs = new HashMap<Block, Integer>();
    
	/** The Redstone Frequencies */
    public static final Map<Integer, Byte> sWirelessRedstone = new HashMap<Integer, Byte>();
    
	/** The IDSU Frequencies */
	public static final Map<Integer, Integer> sIDSUList = new HashMap<Integer, Integer>();
	
	/** A List of all Books, which were created using @GT_Utility.getWrittenBook the original Title is the Key Value */
	public static final Map<String, ItemStack> sBookList = new HashMap<String, ItemStack>();
	
	/** The List of all Sounds used in GT, indices are in the static Block at the bottom */
	public static final Map<Integer, String> sSoundList = new HashMap<Integer, String>();
	
	/** The List of Tools, which can be used. Accepts regular damageable Items and Electric Items */
	public static final List<GT_ItemStack> sToolList = new ArrayList<GT_ItemStack>(), sCrowbarList = new ArrayList<GT_ItemStack>(), sScrewdriverList = new ArrayList<GT_ItemStack>(), sWrenchList = new ArrayList<GT_ItemStack>(), sSoftHammerList = new ArrayList<GT_ItemStack>(), sHardHammerList = new ArrayList<GT_ItemStack>(), sSolderingToolList = new ArrayList<GT_ItemStack>(), sSolderingMetalList = new ArrayList<GT_ItemStack>();
	/** The List of Hazmat Armors */
	public static final List<GT_ItemStack> sGasHazmatList = new ArrayList<GT_ItemStack>(), sBioHazmatList = new ArrayList<GT_ItemStack>(), sFrostHazmatList = new ArrayList<GT_ItemStack>(), sHeatHazmatList = new ArrayList<GT_ItemStack>(), sRadioHazmatList = new ArrayList<GT_ItemStack>(), sElectroHazmatList = new ArrayList<GT_ItemStack>();
	/** The List of Logs which can be turned into Charcoal */
	public static final List<ItemStack> sCharcoalableWoodenLogs = new ArrayList<ItemStack>();
	
	/** 
	 * The List of Dimensions, which are Whitelisted for the Teleporter. This list should not contain other Planets.
	 * Mystcraft Dimensions and other Dimensional Things should be allowed.
	 * Mystcraft and Twilight Forest are automatically considered a Dimension, without being in this List.
	 */
	public static final List<Integer> sDimensionalList = new ArrayList<Integer>();
	
	/** Lists of all the active World generation Features, these are getting Initialized in Postload! */
	public static final List<GT_Worldgen> sWorldgenList = new ArrayList<GT_Worldgen>();
	
	/** Energy Values for Voltage Classifications */
	public static final long VOLTAGE_ULTRALOW = 8, VOLTAGE_LOW = 32, VOLTAGE_MEDIUM = 128, VOLTAGE_HIGH = 512, VOLTAGE_EXTREME = 2048, VOLTAGE_INSANE = 8192, VOLTAGE_LUDICROUS = 32768, VOLTAGE_ZPM = 131072, VOLTAGE_ULTIMATE = 524288, VOLTAGE_MAXIMUM = Integer.MAX_VALUE;
	public static final long[] VOLTAGES = new long[] {VOLTAGE_ULTRALOW, VOLTAGE_LOW, VOLTAGE_MEDIUM, VOLTAGE_HIGH, VOLTAGE_EXTREME, VOLTAGE_INSANE, VOLTAGE_LUDICROUS, VOLTAGE_ZPM, VOLTAGE_ULTIMATE, VOLTAGE_MAXIMUM, VOLTAGE_MAXIMUM, VOLTAGE_MAXIMUM, VOLTAGE_MAXIMUM, VOLTAGE_MAXIMUM, VOLTAGE_MAXIMUM, VOLTAGE_MAXIMUM};
	
	/** A List containing all the Materials, which are somehow in use by GT and therefor receive a specific Set of Items. */
	public static final Materials[] sGeneratedMaterials = new Materials[1000];
	
	/** 
	 * This is worth exactly one Item.
	 * This Constant can be divided by many commonly used Numbers such as 
	 * 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 14, 15, 16, 18, 20, 21, 24, ... 64 or 81
	 * without loosing precision and is for that reason used as Unit of Amount.
	 * But it is also small enough to be multiplied with larger Numbers.
	 * 
	 * This is used to determine the amount of Material contained inside a prefixed Ore.
	 * For example Nugget = MATERIAL_UNIT / 9 as it contains out of 1/9 of an Ingot.
	 */
	public static final long MATERIAL_UNIT = 3628800;
	
	/**
	 * Fluid per Material Unit
	 */
	public static final long FLUID_MATERIAL_UNIT = 144;
	
	/** If you have to give something a World Parameter but there is no World... */
	public static World sDummyWorld;
	
	/**
	 * For getting special GregTech Component Items.
	 * 
	 * This is more of an internal, I just change all Item IDs with the 1.7 Update, and this is a preparation for it.
	 */
	public static ItemStack getGregTechComponent(int aComponentIndex, int aAmount) {
    	return null;
	}
	
	/**
	 * You want OreDict-Unification for YOUR Mod/Addon, when GregTech is installed? This Function is especially for YOU.
	 * Call this Function after the load-Phase, as I register the the most of the Unification at that Phase (Redpowers Storageblocks are registered at postload).
	 * A recommended use of this Function is inside your Recipe-System itself (if you have one), as the unification then makes 100% sure, that every added non-unificated Output gets automatically unificated.
	 * 
	 * I will personally make sure, that only common prefixes of Ores get registered at the Unificator, as of now there are:
	 * pulp, dust, dustSmall, ingot, nugget, gem, ore and block
	 * If another Mod-Author messes these up, then it's not my fault and it's especially not your fault. As these are commonly used prefixes.
	 * 
	 * This Unificator-API-Function uses the same Functions I use, for unificating Items. So if there is something messed up (very unlikely), then everything is messed up.
	 * 
	 * You shouldn't use this to unificate the Inputs of your Recipes, this is only meant for the Outputs.
	 * 
	 * @param aOreStack the Stack you want to get unificated. It is stackSize Sensitive.
	 * @return Either an unificated Stack or the stack you toss in, but it should never be null, unless you throw a Nullpointer into it.
	 */
	public static ItemStack getUnificatedOreDictStack(ItemStack aOreStack) {
		if (!GregTech_API.sPreloadFinished) GT_Log.err.println("GregTech_API ERROR: " + aOreStack.getItem() + "." + aOreStack.getItemDamage() + " - OreDict Unification Entries are not registered now, please call it in the postload phase.");
		return GT_OreDictUnificator.get(true, aOreStack);
	}
	
	/**
	 * Causes a Machineblock Update
	 * This update will cause surrounding MultiBlock Machines to update their Configuration.
	 * You should call this Function in @Block.breakBlock and in @Block.onBlockAdded of your Machine.
	 * @param aWorld is being the World
	 * @param aX is the X-Coord of the update causing Block
	 * @param aY is the Y-Coord of the update causing Block
	 * @param aZ is the Z-Coord of the update causing Block
	 */
	public static boolean causeMachineUpdate(World aWorld, int aX, int aY, int aZ) {
		if (!aWorld.isRemote) new Thread(new GT_Runnable_MachineBlockUpdate(aWorld, aX, aY, aZ), "Machine Block Updating").start();
		return true;
	}
	
	/**
	 * Adds a Multi-Machine Block, like my Machine Casings for example.
	 * You should call @causeMachineUpdate in @Block.breakBlock and in @Block.onBlockAdded of your registered Block.
	 * You don't need to register TileEntities which implement @IMachineBlockUpdateable
	 * @param aID the ID of your Block
	 * @param aMeta the Metadata of the Blocks as Bitmask! -1 or ~0 for all Metavalues
	 */
	public static boolean registerMachineBlock(Block aBlock, int aMeta) {
		if (GT_Utility.isBlockInvalid(aBlock)) return false;
        if (GregTech_API.sThaumcraftCompat != null) GregTech_API.sThaumcraftCompat.registerPortholeBlacklistedBlock(aBlock);
		sMachineIDs.put(aBlock, aMeta);
		return true;
	}
	
	/**
	 * Like above but with boolean Parameters instead of a BitMask
	 */
	public static boolean registerMachineBlock(Block aBlock, boolean... aMeta) {
		if (GT_Utility.isBlockInvalid(aBlock) || aMeta == null || aMeta.length == 0) return false;
        if (GregTech_API.sThaumcraftCompat != null) GregTech_API.sThaumcraftCompat.registerPortholeBlacklistedBlock(aBlock);
		int rMeta = 0;
		for (byte i = 0; i < aMeta.length && i < 16; i++) if (aMeta[i]) rMeta |= (1 << i);
		sMachineIDs.put(aBlock, rMeta);
		return true;
	}
	
	/**
	 * if this Block is a Machine Update Conducting Block
	 */
	public static boolean isMachineBlock(Block aBlock, int aMeta) {
		if (GT_Utility.isBlockInvalid(aBlock)) return false;
		return (sMachineIDs.containsKey(aBlock) && (sMachineIDs.get(aBlock) & (1 << aMeta)) != 0);
	}
	
	/**
	 * Creates a new Coolant Cell Item for your Nuclear Reactor
	 */
	public static Item constructCoolantCellItem(String aUnlocalized, String aEnglish, int aMaxStore) {
		try {
			return (Item)Class.forName("gregtechmod.api.items.GT_CoolantCellIC_Item").getConstructors()[0].newInstance(aUnlocalized, aEnglish, aMaxStore);
		} catch(Throwable e) {/*Do nothing*/}
		try {
			return (Item)Class.forName("gregtechmod.api.items.GT_CoolantCell_Item").getConstructors()[0].newInstance(aUnlocalized, aEnglish, aMaxStore);
		} catch(Throwable e) {/*Do nothing*/}
		return new gregtech.api.items.GT_Generic_Item(aUnlocalized, aEnglish, "Doesn't work as intended, this is a Bug", false);
	}
	
	/**
	 * Creates a new Energy Armor Item
	 */
	public static Item constructElectricArmorItem(String aUnlocalized, String aEnglish, int aCharge, int aTransfer, int aTier, int aDamageEnergyCost, int aSpecials, double aArmorAbsorbtionPercentage, boolean aChargeProvider, int aType, int aArmorIndex) {
		try {
			return (Item)Class.forName("gregtechmod.api.items.GT_EnergyArmorIC_Item").getConstructors()[0].newInstance(aUnlocalized, aEnglish, aCharge, aTransfer, aTier, aDamageEnergyCost, aSpecials, aArmorAbsorbtionPercentage, aChargeProvider, aType, aArmorIndex);
		} catch(Throwable e) {/*Do nothing*/}
		try {
			return (Item)Class.forName("gregtechmod.api.items.GT_EnergyArmor_Item").getConstructors()[0].newInstance(aUnlocalized, aEnglish, aCharge, aTransfer, aTier, aDamageEnergyCost, aSpecials, aArmorAbsorbtionPercentage, aChargeProvider, aType, aArmorIndex);
		} catch(Throwable e) {/*Do nothing*/}
		return new gregtech.api.items.GT_Generic_Item(aUnlocalized, aEnglish, "Doesn't work as intended, this is a Bug", false);
	}
	
	/**
	 * Creates a new Energy Battery Item
	 */
	public static Item constructElectricEnergyStorageItem(String aUnlocalized, String aEnglish, int aCharge, int aTransfer, int aTier, int aEmptyID, int aFullID) {
		try {
			return (Item)Class.forName("gregtechmod.api.items.GT_EnergyStoreIC_Item").getConstructors()[0].newInstance(aUnlocalized, aEnglish, aCharge, aTransfer, aTier, aEmptyID, aFullID);
		} catch(Throwable e) {/*Do nothing*/}
		try {
			return (Item)Class.forName("gregtechmod.api.items.GT_EnergyStore_Item").getConstructors()[0].newInstance(aUnlocalized, aEnglish, aCharge, aTransfer, aTier, aEmptyID, aFullID);
		} catch(Throwable e) {/*Do nothing*/}
		return new gregtech.api.items.GT_Generic_Item(aUnlocalized, aEnglish, "Doesn't work as intended, this is a Bug", false);
	}
	
	/**
	 * Creates a new Hard Hammer Item
	 */
	public static GT_Tool_Item constructHardHammerItem(String aUnlocalized, String aEnglish, int aMaxDamage, int aEntityDamage) {
		try {
			return (GT_Tool_Item)Class.forName("gregtechmod.api.items.GT_HardHammer_Item").getConstructors()[0].newInstance(aUnlocalized, aEnglish, aMaxDamage, aEntityDamage);
		} catch(Throwable e) {/*Do nothing*/}
		return new gregtech.api.items.GT_Tool_Item(aUnlocalized, aEnglish, "Doesn't work as intended, this is a Bug", aMaxDamage, aEntityDamage, false);
	}
	
	/**
	 * Creates a new Crowbar Item
	 */
	public static GT_Tool_Item constructCrowbarItem(String aUnlocalized, String aEnglish, int aMaxDamage, int aEntityDamage) {
		try {
			return (GT_Tool_Item)Class.forName("gregtechmod.api.items.GT_CrowbarRC_Item").getConstructors()[0].newInstance(aUnlocalized, aEnglish, aMaxDamage, aEntityDamage);
		} catch(Throwable e) {/*Do nothing*/}
		try {
			return (GT_Tool_Item)Class.forName("gregtechmod.api.items.GT_Crowbar_Item").getConstructors()[0].newInstance(aUnlocalized, aEnglish, aMaxDamage, aEntityDamage);
		} catch(Throwable e) {/*Do nothing*/}
		return new gregtech.api.items.GT_Tool_Item(aUnlocalized, aEnglish, "Doesn't work as intended, this is a Bug", aMaxDamage, aEntityDamage, false);
	}
	
	/**
	 * Creates a new Wrench Item
	 */
	public static GT_Tool_Item constructWrenchItem(String aUnlocalized, String aEnglish, int aMaxDamage, int aEntityDamage, int aDisChargedGTID) {
		try {
			return (GT_Tool_Item)Class.forName("gregtechmod.api.items.GT_Wrench_Item").getConstructors()[0].newInstance(aUnlocalized, aEnglish, aMaxDamage, aEntityDamage, aDisChargedGTID);
		} catch(Throwable e) {/*Do nothing*/}
		return new gregtech.api.items.GT_Tool_Item(aUnlocalized, aEnglish, "Doesn't work as intended, this is a Bug", aMaxDamage, aEntityDamage, false);
	}
	
	/**
	 * Creates a new electric Screwdriver Item
	 */
	public static GT_Tool_Item constructElectricScrewdriverItem(String aUnlocalized, String aEnglish, int aMaxDamage, int aEntityDamage, int aDisChargedGTID) {
		try {
			return (GT_Tool_Item)Class.forName("gregtechmod.api.items.GT_ScrewdriverIC_Item").getConstructors()[0].newInstance(aUnlocalized, aEnglish, aMaxDamage, aEntityDamage, aDisChargedGTID);
		} catch(Throwable e) {/*Do nothing*/}
		return new gregtech.api.items.GT_Tool_Item(aUnlocalized, aEnglish, "Doesn't work as intended, this is a Bug", aMaxDamage, aEntityDamage, false);
	}
	
	/**
	 * Creates a new electric Wrench Item
	 */
	public static GT_Tool_Item constructElectricWrenchItem(String aUnlocalized, String aEnglish, int aMaxDamage, int aEntityDamage, int aDisChargedGTID) {
		try {
			return (GT_Tool_Item)Class.forName("gregtechmod.api.items.GT_WrenchIC_Item").getConstructors()[0].newInstance(aUnlocalized, aEnglish, aMaxDamage, aEntityDamage, aDisChargedGTID);
		} catch(Throwable e) {/*Do nothing*/}
		return new gregtech.api.items.GT_Tool_Item(aUnlocalized, aEnglish, "Doesn't work as intended, this is a Bug", aMaxDamage, aEntityDamage, false);
	}

	/**
	 * Creates a new electric Saw Item
	 */
	public static GT_Tool_Item constructElectricSawItem(String aUnlocalized, String aEnglish, int aMaxDamage, int aEntityDamage, int aToolQuality, float aToolStrength, int aEnergyConsumptionPerBlockBreak, int aDisChargedGTID) {
		try {
			return (GT_Tool_Item)Class.forName("gregtechmod.api.items.GT_SawIC_Item").getConstructors()[0].newInstance(aUnlocalized, aEnglish, aMaxDamage, aEntityDamage, aToolQuality, aToolStrength, aEnergyConsumptionPerBlockBreak, aDisChargedGTID);
		} catch(Throwable e) {/*Do nothing*/}
		return new gregtech.api.items.GT_Tool_Item(aUnlocalized, aEnglish, "Doesn't work as intended, this is a Bug", aMaxDamage, aEntityDamage, false);
	}
	
	/**
	 * Creates a new electric Drill Item
	 */
	public static GT_Tool_Item constructElectricDrillItem(String aUnlocalized, String aEnglish, int aMaxDamage, int aEntityDamage, int aToolQuality, float aToolStrength, int aEnergyConsumptionPerBlockBreak, int aDisChargedGTID) {
		try {
			return (GT_Tool_Item)Class.forName("gregtechmod.api.items.GT_DrillIC_Item").getConstructors()[0].newInstance(aUnlocalized, aEnglish, aMaxDamage, aEntityDamage, aToolQuality, aToolStrength, aEnergyConsumptionPerBlockBreak, aDisChargedGTID);
		} catch(Throwable e) {/*Do nothing*/}
		return new gregtech.api.items.GT_Tool_Item(aUnlocalized, aEnglish, "Doesn't work as intended, this is a Bug", aMaxDamage, aEntityDamage, false);
	}
	
	/**
	 * Creates a new electric Soldering Tool
	 */
	public static GT_Tool_Item constructElectricSolderingToolItem(String aUnlocalized, String aEnglish, int aMaxDamage, int aEntityDamage, int aDisChargedGTID) {
		try {
			return (GT_Tool_Item)Class.forName("gregtechmod.api.items.GT_SolderingToolIC_Item").getConstructors()[0].newInstance(aUnlocalized, aEnglish, aMaxDamage, aEntityDamage, aDisChargedGTID);
		} catch(Throwable e) {/*Do nothing*/}
		return new gregtech.api.items.GT_Tool_Item(aUnlocalized, aEnglish, "Doesn't work as intended, this is a Bug", aMaxDamage, aEntityDamage, false);
	}
	
	/**
	 * Creates a new empty electric Tool
	 */
	public static GT_Tool_Item constructEmptyElectricToolItem(String aUnlocalized, String aEnglish, int aMaxDamage, int aChargedGTID) {
		try {
			return (GT_Tool_Item)Class.forName("gregtechmod.api.items.GT_EmptyToolIC_Item").getConstructors()[0].newInstance(aUnlocalized, aEnglish, aMaxDamage, aChargedGTID);
		} catch(Throwable e) {/*Do nothing*/}
		return new gregtech.api.items.GT_Tool_Item(aUnlocalized, aEnglish, "Doesn't work as intended, this is a Bug", aMaxDamage, 0, false);
	}
	
	private static Class sBaseMetaTileEntityClass = null;
	
	/**
	 * This gives you a new BaseMetaTileEntity. As some Interfaces are not always loaded (Buildcraft, Univeral Electricity) I have to use Invocation at the Constructor of the BaseMetaTileEntity
	 */
	public static BaseMetaTileEntity constructBaseMetaTileEntity() {
		if (sBaseMetaTileEntityClass == null) {
			try {
				if (UE_ENERGY_COMPATIBILITY) return (BaseMetaTileEntity)(sBaseMetaTileEntityClass = Class.forName("gregtechmod.api.metatileentity.BaseMetaTileEntityUE")).newInstance();
			} catch(Throwable e) {/*Do nothing*/}
			try {
				return (BaseMetaTileEntity)(sBaseMetaTileEntityClass = BaseMetaTileEntity.class).newInstance();
			} catch(Throwable e) {/*Do nothing*/}
		}
		
		try {
			return (BaseMetaTileEntity)(sBaseMetaTileEntityClass.newInstance());
		} catch(Throwable e) {
			GT_Log.err.println("GT_Mod: Fatal Error ocurred while initializing TileEntities, crashing Minecraft.");
			e.printStackTrace(GT_Log.err);
			throw new RuntimeException(e);
		}
	}
	
	public static void registerCover(ItemStack aStack, ITexture aCover, GT_CoverBehavior aBehavior) {
		if (!sCovers.containsKey(new GT_ItemStack(aStack))) sCovers.put(new GT_ItemStack(aStack), aCover==null||!aCover.isValidTexture()?Textures.BlockIcons.ERROR_RENDERING[0]:aCover);
		if (aBehavior != null) sCoverBehaviors.put(new GT_ItemStack(aStack), aBehavior);
	}
	
	public static void registerCoverBehavior(ItemStack aStack, GT_CoverBehavior aBehavior) {
		sCoverBehaviors.put(new GT_ItemStack(aStack), aBehavior==null?sDefaultBehavior:aBehavior);
	}
	
	/**
	 * Registers multiple Cover Items. I use that for the OreDict Functionality.
	 * @param aBehavior can be null
	 */
	public static void registerCover(Collection<ItemStack> aStackList, ITexture aCover, GT_CoverBehavior aBehavior) {
		if (aCover.isValidTexture()) for (ItemStack tStack : aStackList) registerCover(tStack, aCover, aBehavior);
	}
	
	/**
	 * This is the generic Cover behavior. Used for the default Covers, which have no Behavior.
	 */
	public static final GT_CoverBehavior sDefaultBehavior = new GT_Cover_Default(), sNoBehavior = new GT_Cover_None();
	
	/**
	 * returns a Cover behavior, guaranteed to not return null after preload
	 */
	public static GT_CoverBehavior getCoverBehavior(ItemStack aStack) {
		if (aStack == null || aStack.getItem() == null) return sNoBehavior;
		GT_CoverBehavior rCover = sCoverBehaviors.get(new GT_ItemStack(aStack));
		if (rCover == null) return sDefaultBehavior;
		return rCover;
	}
	
	/**
	 * returns a Cover behavior, guaranteed to not return null
	 */
	public static GT_CoverBehavior getCoverBehavior(int aStack) {
		if (aStack == 0) return sNoBehavior;
		return getCoverBehavior(GT_Utility.intToStack(aStack));
	}
	
	/**
	 * Register a Wrench to be usable on GregTech Machines.
	 * The Wrench MUST have some kind of Durability unlike certain Buildcraft Wrenches.
	 * 
	 * You need to register Tools in the Load Phase, because otherwise the Autodetection will assign a Tool Type in certain Cases during postload (When IToolWrench or similar Interfaces are implemented).
	 * 
	 * -----
	 * 
	 * Returning true at isDamagable was a great Idea, KingLemming. Well played.
	 * Since the OmniWrench is just a Single-Item-Mod, people can choose if they want your infinite durability or not. So that's not really a Problem.
	 * I even have a new Config to autodisable most infinite BC Wrenches (but that one is turned off).
	 * 
	 * One last Bug for you to fix:
	 * My Autoregistration detects Railcrafts Crowbars, Buildcrafts Wrenches and alike, due to their Interfaces.
	 * Guess what now became a Crowbar by accident. Try registering the Wrench at the load phase to prevent things like that from happening.
	 * Yes, I know that "You need to register Tools in the Load Phase"-Part wasn't there before this. Sorry about that.
	 */
	public static boolean registerWrench(ItemStack aTool) {
		return registerTool(aTool, sWrenchList);
	}
	
	/**
	 * Register a Crowbar to extract Covers from Machines
	 * Crowbars are NOT Wrenches btw.
	 * 
	 * You need to register Tools in the Load Phase, because otherwise the Autodetection will assign a Tool Type in certain Cases during postload (When IToolWrench or similar Interfaces are implemented).
	 */
	public static boolean registerCrowbar(ItemStack aTool) {
		return registerTool(aTool, sCrowbarList);
	}
	
	/**
	 * Register a Screwdriver to interact directly with Machines and Covers
	 * Did I mention, that it is intentionally not possible to make a Multitool, which doesn't switch ItemID (like a Mode) all the time?
	 * 
	 * You need to register Tools in the Load Phase, because otherwise the Autodetection will assign a Tool Type in certain Cases during postload (When IToolWrench or similar Interfaces are implemented).
	 */
	public static boolean registerScrewdriver(ItemStack aTool) {
		return registerTool(aTool, sScrewdriverList);
	}
	
	/**
	 * Register a Soft Hammer to interact with Machines
	 * 
	 * You need to register Tools in the Load Phase, because otherwise the Autodetection will assign a Tool Type in certain Cases during postload (When IToolWrench or similar Interfaces are implemented).
	 */
	public static boolean registerSoftHammer(ItemStack aTool) {
		return registerTool(aTool, sSoftHammerList);
	}
	
	/**
	 * Register a Hard Hammer to interact with Machines
	 * 
	 * You need to register Tools in the Load Phase, because otherwise the Autodetection will assign a Tool Type in certain Cases during postload (When IToolWrench or similar Interfaces are implemented).
	 */
	public static boolean registerHardHammer(ItemStack aTool) {
		return registerTool(aTool, sHardHammerList);
	}
	
	/**
	 * Register a Soldering Tool to interact with Machines
	 * 
	 * You need to register Tools in the Load Phase, because otherwise the Autodetection will assign a Tool Type in certain Cases during postload (When IToolWrench or similar Interfaces are implemented).
	 */
	public static boolean registerSolderingTool(ItemStack aTool) {
		return registerTool(aTool, sSolderingToolList);
	}
	
	/**
	 * Register a Soldering Tin to interact with Soldering Tools
	 * 
	 * You need to register Tools in the Load Phase, because otherwise the Autodetection will assign a Tool Type in certain Cases during postload (When IToolWrench or similar Interfaces are implemented).
	 */
	public static boolean registerSolderingMetal(ItemStack aTool) {
		return registerTool(aTool, sSolderingMetalList);
	}
	
	/**
	 * Generic Function to add Tools to the Lists.
	 * Contains all sanity Checks for Tools, like preventing one Tool from being registered for multiple purposes as controls would override each other.
	 */
	public static boolean registerTool(ItemStack aTool, Collection<GT_ItemStack> aToolList) {
		if (aTool == null || sToolList.contains(new GT_ItemStack(aTool)) || (!aTool.getItem().isDamageable() && !GT_ModHandler.isElectricItem(aTool) && !(aTool.getItem() instanceof IDamagableItem))) return false;
		aToolList.add(new GT_ItemStack(GT_Utility.copyAmount(1, aTool)));
		sToolList.add(new GT_ItemStack(GT_Utility.copyAmount(1, aTool)));
		return true;
	}
	
	/**
	 * Adds Biomes to the Biome Lists for World Generation
	 */
	static {
		sItemStackMappings.add(sCovers);
		sItemStackMappings.add(sCoverBehaviors);
		
		sDimensionalList.add(-1);
		sDimensionalList.add( 0);
		sDimensionalList.add( 1);
		
		sSoundList.put(  0, "random.break");
		sSoundList.put(  1, "random.anvil_use");
		sSoundList.put(  2, "random.anvil_break");
		sSoundList.put(  3, "random.click");
		sSoundList.put(  4, "random.fizz");
		sSoundList.put(  5, "random.explode");
		
		sSoundList.put(100, GregTech_API.IC2_MOD_ID.toLowerCase() + ":" + "tools.Wrench");
		sSoundList.put(101, GregTech_API.IC2_MOD_ID.toLowerCase() + ":" + "tools.RubberTrampoline");
		sSoundList.put(102, GregTech_API.IC2_MOD_ID.toLowerCase() + ":" + "tools.Painter");
		sSoundList.put(103, GregTech_API.IC2_MOD_ID.toLowerCase() + ":" + "tools.BatteryUse");
		sSoundList.put(104, GregTech_API.IC2_MOD_ID.toLowerCase() + ":" + "tools.chainsaw.ChainsawUseOne");
		sSoundList.put(105, GregTech_API.IC2_MOD_ID.toLowerCase() + ":" + "tools.chainsaw.ChainsawUseTwo");
		sSoundList.put(106, GregTech_API.IC2_MOD_ID.toLowerCase() + ":" + "tools.drill.DrillSoft");
		sSoundList.put(107, GregTech_API.IC2_MOD_ID.toLowerCase() + ":" + "tools.drill.DrillHard");
		sSoundList.put(108, GregTech_API.IC2_MOD_ID.toLowerCase() + ":" + "tools.ODScanner");
		
		sSoundList.put(200, GregTech_API.IC2_MOD_ID.toLowerCase() + ":" + "machines.ExtractorOp");
		sSoundList.put(201, GregTech_API.IC2_MOD_ID.toLowerCase() + ":" + "machines.MaceratorOp");
		sSoundList.put(202, GregTech_API.IC2_MOD_ID.toLowerCase() + ":" + "machines.InductionLoop");
		sSoundList.put(203, GregTech_API.IC2_MOD_ID.toLowerCase() + ":" + "machines.CompressorOp");
		sSoundList.put(204, GregTech_API.IC2_MOD_ID.toLowerCase() + ":" + "machines.RecyclerOp");
		sSoundList.put(205, GregTech_API.IC2_MOD_ID.toLowerCase() + ":" + "machines.MinerOp");
		sSoundList.put(206, GregTech_API.IC2_MOD_ID.toLowerCase() + ":" + "machines.PumpOp");
		sSoundList.put(207, GregTech_API.IC2_MOD_ID.toLowerCase() + ":" + "machines.ElectroFurnaceLoop");
		sSoundList.put(208, GregTech_API.IC2_MOD_ID.toLowerCase() + ":" + "machines.InductionLoop");
		sSoundList.put(209, GregTech_API.IC2_MOD_ID.toLowerCase() + ":" + "machines.MachineOverload");
		sSoundList.put(210, GregTech_API.IC2_MOD_ID.toLowerCase() + ":" + "machines.InterruptOne");
		sSoundList.put(211, GregTech_API.IC2_MOD_ID.toLowerCase() + ":" + "machines.KaChing");
		sSoundList.put(212, GregTech_API.IC2_MOD_ID.toLowerCase() + ":" + "machines.MagnetizerLoop");
	}
}
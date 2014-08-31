package gregtech.api.enums;

import java.util.ArrayList;

/**
 * Just a simple Class to be able to add special Tags for Materials.
 * 
 * The Tags should be added in preload to the Materials.
 * In order to make yourself a new SubTag, just create one new instance of SubTag
 * and use that one instance on all Materials you want to add those Tags to.
 */
public final class SubTag {
	private static long sSubtagID = 0;
	
	public static final ArrayList<SubTag> sSubTags = new ArrayList<SubTag>();
	
	/**
	 * Add this to your Material if you want to have its Ore Calcite heated in a Blast Furnace for more output. Already listed are:
	 * Iron, Pyrite, PigIron, DeepIron, ShadowIron, WroughtIron and MeteoricIron.
	 */
	public static final SubTag BLASTFURNACE_CALCITE_DOUBLE				= new SubTag("BLASTFURNACE_CALCITE_DOUBLE"), BLASTFURNACE_CALCITE_TRIPLE = new SubTag("BLASTFURNACE_CALCITE_TRIPLE");
	
	/**
	 * Materials which are outputting less in an Induction Smelter. Already listed are:
	 * Pyrite, Tetrahedrite, Sphalerite, Cinnabar
	 */
	public static final SubTag INDUCTIONSMELTING_LOW_OUTPUT				= new SubTag("INDUCTIONSMELTING_LOW_OUTPUT");
	
	/**
	 * Add this to your Material if you want to have its Ore Sodium Persulfate washed. Already listed are:
	 * Zinc, Nickel, Copper, Cobalt, Cobaltite and Tetrahedrite.
	 */
	public static final SubTag WASHING_SODIUMPERSULFATE					= new SubTag("WASHING_SODIUMPERSULFATE");
	
	/**
	 * Add this to your Material if you want to have its Ore Mercury washed. Already listed are:
	 * Gold, Silver, Osmium, Mithril, Platinum, Midasium, Cooperite and AstralSilver.
	 */
	public static final SubTag WASHING_MERCURY							= new SubTag("WASHING_MERCURY");
	
	/**
	 * Add this to your Material if you want to have its Ore electromagnetically separated to give Gold.
	 */
	public static final SubTag ELECTROMAGNETIC_SEPERATION_GOLD			= new SubTag("ELECTROMAGNETIC_SEPERATION_GOLD");

	/**
	 * Add this to your Material if you want to have its Ore electromagnetically separated to give Iron.
	 */
	public static final SubTag ELECTROMAGNETIC_SEPERATION_IRON			= new SubTag("ELECTROMAGNETIC_SEPERATION_IRON");
	
	/**
	 * Add this to your Material if you want to have its Ore electromagnetically separated to give Neodymium.
	 */
	public static final SubTag ELECTROMAGNETIC_SEPERATION_NEODYMIUM		= new SubTag("ELECTROMAGNETIC_SEPERATION_NEODYMIUM");
	
	/**
	 * Add this to your Material if you want to have its Ore giving Cinnabar Crystals on Pulverization. Already listed are:
	 * Redstone
	 */
	public static final SubTag PULVERIZING_CINNABAR						= new SubTag("PULVERIZING_CINNABAR");
	
	/**
	 * This Material cannot be used for regular Metal working techniques since it is not possible to bend it. Already listed are:
	 * Rubber, Plastic, Paper, Wood, Stone
	 */
	public static final SubTag NO_SMASHING								= new SubTag("NO_SMASHING");
	
	/**
	 * This Material cannot be used in any Furnace alike Structure. Already listed are:
	 * Paper, Wood, Gunpowder, Stone
	 */
	public static final SubTag NO_SMELTING								= new SubTag("NO_SMELTING");
	
	/**
	 * This Ore should be smolten directly into a Gem of this Material, if the Ingot is missing. Already listed are:
	 * Cinnabar
	 */
	public static final SubTag SMELTING_TO_GEM							= new SubTag("SMELTING_TO_GEM");
	
	/**
	 * If this Material is some kind of Wood
	 */
	public static final SubTag WOOD										= new SubTag("WOOD");
	
	/**
	 * If this Material is some kind of Stone
	 */
	public static final SubTag STONE									= new SubTag("STONE");
	
	/**
	 * If this Material is some kind of Quartz
	 */
	public static final SubTag QUARTZ									= new SubTag("QUARTZ");
	
	/**
	 * If this Material is Crystallisable
	 */
	public static final SubTag CRYSTALLISABLE							= new SubTag("CRYSTALLISABLE");
	
	/**
	 * If this Material is some kind of Crystal
	 */
	public static final SubTag CRYSTAL									= new SubTag("CRYSTAL");
	
	/**
	 * If this Material is some kind of Magical
	 */
	public static final SubTag MAGICAL									= new SubTag("MAGICAL");
	
	/**
	 * If this Material is some kind of flammable
	 */
	public static final SubTag FLAMMABLE								= new SubTag("FLAMMABLE");
	
	/**
	 * If this Material is some kind of explosive
	 */
	public static final SubTag EXPLOSIVE								= new SubTag("EXPLOSIVE");
	
	/**
	 * If this Material is bouncy
	 */
	public static final SubTag BOUNCY									= new SubTag("BOUNCY");

	/**
	 * If this Material is invisible
	 */
	public static final SubTag INVISIBLE								= new SubTag("INVISIBLE");
	
	/**
	 * If this Material is stretchable
	 */
	public static final SubTag STRETCHY									= new SubTag("STRETCHY");
	
	public final long mSubtagID;
	public final String mName;
	
	public SubTag(String aName) {
		mSubtagID = sSubtagID++;
		mName = aName;
		sSubTags.add(this);
	}
}
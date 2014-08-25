package gregtech.api.enums;

public enum ConfigCategories {
	news,
	general,
	machineconfig,
	specialunificationtargets;

	public enum IDs {
		crops,
		enchantments;
	}
	
	public enum Materials {
		oreprocessingoutputmultiplier,
		blastfurnacerequirements,
		blastinductionsmelter,
		UUM_MaterialCost,
		UUM_EnergyCost;
	}
	
	public enum Recipes {
		researches,
		harderrecipes,
		gregtechrecipes,
		disabledrecipes,
		recipereplacements,
		storageblockcrafting,
		storageblockdecrafting;
	}
	
	public enum Machines {
		smelting,
		squeezer,
		liquidtransposer,
		liquidtransposerfilling,
		liquidtransposeremptying,
		extractor,
		sawmill,
		compression,
		thermalcentrifuge,
		orewashing,
		inductionsmelter,
		rcblastfurnace,
		scrapboxdrops,
		massfabamplifier,
		maceration,
		rockcrushing,
		pulverization;
	}
	
	public enum Fuels {
		boilerfuels;
	}
	
	public enum Tools {
		mortar,
		hammerrings,
		hammerplating,
		hammerdoubleingot,
		hammertripleingot,
		hammerquadrupleingot,
		hammerquintupleingot,
		hammerdoubleplate,
		hammertripleplate,
		hammerquadrupleplate,
		hammerquintupleplate;
	}
}
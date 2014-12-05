package pl.asie.computronics.integration.forestry;

import forestry.api.genetics.IClassification;
import forestry.apiculture.genetics.AlleleBeeSpecies;

/**
 * @author Vexatos
 */
public class OCBeeSpecies extends AlleleBeeSpecies {

	public OCBeeSpecies(String uid, boolean dominant, String name, IClassification branch, String binomial, int primaryColor, int secondaryColor) {
		super(uid, dominant, name, branch, binomial, primaryColor, secondaryColor);
	}

	@Override
	public boolean isNocturnal() {
		return true;
	}

	@Override
	public String getAuthority() {
		return "Sangar";
	}

	@Override
	public String getUID() {
		return this.uid;
	}
}

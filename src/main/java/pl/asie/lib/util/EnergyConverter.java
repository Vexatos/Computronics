package pl.asie.lib.util;

import java.util.HashMap;

public class EnergyConverter {

	public static HashMap<String, Double> energyConversionRates = new HashMap<String, Double>();

	static {
		energyConversionRates.put("RF", 10.0);
		energyConversionRates.put("EU", 2.5);
		energyConversionRates.put("AE", 5.0);
		energyConversionRates.put("OC", 1.0);
		energyConversionRates.put("COAL", 1.0);
	}

	public static double convertEnergy(double in, String from, String to) {
		double intermediate = in / energyConversionRates.get(from);
		return intermediate * energyConversionRates.get(to);
	}

	public static boolean isValidEnergyName(String name) {
		return energyConversionRates.containsKey(name);
	}

	public static String[] getNames() {
		return energyConversionRates.keySet().toArray(new String[energyConversionRates.keySet().size()]);
	}
}

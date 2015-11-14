package pl.asie.computronics.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import pl.asie.computronics.util.internal.IColorable;

import java.util.HashMap;

/**
 * @author Sangar, Vexatos
 */
public class ColorUtils {

	public static final HashMap<String, Colors> dyes = new HashMap<String, Colors>();

	public static Colors getColor(ItemStack stack) {
		int[] oreIDs = OreDictionary.getOreIDs(stack);
		for(Colors color : Colors.VALUES) {
			int colorID = OreDictionary.getOreID(color.dyeName);
			for(int oreID : oreIDs) {
				if(colorID == oreID) {
					return color;
				}
			}
		}
		return null;
	}

	public static Colors fromName(String name) {
		if(dyes.containsKey(name)) {
			return dyes.get(name);
		}
		return Colors.White;
	}

	public static Colors fromDyeMeta(int meta) {
		if(meta >= 0 && meta < Colors.VALUES.length) {
			return Colors.VALUES[meta];
		}
		return Colors.White;
	}

	public static Colors fromWoolMeta(int meta) {
		if(meta >= 0 && meta < Colors.VALUES.length) {
			return fromDyeMeta(15 - meta);
		}
		return Colors.White;
	}

	public static boolean isSameOrDefault(IColorable one, IColorable two) {
		int oc = one.getColor(),
			tc = two.getColor(),
			ocd = one.getDefaultColor(),
			tcd = two.getDefaultColor();
		return oc == ocd || tc == tcd || oc == tc;
	}

	public enum Colors {
		Black(0x444444, "dyeBlack"),
		Red(0xB3312C, "dyeRed"),
		Green(0x339911, "dyeGreen"),
		Brown(0x51301A, "dyeBrown"),
		Blue(0x6666FF, "dyeBlue"),
		Purple(0x7B2FBE, "dyePurple"),
		Cyan(0x66FFFF, "dyeCyan"),
		LightGray(0xABABAB, "dyeLightGray"),
		Gray(0x666666, "dyeGray"),
		Pink(0xD88198, "dyePink"),
		Lime(0x66FF66, "dyeLime"),
		Yellow(0xFFFF66, "dyeYellow"),
		LightBlue(0xAAAAFF, "dyeLightBlue"),
		Magenta(0xC354CD, "dyeMagenta"),
		Orange(0xEB8844, "dyeOrange"),
		White(0xFFFFFF, "dyeWhite");
		//OxF0F0F0

		public static final Colors[] VALUES = values();

		public final int color;
		public final String dyeName;

		Colors(int color, String dyeName) {
			this.color = color;
			this.dyeName = dyeName;
			dyes.put(dyeName, this);
		}
	}
}

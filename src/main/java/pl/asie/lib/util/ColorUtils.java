package pl.asie.lib.util;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import pl.asie.lib.util.internal.IColorable;

import javax.annotation.Nullable;
import java.util.HashMap;

/**
 * @author Sangar, Vexatos
 */
public class ColorUtils {

	public static final HashMap<String, Color> dyes = new HashMap<String, Color>();
	public static final HashMap<Integer, Color> colorValues = new HashMap<Integer, Color>();

	@Nullable
	public static Color getColor(ItemStack stack) {
		int[] oreIDs = OreDictionary.getOreIDs(stack);
		for(Color color : Color.VALUES) {
			int colorID = OreDictionary.getOreID(color.dyeName);
			for(int oreID : oreIDs) {
				if(colorID == oreID) {
					return color;
				}
			}
		}
		return null;
	}

	public static Color fromName(String name) {
		if(dyes.containsKey(name)) {
			return dyes.get(name);
		}
		return Color.White;
	}

	public static Color fromDyeMeta(int meta) {
		if(meta >= 0 && meta < Color.VALUES.length) {
			return Color.VALUES[meta];
		}
		return Color.White;
	}

	public static Color fromWoolMeta(int meta) {
		if(meta >= 0 && meta < Color.VALUES.length) {
			return fromDyeMeta(15 - meta);
		}
		return Color.White;
	}

	public static Color fromColor(int color) {
		if(colorValues.containsKey(color)) {
			return colorValues.get(color);
		}
		return Color.White;
	}

	public static Color fromColor(EnumDyeColor color) {
		return fromDyeMeta(color.getDyeDamage());
	}

	public static boolean isSameOrDefault(IColorable one, IColorable two) {
		int oc = one.getColor(),
			tc = two.getColor(),
			ocd = one.getDefaultColor(),
			tcd = two.getDefaultColor();
		return oc == ocd || tc == tcd || oc == tc;
	}

	public enum Color {
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

		public static final Color[] VALUES = values();

		public final int color;
		public final String dyeName;

		Color(int color, String dyeName) {
			this.color = color;
			this.dyeName = dyeName;
			dyes.put(dyeName, this);
			colorValues.put(color, this);
		}
	}
}

package pl.asie.computronics.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import li.cil.oc.api.Driver;
import li.cil.oc.api.driver.DeviceInfo.DeviceAttribute;
import li.cil.oc.api.driver.Item;
import li.cil.oc.client.KeyBindings;
import li.cil.oc.util.ItemCosts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Vexatos
 */
public class OCUtils {

	public static NBTTagCompound dataTag(final ItemStack stack) {
		if(!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		final NBTTagCompound nbt = stack.getTagCompound();
		// This is the suggested key under which to store item component data.
		// You are free to change this as you please.
		if(!nbt.hasKey("oc:data")) {
			nbt.setTag("oc:data", new NBTTagCompound());
		}
		return nbt.getCompoundTag("oc:data");
	}

	public static class Device {

		private final String Class;
		private final String Description;
		private final String Vendor;
		private final String Product;
		private final String[] other;

		public Device(String Class, String Description, String Vendor, String Product, String... other) {
			this.Class = Class;
			this.Description = Description;
			this.Vendor = Vendor;
			this.Product = Product;
			this.other = other;
		}

		public Map<String, String> deviceInfo() {
			Map<String, String> deviceInfo = new HashMap<String, String>();
			deviceInfo.put(DeviceAttribute.Class, Class);
			deviceInfo.put(DeviceAttribute.Description, Description);
			deviceInfo.put(DeviceAttribute.Vendor, Vendor);
			deviceInfo.put(DeviceAttribute.Product, Product);
			for(int i = 0; i + 1 < other.length; i += 2) {
				deviceInfo.put(other[i], other[i + 1]);
			}
			return deviceInfo;
		}
	}

	public static final class Vendors {

		public static final String
			ACME = "ACME Co.",
			BuildCraft = "BuildCraft, Inc.",
			DFKI = "DFKI GmbH",
			Hosencorp = "Hosencorp AG",
			HuggingCreeper = "Hugging Creeper Industries",
			Lumiose = "Lumiose Lighting",
			NSA = "National Security Agency",
			Railcraft = "Railcraft, Inc.",
			Siekierka = "Siekierka Innovations",
			Soluna = "Soluna Technologies",
			Trumbour = "Trumbour Technology",
			Yanaki = "Yanaki Sound Systems";

		private Vendors() {
		}
	}

	private static final int maxWidth = 220;

	//Mostly stolen from Sangar
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("unchecked")
	public static void addTooltip(ItemStack stack, List tooltip, boolean advanced) {
		{
			FontRenderer font = Minecraft.getMinecraft().fontRenderer;
			final String key = stack.getUnlocalizedName() + ".tip";
			String tip = StringUtil.localize(key);
			if(!tip.equals(key)) {
				String[] lines = tip.split("\n");
				if(font == null) {
					Collections.addAll(tooltip, lines);
				} else {
					boolean shouldShorten = (font.getStringWidth(tip) > maxWidth) && !KeyBindings.showExtendedTooltips();
					if(shouldShorten) {
						tooltip.add(StringUtil.localizeAndFormat("oc:tooltip.TooLong",
							KeyBindings.getKeyBindingName(KeyBindings.extendedTooltip())));
					} else {
						for(String line : lines) {
							List list = font.listFormattedStringToWidth(line, maxWidth);
							tooltip.addAll(list);
						}
					}
				}
			}
		}
		if(ItemCosts.hasCosts(stack)) {
			if(KeyBindings.showMaterialCosts()) {
				ItemCosts.addTooltip(stack, tooltip);
			} else {
				tooltip.add(StringUtil.localizeAndFormat(
					"oc:tooltip.MaterialCosts",
					KeyBindings.getKeyBindingName(KeyBindings.materialCosts())));
			}
		}
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey("oc:data")) {
			NBTTagCompound data = stack.getTagCompound().getCompoundTag("oc:data");
			if(data.hasKey("node") && data.getCompoundTag("node").hasKey("address")) {
				tooltip.add(EnumChatFormatting.DARK_GRAY
					+ data.getCompoundTag("node").getString("address").substring(0, 13) + "..."
					+ EnumChatFormatting.GRAY);
			}
		}
		if(advanced) {
			Item item = Driver.driverFor(stack);
			tooltip.add(StringUtil.localizeAndFormat("oc:tooltip.Tier", item != null ? item.tier(stack) + 1 : 0));
		}
	}

	private static final EnumRarity[] rarities = new EnumRarity[] { EnumRarity.common, EnumRarity.uncommon, EnumRarity.rare, EnumRarity.epic };

	public static EnumRarity getRarityByTier(ItemStack stack) {
		Item item = Driver.driverFor(stack);
		int tier = item != null ? Math.min(Math.max(item.tier(stack), 0), rarities.length - 1) : 0;
		return rarities[tier];
	}

	public static EnumRarity getRarityByTier(int tier) {
		return rarities[Math.min(Math.max(tier, 0), rarities.length - 1)];
	}
}

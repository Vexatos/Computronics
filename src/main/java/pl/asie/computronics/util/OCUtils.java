package pl.asie.computronics.util;

import li.cil.oc.client.KeyBindings;
import li.cil.oc.util.ItemCosts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

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

	private static final int maxWidth = 220;

	//Mostly stolen from Sangar
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("unchecked")
	public static void addTooltip(ItemStack stack, List tooltip) {
		{
			FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
			final String key = stack.getUnlocalizedName() + ".tip";
			String tip = StringUtil.localize(key);
			if(!tip.equals(key)) {
				String[] lines = tip.split("\n");
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
				tooltip.add(TextFormatting.DARK_GRAY
					+ data.getCompoundTag("node").getString("address").substring(0, 13) + "..."
					+ TextFormatting.GRAY);
			}
		}
	}

}

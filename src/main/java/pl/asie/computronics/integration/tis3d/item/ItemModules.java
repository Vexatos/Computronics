package pl.asie.computronics.integration.tis3d.item;

import li.cil.tis3d.api.machine.Casing;
import li.cil.tis3d.api.machine.Face;
import li.cil.tis3d.api.module.Module;
import li.cil.tis3d.api.module.ModuleProvider;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.integration.tis3d.IntegrationTIS3D;
import pl.asie.computronics.integration.tis3d.manual.IModuleWithDocumentation;
import pl.asie.computronics.integration.tis3d.module.ModuleBoom;
import pl.asie.computronics.integration.tis3d.module.ModuleColorful;
import pl.asie.computronics.integration.tis3d.module.ModuleTapeReader;
import pl.asie.computronics.item.ItemMultipleComputronics;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.internal.IItemWithColor;

import java.awt.*;

/**
 * @author Vexatos
 */
public class ItemModules extends ItemMultipleComputronics implements ModuleProvider, IModuleWithDocumentation, IItemWithColor {

	public ItemModules() {
		super(Mods.Computronics, new String[] {
			"module_colorful",
			"module_tape_reader",
			"module_boom"
		});
		this.setCreativeTab(Computronics.tab);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tabs, NonNullList<ItemStack> list) {
		if(!this.isInCreativeTab(tabs)) {
			return;
		}
		if(Config.TIS3D_MODULE_COLORFUL) {
			list.add(new ItemStack(this, 1, 0));
		}
		if(Config.TIS3D_MODULE_TAPE_READER) {
			list.add(new ItemStack(this, 1, 1));
		}
		if(Config.TIS3D_MODULE_BOOM) {
			list.add(new ItemStack(this, 1, 2));
		}
	}

	@Override
	protected void registerItemModel(int meta) {
		Computronics.proxy.registerItemModel(this, meta, "computronics:tis3d/" + parts[meta]);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemstack(ItemStack stack, int pass) {
		switch(stack.getItemDamage()) {
			case 0: {
				if(pass == 1) {
					return Color.HSBtoRGB((((System.currentTimeMillis() + (stack.hashCode() % 30000)) % 30000) / 30000F), 1F, 1F) & 0xFFFFFF;
				}
			}
			case 2: {
				if(pass == 1) {
					int red = 0x44;
					red = red + (int) ((0xFF - red) * ((Math.sin(System.currentTimeMillis() / 5000D * 2D * Math.PI) + 1D) / 2D));
					return (red << 16);
				}
			}
			default: {
				return 0xFFFFFFFF;
			}
		}
	}

	@Override
	public String getDocumentationName(ItemStack stack) {
		switch(stack.getItemDamage()) {
			case 0:
				return "colorful_module";
			case 1:
				return "tape_reader_module";
			case 2:
				return "self_destructing_module";
			default:
				return "index";
		}
	}

	@Override
	public boolean worksWith(ItemStack stack, Casing casing, Face face) {
		return stack.getItem() == IntegrationTIS3D.itemModules;
	}

	@Override
	public Module createModule(ItemStack stack, Casing casing, Face face) {
		switch(stack.getItemDamage()) {
			case 0:
				return new ModuleColorful(casing, face);
			case 1:
				return new ModuleTapeReader(casing, face);
			case 2:
				return new ModuleBoom(casing, face);
			default:
				return null;
		}
	}
}

package pl.asie.computronics.integration.tis3d.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import li.cil.tis3d.api.machine.Casing;
import li.cil.tis3d.api.machine.Face;
import li.cil.tis3d.api.module.Module;
import li.cil.tis3d.api.module.ModuleProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.integration.tis3d.IntegrationTIS3D;
import pl.asie.computronics.integration.tis3d.manual.IModuleWithDocumentation;
import pl.asie.computronics.integration.tis3d.module.ModuleBoom;
import pl.asie.computronics.integration.tis3d.module.ModuleColorful;
import pl.asie.computronics.integration.tis3d.module.ModuleTapeReader;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.lib.item.ItemMultiple;

import java.util.List;

/**
 * @author Vexatos
 */
public class ItemModules extends ItemMultiple implements ModuleProvider, IModuleWithDocumentation {

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
	@SuppressWarnings("unchecked")
	public void getSubItems(Item item, CreativeTabs tabs, List list) {
		if(Config.TIS3D_MODULE_COLORFUL) {
			list.add(new ItemStack(item, 1, 0));
		}
		if(Config.TIS3D_MODULE_TAPE_READER) {
			list.add(new ItemStack(item, 1, 1));
		}
		if(Config.TIS3D_MODULE_BOOM) {
			list.add(new ItemStack(item, 1, 2));
		}
	}

	//private IIcon tapeReaderBack, tapeReaderCenter, tapeReaderOff;

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister r) {
		super.registerIcons(r);
		/*for(int i = 0; i < this.parts.length; ++i) {
			this.partIcons[i] = r.registerIcon(this.mod + ":tis3d/" + this.parts[i]);
		}
		tapeReaderBack = r.registerIcon("computronics:tis3d/module_tape_reader_back");
		tapeReaderCenter = r.registerIcon("computronics:module_tape_reader_center");
		tapeReaderOff = r.registerIcon("computronics:module_tape_reader_off");*/
	}

	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public int getRenderPasses(int meta) {
		switch(meta) {
			default: // Nothing so far
				return 1;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamageForRenderPass(int meta, int pass) {
		/*switch(meta) {
			case 1: {
				switch(pass) {
					case 1:
						return tapeReaderBack;
					case 2:
						return tapeReaderCenter;
					case 3:
						return tapeReaderOff;
				}
			}
			default: {
				return super.getIconFromDamageForRenderPass(meta, pass);
			}
		}*/
		return super.getIconFromDamageForRenderPass(meta, pass);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int meta) {
		//return getIconFromDamageForRenderPass(meta, 0);
		return super.getIconFromDamage(meta);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int pass) {
		return super.getColorFromItemStack(stack, pass);
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
		return stack.getItem() != null && stack.getItem() == IntegrationTIS3D.itemModules;
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

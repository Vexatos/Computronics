package pl.asie.computronics.integration.gregtech;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.reference.Mods;
import pl.asie.lib.item.ItemMultiple;

/**
 * @author Vexatos
 */
public class ItemPartsGreg extends ItemMultiple {

	public ItemPartsGreg() {
		super(Mods.Computronics, new String[] {
			"reelChromoxide"
		});
		this.setCreativeTab(Computronics.tab);
	}

	private IIcon reelChromoxide_GT6;

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister r) {
		super.registerIcons(r);
		reelChromoxide_GT6 = r.registerIcon("computronics:reelChromoxide_GT6");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int meta) {
		switch(meta) {
			case 0: {
				if(Mods.hasVersion(Mods.GregTech, Mods.Versions.GregTech6)) {
					return reelChromoxide_GT6;
				}
			}
		}
		return super.getIconFromDamage(meta);
	}
}

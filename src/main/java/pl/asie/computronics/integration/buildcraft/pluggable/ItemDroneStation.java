package pl.asie.computronics.integration.buildcraft.pluggable;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.pluggable.IPipePluggableItem;
import buildcraft.api.transport.pluggable.PipePluggable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.oc.manual.IItemWithPrefix;

/**
 * @author Vexatos
 */
public class ItemDroneStation extends Item implements IPipePluggableItem, IItemWithPrefix {

	public ItemDroneStation() {
		super();
		setCreativeTab(Computronics.tab);
		setUnlocalizedName("computronics.dockingStation");
	}

	@Override
	public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		// NOOP
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getSpriteNumber() {
		return 0;
	}

	@Override
	public PipePluggable createPipePluggable(IPipe pipe, ForgeDirection side, ItemStack stack) {
		switch(side) {
			case UP:
				return new DroneStationPluggable();
			default:
				return null;
		}
	}

	@Override
	public String getDocumentationName(ItemStack stack) {
		return "drone_station";
	}

	@Override
	public String getPrefix(ItemStack stack) {
		return "buildcraft/";
	}
}

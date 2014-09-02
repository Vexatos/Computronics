package pl.asie.computronics.item;

import mods.railcraft.common.carts.EntityLocomotive;
import mods.railcraft.common.carts.EntityLocomotiveElectric;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.tile.TileLocomotiveRelay;

import java.util.List;

/**
 * @author Vexatos
 */
public class ItemRelaySensor extends Item {

	private IIcon icon;

	public ItemRelaySensor() {
		super();
		this.setCreativeTab(Computronics.tab);
		this.setHasSubtypes(false);
		this.setUnlocalizedName("computronics.relaySensor");
		this.setTextureName("computronics:relay_sensor");
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		this.setNoRepair();
	}

	@Override
	public void registerIcons(IIconRegister ir) {
		icon = ir.registerIcon("computronics:relay_sensor");
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		return icon;
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if(player.isSneaking() && world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileLocomotiveRelay && !player.worldObj.isRemote) {
			if(!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			if(stack.hasTagCompound()) {
				NBTTagCompound data = stack.getTagCompound();
				data.setInteger("relayX", x);
				data.setInteger("relayY", y);
				data.setInteger("relayZ", z);
				data.setBoolean("bound", true);
				player.swingItem();
			}
		}
		return false;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		if(player.isSneaking() && entity != null) {
			if(stack.hasTagCompound() && stack.getTagCompound().getBoolean("bound") && !player.worldObj.isRemote) {
				NBTTagCompound data = stack.getTagCompound();
				int x = data.getInteger("relayX");
				int y = data.getInteger("relayY");
				int z = data.getInteger("relayZ");
				if(entity instanceof EntityLocomotiveElectric) {
					if(entity.worldObj.getTileEntity(x, y, z) instanceof TileLocomotiveRelay) {
						TileLocomotiveRelay relay = (TileLocomotiveRelay) entity.worldObj.getTileEntity(x, y, z);
						EntityLocomotiveElectric loco = (EntityLocomotiveElectric) entity;
						if(loco.dimension == relay.getWorldObj().provider.dimensionId) {
							if(loco.getDistance(relay.xCoord, relay.yCoord, relay.zCoord) <= Computronics.LOCOMOTIVE_RELAY_RANGE) {
								relay.setLocomotive(loco);
								player.addChatComponentMessage(new ChatComponentTranslation("chat.computronics.sensor.bound"));
								player.swingItem();
								player.destroyCurrentEquippedItem();
							} else {
								player.addChatComponentMessage(new ChatComponentTranslation("chat.computronics.sensor.tooFarAway"));
							}
						} else {
							player.addChatComponentMessage(new ChatComponentTranslation("chat.computronics.sensor.wrongDim"));
						}
					} else {
						player.addChatComponentMessage(new ChatComponentTranslation("chat.computronics.sensor.noRelay"));
					}
				} else if(entity instanceof EntityLocomotive) {
					player.addChatComponentMessage(new ChatComponentTranslation("chat.computronics.sensor.wrongLoco"));
					return true;
				}
			}
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List text, boolean par4) {
		String descKey;
		if(stack.hasTagCompound() && stack.getTagCompound().getBoolean("bound")) {
			NBTTagCompound data = stack.getTagCompound();
			int x = data.getInteger("relayX");
			int y = data.getInteger("relayY");
			int z = data.getInteger("relayZ");
			text.add(EnumChatFormatting.AQUA + StatCollector.translateToLocalFormatted("tooltip.computronics.sensor.bound",
				String.valueOf(x), String.valueOf(y), String.valueOf(z)));

			descKey = "tooltip.computronics.sensor.desc2";
		} else {
			descKey = "tooltip.computronics.sensor.desc1";
		}
		String[] local = StatCollector.translateToLocal(descKey)
			.replace("\\n", "\n").split("\\n");
		for(String s : local) {
			text.add(EnumChatFormatting.GRAY + s);
		}
	}

}

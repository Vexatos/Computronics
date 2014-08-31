package pl.asie.computronics.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.common.carts.EntityLocomotive;
import mods.railcraft.common.carts.EntityLocomotiveElectric;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
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

	private int relayX, relayY, relayZ;
	private boolean isBound, isInitialized = false;

	public ItemRelaySensor() {
		super();
		this.setCreativeTab(Computronics.tab);
		this.setHasSubtypes(true);
		this.setUnlocalizedName("computronics.relaySensor");
		this.setTextureName("computronics:relay_sensor");
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		this.setNoRepair();
	}

	@SuppressWarnings("unchecked")
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tabs, List list) {
		list.add(new ItemStack(item, 1, 0));
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
		if(!isInitialized) {
			if(!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			if(!stack.getTagCompound().hasKey("relay")){
				stack.getTagCompound().setTag("relay", new NBTTagCompound());
			}
			if(stack.hasTagCompound()) {
				NBTTagCompound data = stack.getTagCompound().getCompoundTag("relay");
				if(data.getBoolean("bound")) {
					this.relayX = data.getInteger("relayX");
					this.relayY = data.getInteger("relayY");
					this.relayZ = data.getInteger("relayZ");
					this.isBound = true;
				}
			}
			isInitialized = true;
		}
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
		if(player.isSneaking() && world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileLocomotiveRelay && player.worldObj.isRemote) {
			this.relayX = x;
			this.relayY = y;
			this.relayZ = z;
			if(stack.hasTagCompound() && stack.getTagCompound().hasKey("relay")) {
				NBTTagCompound data = stack.getTagCompound().getCompoundTag("relay");
				data.setInteger("relayX", relayX);
				data.setInteger("relayY", relayY);
				data.setInteger("relayZ", relayZ);
				data.setBoolean("bound", true);
				isBound = true;
				player.swingItem();
			}
		}
		return false;
	}

	/*@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if(player.isSneaking()) {
			MovingObjectPosition objectMouseOver = player.rayTrace(5, 1);
			if(objectMouseOver != null && objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
				Entity entity = objectMouseOver.entityHit;
				if(entity != null && entity instanceof EntityLocomotiveElectric && world.getTileEntity(relayX, relayY, relayZ) instanceof TileLocomotiveRelay) {
					TileLocomotiveRelay relay = ((TileLocomotiveRelay) world.getTileEntity(relayX, relayY, relayZ));
					relay.setLocomotive((EntityLocomotiveElectric) entity);
					player.addChatComponentMessage(new ChatComponentTranslation("chat.computronics.sensor.bound"));
					player.destroyCurrentEquippedItem();
				} else if(entity != null && entity instanceof EntityLocomotive) {
					player.addChatComponentMessage(new ChatComponentTranslation("chat.computronics.sensor.error"));
				}
			}
		}
		return stack;
	}*/

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		if(player.isSneaking() && entity != null) {
			if(isBound && player.worldObj.isRemote) {
				if(entity instanceof EntityLocomotiveElectric) {
					if(entity.worldObj.getTileEntity(relayX, relayY, relayZ) instanceof TileLocomotiveRelay) {
						TileLocomotiveRelay relay = ((TileLocomotiveRelay) entity.worldObj.getTileEntity(relayX, relayY, relayZ));
						relay.setLocomotive((EntityLocomotiveElectric) entity);
						player.addChatComponentMessage(new ChatComponentTranslation("chat.computronics.sensor.bound"));
						player.swingItem();
						player.destroyCurrentEquippedItem();
					} else {
						player.addChatComponentMessage(new ChatComponentTranslation("chat.computronics.sensor.error1"));
					}
				} else if(entity instanceof EntityLocomotive) {
					player.addChatComponentMessage(new ChatComponentTranslation("chat.computronics.sensor.error2"));
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
		if(isBound) {
			text.add(EnumChatFormatting.AQUA + StatCollector.translateToLocalFormatted("tooltip.computronics.sensor.bound",
				String.valueOf(relayX), String.valueOf(relayY), String.valueOf(relayZ)));

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

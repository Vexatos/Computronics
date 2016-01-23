package pl.asie.lib.tweak.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import pl.asie.lib.AsieLibMod;
import pl.asie.lib.util.RayTracer;

/**
 * @author Vexatos
 */
public class EnchantmentTweak {

	public static EnchantmentBetterBane bane;

	public static void registerBaneEnchantment(int enchID) {
		if(!(enchID < 0 || enchID >= 256)) {
			if(Enchantment.getEnchantmentById(enchID) == null) {
				bane = new EnchantmentBetterBane(244);
				return;
			}
			for(int i = enchID; i < 256; i++) {
				if(Enchantment.getEnchantmentById(i) == null) {
					AsieLibMod.log.info("Enchantment ID " + enchID + " already occupied, using " + i + " instead");
					bane = new EnchantmentBetterBane(i);
					return;
				}
			}
		}
		throw new IllegalArgumentException("No valid enchantment id! " + EnchantmentBetterBane.class + " Enchantment ID:" + enchID);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void anvilEvent(AnvilUpdateEvent e) {
		if(e.left == null || e.right == null || e.left.getItem() == null || e.right.getItem() == null || e.isCanceled()) {
			return;
		}
		if(e.left.isItemStackDamageable() && e.left.isItemEnchanted()) {
			if(e.right.getItem() == Items.fermented_spider_eye && !hasBaneEnchantment(e.left)) {
				if(e.right.stackSize == e.right.getMaxStackSize()) {
					e.output = e.left.copy();
					e.cost = 37;
					if(!addBaneEnchantment(e.output, 9)) {
						e.output = null;
						if(e.isCancelable()) {
							e.setCanceled(true);
						}
					}
				} else {
					e.output = null;
					if(e.isCancelable()) {
						e.setCanceled(true);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void enchEvent(TickEvent.PlayerTickEvent e) {
		EntityPlayer player = e.player;
		if(player.worldObj.isRemote) {
			return;
		}
		if(player.getCurrentEquippedItem() != null && hasBaneEnchantment(player.getCurrentEquippedItem())
			&& player.getCurrentEquippedItem().isItemStackDamageable()) {

			RayTracer.instance().fire(player, 10.0);
			MovingObjectPosition target = RayTracer.instance().getTarget();
			if(target != null && target.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
				Entity entity = target.entityHit;
				if(entity != null
					&& entity instanceof EntityLivingBase
					&& ((EntityLivingBase) entity).getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD
					&& entity.hurtResistantTime <= 10
					&& !player.isBlocking()) {
					player.attackTargetEntityWithCurrentItem(entity);
					if(player.getCurrentEquippedItem().isItemStackDamageable()) {
						float distance = player.getDistanceToEntity(entity);
						int damage = Math.max(Math.min((int) distance + 1, 10), 1);
						player.getCurrentEquippedItem().damageItem(damage, player);
					}
				}
			}
		}
	}

	private static boolean hasBaneEnchantment(ItemStack stack) {
		if(stack.getTagCompound() == null || stack.getTagCompound().hasNoTags()) {
			return false;
		}

		if(!stack.getTagCompound().hasKey("ench", 9)) {
			return false;
		}

		NBTTagList list = stack.getTagCompound().getTagList("ench", 10);
		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			if(tag != null && tag.getShort("id") == bane.effectId) {
				return true;
			}
		}
		return false;
	}

	private static boolean addBaneEnchantment(ItemStack stack, int level) {
		if(stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
		}

		if(!stack.getTagCompound().hasKey("ench", 9)) {
			stack.getTagCompound().setTag("ench", new NBTTagList());
		}

		NBTTagList list = stack.getTagCompound().getTagList("ench", 10);
		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			if(tag != null && tag.getShort("id") == Enchantment.baneOfArthropods.effectId
				&& tag.getShort("lvl") == (short) 5) {
				list.removeTag(i);
				NBTTagCompound data = new NBTTagCompound();
				data.setShort("id", (short) bane.effectId);
				data.setShort("lvl", (short) ((byte) level));
				list.appendTag(data);
				return true;
			}
		}
		return false;
	}
}

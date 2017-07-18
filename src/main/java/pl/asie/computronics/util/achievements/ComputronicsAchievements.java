/*package pl.asie.computronics.util.achievements;

//import net.minecraftforge.fml.common.FMLCommonHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import pl.asie.computronics.Computronics;

import javax.annotation.Nullable;
import java.util.HashMap;

/**
 * @author Vexatos
 * /
public class ComputronicsAchievements {

	public HashMap<String, Achievement> achievementMap = new HashMap<String, Achievement>();

	private enum EnumAchievements {

		Tape("gotTape"),
		Tape_Star("gotStarTape"),
		Tape_IG("gotIGTape"),
		Tape_IG_Dropped("droppedIGTape"),
		Locomotive("gotLoco"),
		Relay("gotRelay");

		String key;

		EnumAchievements(String key) {
			this.key = key;
		}

		private String getKey() {
			return this.key;
		}

	}

	public void initialize() {
		initializeAchievements();

		AchievementPage.registerAchievementPage(new AchievementPage("Computronics", (Achievement[]) this.achievementMap.values().toArray(new Achievement[this.achievementMap.size()])));

		MinecraftForge.EVENT_BUS.register(this);
	}

	private void initializeAchievements() {
		if(Computronics.itemTape != null) {
			this.registerAchievement(EnumAchievements.Tape, 0, 0, new ItemStack(Computronics.itemTape, 1, 0), null, false, true);
			this.registerAchievement(EnumAchievements.Tape_Star, 4, 0, new ItemStack(Computronics.itemTape, 1, 8), this.getAchievement(EnumAchievements.Tape), false, false);

			/*if(Mods.hasVersion(Mods.GregTech, Mods.Versions.GregTech5)) { TODO GregTech
				this.registerAchievement(EnumAchievements.Tape_IG, 8, 2, new ItemStack(Computronics.itemTape, 1, 9), this.getAchievement(EnumAchievements.Tape_Star), true, false);
				this.registerAchievement(EnumAchievements.Tape_IG_Dropped, 8, 10, ItemList.IC2_Scrap.get(1), this.getAchievement(EnumAchievements.Tape_IG), true, false);
			} else if(Mods.hasVersion(Mods.GregTech, Mods.Versions.GregTech6)) {
				this.registerAchievement(EnumAchievements.Tape_IG, 8, 2, new ItemStack(Computronics.itemTape, 1, 9), this.getAchievement(EnumAchievements.Tape_Star), true, false);
				this.registerAchievement(EnumAchievements.Tape_IG_Dropped, 8, 10, IL.IC2_Scrap.get(1), this.getAchievement(EnumAchievements.Tape_IG), true, false);
			}* /
		}

		/*if(Mods.isLoaded(Mods.Railcraft)) { TODO Railcraft
			RailcraftAchievements.initializeRCAchievements();
		}* /
	}

	private Achievement registerAchievement(EnumAchievements key, int x, int y, ItemStack icon, @Nullable Achievement requirement, boolean special, boolean independent) {
		Achievement achievement = new Achievement("computronics." + key.getKey(), "computronics." + key.getKey(), x, y, icon, requirement);
		if(special) {
			achievement.setSpecial();
		}
		if(independent) {
			achievement.initIndependentStat();
		}
		achievement.registerStat();
		this.achievementMap.put(key.getKey(), achievement);
		return achievement;
	}

	private void triggerAchievement(@Nullable EntityPlayer player, EnumAchievements key) {
		if(player != null && this.achievementMap.containsKey(key.getKey())) {
			player.addStat(this.achievementMap.get(key.getKey()));
		}
	}

	@Nullable
	public Achievement getAchievement(EnumAchievements key) {
		if(this.achievementMap.containsKey(key.getKey())) {
			return this.achievementMap.get(key.getKey());
		}
		return null;
	}

	@SubscribeEvent
	public void onCrafting(PlayerEvent.ItemCraftedEvent event) {
		ItemStack stack = event.crafting;
		EntityPlayer player = event.player;
		if(player == null || stack.isEmpty()) {
			return;
		}
		if(Computronics.itemTape != null && stack.getItem() == Computronics.itemTape) {
			switch(stack.getItemDamage()) {
				case 9: {
					this.triggerAchievement(player, EnumAchievements.Tape_IG);
					break;
				}
				case 4:
				case 8: {
					this.triggerAchievement(player, EnumAchievements.Tape_Star);
					break;
				}
				default: {
					this.triggerAchievement(player, EnumAchievements.Tape);
					break;
				}
			}
		} /*else if(Mods.isLoaded(Mods.Railcraft)) { TODO Railcraft
			RailcraftAchievements.onCrafting(stack, player);
		}* /
	}

	/*@SubscribeEvent
	public void onLeftClickEntity(AttackEntityEvent event) {
		if(Mods.isLoaded(Mods.Railcraft)) {
			RailcraftAchievements.onLeftClickEntity(event);
		}
	}* /

	/*@SubscribeEvent
	public void onItemDropped(ItemTossEvent event) {
		if(event == null || event.player == null || event.entityItem == null
			|| (event.entityItem.world != null && event.entityItem.world.isRemote)) {
			return;
		}
		EntityPlayer player = event.player;
		EntityItem item = event.entityItem;
		ItemStack stack = item.getEntityItem();

		if(!stack.isEmpty() && Computronics.itemTape != null
			&& stack.getItem() == Computronics.itemTape && stack.getItemDamage() == 9) {
			if(!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			NBTTagCompound data = stack.getTagCompound();
			data.setString("computronics:dropplayer", player.getCommandSenderName());
			stack.setTagCompound(data);
		}
	}* /

	/*@SubscribeEvent
	public void onItemDespawn(ItemExpireEvent event) {
		if(event == null || event.entityItem == null
			|| (event.entityItem.world != null && event.entityItem.world.isRemote)) {
			return;
		}
		EntityItem item = event.entityItem;
		ItemStack stack = item.getEntityItem();
		if(!stack.isEmpty() && Computronics.itemTape != null
			&& stack.getItem() == Computronics.itemTape && stack.getItemDamage() == 9) {
			if(stack.hasTagCompound()) {
				NBTTagCompound data = stack.getTagCompound();
				if(data.hasKey("computronics:dropplayer")) {
					String playername = data.getString("computronics:dropplayer");
					if(playername != null && !playername.isEmpty()) {
						for(EntityPlayer o : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerList()) {
							if(o instanceof EntityPlayer && ((EntityPlayer) o).getCommandSenderName().equals(playername)) {
								this.triggerAchievement((EntityPlayer) o, EnumAchievements.Tape_IG_Dropped);
								((EntityPlayer) o).addChatMessage(new ChatComponentText("Test"));
								data.removeTag("computronics:dropplayer");
								if(data.hasNoTags()) {
									stack.setTagCompound(null);
								} else {
									stack.setTagCompound(data);
								}
							}
						}
					}
				}
			}
		}
	}* /

	//**
	 * All the Railcraft related Achievements
	 * /
	private static class RailcraftAchievements {

		private static void initializeRCAchievements() {
			Computronics.instance.achievements.registerAchievement(EnumAchievements.Locomotive, 0, 4, RailcraftCarts.LOCO_ELECTRIC.getStack(), null, false, true);
			Computronics.instance.achievements.registerAchievement(EnumAchievements.Relay, 2, 6, new ItemStack(Computronics.railcraft.relaySensor), Computronics.instance.achievements.getAchievement(EnumAchievements.Locomotive), false, false);
		}

		private static void onCrafting(ItemStack stack, EntityPlayer player) {
			if(stack.getItem() instanceof ItemLocomotive
				&& ItemLocomotive.getModel(stack).equals(ItemLocomotive.getModel(RailcraftCarts.LOCO_ELECTRIC.getStack()))) {
				Computronics.instance.achievements.triggerAchievement(player, EnumAchievements.Locomotive);
			}
		}

		private static void onLeftClickEntity(AttackEntityEvent event) {
			if(Mods.isLoaded(Mods.Railcraft) && event.getTarget() != null && event.getTarget() instanceof EntityLocomotiveElectric) {

				EntityPlayer player = event.getEntityPlayer();
				EntityLocomotiveElectric loco = (EntityLocomotiveElectric) event.getTarget();

				if(player == null) {
					return;
				}

				ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);

				if(stack.isEmpty()) {
					return;
				}

				if(player.isSneaking() && stack.getItem() == Computronics.railcraft.relaySensor
					&& stack.hasTagCompound()) {

					NBTTagCompound data = stack.getTagCompound();
					if(data != null && data.hasKey("bound") && data.getBoolean("bound")) {

						final BlockPos pos = new BlockPos(
							data.getInteger("relayX"),
							data.getInteger("relayY"),
							data.getInteger("relayZ")
						);
						if(!player.world.isBlockLoaded(pos)) {
							return;
						}
						TileEntity tile = loco.world.getTileEntity(pos);
						if(tile != null && tile instanceof TileLocomotiveRelay) {
							TileLocomotiveRelay relay = (TileLocomotiveRelay) tile;
							if(loco.dimension == relay.getWorld().provider.getDimension()
								&& loco.getDistanceSq(relay.getPos()) <= Config.LOCOMOTIVE_RELAY_RANGE * Config.LOCOMOTIVE_RELAY_RANGE) {

								Computronics.instance.achievements.triggerAchievement(player, EnumAchievements.Relay);
							}
						}
					}
				}
			}
		}
	}* /
}
*/

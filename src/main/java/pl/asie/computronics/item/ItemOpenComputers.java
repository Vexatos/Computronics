package pl.asie.computronics.item;

import li.cil.oc.api.driver.DriverItem;
import li.cil.oc.api.driver.EnvironmentProvider;
import li.cil.oc.api.driver.item.HostAware;
import li.cil.oc.api.driver.item.Slot;
import li.cil.oc.api.driver.item.UpgradeRenderer;
import li.cil.oc.api.event.RobotRenderEvent;
import li.cil.oc.api.internal.Adapter;
import li.cil.oc.api.internal.Drone;
import li.cil.oc.api.internal.Microcontroller;
import li.cil.oc.api.internal.Rack;
import li.cil.oc.api.internal.Robot;
import li.cil.oc.api.internal.Tablet;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.ManagedEnvironment;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.oc.IntegrationOpenComputers;
import pl.asie.computronics.oc.driver.DriverBoardBoom;
import pl.asie.computronics.oc.driver.DriverBoardCapacitor;
import pl.asie.computronics.oc.driver.DriverBoardLight;
import pl.asie.computronics.oc.driver.DriverBoardSwitch;
import pl.asie.computronics.oc.driver.DriverCardBeep;
import pl.asie.computronics.oc.driver.DriverCardBoom;
import pl.asie.computronics.oc.driver.DriverCardFX;
import pl.asie.computronics.oc.driver.DriverCardNoise;
import pl.asie.computronics.oc.driver.DriverCardSound;
import pl.asie.computronics.oc.driver.DriverCardSpoof;
import pl.asie.computronics.oc.driver.RobotUpgradeCamera;
import pl.asie.computronics.oc.driver.RobotUpgradeChatBox;
import pl.asie.computronics.oc.driver.RobotUpgradeColorful;
import pl.asie.computronics.oc.driver.RobotUpgradeRadar;
import pl.asie.computronics.oc.driver.RobotUpgradeSpeech;
import pl.asie.computronics.oc.manual.IItemWithDocumentation;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.OCUtils;
import pl.asie.computronics.util.internal.IItemWithColor;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Set;

@Optional.InterfaceList({
	@Optional.Interface(iface = "li.cil.oc.api.driver.DriverItem", modid = Mods.OpenComputers),
	@Optional.Interface(iface = "li.cil.oc.api.driver.EnvironmentProvider", modid = Mods.OpenComputers),
	@Optional.Interface(iface = "li.cil.oc.api.driver.item.HostAware", modid = Mods.OpenComputers),
	@Optional.Interface(iface = "li.cil.oc.api.driver.item.UpgradeRenderer", modid = Mods.OpenComputers)
})
public class ItemOpenComputers extends ItemMultipleComputronics implements DriverItem, EnvironmentProvider, HostAware, UpgradeRenderer, IItemWithDocumentation, IItemWithColor {

	public ItemOpenComputers() {
		super(Mods.Computronics, new String[] {
			"robot_upgrade_camera",
			"robot_upgrade_chatbox",
			"robot_upgrade_radar",
			"card_fx",
			"card_spoof",
			"card_beep",
			"card_boom",
			"robot_upgrade_colorful",
			"card_noise",
			"card_sound",
			"rack_board_light",
			"rack_board_boom",
			"rack_board_capacitor",
			"rack_board_switch",
			"robot_upgrade_speech"
		});
		this.setCreativeTab(Computronics.tab);
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public boolean worksWith(ItemStack stack) {
		return stack.getItem().equals(this);
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public boolean worksWith(ItemStack stack, Class<? extends EnvironmentHost> host) {
		boolean works = worksWith(stack);
		works = works && !Adapter.class.isAssignableFrom(host);
		switch(stack.getItemDamage()) {
			case 4: {
				works = works
					&& !Tablet.class.isAssignableFrom(host)
					&& !Drone.class.isAssignableFrom(host)
					&& !Microcontroller.class.isAssignableFrom(host);
				break;
			}
			case 6: {
				works = works
					&& !Tablet.class.isAssignableFrom(host)
					&& !Drone.class.isAssignableFrom(host);
				break;
			}
			case 7: {
				works = works
					&& Robot.class.isAssignableFrom(host);
				break;
			}
			case 10:
			case 11:
			case 12:
			case 13: {
				works = works && Rack.class.isAssignableFrom(host);
				break;
			}
		}
		return works;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Class<? extends Environment> getEnvironment(ItemStack stack) {
		if(!worksWith(stack)) {
			return null;
		}
		switch(stack.getItemDamage()) {
			case 0:
				return RobotUpgradeCamera.class;
			case 1:
				return RobotUpgradeChatBox.class;
			case 2:
				return RobotUpgradeRadar.class;
			case 3:
				return DriverCardFX.class;
			case 4:
				return DriverCardSpoof.class;
			case 5:
				return DriverCardBeep.class;
			case 6:
				return DriverCardBoom.class;
			case 7:
				return RobotUpgradeColorful.class;
			case 8:
				return DriverCardNoise.class;
			case 9:
				return DriverCardSound.class;
			case 10:
				return DriverBoardLight.class;
			case 11:
				return DriverBoardBoom.class;
			case 12:
				return DriverBoardCapacitor.class;
			case 13:
				return DriverBoardSwitch.class;
			case 14:
				return RobotUpgradeSpeech.class;
			default:
				return null;
		}
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public ManagedEnvironment createEnvironment(ItemStack stack,
		EnvironmentHost container) {
		switch(stack.getItemDamage()) {
			case 0:
				return new RobotUpgradeCamera(container);
			case 1:
				return new RobotUpgradeChatBox(container);
			case 2:
				return new RobotUpgradeRadar(container);
			case 3:
				return new DriverCardFX(container);
			case 4:
				return new DriverCardSpoof(container);
			case 5:
				return new DriverCardBeep(container);
			case 6:
				return new DriverCardBoom(container);
			case 7:
				return new RobotUpgradeColorful(container);
			case 8:
				return new DriverCardNoise(container);
			case 9:
				return new DriverCardSound(container);
			case 10:
				return container instanceof Rack ? new DriverBoardLight((Rack) container) : null;
			case 11:
				return container instanceof Rack ? new DriverBoardBoom((Rack) container) : null;
			case 12:
				return container instanceof Rack ? new DriverBoardCapacitor((Rack) container) : null;
			case 13:
				return container instanceof Rack ? new DriverBoardSwitch((Rack) container) : null;
			case 14:
				return new RobotUpgradeSpeech(container);
			default:
				return null;
		}
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public String slot(ItemStack stack) {
		switch(stack.getItemDamage()) {
			case 0:
				return Slot.Upgrade;
			case 1:
				return Slot.Upgrade;
			case 2:
				return Slot.Upgrade;
			case 3:
				return Slot.Card;
			case 4:
				return Slot.Card;
			case 5:
				return Slot.Card;
			case 6:
				return Slot.Card;
			case 7:
				return Slot.Upgrade;
			case 8:
				return Slot.Card;
			case 9:
				return Slot.Card;
			case 10:
			case 11:
			case 12:
			case 13:
				return Slot.RackMountable;
			case 14:
				return Slot.Upgrade;
			default:
				return Slot.None;
		}
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public int tier(ItemStack stack) {
		switch(stack.getItemDamage()) {
			case 0:
				return 1; // Tier 2
			case 1:
				return 1; // Tier 2
			case 2:
				return 2; // Tier 3
			case 3:
				return 1; // Tier 2
			case 4:
				return 1; // Tier 2
			case 5:
				return 1; // Tier 2
			case 6:
				return 0; // Tier 1
			case 7:
				return 1; // Tier 2
			case 8:
				return 1; // Tier 2
			case 9:
				return 1; // Tier 2
			case 10:
			case 11:
			case 12:
			case 13:
				return 0; // Tier 1
			case 14:
				return 1; // Tier 2
			default:
				return 0; // Tier 1 default
		}
	}

	@Override
	public String getDocumentationName(ItemStack stack) {
		switch(stack.getItemDamage()) {
			case 0:
				return "camera_upgrade";
			case 1:
				return "chat_upgrade";
			case 2:
				return "radar_upgrade";
			case 3:
				return "particle_card";
			case 4:
				return "spoofing_card";
			case 5:
				return "beep_card";
			case 6:
				return "self_destructing_card";
			case 7:
				return "colorful_upgrade";
			case 8:
				return "noise_card";
			case 9:
				return "sound_card";
			case 10:
				return "light_board";
			case 11:
				return "server_self_destructor";
			case 12:
				return "rack_capacitor";
			case 13:
				return "switch_board";
			case 14:
				return "speech_upgrade";
			default:
				return "index";
		}
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public NBTTagCompound dataTag(ItemStack stack) {
		return OCUtils.dataTag(stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tabs, NonNullList<ItemStack> list) {
		if(!this.isInCreativeTab(tabs)) {
			return;
		}
		if(Config.OC_UPGRADE_CAMERA) {
			list.add(new ItemStack(this, 1, 0));
		}
		if(Config.OC_UPGRADE_CHATBOX) {
			list.add(new ItemStack(this, 1, 1));
		}
		if(Config.OC_UPGRADE_RADAR) {
			list.add(new ItemStack(this, 1, 2));
		}
		if(Config.OC_CARD_FX) {
			list.add(new ItemStack(this, 1, 3));
		}
		if(Config.OC_CARD_SPOOF) {
			list.add(new ItemStack(this, 1, 4));
		}
		if(Config.OC_CARD_BEEP) {
			list.add(new ItemStack(this, 1, 5));
		}
		if(Config.OC_CARD_BOOM) {
			list.add(new ItemStack(this, 1, 6));
		}
		if(Config.OC_UPGRADE_COLORFUL) {
			list.add(new ItemStack(this, 1, 7));
		}
		if(Config.OC_CARD_NOISE) {
			list.add(new ItemStack(this, 1, 8));
		}
		if(Config.OC_CARD_SOUND) {
			list.add(new ItemStack(this, 1, 9));
		}
		if(Config.OC_BOARD_LIGHT) {
			list.add(new ItemStack(this, 1, 10));
		}
		if(Config.OC_BOARD_BOOM) {
			list.add(new ItemStack(this, 1, 11));
		}
		if(Config.OC_BOARD_CAPACITOR) {
			list.add(new ItemStack(this, 1, 12));
		}
		if(Config.OC_BOARD_SWITCH) {
			list.add(new ItemStack(this, 1, 13));
		}
		if(Config.OC_UPGRADE_SPEECH) {
			list.add(new ItemStack(this, 1, 14));
		}
	}

	//private IIcon colorfulUpgradeCanvasIcon, colorfulUpgradeTopIcon;

	/*@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister r) {
		super.registerIcons(r);
		colorfulUpgradeCanvasIcon = r.registerSprite("computronics:robot_upgrade_colorful_canvas");
		colorfulUpgradeTopIcon = r.registerSprite("computronics:robot_upgrade_colorful_top");
	}

	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public int getRenderPasses(int meta) {
		return meta == 7 ? 3 : 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamageForRenderPass(int meta, int pass) {
		switch(meta) {
			case 7: {
				switch(pass) {
					case 1: {
						return colorfulUpgradeCanvasIcon;
					}
					case 2: {
						return colorfulUpgradeTopIcon;
					}
				}
			}
			default: {
				return super.getIconFromDamageForRenderPass(meta, pass);
			}
		}
	}*/

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemstack(ItemStack stack, int pass) {
		switch(stack.getItemDamage()) {
			case 7: {
				if(pass == 1) {
					NBTTagCompound tag = dataTag(stack);
					if(tag.hasKey("computronics:color")) {
						int col = tag.getInteger("computronics:color");
						if(col >= 0) {
							return col;
						}
					}
					return Color.HSBtoRGB((((System.currentTimeMillis() + (stack.hashCode() % 30000)) % 30000) / 30000F), 1F, 1F) & 0xFFFFFF;
				}
			}
			default: {
				return 0xFFFFFFFF;
			}
		}
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return OCUtils.getRarityByTier(stack);
	}

	//Mostly stolen from Sangar
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		OCUtils.addTooltip(stack, tooltip, flag);
	}

	@Override
	public void registerItemModels() {
		if(!Computronics.proxy.isClient()) {
			return;
		}
		if(Config.OC_UPGRADE_CAMERA) {
			registerItemModel(0);
		}
		if(Config.OC_UPGRADE_CHATBOX) {
			registerItemModel(1);
		}
		if(Config.OC_UPGRADE_RADAR) {
			registerItemModel(2);
		}
		if(Config.OC_CARD_FX) {
			registerItemModel(3);
		}
		if(Config.OC_CARD_SPOOF) {
			registerItemModel(4);
		}
		if(Config.OC_CARD_BEEP) {
			registerItemModel(5);
		}
		if(Config.OC_CARD_BOOM) {
			registerItemModel(6);
		}
		if(Config.OC_UPGRADE_COLORFUL) {
			registerItemModel(7);
		}
		if(Config.OC_CARD_NOISE) {
			registerItemModel(8);
		}
		if(Config.OC_CARD_SOUND) {
			registerItemModel(9);
		}
		if(Config.OC_BOARD_LIGHT) {
			registerItemModel(10);
		}
		if(Config.OC_BOARD_BOOM) {
			registerItemModel(11);
		}
		if(Config.OC_BOARD_CAPACITOR) {
			registerItemModel(12);
		}
		if(Config.OC_BOARD_SWITCH) {
			registerItemModel(13);
		}
		registerItemModel(14);
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public String computePreferredMountPoint(ItemStack stack, Robot robot, Set<String> availableMountPoints) {
		return IntegrationOpenComputers.upgradeRenderer.computePreferredMountPoint(stack, robot, availableMountPoints);
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public void render(ItemStack stack, RobotRenderEvent.MountPoint mountPoint, Robot robot, float pt) {
		IntegrationOpenComputers.upgradeRenderer.render(stack, mountPoint, robot, pt);
	}
}

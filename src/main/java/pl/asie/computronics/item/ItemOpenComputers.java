package pl.asie.computronics.item;

import li.cil.oc.api.driver.EnvironmentAware;
import li.cil.oc.api.driver.EnvironmentHost;
import li.cil.oc.api.driver.Item;
import li.cil.oc.api.driver.item.HostAware;
import li.cil.oc.api.driver.item.Slot;
import li.cil.oc.api.driver.item.UpgradeRenderer;
import li.cil.oc.api.event.RobotRenderEvent;
import li.cil.oc.api.internal.Adapter;
import li.cil.oc.api.internal.Drone;
import li.cil.oc.api.internal.Microcontroller;
import li.cil.oc.api.internal.Robot;
import li.cil.oc.api.internal.Tablet;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.client.KeyBindings;
import li.cil.oc.util.ItemCosts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.oc.DriverCardBoom;
import pl.asie.computronics.oc.DriverCardFX;
import pl.asie.computronics.oc.DriverCardSound;
import pl.asie.computronics.oc.DriverCardSpoof;
import pl.asie.computronics.oc.IntegrationOpenComputers;
import pl.asie.computronics.oc.RobotUpgradeCamera;
import pl.asie.computronics.oc.RobotUpgradeChatBox;
import pl.asie.computronics.oc.RobotUpgradeColorful;
import pl.asie.computronics.oc.RobotUpgradeRadar;
import pl.asie.computronics.oc.manual.IItemWithDocumentation;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.StringUtil;

import java.awt.*;
import java.util.List;
import java.util.Set;

@Optional.InterfaceList({
	@Optional.Interface(iface = "li.cil.oc.api.driver.Item", modid = Mods.OpenComputers),
	@Optional.Interface(iface = "li.cil.oc.api.driver.EnvironmentAware", modid = Mods.OpenComputers),
	@Optional.Interface(iface = "li.cil.oc.api.driver.item.HostAware", modid = Mods.OpenComputers),
	@Optional.Interface(iface = "li.cil.oc.api.driver.item.UpgradeRenderer", modid = Mods.OpenComputers)
})
public class ItemOpenComputers extends ItemMultiple implements Item, EnvironmentAware, HostAware, UpgradeRenderer, IItemWithDocumentation {

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
		}
		return works;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Class<? extends Environment> providedEnvironment(ItemStack stack) {
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
				return DriverCardSound.class;
			case 6:
				return DriverCardBoom.class;
			case 7:
				return RobotUpgradeColorful.class;
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
				return new DriverCardSound(container);
			case 6:
				return new DriverCardBoom(container);
			case 7:
				return new RobotUpgradeColorful(container);
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
			default:
				return 0; // Tier 1 default
		}
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public NBTTagCompound dataTag(ItemStack stack) {
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

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(net.minecraft.item.Item item, CreativeTabs tabs, List<ItemStack> list) {
		if(Config.OC_UPGRADE_CAMERA) {
			list.add(new ItemStack(item, 1, 0));
		}
		if(Config.OC_UPGRADE_CHATBOX) {
			list.add(new ItemStack(item, 1, 1));
		}
		if(Config.OC_UPGRADE_RADAR) {
			list.add(new ItemStack(item, 1, 2));
		}
		if(Config.OC_CARD_FX) {
			list.add(new ItemStack(item, 1, 3));
		}
		if(Config.OC_CARD_SPOOF) {
			list.add(new ItemStack(item, 1, 4));
		}
		if(Config.OC_CARD_SOUND) {
			list.add(new ItemStack(item, 1, 5));
		}
		if(Config.OC_CARD_BOOM) {
			list.add(new ItemStack(item, 1, 6));
		}
		if(Config.OC_UPGRADE_COLORFUL) {
			list.add(new ItemStack(item, 1, 7));
		}
	}

	//private IIcon colorfulUpgradeCanvasIcon, colorfulUpgradeTopIcon;

	/*@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister r) {
		super.registerIcons(r);
		colorfulUpgradeCanvasIcon = r.registerIcon("computronics:robot_upgrade_colorful_canvas");
		colorfulUpgradeTopIcon = r.registerIcon("computronics:robot_upgrade_colorful_top");
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
	public int getColorFromItemStack(ItemStack stack, int pass) {
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
				return super.getColorFromItemStack(stack, pass);
			}
		}
	}

	private static final int maxWidth = 220;

	//Mostly stolen from Sangar
	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("unchecked")
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean par4) {
		{
			FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
			final String key = "item.computronics." + parts[stack.getItemDamage() % parts.length] + ".tip";
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
				tooltip.add(EnumChatFormatting.DARK_GRAY
					+ data.getCompoundTag("node").getString("address").substring(0, 13) + "..."
					+ EnumChatFormatting.GRAY);
			}
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
			default:
				return "index";
		}
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

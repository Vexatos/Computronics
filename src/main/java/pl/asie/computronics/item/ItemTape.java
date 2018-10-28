package pl.asie.computronics.item;

import dan200.computercraft.api.filesystem.IMount;
import dan200.computercraft.api.media.IMedia;
import dan200.computercraft.api.media.IMediaProvider;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.tape.IItemTapeStorage;
import pl.asie.computronics.api.tape.ITapeStorage;
import pl.asie.computronics.item.entity.EntityItemIndestructable;
import pl.asie.computronics.oc.manual.IItemWithDocumentation;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tape.TapeStorage;
import pl.asie.computronics.util.StringUtil;
import pl.asie.computronics.util.internal.IItemWithColor;
import pl.asie.lib.util.color.ItemColorizer;

import javax.annotation.Nullable;
import java.util.List;

@Optional.InterfaceList({
	@Optional.Interface(iface = "dan200.computercraft.api.media.IMediaProvider", modid = Mods.ComputerCraft),
	@Optional.Interface(iface = "dan200.computercraft.api.media.IMedia", modid = Mods.ComputerCraft)
})
public class ItemTape extends Item implements IItemTapeStorage, IMedia, IMediaProvider, IItemWithDocumentation, IItemWithColor {

	public static final int L_SECOND = 1500 * 4;
	public static final int L_MINUTE = 1500 * 4 * 60;

	private static final int TAPE_COUNT = 10;
	private static final int[] DEFAULT_LENGTHS = { 4, 8, 16, 32, 64, 2, 6, 16, 128, 128 };

	private int[] sizes;

	public ItemTape(String lengths) {
		super();
		this.setTranslationKey("computronics.tape");
		this.setCreativeTab(Computronics.tab);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);

		// parse lengths
		String[] l = lengths.split(",");

		/*if(l.length < TAPE_COUNT) {
			Computronics.log.error("Config error: Property 'tapedrive.tapeLengths' in computronics.cfg"
				+ " must contain 10 numbers separated by commas. Setting values to default"
				+ " [default: 4,8,16,32,64,2,6,16,128,128]");
			Config.TAPE_LENGTHS = "4,8,16,32,64,2,6,16,128,128";
			lengths = Config.TAPE_LENGTHS;
			l = lengths.split(",");
		}*/

		sizes = new int[TAPE_COUNT];
		for(int i = 0; i < TAPE_COUNT; i++) {
			if(i < l.length) {
				try {
					sizes[i] = Integer.parseInt(l[i]) * L_MINUTE;
				} catch(NumberFormatException e) {
					Computronics.log.error("Property 'tapedrive.tapeLengths' in computronics.cfg contains entry"
						+ " that is not a number! Setting entry "
						+ String.valueOf(i + 1) + " ['" + l[i] + "'] to " + DEFAULT_LENGTHS[i]);
					e.printStackTrace();
					sizes[i] = DEFAULT_LENGTHS[i] * L_MINUTE;
				}
				if(sizes[i] <= 0) {
					sizes[i] = 4;
				}
			} else {
				Computronics.log.warn("Property 'tapedrive.tapeLengths' contains too few entries, setting entry "
					+ String.valueOf(i + 1) + " to " + DEFAULT_LENGTHS[i]);
				sizes[i] = DEFAULT_LENGTHS[i] * L_MINUTE;
			}
		}
	}

	@Override
	public String getLabel(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().hasKey("label")
			? stack.getTagCompound().getString("label") : "";
	}

	@Override
	public boolean setLabel(ItemStack stack, String label) {
		if(stack.isEmpty()) {
			return false;
		}
		stack.getTagCompound().setString("label", label);
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> text, ITooltipFlag flag) {
		int size = getSize(stack);
		int len = (int) Math.floor(size / L_MINUTE);
		if(stack.getTagCompound() != null) {
			String label = getLabel(stack);
			if(label.length() > 0) {
				text.add(TextFormatting.WHITE + "" + TextFormatting.ITALIC + label);
			}
		}
		text.add(TextFormatting.GRAY + StringUtil.localizeAndFormat("tooltip.computronics.tape.length", "" + len));

		switch(stack.getItemDamage()) {
			case 7: {
				text.add(TextFormatting.AQUA + StringUtil.localize("tooltip.computronics.tape.balanced"));
				break;
			}
			case 9: {
				String[] local = StringUtil.localize("tooltip.computronics.tape.ig")
					.replace("\\n", "\n").split("\\n");
				for(String s : local) {
					text.add(TextFormatting.AQUA + s);
				}
				break;
			}
			default:
				break;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tabs, NonNullList<ItemStack> list) {
		if(!this.isInCreativeTab(tabs)) {
			return;
		}
		for(int i = 0; i < TAPE_COUNT; i++) {
			if((i == 7 || i == 9) && !Mods.isLoaded(Mods.GregTech)) {
				//Do nothing. If we return here, we lose all new tapes.
				continue;
			}
			list.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public int getSize(ItemStack stack) {
		return getSize(stack.getItemDamage());
	}

	public int getSize(int meta) {
		return sizes[meta % sizes.length];
	}

	@Override
	public ITapeStorage getStorage(ItemStack stack) {
		int size = getSize(stack);

		if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("storage")) {
			// Exists, read NBT data if everything is alright
			NBTTagCompound nbt = stack.getTagCompound();
			String storageName = nbt.getString("storage");
			if(Computronics.storage.exists(storageName)) {
				return Computronics.storage.get(storageName, size, 0);
			}
		}

		// Doesn't exist, create new storage and write NBT data
		TapeStorage storage = Computronics.storage.newStorage(size);
		if(stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setString("storage", storage.getUniqueId());
		return storage;
	}

	@Override
	public String getTranslationKey(ItemStack itemstack) {
		if(itemstack.isEmpty() && itemstack.getItemDamage() == 9) {
			return "item.computronics.tape.ig";
		}
		return super.getTranslationKey(itemstack);
	}

	@Override
	public boolean hasCustomEntity(ItemStack stack) {
		return !stack.isEmpty() && stack.getItemDamage() == 9;
	}

	@Override
	public Entity createEntity(World world, Entity location, ItemStack itemstack) {
		if(itemstack.isEmpty() && itemstack.getItemDamage() == 9) {
			EntityItemIndestructable newTapeEntity = new EntityItemIndestructable(
				world, location.posX, location.posY, location.posZ, itemstack);
			newTapeEntity.setPickupDelay(40);
			newTapeEntity.motionX = location.motionX;
			newTapeEntity.motionY = location.motionY;
			newTapeEntity.motionZ = location.motionZ;
			return newTapeEntity;
		}
		return super.createEntity(world, location, itemstack);
	}

	private static final String[] TAPE_NAMES = {
		"tape_iron",
		"tape_gold",
		"tape_gold",
		"tape_diamond",
		"tape_nether_star",
		"tape_copper",
		"tape_steel",
		"tape_greg",
		"tape_nether_star",
		"tape_ig"
	};

	public void registerItemModels() {
		if(!Computronics.proxy.isClient()) {
			return;
		}
		for(int i = 0; i < TAPE_NAMES.length; i++) {
			Computronics.proxy.registerItemModel(this, i, "computronics:" + TAPE_NAMES[i]);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemstack(ItemStack stack, int pass) {
		return pass == 0 ? 0xFFFFFFFF : (ItemColorizer.hasColor(stack) ? ItemColorizer.getColor(stack) : 0xFFFFFFFF);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return stack.getItemDamage() == 8 || super.hasEffect(stack);
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public IMedia getMedia(ItemStack stack) {
		if(!stack.isEmpty() && stack.getCount() > 0 && stack.getItem() instanceof ItemTape) {
			return ((IMedia) stack.getItem());
		}
		return null;
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String getAudioTitle(ItemStack stack) {
		return null;
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public SoundEvent getAudio(ItemStack stack) {
		return null;
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public IMount createDataMount(ItemStack stack, World world) {
		return null;
	}

	@Override
	public String getDocumentationName(ItemStack stack) {
		return "tape";
	}
}

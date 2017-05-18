package pl.asie.computronics.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.oc.manual.IItemWithDocumentation;
import pl.asie.computronics.tape.PortableDriveManager;
import pl.asie.computronics.tape.PortableTapeDrive;
import pl.asie.computronics.tile.TapeDriveState.State;
import pl.asie.computronics.util.StringUtil;

import java.util.List;
import java.util.Locale;

/**
 * @author Vexatos
 */
public class ItemPortableTapeDrive extends Item implements IItemWithDocumentation {

	public ItemPortableTapeDrive() {
		super();
		this.setCreativeTab(Computronics.tab);
		this.setHasSubtypes(false);
		this.setUnlocalizedName("computronics.portableTapeDrive");
		this.setTextureName("computronics:portable_tape_drive_modern");
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		this.setNoRepair();
	}

	private IIcon[] stateIcons = new IIcon[State.VALUES.length];

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister r) {
		super.registerIcons(r);
		for(int i = 0; i < stateIcons.length; i++) {
			stateIcons[i] = r.registerIcon("computronics:portable_tape_drive_" + State.VALUES[i].name().toLowerCase(Locale.ENGLISH));
		}
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		if(pass == 1 && stack.hasTagCompound()) {
			NBTTagCompound tag = stack.getTagCompound();
			if(tag.hasKey("state") && tag.hasKey("inv") && tag.hasKey("tid")
				&& ItemStack.loadItemStackFromNBT(tag.getCompoundTag("inv")) != null
				&& PortableDriveManager.INSTANCE.exists(tag.getString("tid"), true)) {
				byte state = tag.getByte("state");
				if(state >= 0 && state < stateIcons.length) {
					return stateIcons[state];
				}
			}
		}
		return super.getIcon(stack, pass);
	}

	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("unchecked")
	public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean advanced) {
		if(stack.hasTagCompound()) {
			NBTTagCompound tag = stack.getTagCompound();
			if(tag.hasKey("state") && tag.hasKey("tid")
				&& PortableDriveManager.INSTANCE.exists(tag.getString("tid"), true)) {
				ItemStack tape = tag.hasKey("inv") ? ItemStack.loadItemStackFromNBT(tag.getCompoundTag("inv")) : null;
				if(tape != null) {
					String label = Computronics.itemTape.getLabel(tape);
					if(label.length() > 0) {
						info.add(StringUtil.localizeAndFormat("tooltip.computronics.tape.labeltapeinserted",
							label + EnumChatFormatting.RESET + EnumChatFormatting.GRAY));
					} else {
						info.add(StringUtil.localize("tooltip.computronics.tape.tapeinserted"));
					}
					byte state = tag.getByte("state");
					if(state >= 0 && state < State.VALUES.length) {
						info.add(StringUtil.localizeAndFormat("tooltip.computronics.tape.state",
							StringUtil.localize("tooltip.computronics.tape.state."
								+ State.VALUES[state].name().toLowerCase(Locale.ENGLISH))));
					}
				} else {
					info.add(StringUtil.localize("tooltip.computronics.tape.notapeinserted"));
				}
			}
		}
		super.addInformation(stack, player, info, advanced);
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entity) {
		PortableTapeDrive drive = PortableDriveManager.INSTANCE.getOrCreate(entity.getEntityItem(), entity.worldObj.isRemote);
		drive.resetTime();
		drive.updateCarrier(entity, entity.getEntityItem());
		drive.update();
		return super.onEntityItemUpdate(entity);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity carrier, int slot, boolean isSelected) {
		super.onUpdate(stack, world, carrier, slot, isSelected);
		/*if(!world.isRemote) {
			return;
		}
		PortableTapeDrive drive = PortableDriveManager.INSTANCE.getOrCreate(stack, world.isRemote);
		drive.resetTime();
		drive.updateCarrier(carrier, stack);
		drive.update();*/
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		PortableTapeDrive drive = PortableDriveManager.INSTANCE.getOrCreate(stack, world.isRemote);
		drive.updateCarrier(player, stack);
		if(world.isRemote) {
			return super.onItemRightClick(stack, world, player);
		}
		if(player.isSneaking()) {
			player.openGui(Computronics.instance, Computronics.guiPortableTapeDrive.getGuiID(), world, 0, 0, 0);
		} else {
			drive.switchState(drive.getEnumState() != State.STOPPED ? State.STOPPED : State.PLAYING);
		}
		return super.onItemRightClick(stack, world, player);
	}

	@Override
	public String getDocumentationName(ItemStack stack) {
		return "portable_tape_drive";
	}
}

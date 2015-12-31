package pl.asie.computronics.integration.glibyvc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import pl.asie.computronics.Computronics;

public class ItemMicrophone extends ItemArmor {
    @SideOnly(Side.CLIENT)
    protected IIcon[] icons;

    public ItemMicrophone() {
        super(ArmorMaterial.CLOTH, 0, 0);
        setUnlocalizedName("computronics.microphone");
        setCreativeTab(Computronics.tab);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return icons[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconIndex(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return icons[0];
        } else {
            TileMicrophoneReceiver microphoneReceiver = MicrophoneUtils.getMicrophoneReceiver(Minecraft.getMinecraft().thePlayer);
            return microphoneReceiver != null ? icons[2] : icons[1];
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
        if (!stack.hasTagCompound()) {
            return icons[0];
        } else {
            TileMicrophoneReceiver microphoneReceiver = MicrophoneUtils.getMicrophoneReceiver(player);
            return microphoneReceiver != null ? icons[2] : icons[1];
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int renderPass) {
        return getIconIndex(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        icons = new IIcon[] {
                reg.registerIcon("computronics:microphone_none"),
                reg.registerIcon("computronics:microphone_off"),
                reg.registerIcon("computronics:microphone_on")
        };
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return "computronics:textures/models/microphone_armor.png";
    }

    public boolean unbind(ItemStack stack) {
        stack.setTagCompound(null);
        return true;
    }

    public boolean bind(ItemStack stack, TileMicrophoneReceiver receiver) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        if (receiver == null || receiver.getWorldObj() == null) {
            return false;
        }

        NBTTagCompound cpd = stack.getTagCompound();
        cpd.setInteger("w", receiver.getWorldObj().provider.dimensionId);
        cpd.setInteger("x", receiver.xCoord);
        cpd.setInteger("y", receiver.yCoord);
        cpd.setInteger("z", receiver.zCoord);
        return true;
    }
}

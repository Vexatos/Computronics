package pl.asie.computronics.item;

import java.util.List;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.util.EnumChatFormatting;
import pl.asie.computronics.Computronics;

public class ItemBlockChatBox extends ItemBlock {
	public ItemBlockChatBox(Block block) {
		super(block);
		this.setHasSubtypes(true);
	}
	
	/*
	@Override
	public void getSubItems(Item id, CreativeTabs tab, List list) {
		list.add(new ItemStack(this, 1, 0));
		if(Computronics.CHATBOX_CREATIVE) list.add(new ItemStack(this, 1, 8));
	}
	*/
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean wat) {
		if(stack.getItemDamage() >= 8) list.add(EnumChatFormatting.GRAY + I18n.format("tooltip.computronics.chatBox.creative"));
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return (stack.getItemDamage() >= 8 ? "tile.computronics.chatBox.creative" : "tile.computronics.chatBox");
	}
	
	@Override
	public int getMetadata(int meta) {
		return meta;
	}
	
	public int damageDropped(int meta) {
        return meta;
    }

}

package pl.asie.computronics.item;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import pl.asie.computronics.Computronics;

import java.util.List;

public class ItemBlockChatBox extends ItemBlock {
	public ItemBlockChatBox(Block block) {
		super(block);
		this.setHasSubtypes(true);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void getSubItems(Item id, CreativeTabs tab, List list) {
		list.add(new ItemStack(this, 1, 0));
		if(Computronics.CHATBOX_CREATIVE) list.add(new ItemStack(this, 1, 8));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean wat) {
		if(stack.getItemDamage() >= 8) list.add(EnumChatFormatting.GRAY + I18n.format("tooltip.computronics.chatBox.creative"));
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return (stack.getItemDamage() >= 8 ? "tile.computronics.chatBox.creative" : "tile.computronics.chatBox");
	}
}

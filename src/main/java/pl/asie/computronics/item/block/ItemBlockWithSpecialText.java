package pl.asie.computronics.item.block;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemBlockWithSpecialText extends ItemBlock {

	private IBlockWithSpecialText specialBlock;

	public ItemBlockWithSpecialText(Block block) {
		super(block);
		if(block instanceof IBlockWithSpecialText) {
			this.specialBlock = (IBlockWithSpecialText) block;
			this.setHasSubtypes(specialBlock.hasSubTypes());
		}
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {
		if(this.specialBlock != null) {
			this.specialBlock.addInformation(stack, player, list, advanced);
		} else {
			super.addInformation(stack, player, list, advanced);
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		if(this.specialBlock != null) {
			return this.specialBlock.getUnlocalizedName(stack);
		}
		return super.getUnlocalizedName(stack);
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}
}

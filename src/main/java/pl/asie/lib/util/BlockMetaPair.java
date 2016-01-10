package pl.asie.lib.util;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class BlockMetaPair {
	private Block block;
	private int meta;
	
	public BlockMetaPair(Block block, int metadata) {
		this.block = block;
		this.meta = metadata;
	}
	
	public BlockMetaPair(ItemStack stack) {
		this.block = Block.getBlockFromItem(stack.getItem());
		this.meta = stack.getItemDamage();
	}
	
	public Block getBlock() { return block; }
	public int getMetadata() { return meta; }
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof BlockMetaPair) {
			BlockMetaPair other = (BlockMetaPair)o;
			if(this.block.equals(other.block)) {
				return (this.meta == other.meta || this.meta == OreDictionary.WILDCARD_VALUE || other.meta == OreDictionary.WILDCARD_VALUE);
			}
			return false;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Block.getIdFromBlock(block) * 11 + meta;
	}
}

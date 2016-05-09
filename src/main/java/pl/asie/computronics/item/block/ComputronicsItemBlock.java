package pl.asie.computronics.item.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ComputronicsItemBlock extends ItemBlock implements IItemColor {

	private IBlockWithSpecialText specialBlock;
	private IBlockWithDifferentColors coloredBlock;

	public ComputronicsItemBlock(Block block) {
		super(block);
		if(block instanceof IBlockWithSpecialText) {
			this.specialBlock = (IBlockWithSpecialText) block;
			this.setHasSubtypes(specialBlock.hasSubTypes());
		}
		if(block instanceof IBlockWithDifferentColors) {
			this.coloredBlock = ((IBlockWithDifferentColors) block);
			this.setHasSubtypes(coloredBlock.hasSubTypes());
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

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemstack(ItemStack stack, int pass) {
		if(this.coloredBlock != null) {
			return this.coloredBlock.getColorFromItemstack(stack, pass);
		}
		return 0xFFFFFFFF;
	}
}

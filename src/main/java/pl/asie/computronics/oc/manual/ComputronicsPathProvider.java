package pl.asie.computronics.oc.manual;

import li.cil.oc.api.Manual;
import li.cil.oc.api.manual.PathProvider;
import li.cil.oc.api.manual.TabIconRenderer;
import li.cil.oc.api.prefab.ItemStackTabIconRenderer;
import li.cil.oc.api.prefab.ResourceContentProvider;
import li.cil.oc.api.prefab.TextureTabIconRenderer;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Vexatos
 */
public class ComputronicsPathProvider implements PathProvider {

	public static void initialize() {
		Manual.addProvider(new ComputronicsPathProvider());
		Manual.addProvider(new ResourceContentProvider("computronics", "doc/opencomputers/"));
		Manual.addTab(findTabIconRenderer(),
			"tooltip.computronics.manual.oc.tab.blocks", "computronics/%LANGUAGE%/block/index.md");
		Manual.addTab(new TextureTabIconRenderer(new ResourceLocation("computronics", "textures/items/tape_steel.png")),
			"tooltip.computronics.manual.oc.tab.items", "computronics/%LANGUAGE%/item/index.md");
	}

	private static TabIconRenderer findTabIconRenderer() {
		ArrayList<Block> blocks = new ArrayList<Block>();
		Collections.addAll(blocks,
			Computronics.tapeReader,
			Computronics.colorfulLamp,
			Computronics.camera,
			Computronics.chatBox,
			Computronics.ironNote,
			Computronics.cipher,
			Computronics.radar,
			Computronics.cipher_advanced);

		for(Block block : blocks) {
			if(block != null) {
				return new ItemStackTabIconRenderer(new ItemStack(block));
			}
		}
		return new TextureTabIconRenderer(new ResourceLocation("computronics", "textures/blocks/tape_drive_front.png"));
	}

	@Override
	public String pathFor(ItemStack stack) {
		if(stack.isEmpty() || stack.getItem() == null) {
			return null;
		}
		if(stack.getItem() instanceof IItemWithDocumentation) {
			return makePath("item",
				stack.getItem() instanceof IItemWithPrefix ?
					((IItemWithPrefix) stack.getItem()).getPrefix(stack)
						+ ((IItemWithDocumentation) stack.getItem()).getDocumentationName(stack)
					: ((IItemWithDocumentation) stack.getItem()).getDocumentationName(stack));
		}
		if(stack.getItem() instanceof ItemBlock) {
			Block block = Block.getBlockFromItem(stack.getItem());
			if(block != null && block instanceof IBlockWithDocumentation) {
				return makePath("block",
					block instanceof IBlockWithPrefix ?
						((IBlockWithPrefix) block).getPrefix(stack)
							+ ((IBlockWithDocumentation) block).getDocumentationName(stack)
						: ((IBlockWithDocumentation) block).getDocumentationName(stack));
			}
		}
		return null;
	}

	@Override
	public String pathFor(World world, BlockPos pos) {
		if(world == null) {
			return null;
		}
		Block block = world.getBlockState(pos).getBlock();
		if(block instanceof IBlockWithDocumentation) {
			return makePath("block",
				block instanceof IBlockWithPrefix ?
					((IBlockWithPrefix) block).getPrefix(world, pos)
						+ ((IBlockWithDocumentation) block).getDocumentationName(world, pos)
					: ((IBlockWithDocumentation) block).getDocumentationName(world, pos));
		}
		return null;
	}

	private String makePath(String type, String documentationName) {
		return "computronics/%LANGUAGE%/" + type + "/" + documentationName + ".md";
	}
}

package pl.asie.computronics.integration.tis3d.manual;

import li.cil.tis3d.api.ManualAPI;
import li.cil.tis3d.api.manual.PathProvider;
import li.cil.tis3d.api.manual.TabIconRenderer;
import li.cil.tis3d.api.prefab.manual.ItemStackTabIconRenderer;
import li.cil.tis3d.api.prefab.manual.ResourceContentProvider;
import li.cil.tis3d.api.prefab.manual.TextureTabIconRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pl.asie.computronics.integration.tis3d.IntegrationTIS3D;

/**
 * @author Vexatos
 */
public class ComputronicsPathProvider implements PathProvider {

	public static void initialize() {
		ManualAPI.addProvider(new ComputronicsPathProvider());
		ManualAPI.addProvider(new ResourceContentProvider("computronics", "doc/tis3d/"));
		/*ManualAPI.addTab(findTabIconRenderer(),
			"tooltip.computronics.manual.tis3d.tab.blocks", "computronics/%LANGUAGE%/block/index.md");*/
		ManualAPI.addTab(findTabIconRenderer(),
			"tooltip.computronics.manual.tis3d.tab.items", "computronics/%LANGUAGE%/item/index.md");
	}

	private static TabIconRenderer findTabIconRenderer() {
		if(IntegrationTIS3D.itemModules != null) {
			return new ItemStackTabIconRenderer(new ItemStack(IntegrationTIS3D.itemModules, 1, 1));
		}
		return new TextureTabIconRenderer(new ResourceLocation("computronics", "textures/items/tape_steel.png"));
	}

	@Override
	public String pathFor(ItemStack stack) {
		if(stack.isEmpty()) {
			return null;
		}
		if(stack.getItem() instanceof IModuleWithDocumentation) {
			return makePath("item",
				stack.getItem() instanceof IModuleWithPrefix ?
					((IModuleWithPrefix) stack.getItem()).getPrefix(stack)
						+ ((IModuleWithDocumentation) stack.getItem()).getDocumentationName(stack)
					: ((IModuleWithDocumentation) stack.getItem()).getDocumentationName(stack));
		}
		return null;
	}

	@Override
	public String pathFor(World world, BlockPos blockPos) {
		return null;
	}

	private String makePath(String type, String documentationName) {
		return "computronics/%LANGUAGE%/" + type + "/" + documentationName + ".md";
	}
}

package pl.asie.computronics.integration.buildcraft;

import buildcraft.api.blueprints.BuilderAPI;
import cpw.mods.fml.common.Optional;
import pl.asie.computronics.reference.Mods;
import pl.asie.lib.block.BlockBase;
import pl.asie.lib.integration.buildcraft.SchematicBlockBase;

import java.util.ArrayList;

/**
 * @author Vexatos
 */
public class IntegrationBuildCraftBuilder {
	public static final IntegrationBuildCraftBuilder INSTANCE = new IntegrationBuildCraftBuilder();

	private IntegrationBuildCraftBuilder() {
	}

	private final ArrayList<BlockBase> blocks = new ArrayList<BlockBase>();

	public void registerBlockBaseSchematic(BlockBase block) {
		if(Mods.API.hasAPI(Mods.API.BuildCraftBlueprints)) {
			blocks.add(block);
		}
	}

	@Optional.Method(modid = Mods.API.BuildCraftBlueprints)
	public void init() {
		for(BlockBase block : blocks) {
			BuilderAPI.schematicRegistry.registerSchematicBlock(block, SchematicBlockBase.class);
		}
	}
}

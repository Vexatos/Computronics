package pl.asie.computronics.integration.buildcraft.statements;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pl.asie.computronics.integration.buildcraft.statements.actions.Actions;
import pl.asie.computronics.integration.buildcraft.statements.triggers.Triggers;

/**
 * @author Vexatos
 */
public class StatementTextureManager {

	@SubscribeEvent
	public void stitchTextures(TextureStitchEvent.Pre event) {
		for(Triggers trigger : Triggers.VALUES) {
			trigger.stitchTextures(event);
		}
		for(Actions action : Actions.VALUES) {
			action.stitchTextures(event);
		}
	}

}

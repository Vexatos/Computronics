package pl.asie.computronics.tape;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.integration.conventional.IntegrationConventional;
import pl.asie.computronics.item.ItemPortableTapeDrive;
import pl.asie.computronics.network.PacketType;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TapeDriveState.State;

/**
 * @author Vexatos
 */
@SideOnly(Side.CLIENT)
public class TapeScrollEventHandler {

	@SubscribeEvent
	public void onMouseEvent(MouseEvent event) {
		EntityPlayerSP player = Minecraft.getMinecraft().player;

		if(event.getDwheel() != 0 && player != null && player.isSneaking()) {
			ItemStack stack = player.getHeldItemMainhand();

			if(!stack.isEmpty()) {
				Item item = stack.getItem();

				if(item instanceof ItemPortableTapeDrive) {
					if(Mods.isLoaded(Mods.Conventional)) {
						if(IntegrationConventional.INSTANCE.isDenied(IntegrationConventional.Permissions.TapeScroll, player)) {
							event.setCanceled(true);
							return;
						}
					}

					scrollTapeDrive(stack, player, event.getDwheel());
					event.setCanceled(true);
				}
			}
		}
	}

	public static void scrollTapeDrive(ItemStack stack, EntityPlayer player, int dWheel) {
		PortableTapeDrive tapeDrive = PortableDriveManager.INSTANCE.getOrCreate(stack, true);
		State state = tapeDrive.getEnumState();
		State newState = null;
		switch(state) {
			case STOPPED:
			case PLAYING:
				newState = dWheel < 0 ? State.REWINDING : State.FORWARDING;
				break;
			case FORWARDING:
				newState = dWheel < 0 ? State.STOPPED : null;
				break;
			case REWINDING:
				newState = dWheel > 0 ? State.STOPPED : null;
				break;
		}
		String id = PortableDriveManager.INSTANCE.getID(tapeDrive, true);
		if(newState != null && id != null) {
			try {
				Computronics.packet.sendToServer(Computronics.packet.create(PacketType.PORTABLE_TAPE_STATE.ordinal())
					.writeString(id)
					.writeByte((byte) newState.ordinal()));
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}

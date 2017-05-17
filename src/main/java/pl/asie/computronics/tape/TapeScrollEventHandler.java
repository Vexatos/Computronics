package pl.asie.computronics.tape;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.MouseEvent;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.item.ItemPortableTapeDrive;
import pl.asie.computronics.network.PacketType;
import pl.asie.computronics.tile.TapeDriveState.State;

/**
 * @author Vexatos
 */
@SideOnly(Side.CLIENT)
public class TapeScrollEventHandler {

	@SubscribeEvent
	public void onMouseEvent(MouseEvent event) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

		if(event.dwheel != 0 && player != null && player.isSneaking()) {
			ItemStack stack = player.getCurrentEquippedItem();

			if(stack != null) {
				Item item = stack.getItem();

				if(item instanceof ItemPortableTapeDrive) {
					scrollTapeDrive(stack, player, event.dwheel);
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

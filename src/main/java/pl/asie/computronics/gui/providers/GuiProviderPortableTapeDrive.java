package pl.asie.computronics.gui.providers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.gui.GuiTapePlayer;
import pl.asie.computronics.gui.IGuiTapeDrive;
import pl.asie.computronics.gui.container.ContainerPortableTapeDrive;
import pl.asie.computronics.item.ItemPortableTapeDrive;
import pl.asie.computronics.network.PacketType;
import pl.asie.computronics.tape.PortableDriveManager;
import pl.asie.computronics.tape.PortableTapeDrive;
import pl.asie.computronics.tile.TapeDriveState;
import pl.asie.lib.block.ContainerInventory;
import pl.asie.lib.gui.managed.GuiProviderBase;
import pl.asie.lib.network.Packet;

/**
 * @author Vexatos
 */
public class GuiProviderPortableTapeDrive extends GuiProviderBase {

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer makeGui(int ID, EntityPlayer player, final World world, int x, int y, int z) {
		ItemStack stack = player.getHeldItem();
		if(stack != null && stack.getItem() instanceof ItemPortableTapeDrive) {
			final PortableTapeDrive tapeDrive = PortableDriveManager.INSTANCE.getOrCreate(stack, world.isRemote);
			return new GuiTapePlayer(new IGuiTapeDrive() {
				@Override
				public void setState(TapeDriveState.State state) {
					String id = PortableDriveManager.INSTANCE.getID(tapeDrive, world.isRemote);
					if(id != null) {
						try {
							Packet packet = Computronics.packet.create(PacketType.PORTABLE_TAPE_STATE.ordinal())
								.writeString(id)
								.writeByte((byte) state.ordinal());
							Computronics.packet.sendToServer(packet);
							tapeDrive.switchState(state);
						} catch(Exception e) {
							//NO-OP
						}
					}
				}

				@Override
				public TapeDriveState.State getState() {
					return tapeDrive.getEnumState();
				}

				@Override
				public boolean isLocked(Slot slot, int index, int button, int shift) {
					ItemStack slotstack = slot.getStack();
					return slotstack != null && ItemStack.areItemStacksEqual(tapeDrive.getSelf(), slotstack);
				}

				@Override
				public boolean shouldCheckHotbarKeys() {
					return false;
				}
			}, makeContainer(player, tapeDrive));
		}
		return null;
	}

	@Override
	public Container makeContainer(int ID, EntityPlayer player, World world, int x, int y, int z) {
		ItemStack stack = player.getHeldItem();
		if(stack != null && stack.getItem() instanceof ItemPortableTapeDrive) {
			return makeContainer(player, PortableDriveManager.INSTANCE.getOrCreate(stack, world.isRemote));
		}
		return null;
	}

	protected ContainerInventory makeContainer(EntityPlayer player, PortableTapeDrive tapeDrive) {
		return new ContainerPortableTapeDrive(tapeDrive, player.inventory);
	}
}

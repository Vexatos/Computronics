package pl.asie.computronics.gui.providers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.gui.GuiTapePlayer;
import pl.asie.computronics.gui.IGuiTapeDrive;
import pl.asie.computronics.gui.container.ContainerTapeReader;
import pl.asie.computronics.network.PacketType;
import pl.asie.computronics.tile.TapeDriveState;
import pl.asie.computronics.tile.TileTapeDrive;
import pl.asie.lib.block.ContainerInventory;
import pl.asie.lib.gui.managed.GuiProviderBase;
import pl.asie.lib.network.Packet;

/**
 * @author Vexatos
 */
public class GuiProviderTapeDrive extends GuiProviderBase {

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer makeGui(int guiID, EntityPlayer entityPlayer, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if(tileEntity instanceof TileTapeDrive) {
			final TileTapeDrive tile = (TileTapeDrive) tileEntity;
			return new GuiTapePlayer(new IGuiTapeDrive() {
				@Override
				public void setState(TapeDriveState.State state) {
					try {
						Packet packet = Computronics.packet.create(PacketType.TAPE_GUI_STATE.ordinal())
							.writeTileLocation(tile)
							.writeByte((byte) state.ordinal());
						Computronics.packet.sendToServer(packet);
						tile.switchState(state);
					} catch(Exception e) {
						//NO-OP
					}
				}

				@Override
				public TapeDriveState.State getState() {
					return tile.getEnumState();
				}

				@Override
				public boolean isLocked(Slot slot, int index, int button, int shift) {
					return false;
				}

				@Override
				public boolean shouldCheckHotbarKeys() {
					return true;
				}
			}, makeContainer(entityPlayer, tile));
		}
		return null;
	}

	@Override
	public Container makeContainer(int guiID, EntityPlayer entityPlayer, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if(tileEntity instanceof TileTapeDrive) {
			return makeContainer(entityPlayer, ((TileTapeDrive) tileEntity));
		}
		return null;
	}

	protected ContainerInventory makeContainer(EntityPlayer entityPlayer, TileTapeDrive tile) {
		return new ContainerTapeReader(tile, entityPlayer.inventory);
	}
}

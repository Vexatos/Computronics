package pl.asie.computronics;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

import paulscode.sound.SoundBuffer;
import paulscode.sound.SoundSystem;
import pl.asie.computronics.gui.GuiTapePlayer;
import pl.asie.computronics.tile.TileTapeDrive;
import pl.asie.computronics.tile.TapeDriveState.State;
import pl.asie.lib.AsieLibMod;
import pl.asie.lib.audio.DFPWM;
import pl.asie.lib.audio.StreamingAudioPlayer;
import pl.asie.lib.network.MessageHandlerBase;
import pl.asie.lib.network.Packet;
import pl.asie.lib.util.GuiUtils;
import pl.asie.lib.util.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.sound.PlayStreamingSourceEvent;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class NetworkHandlerServer extends MessageHandlerBase {
	@Override
	public void onMessage(Packet packet, INetHandler handler, EntityPlayer player, int command)
			throws IOException {
		System.out.println("SERVER PACKET " + command);
		switch(command) {
			case Packets.PACKET_TAPE_GUI_STATE: {
				TileEntity entity = packet.readTileEntityServer();
				State state = State.values()[packet.readUnsignedByte()];
				if(entity instanceof TileTapeDrive) {
					TileTapeDrive tile = (TileTapeDrive)entity;
					tile.switchState(state);
				}
			} break;
		}
	}
}

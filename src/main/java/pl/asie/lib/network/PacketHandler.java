package pl.asie.lib.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;
import java.util.EnumMap;

public class PacketHandler {

	private EnumMap<Side, FMLEmbeddedChannel> channels;

	public PacketHandler(String channelName, MessageHandlerBase client, MessageHandlerBase server) {
		channels = NetworkRegistry.INSTANCE.newChannel(channelName, new PacketChannelHandler(client, server));
	}

	public Packet create() {
		return new Packet();
	}

	public Packet create(int prefix) throws IOException {
		return new Packet().writeShort((short) prefix);
	}

	public net.minecraft.network.Packet getPacketFrom(Packet message) {
		return channels.get(Side.SERVER).generatePacketFrom(message);
	}

	public void sendToAll(Packet message) {
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
		channels.get(Side.SERVER).writeOutbound(message);
	}

	public void sendTo(Packet message, EntityPlayerMP player) {
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
		channels.get(Side.SERVER).writeOutbound(message);
	}

	public void sendToAllAround(Packet message, NetworkRegistry.TargetPoint point) {
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
		channels.get(Side.SERVER).writeOutbound(message);
	}

	public void sendToDimension(Packet message, int dimensionId) {
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimensionId);
		channels.get(Side.SERVER).writeOutbound(message);
	}

	public void sendToServer(Packet message) {
		channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
		channels.get(Side.CLIENT).writeOutbound(message);
	}

	public void sendToAllAround(Packet packet, TileEntity entity,
		double d) {
		final BlockPos pos = entity.getPos();
		this.sendToAllAround(packet, new TargetPoint(entity.getWorld().provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), d));
	}

	public void sendToAllAround(Packet packet, Entity entity,
		double d) {
		this.sendToAllAround(packet, new TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, d));
	}
}

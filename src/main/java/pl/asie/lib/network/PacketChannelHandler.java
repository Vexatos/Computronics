package pl.asie.lib.network;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import pl.asie.lib.AsieLibMod;

import java.util.List;

@Sharable
public class PacketChannelHandler extends MessageToMessageCodec<FMLProxyPacket, Packet> {

	private final MessageHandlerBase handlerClient, handlerServer;

	public PacketChannelHandler(MessageHandlerBase client, MessageHandlerBase server) {
		this.handlerClient = client;
		this.handlerServer = server;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, Packet msg,
		List<Object> out) throws Exception {
		PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
		msg.toBytes(buffer);
		FMLProxyPacket proxy = new FMLProxyPacket(buffer, ctx.channel().attr(NetworkRegistry.FML_CHANNEL).get());
		out.add(proxy);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, FMLProxyPacket msg,
		List<Object> out) throws Exception {
		Packet newMsg = new Packet();
		newMsg.fromBytes(msg.payload());
		INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
		AsieLibMod.proxy.handlePacket(handlerClient, handlerServer, newMsg, netHandler);
	}
}

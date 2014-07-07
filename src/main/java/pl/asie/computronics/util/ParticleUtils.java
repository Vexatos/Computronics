package pl.asie.computronics.util;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;

public class ParticleUtils {
	public static FMLEventChannel channel;
	
	public static void sendParticlePacket(String name, int dimension, double x, double y, double z, double vx, double vy, double vz) {
        ByteBuf data = Unpooled.buffer();
        byte[] nameBytes = name.getBytes();
        data.writeShort(nameBytes.length);
        data.writeBytes(nameBytes);
        data.writeFloat((float) x);
        data.writeFloat((float) y);
        data.writeFloat((float) z);
        data.writeFloat((float) vx);
        data.writeFloat((float) vy);
        data.writeFloat((float) vz);
        channel.sendToAllAround(new FMLProxyPacket(data, "Computronics"), new NetworkRegistry.TargetPoint(dimension, x, y, z, 64));
    }

    @SubscribeEvent
    public void onPacket(FMLNetworkEvent.ClientCustomPacketEvent event) {
        ByteBuf data = event.packet.payload();
        int nameLength = data.readShort();
        String name = new String(data.readBytes(nameLength).array());
        double x = data.readFloat();
        double y = data.readFloat();
        double z = data.readFloat();
        double vx = data.readFloat();
        double vy = data.readFloat();
        double vz = data.readFloat();
        Minecraft.getMinecraft().thePlayer.getEntityWorld().spawnParticle(name, x, y, z, vx, vy, vz);
    }
}
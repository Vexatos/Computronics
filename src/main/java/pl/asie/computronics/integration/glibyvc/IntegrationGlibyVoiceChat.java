package pl.asie.computronics.integration.glibyvc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.util.ForgeDirection;

import net.gliby.voicechat.common.api.VoiceChatAPI;
import net.gliby.voicechat.common.api.events.ServerStreamEvent;
import net.gliby.voicechat.common.networking.ServerDatalet;
import net.gliby.voicechat.common.networking.ServerStream;
import net.gliby.voicechat.common.networking.ServerStreamManager;
import pl.asie.computronics.api.audio.AudioPacket;
import pl.asie.computronics.api.audio.IAudioReceiver;
import pl.asie.computronics.reference.Config;
import pl.asie.lib.network.Packet;

public class IntegrationGlibyVoiceChat {
    public static final IntegrationGlibyVoiceChat instance = new IntegrationGlibyVoiceChat();

    public static Block microphoneReceiverBlock;
    public static ItemMicrophone microphoneItem;

    public void preInit() {
        microphoneReceiverBlock = new BlockMicrophoneReceiver();
        GameRegistry.registerBlock(microphoneReceiverBlock, "microphoneReceiver");

        microphoneItem = new ItemMicrophone();
        GameRegistry.registerItem(microphoneItem, "microphone");

        GameRegistry.registerTileEntity(TileMicrophoneReceiver.class, "computronics.microphoneReceiver");
    }

    public void init() {
        VoiceChatAPI.instance().setCustomStreamHandler(this);
    }

    @SubscribeEvent
    public void createdStream(ServerStreamEvent.StreamCreated event) {
        TileMicrophoneReceiver microphoneReceiver = MicrophoneUtils.getMicrophoneReceiver(event.stream.player);
        if (microphoneReceiver != null && microphoneReceiver.getBlockMetadata() == 8) {
            event.stream.chatMode = 2;
        } else {
            event.stream.chatMode = 0;
        }
    }

    private class FakeAudioReceiver implements IAudioReceiver {
        private final EntityPlayer player;

        private FakeAudioReceiver(EntityPlayer player) {
            this.player = player;
        }

        @Override
        public World getSoundWorld() {
            return player.getEntityWorld();
        }

        @Override
        public int getSoundX() {
            return (int) player.posX;
        }

        @Override
        public int getSoundY() {
            return (int) player.posY;
        }

        @Override
        public int getSoundZ() {
            return (int) player.posZ;
        }

        @Override
        public int getSoundDistance() {
            return Config.GLIBY_PLAYER_DISTANCE;
        }

        @Override
        public void receivePacket(AudioPacket packet, ForgeDirection side) {

        }

        @Override
        public boolean connectsAudio(ForgeDirection side) {
            return false;
        }
    }

    public void feedWithinEntityWithRadius(ServerStreamManager streamManager, ServerStream stream, ServerDatalet voiceData) {
        EntityPlayerMP speaker = stream.player;
        List players = speaker.worldObj.playerEntities;
        List<IAudioReceiver> receivers = new ArrayList<IAudioReceiver>();
        receivers.add(new FakeAudioReceiver(speaker));

        TileMicrophoneReceiver microphoneReceiver = MicrophoneUtils.getMicrophoneReceiver(speaker);
        if (microphoneReceiver != null) {
            AudioPacket packet = new AudioPacket(microphoneReceiver, (byte) 255) {
                @Override
                protected void writeData(Packet p) throws IOException {

                }
            };

            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                TileEntity t = microphoneReceiver.getWorldObj().getTileEntity(
                        microphoneReceiver.xCoord + dir.offsetX,
                        microphoneReceiver.yCoord + dir.offsetY,
                        microphoneReceiver.zCoord + dir.offsetZ
                );

                if (t instanceof IAudioReceiver) {
                    ((IAudioReceiver) t).receivePacket(packet, dir.getOpposite());
                }
            }

            receivers.addAll(packet.getReceivers());
        }

        for (int i = 0; i < players.size(); i++) {
            EntityPlayer player = (EntityPlayer) players.get(i);
            if (player.getEntityId() == speaker.getEntityId()) {
                continue;
            }

            Vec3 pPos = Vec3.createVectorHelper(player.posX, player.posY, player.posZ);
            float vTmp = 1.0f;

            for (int j = 0; j < receivers.size(); j++) {
                IAudioReceiver receiver = receivers.get(j);
                double dist = pPos.squareDistanceTo(receiver.getSoundX() + 0.5, receiver.getSoundY() + 0.5, receiver.getSoundZ() + 0.5);
                if (dist <= receiver.getSoundDistance() * receiver.getSoundDistance()) {
                    float playerDistance1 = (float) Math.sqrt(dist);
                    float distanceUsed1 = receiver.getSoundDistance();
                    float v = 1.0F - playerDistance1 / distanceUsed1;
                    vTmp *= (1.0f - v);
                }
            }

            float volume = 1.0f - vTmp;
            if (volume < 0.0f) {
                volume = 0.0f;
            } else if (volume > 1.0f) {
                volume = 1.0f;
            }

            if (volume > 0.0f) {
                System.out.println(speaker.getCommandSenderName() + " -> " + player.getCommandSenderName() + " @ " + volume);
                streamManager.feedStreamToPlayer(stream, voiceData, (EntityPlayerMP) player, false);
            }
        }
    }

    @SubscribeEvent
    public void feedStream(ServerStreamEvent.StreamFeed event) {
        switch(event.stream.chatMode) {
            case 0:
                feedWithinEntityWithRadius(event.streamManager, event.stream, event.voiceLet);
                break;
            case 2:
                event.streamManager.feedStreamToAllPlayers(event.stream, event.voiceLet);
        }
    }

    @SubscribeEvent
    public void killStream(ServerStreamEvent.StreamDestroyed event) {
    }
}

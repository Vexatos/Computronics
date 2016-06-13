package pl.asie.computronics.integration.charset.audio;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pl.asie.charset.api.audio.AudioAPI;
import pl.asie.charset.api.audio.AudioSink;
import pl.asie.charset.api.audio.IAudioReceiver;
import pl.asie.charset.api.audio.IAudioSource;
import pl.asie.computronics.tile.TileAudioCable;
import pl.asie.computronics.tile.TileSpeaker;

/**
 * @author Vexatos
 */
public class IntegrationCharsetAudio {
	@CapabilityInject(IAudioSource.class)
	static Capability<IAudioSource> SOURCE_CAPABILITY;
	@CapabilityInject(IAudioReceiver.class)
	static Capability<IAudioReceiver> RECEIVER_CAPABILITY;

	private static final ResourceLocation CABLE_SINK_KEY = new ResourceLocation("computronics:cableSink");
	private static final ResourceLocation SPEAKER_SINK_KEY = new ResourceLocation("computronics:speakerSink");

	public void postInit() {
		AudioAPI.SINK_REGISTRY.register(AudioSinkSpeaker.class);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onAttach(final AttachCapabilitiesEvent.TileEntity event) {
		if (event.getTileEntity() instanceof TileSpeaker
				&& RECEIVER_CAPABILITY != null) {
			event.addCapability(SPEAKER_SINK_KEY, new ICapabilityProvider() {
				private final AudioSink sink = new AudioSinkSpeaker((TileSpeaker) event.getTileEntity());

				@Override
				public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
					return capability == RECEIVER_CAPABILITY && facing != null;
				}

				@Override
				public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
					return capability == RECEIVER_CAPABILITY ? (T) sink : null;
				}
			});
		} else if (event.getTileEntity() instanceof TileAudioCable
				&& RECEIVER_CAPABILITY != null) {
			event.addCapability(CABLE_SINK_KEY, new ICapabilityProvider() {
				private final TileAudioCable cable = (TileAudioCable) event.getTileEntity();
				private final AudioReceiverCable[] RECEIVERS = new AudioReceiverCable[6];

				@Override
				public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
					return capability == RECEIVER_CAPABILITY && facing != null;
				}

				@Override
				public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
					if (capability == RECEIVER_CAPABILITY && facing != null) {
						if (RECEIVERS[facing.ordinal()] == null) {
							RECEIVERS[facing.ordinal()] = new AudioReceiverCable(cable, facing);
						}

						return (T) RECEIVERS[facing.ordinal()];
					} else {
						return null;
					}
				}
			});
		}
	}

	public static boolean connects(TileEntity tile, EnumFacing dir) {
		return tile != null && (tile.hasCapability(SOURCE_CAPABILITY, dir)
				|| tile.hasCapability(RECEIVER_CAPABILITY, dir));
	}
}

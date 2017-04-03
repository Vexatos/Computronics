package pl.asie.computronics.reference;

import net.minecraft.nbt.NBTBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pl.asie.computronics.api.audio.AudioPacket;
import pl.asie.computronics.api.audio.IAudioReceiver;
import pl.asie.computronics.api.audio.IAudioSource;

import javax.annotation.Nullable;

/**
 * @author Vexatos
 */
public class Capabilities {

	public static final Capabilities INSTANCE = new Capabilities();

	@CapabilityInject(IAudioSource.class)
	public static Capability<IAudioSource> AUDIO_SOURCE_CAPABILITY;
	@CapabilityInject(IAudioReceiver.class)
	public static Capability<IAudioReceiver> AUDIO_RECEIVER_CAPABILITY;

	private static final ResourceLocation AUDIO_SOURCE_KEY = new ResourceLocation("computronics:audio_source");
	private static final ResourceLocation AUDIO_RECEIVER_KEY = new ResourceLocation("computronics:audio_receiver");

	public void init() {
		MinecraftForge.EVENT_BUS.register(this);
		CapabilityManager.INSTANCE.register(IAudioSource.class, new NullCapabilityStorage<IAudioSource>(), DefaultAudioSource.class);
		CapabilityManager.INSTANCE.register(IAudioReceiver.class, new NullCapabilityStorage<IAudioReceiver>(), DefaultAudioReceiver.class);
	}

	public static boolean hasAny(@Nullable ICapabilityProvider provider, EnumFacing dir, Capability... caps) {
		if(provider == null) {
			return false;
		}
		for(Capability cap : caps) {
			if(provider.hasCapability(cap, dir)) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasAll(@Nullable ICapabilityProvider provider, EnumFacing dir, Capability... caps) {
		if(provider == null) {
			return false;
		}
		for(Capability cap : caps) {
			if(!provider.hasCapability(cap, dir)) {
				return false;
			}
		}
		return true;
	}

	@Nullable
	public static <T> T getFirst(@Nullable ICapabilityProvider provider, EnumFacing dir, Iterable<Capability<? extends T>> caps) {
		if(provider == null) {
			return null;
		}
		for(Capability<? extends T> cap : caps) {
			if(provider.hasCapability(cap, dir)) {
				return provider.getCapability(cap, dir);
			}
		}
		return null;
	}

	@Nullable
	public static <T> T getFirst(@Nullable ICapabilityProvider provider, EnumFacing dir, Capability<? extends T> first, Capability<? extends T> second) {
		if(provider == null) {
			return null;
		}
		if(provider.hasCapability(first, dir)) {
			return provider.getCapability(first, dir);
		}
		if(provider.hasCapability(second, dir)) {
			return provider.getCapability(second, dir);
		}
		return null;
	}

	@SubscribeEvent
	public void onAttachCapabilities(AttachCapabilitiesEvent<TileEntity> e) {
		final TileEntity tile = e.getObject();
		if(tile instanceof IAudioSource) {
			e.addCapability(AUDIO_SOURCE_KEY, new ICapabilityProvider() {
				@Override
				public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
					return capability == AUDIO_SOURCE_CAPABILITY;
				}

				@Nullable
				@Override
				public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
					return hasCapability(capability, facing) ? AUDIO_SOURCE_CAPABILITY.<T>cast((IAudioSource) tile) : null;
				}
			});
		}
		if(tile instanceof IAudioReceiver) {
			e.addCapability(AUDIO_RECEIVER_KEY, new ICapabilityProvider() {
				@Override
				public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
					return capability == AUDIO_RECEIVER_CAPABILITY;
				}

				@Nullable
				@Override
				public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
					return hasCapability(capability, facing) ? AUDIO_RECEIVER_CAPABILITY.<T>cast((IAudioReceiver) tile) : null;
				}
			});
		}
	}

	/**
	 * @author asie
	 */
	public static class NullCapabilityStorage<T> implements Capability.IStorage<T> {

		public NullCapabilityStorage() {
		}

		@Nullable
		@Override
		public NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side) {
			return null;
		}

		@Override
		public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt) {
		}
	}

	private static class DefaultAudioSource implements IAudioSource {

		@Override
		public int getSourceId() {
			return -1;
		}

		@Override
		public boolean connectsAudio(EnumFacing side) {
			return false;
		}
	}

	private static class DefaultAudioReceiver implements IAudioReceiver {

		@Nullable
		@Override
		public World getSoundWorld() {
			return null;
		}

		@Override
		public BlockPos getSoundPos() {
			return BlockPos.ORIGIN;
		}

		@Override
		public int getSoundDistance() {
			return 0;
		}

		@Override
		public void receivePacket(AudioPacket packet, @Nullable EnumFacing side) {

		}

		@Override
		public boolean connectsAudio(EnumFacing side) {
			return false;
		}
	}

	private Capabilities() {
	}
}

package pl.asie.computronics.tile;

import com.google.common.base.Throwables;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.audio.AudioPacket;
import pl.asie.computronics.api.audio.AudioPacketDFPWM;
import pl.asie.computronics.api.audio.IAudioReceiver;
import pl.asie.computronics.api.audio.IAudioSource;
import pl.asie.computronics.audio.AudioUtils;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.OCUtils;
import pl.asie.lib.util.ColorUtils;
import pl.asie.lib.util.internal.IColorable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author Vexatos
 */
public class TileSpeechBox extends TileEntityPeripheralBase implements IAudioSource, ITickable {

	public TileSpeechBox() {
		super("speech_box");
	}

	private final IAudioReceiver internalSpeaker = new IAudioReceiver() {
		@Override
		public boolean connectsAudio(EnumFacing side) {
			return true;
		}

		@Override
		public World getSoundWorld() {
			return worldObj;
		}

		@Override
		public BlockPos getSoundPos() {
			return pos;
		}

		@Override
		public int getSoundDistance() {
			return Config.TAPEDRIVE_DISTANCE;
		}

		@Override
		public void receivePacket(AudioPacket packet, EnumFacing direction) {
			packet.addReceiver(this);
		}
	};

	private long lastCodecTime;
	private Integer codecId;
	protected int packetSize = 1500;
	protected int soundVolume = 127;
	private boolean locked = false;

	@Override
	public void update() {
		super.update();
		AudioPacket pkt = null;
		long time = System.nanoTime();
		if((time - (250 * 1000000)) > lastCodecTime) {
			lastCodecTime += (250 * 1000000);
			pkt = createMusicPacket(this);
		}
		if(pkt != null) {
			int receivers = 0;
			for(EnumFacing dir : EnumFacing.VALUES) {
				TileEntity tile = worldObj.getTileEntity(getPos().offset(dir));
				if(tile instanceof IAudioReceiver) {
					if(tile instanceof IColorable && ((IColorable) tile).canBeColored()
						&& !ColorUtils.isSameOrDefault(this, (IColorable) tile)) {
						continue;
					}
					((IAudioReceiver) tile).receivePacket(pkt, dir.getOpposite());
					receivers++;
				}
			}

			if(receivers == 0) {
				internalSpeaker.receivePacket(pkt, null);
			}

			pkt.sendPacket();
		}
	}

	public void startTalking(byte[] data) {
		if(worldObj.isRemote) {
			return;
		}
		storage = new ByteArrayInputStream(data);
		codecId = Computronics.instance.audio.newPlayer();
		Computronics.instance.audio.getPlayer(codecId);
		lastCodecTime = System.nanoTime();
	}

	private void stopTalking() {
		AudioUtils.removePlayer(Computronics.instance.managerId, codecId);
		locked = false;
		storage = null;
	}

	private Object[] sendNewText(String text) throws IOException {
		if(Computronics.tts != null) {
			locked = true;
			Computronics.tts.say(text, worldObj.provider.getDimension(), pos);
		} else {
			return new Object[] { false, "text-to-speech system not available" };
		}
		return new Object[] { true };
	}

	private ByteArrayInputStream storage;

	private AudioPacket createMusicPacket(IAudioSource source) {
		if(storage == null) {
			return null;
		}
		byte[] pktData = new byte[packetSize];
		int amount = storage.read(pktData, 0, pktData.length); // read data into packet array

		if(amount > 0) {
			return new AudioPacketDFPWM(source, (byte) soundVolume, packetSize * 8 * 4, amount == packetSize ? pktData : Arrays.copyOf(pktData, amount));
		} else {
			stopTalking();
			return null;
		}
	}

	@Callback(doc = "function(text:string):boolean; Say the specified message. Returns true on success, false and an error message otherwise.")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] say(Context context, Arguments args) {
		if(locked || storage != null) {
			return new Object[] { false, "already processing" };
		}
		String text = args.checkString(0);
		if(text.length() > Config.TTS_MAX_LENGTH) {
			return new Object[] { false, "text too long" };
		}
		try {
			return this.sendNewText(text);
		} catch(IOException e) {
			throw new IllegalArgumentException("could not send string");
		} catch(Exception e) {
			e.printStackTrace();
			Throwables.propagate(e);
		}
		return new Object[] { false };
	}

	@Callback(doc = "function():boolean; Stops the currently spoken phrase. Returns true on success, false and an error message otherwise.")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] stop(Context context, Arguments args) {
		if(locked || storage != null) {
			stopTalking();
			return new Object[] { true };
		}
		return new Object[] { false, "not talking" };
	}

	@Callback(doc = "function():boolean; Returns true if the device is currently processing text.", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] isProcessing(Context context, Arguments args) {
		return new Object[] { locked || storage != null };
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String[] getMethodNames() {
		return new String[] { "say", "stop", "isProcessing" };
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		switch(method) {
			case 0: {
				if(arguments.length < 1 || !(arguments[0] instanceof String)) {
					throw new LuaException("first argument needs to be a string");
				}
				if(locked || storage != null) {
					return new Object[] { false, "already processing" };
				}
				String text = (String) arguments[0];
				if(text.length() > Config.TTS_MAX_LENGTH) {
					return new Object[] { false, "text too long" };
				}
				try {
					return new Object[] { this.sendNewText(text) };
				} catch(IOException e) {
					throw new LuaException("could not send string");
				}
			}
			case 1: {
				if(locked || storage != null) {
					stopTalking();
					return new Object[] { true };
				}
				return new Object[] { false, "not talking" };
			}
			case 2: {
				return new Object[] { locked || storage != null };
			}
		}
		return null;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	protected OCUtils.Device deviceInfo() {
		return new OCUtils.Device(
			DeviceClass.Multimedia,
			"Text-To-Speech Interface",
			OCUtils.Vendors.DFKI,
			"Mary"
		);
	}

	@Override
	public int getSourceId() {
		return codecId;
	}

	@Override
	public boolean connectsAudio(EnumFacing side) {
		return worldObj.getBlockState(getPos()).getValue(Computronics.speechBox.rotation.FACING) != side;
	}
}

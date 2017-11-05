package pl.asie.computronics.tile;

import com.google.common.base.Throwables;
import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.audio.AudioPacket;
import pl.asie.computronics.api.audio.AudioPacketDFPWM;
import pl.asie.computronics.api.audio.IAudioReceiver;
import pl.asie.computronics.api.audio.IAudioSource;
import pl.asie.computronics.audio.AudioUtils;
import pl.asie.computronics.audio.tts.TextToSpeech.ICanSpeak;
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
public class TileSpeechBox extends TileEntityPeripheralBase implements IAudioSource, ICanSpeak {

	public TileSpeechBox() {
		super("speech_box");
	}

	private final IAudioReceiver internalSpeaker = new IAudioReceiver() {
		@Override
		public boolean connectsAudio(ForgeDirection side) {
			return true;
		}

		@Override
		public World getSoundWorld() {
			return worldObj;
		}

		@Override
		public Vec3 getSoundPos() {
			return Vec3.createVectorHelper(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D);
		}

		@Override
		public int getSoundDistance() {
			return Config.TAPEDRIVE_DISTANCE;
		}

		@Override
		public void receivePacket(AudioPacket packet, ForgeDirection direction) {
			packet.addReceiver(this);
		}

		@Override
		public String getID() {
			return AudioUtils.positionId(xCoord, yCoord, zCoord);
		}

	};

	private long lastCodecTime;
	private int codecId = -1;
	protected int packetSize = 1024;
	protected int soundVolume = 127;
	private boolean locked = false;

	@Override
	public void updateEntity() {
		super.updateEntity();
		AudioPacket pkt = null;
		long time = System.nanoTime();
		if((time - (250 * 1000000)) > lastCodecTime) {
			lastCodecTime += (250 * 1000000);
			pkt = createMusicPacket(this, worldObj, xCoord, yCoord, zCoord);
		}
		if(pkt != null) {
			int receivers = 0;
			for(int i = 0; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.getOrientation(i);
				TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
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
				internalSpeaker.receivePacket(pkt, ForgeDirection.UNKNOWN);
			}

			pkt.sendPacket();
		}
	}

	@Override
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
		if(hasWorldObj() && worldObj.isRemote) {
			return;
		}
		AudioUtils.removePlayer(Computronics.instance.managerId, codecId);
		locked = false;
		storage = null;
	}

	private Object[] sendNewText(String text) throws IOException {
		if(Computronics.tts != null) {
			locked = true;
			Computronics.tts.say(this, text);
		} else {
			return new Object[] { false, "text-to-speech system not available" };
		}
		return new Object[] { true };
	}

	private ByteArrayInputStream storage;

	private AudioPacket createMusicPacket(IAudioSource source, World worldObj, int x, int y, int z) {
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

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if(tag.hasKey("vo")) {
			this.soundVolume = tag.getByte("vo");
		} else {
			this.soundVolume = 127;
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if(this.soundVolume != 127) {
			tag.setByte("vo", (byte) this.soundVolume);
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		stopTalking();
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		stopTalking();
	}

	public void setVolume(float volume) {
		if(volume < 0.0F) {
			volume = 0.0F;
		}
		if(volume > 1.0F) {
			volume = 1.0F;
		}
		this.soundVolume = (int) Math.floor(volume * 127.0F);
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

	@Callback(doc = "function(speed:number); Sets the volume of the speech box. Needs to be beween 0 and 1")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setVolume(Context context, Arguments args) {
		this.setVolume((float) args.checkDouble(0));
		return null;
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String[] getMethodNames() {
		return new String[] { "say", "stop", "isProcessing", "setVolume" };
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
					return this.sendNewText(text);
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
			case 3: {
				if(arguments.length < 1 || !(arguments[0] instanceof Number)) {
					throw new LuaException("first argument needs to be a number");
				}
				this.setVolume(((Number) arguments[0]).floatValue());
				return null;
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
	public boolean connectsAudio(ForgeDirection side) {
		return Computronics.speechBox.getFrontSide(getBlockMetadata()) != side.ordinal();
	}
}

package pl.asie.computronics.integration.tis3d.module;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import li.cil.tis3d.api.FontRendererAPI;
import li.cil.tis3d.api.machine.Casing;
import li.cil.tis3d.api.machine.Face;
import li.cil.tis3d.api.machine.Pipe;
import li.cil.tis3d.api.machine.Port;
import li.cil.tis3d.api.util.RenderUtil;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;
import pl.asie.computronics.tile.TapeDriveState.State;
import pl.asie.computronics.tile.TileTapeDrive;

import java.util.HashMap;

/**
 * @author Vexatos
 */
public class ModuleTapeReader extends ComputronicsModule {

	private enum Mode {
		IDLE, WAITING, WRITING;
		private static final Mode[] VALUES = values();
	}

	private final HashMap<String, Command> commandMap = new HashMap<String, Command>();

	private abstract class Command {

		protected final String uid;

		protected Command(String uid) {
			this.uid = uid;
			if(commandMap.containsKey(uid)) {
				throw new IllegalArgumentException("Attempt to add command with UID that has already been registered!");
			}
			commandMap.put(uid, this);
		}

		protected final String getUID() {
			return this.uid;
		}

		protected abstract void process(TileTapeDrive tile);

		protected abstract void finishWriting(TileTapeDrive tile, Port writtenPort);

		protected void save(NBTTagCompound nbt) {
		}

		protected void load(NBTTagCompound nbt) {
		}
	}

	/**
	 * Command which never returns anything.
	 */
	private abstract class NeverWritingCommand extends Command {

		private NeverWritingCommand(String uid) {
			super(uid);
		}

		@Override
		protected void finishWriting(TileTapeDrive tile, Port writtenPort) {
		}
	}

	/**
	 * Command which is finished after returning one value.
	 */
	private abstract class IdleAfterWritingCommand extends Command {

		private IdleAfterWritingCommand(String uid) {
			super(uid);
		}

		@Override
		protected void finishWriting(TileTapeDrive tile, Port writtenPort) {
			cancelWrite();
			mode = Mode.IDLE;
			command = null;
			for(Port port : Port.VALUES) {
				Pipe receivingPipe = getCasing().getReceivingPipe(getFace(), port);
				if(!receivingPipe.isReading()) {
					receivingPipe.beginRead();
				}
			}
			sendDataToClient();
		}
	}

	/**
	 * Command which takes no argument and returns a value.
	 */
	private abstract class ImmediateReturnCommand extends IdleAfterWritingCommand {

		private ImmediateReturnCommand(String uid) {
			super(uid);
		}

		@Override
		protected void process(TileTapeDrive tile) {
			if(mode == Mode.IDLE) {
				cancelRead();
				mode = Mode.WRITING;
				for(Port port : Port.VALUES) {
					Pipe sendingPipe = getCasing().getSendingPipe(getFace(), port);
					if(!sendingPipe.isWriting()) {
						sendingPipe.beginWrite(getValue(tile));
					}
				}
			}
		}

		protected abstract short getValue(TileTapeDrive tile);

	}

	/**
	 * Command which takes a single argument and returns nothing.
	 */
	private abstract class SetterCommand extends NeverWritingCommand {

		private SetterCommand(String uid) {
			super(uid);
		}

		@Override
		protected void process(TileTapeDrive tile) {
			switch(mode) {
				case IDLE: {
					mode = Mode.WAITING;
					break;
				}
				case WAITING: {
					for(Port port : Port.VALUES) {
						Pipe receivingPipe = getCasing().getReceivingPipe(getFace(), port);
						if(!receivingPipe.isReading()) {
							receivingPipe.beginRead();
						}
						if(receivingPipe.canTransfer()) {
							setValue(tile, receivingPipe.read());
							mode = Mode.IDLE;
							command = null;
							return;
						}
					}
					break;
				}
			}
		}

		protected abstract void setValue(TileTapeDrive tile, short val);
	}

	private final Command[] COMMANDS = new Command[] {
		new ImmediateReturnCommand("isEnd") { // isEnd
			@Override
			protected short getValue(TileTapeDrive tile) {
				return (short) (tile.isEnd() ? 1 : 0);
			}
		},
		new ImmediateReturnCommand("isReady") { // isReady
			@Override
			protected short getValue(TileTapeDrive tile) {
				return (short) (tile.isReady() ? 1 : 0);
			}
		},
		new ImmediateReturnCommand("getState") { // getState
			@Override
			protected short getValue(TileTapeDrive tile) {
				return (short) tile.getEnumState().ordinal();
			}
		},
		new ImmediateReturnCommand("getSize") { // getSize relative to the last multiple of 1024
			@Override
			protected short getValue(TileTapeDrive tile) {
				return (short) (tile.getSize() % 1024);
			}
		},
		new ImmediateReturnCommand("getSize1024") { // getSize /1024 (in Kibibytes)
			@Override
			protected short getValue(TileTapeDrive tile) {
				return (short) (tile.getSize() / 1024);
			}
		},
		new SetterCommand("setSpeed") { // setSpeed, in percent, between 25 and 200
			@Override
			protected void setValue(TileTapeDrive tile, short val) {
				tile.setSpeed((float) val / 100F);
			}
		},
		new SetterCommand("setVolume") { // setVolume in percent, between 0 and 100
			@Override
			protected void setValue(TileTapeDrive tile, short val) {
				tile.setVolume((float) val / 100F);
			}
		},
		new SetterCommand("seek") { // seek
			@Override
			protected void setValue(TileTapeDrive tile, short val) {
				tile.seek(val);
			}
		},
		new SetterCommand("seek1024") { // seek * 1024
			@Override
			protected void setValue(TileTapeDrive tile, short val) {
				tile.seek(val * 1024);
			}
		},
		new ImmediateReturnCommand("read") { // read a single byte
			@Override
			protected short getValue(TileTapeDrive tile) {
				return (short) tile.read(true);
			}

			@Override
			protected void finishWriting(TileTapeDrive tile, Port writtenPort) {
				tile.seek(1);
				super.finishWriting(tile, writtenPort);
			}
		},
		new Command("readMultiple") { // read a set number of bytes

			private short byteQueue = 0;

			@Override
			protected void process(TileTapeDrive tile) {
				switch(mode) {
					case IDLE: {
						mode = Mode.WAITING;
						break;
					}
					case WAITING: {
						for(Port port : Port.VALUES) {
							Pipe receivingPipe = getCasing().getReceivingPipe(getFace(), port);
							if(!receivingPipe.isReading()) {
								receivingPipe.beginRead();
							}
							if(receivingPipe.canTransfer()) {
								byteQueue = receivingPipe.read();
								if(byteQueue < 1) {
									mode = Mode.IDLE;
									command = null;
									sendDataToClient();
									return;
								}
								cancelRead();
								mode = Mode.WRITING;
								for(Port p : Port.VALUES) {
									Pipe sendingPipe = getCasing().getSendingPipe(getFace(), p);
									if(!sendingPipe.isWriting()) {
										sendingPipe.beginWrite((short) tile.read(true));
									}
								}
								return;
							}
						}
						break;
					}
				}
			}

			@Override
			protected void finishWriting(TileTapeDrive tile, Port writtenPort) {
				cancelWrite();
				if(--byteQueue > 0) {
					tile.seek(1);
					for(Port port : Port.VALUES) {
						Pipe sendingPipe = getCasing().getSendingPipe(getFace(), port);
						if(!sendingPipe.isWriting()) {
							sendingPipe.beginWrite((short) tile.read(true));
						}
					}
				} else {
					tile.seek(1);
					mode = Mode.IDLE;
					command = null;
					for(Port port : Port.VALUES) {
						Pipe receivingPipe = getCasing().getReceivingPipe(getFace(), port);
						if(!receivingPipe.isReading()) {
							receivingPipe.beginRead();
						}
					}
					sendDataToClient();
				}
			}

			@Override
			protected void save(NBTTagCompound nbt) {
				super.save(nbt);
				nbt.setShort("bq", byteQueue);
			}

			@Override
			protected void load(NBTTagCompound nbt) {
				super.load(nbt);
				if(nbt.hasKey("bq")) {
					byteQueue = nbt.getShort("bq");
				}
			}
		},
		new SetterCommand("write") { // write a single byte
			@Override
			protected void setValue(TileTapeDrive tile, short val) {
				tile.write((byte) val);
			}
		},
		new NeverWritingCommand("writeMultiple") { // write a number of bytes. First argument is the number of bytes to write

			private short byteQueue = 0;

			@Override
			protected void process(TileTapeDrive tile) {
				switch(mode) {
					case IDLE: {
						mode = Mode.WAITING;
						break;
					}
					case WAITING: {
						for(Port port : Port.VALUES) {
							Pipe receivingPipe = getCasing().getReceivingPipe(getFace(), port);
							if(!receivingPipe.isReading()) {
								receivingPipe.beginRead();
							}
							if(receivingPipe.canTransfer()) {
								byteQueue = receivingPipe.read();
								if(byteQueue < 1) {
									mode = Mode.IDLE;
									command = null;
									sendDataToClient();
									return;
								}
								mode = Mode.WRITING;
							}
						}
						break;
					}
					case WRITING: {
						for(Port port : Port.VALUES) {
							Pipe receivingPipe = getCasing().getReceivingPipe(getFace(), port);
							if(!receivingPipe.isReading()) {
								receivingPipe.beginRead();
							}
							if(receivingPipe.canTransfer()) {
								tile.write((byte) receivingPipe.read());
								if(--byteQueue <= 0) {
									mode = Mode.IDLE;
									command = null;
									sendDataToClient();
								}
							}
						}
						break;
					}
				}
			}

			@Override
			protected void save(NBTTagCompound nbt) {
				super.save(nbt);
				nbt.setShort("bq", byteQueue);
			}

			@Override
			protected void load(NBTTagCompound nbt) {
				super.load(nbt);
				if(nbt.hasKey("bq")) {
					byteQueue = nbt.getShort("bq");
				}
			}
		},
		new SetterCommand("switchState") { // switchState
			@Override
			protected void setValue(TileTapeDrive tile, short val) {
				if(val < 0 || val >= State.VALUES.length) {
					return;
				}
				tile.switchState(State.VALUES[val % State.VALUES.length]);
			}
		}
	};

	private Command getCommand(short ordinal) {
		return ordinal >= 0 && ordinal < COMMANDS.length ? COMMANDS[ordinal] : null;
	}

	public ModuleTapeReader(Casing casing, Face face) {
		super(casing, face);
	}

	private Mode mode = Mode.IDLE;
	private Command command = null;

	@Override
	public void step() {
		super.step();
		switch(mode) {
			case IDLE: {
				TileTapeDrive tile = getTapeDrive();
				if(tile != null) {
					for(Port port : Port.VALUES) {
						Pipe receivingPipe = this.getCasing().getReceivingPipe(this.getFace(), port);
						if(!receivingPipe.isReading()) {
							receivingPipe.beginRead();
						}
						if(receivingPipe.canTransfer()) {
							command = getCommand(receivingPipe.read());
							break;
						}
					}
					if(command != null) {
						command.process(tile);
						sendDataToClient();
					}
				}
				break;
			}
			case WAITING:
			case WRITING: {
				TileTapeDrive tile = getTapeDrive();
				if(tile != null && command != null) {
					command.process(tile);
				} else {
					cancelWrite();
					mode = Mode.IDLE;
					command = null;
					for(Port port : Port.VALUES) {
						Pipe receivingPipe = this.getCasing().getReceivingPipe(this.getFace(), port);
						if(!receivingPipe.isReading()) {
							receivingPipe.beginRead();
						}
					}
					sendDataToClient();
				}
				break;
			}
		}

	}

	@Override
	public void onWriteComplete(Port port) {
		super.onWriteComplete(port);
		if(command != null) {
			command.finishWriting(getTapeDrive(), port);
		}
	}

	@Override
	public void onDisabled() {
		super.onDisabled();
		mode = Mode.IDLE;
		command = null;
		sendDataToClient();
	}

	// ---

	public TileTapeDrive getTapeDrive() {
		TileEntity tile = getCasing().getCasingWorld().getTileEntity(
			getCasing().getPositionX() + Face.toEnumFacing(getFace()).getFrontOffsetX(),
			getCasing().getPositionY() + Face.toEnumFacing(getFace()).getFrontOffsetY(),
			getCasing().getPositionZ() + Face.toEnumFacing(getFace()).getFrontOffsetZ()
		);
		return tile instanceof TileTapeDrive ? (TileTapeDrive) tile : null;
	}

	// ---

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if(nbt.hasKey("mode")) {
			int modeTag = nbt.getInteger("mode");
			if(modeTag >= 0 && modeTag < Mode.VALUES.length) {
				this.mode = Mode.VALUES[modeTag];
			}
		}
		if(nbt.hasKey("cmdUID")) {
			Command cmd = commandMap.get(nbt.getString("cmdUID"));
			if(cmd != null) {
				command = cmd;
				if(nbt.hasKey("cmdTag")) {
					NBTTagCompound cmdTag = nbt.getCompoundTag("cmdTag");
					command.load(cmdTag);
				}
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("mode", mode.ordinal());
		if(command != null) {
			nbt.setString("cmdUID", command.getUID());
			NBTTagCompound cmdTag = new NBTTagCompound();
			command.save(cmdTag);
			nbt.setTag("cmdTag", cmdTag);
		}
	}

	@Override
	protected void sendDataToClient() {
		// super.sendDataToClient();
	}

	private static final ResourceLocation BACK_ICON = new ResourceLocation("computronics:textures/blocks/tis3d/module_tape_reader_back.png");
	private static final ResourceLocation CENTER_ICON = new ResourceLocation("computronics:textures/blocks/tis3d/module_tape_reader_center.png");
	private static final ResourceLocation OFF_ICON = new ResourceLocation("computronics:textures/blocks/tis3d/module_tape_reader_off.png");
	private static final ResourceLocation ON_ICON = new ResourceLocation("computronics:textures/blocks/tis3d/module_tape_reader_on.png");

	@Override
	@SideOnly(Side.CLIENT)
	public void render(boolean enabled, float partialTicks) {

		RenderUtil.bindTexture(BACK_ICON);
		RenderUtil.drawQuad();

		if(enabled) {
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 0.0F);

			RenderUtil.bindTexture(CENTER_ICON);
			RenderUtil.drawQuad();

			final boolean hasTapeDrive = getTapeDrive() != null;

			RenderUtil.bindTexture(hasTapeDrive ? ON_ICON : OFF_ICON);
			RenderUtil.drawQuad();

			if(!hasTapeDrive) {
				String s = StringUtils.center("NO TAPE DRIVE", 16);
				GL11.glTranslatef((s.length() + 1) / 64f, 3 / 16f, 0);
				GL11.glScalef(1 / 128f, 1 / 128f, 1);

				FontRendererAPI.drawString(s);
			}
		}
	}
}

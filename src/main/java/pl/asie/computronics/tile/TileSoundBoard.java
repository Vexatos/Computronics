package pl.asie.computronics.tile;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.SidedEnvironment;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.audio.AudioPacket;
import pl.asie.computronics.api.audio.IAudioReceiver;
import pl.asie.computronics.api.audio.IAudioSource;
import pl.asie.computronics.audio.AudioUtils;
import pl.asie.computronics.audio.SoundCardPacket;
import pl.asie.computronics.cc.CCArgs;
import pl.asie.computronics.cc.ISidedPeripheral;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.OCUtils;
import pl.asie.computronics.util.sound.Instruction.Close;
import pl.asie.computronics.util.sound.Instruction.Open;
import pl.asie.computronics.util.sound.Instruction.ResetAM;
import pl.asie.computronics.util.sound.Instruction.ResetEnvelope;
import pl.asie.computronics.util.sound.Instruction.ResetFM;
import pl.asie.computronics.util.sound.Instruction.SetADSR;
import pl.asie.computronics.util.sound.Instruction.SetAM;
import pl.asie.computronics.util.sound.Instruction.SetFM;
import pl.asie.computronics.util.sound.Instruction.SetFrequency;
import pl.asie.computronics.util.sound.Instruction.SetLFSR;
import pl.asie.computronics.util.sound.Instruction.SetVolume;
import pl.asie.computronics.util.sound.SoundBoard;

import javax.annotation.Nullable;

/**
 * @author Vexatos
 */
@Optional.InterfaceList({
	@Optional.Interface(iface = "li.cil.oc.api.network.SidedEnvironment", modid = Mods.OpenComputers)
})
public class TileSoundBoard extends TileEntityPeripheralBase implements IAudioSource, ITickable, ISidedPeripheral, SidedEnvironment, SoundBoard.ISoundHost {

	protected SoundBoard board;

	public TileSoundBoard() {
		super("sound");
		board = new SoundBoard(this);
	}

	@Override
	public void update() {
		super.update();
		board.update();
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public boolean canConnectPeripheralOnSide(EnumFacing side) {
		return side == world.getBlockState(getPos()).getValue(Computronics.computercraft.soundBoard.rotation.FACING);
	}

	@Optional.Method(modid = Mods.ComputerCraft)
	protected int checkChannel(CCArgs args, int index) throws LuaException {
		return board.checkChannel(args.checkInteger(index));
	}

	@Optional.Method(modid = Mods.ComputerCraft)
	protected int checkChannel(CCArgs args) throws LuaException {
		return checkChannel(args, 0);
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String[] getMethodNames() {
		return new String[] {
			"getModes", "getChannelCount", "setTotalVolume", "clear",
			"open", "close", "setWave", "setFrequency", "setLFSR",
			"delay", "setFM", "resetFM", "setAM", "resetAM",
			"setADSR", "resetEnvelope", "setVolume", "process"
		};
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
		int method, Object[] arguments) throws LuaException,
		InterruptedException {
		CCArgs args = new CCArgs(arguments);
		switch(method) {
			case 0: // getModes
				return new Object[] { SoundBoard.compileModes() };
			case 1: // getChannelCount
				return new Object[] { board.process.states.size() };
			case 2: // setTotalVolume
				board.setTotalVolume(args.checkDouble(0));
				return new Object[] {};
			case 3: // clear
				board.clear();
				return new Object[] {};
			case 4: // open
				return board.tryAdd(new Open(checkChannel(args)));
			case 5: // close
				return board.tryAdd(new Close(checkChannel(args)));
			case 6: // setWave
				return board.setWave(args.checkInteger(0), args.checkInteger(1));
			case 7: // setFrequency
				return board.tryAdd(new SetFrequency(checkChannel(args), (float) args.checkDouble(1)));
			case 8: // setLFSR
				return board.tryAdd(new SetLFSR(checkChannel(args), args.checkInteger(1), args.checkInteger(2)));
			case 9: // delay
				return board.delay(args.checkInteger(0));
			case 10: // setFM
				return board.tryAdd(new SetFM(checkChannel(args), checkChannel(args, 1), (float) args.checkDouble(2)));
			case 11: // resetFM
				return board.tryAdd(new ResetFM(checkChannel(args)));
			case 12: // setAM
				return board.tryAdd(new SetAM(checkChannel(args), checkChannel(args, 1)));
			case 13: // resetAM
				return board.tryAdd(new ResetAM(checkChannel(args)));
			case 14: // setADSR
				return board.tryAdd(new SetADSR(checkChannel(args), args.checkInteger(1), args.checkInteger(2), (float) args.checkDouble(3), args.checkInteger(4)));
			case 15: // resetEnvelope
				return board.tryAdd(new ResetEnvelope(checkChannel(args)));
			case 16: // setVolume
				return board.tryAdd(new SetVolume(checkChannel(args), (float) args.checkDouble(1)));
			case 17: // process
				return board.process();
		}
		return new Object[] {};
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public void detach(IComputerAccess computer) {
		super.detach(computer);
		if(attachedComputersCC.isEmpty()) {
			board.clearAndStop();
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		board.clearAndStop();
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		board.clearAndStop();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		board.load(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt = super.writeToNBT(nbt);
		board.save(nbt);
		return nbt;
	}

	// No OC

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	protected void initOC(double s) {
		// NO-OP
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	protected void initOC() {
		// NO-OP
	}

	@Nullable
	@Override
	protected OCUtils.Device deviceInfo() {
		return null;
	}

	@Nullable
	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Node sidedNode(EnumFacing side) {
		return null;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public boolean canConnect(EnumFacing side) {
		return false;
	}

	@Override
	public boolean tryConsumeEnergy(double energy) {
		return true;
	}

	private final IAudioReceiver internalSpeaker = new IAudioReceiver() {
		@Override
		public boolean connectsAudio(EnumFacing side) {
			return true;
		}

		@Override
		public World getSoundWorld() {
			return world;
		}

		@Override
		public Vec3d getSoundPos() {
			return new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
		}

		@Override
		public int getSoundDistance() {
			return Config.TAPEDRIVE_DISTANCE;
		}

		@Override
		public void receivePacket(AudioPacket packet, @Nullable EnumFacing direction) {
			packet.addReceiver(this);
		}

		@Override
		public String getID() {
			return AudioUtils.positionId(getPos());
		}

	};

	@Override
	public String address() {
		for(IComputerAccess computer : attachedComputersCC) {
			if(computer != null) {
				return "cc_" + computer.getID();
			}
		}
		return this.toString();
	}

	@Override
	public void sendMusicPacket(SoundCardPacket pkt) {
		internalSpeaker.receivePacket(pkt, null);
		pkt.sendPacket();
	}

	@Override
	public World world() {
		return getWorld();
	}

	public Vec3d position() {
		return new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
	}

	@Override
	public void setDirty() {
		markDirty();
	}

	@Override
	public int getSourceId() {
		return board.codecId;
	}

	@Override
	public boolean connectsAudio(EnumFacing side) {
		return false;
	}
}

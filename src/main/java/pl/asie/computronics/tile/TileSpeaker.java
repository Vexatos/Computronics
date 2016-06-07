package pl.asie.computronics.tile;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import gnu.trove.set.hash.TIntHashSet;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.SidedEnvironment;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.audio.AudioPacket;
import pl.asie.computronics.api.audio.IAudioReceiver;
import pl.asie.computronics.api.audio.IAudioSource;
import pl.asie.computronics.cc.ISidedPeripheral;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.OCUtils;

import javax.annotation.Nullable;

@Optional.InterfaceList({
	@Optional.Interface(iface = "li.cil.oc.api.network.SidedEnvironment", modid = Mods.OpenComputers)
})
public class TileSpeaker extends TileEntityPeripheralBase implements IAudioReceiver, ITickable, ISidedPeripheral, SidedEnvironment {

	private final TIntHashSet packetIds = new TIntHashSet();
	private IAudioSource lastSource;

	public TileSpeaker() {
		super("speaker");
	}

	@Override
	public void update() {
		super.update();
		packetIds.clear();
	}

	@Override
	public World getSoundWorld() {
		return worldObj;
	}

	@Override
	public BlockPos getSoundPos() {
		return getPos();
	}

	@Override
	public int getSoundDistance() {
		return Config.TAPEDRIVE_DISTANCE;
	}

	@Override
	public void receivePacket(AudioPacket packet, EnumFacing direction) {
		if(!packetIds.contains(packet.id)) {
			packetIds.add(packet.id);

			lastSource = packet.source;
			packet.addReceiver(this);
		}
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String[] getMethodNames() {
		return new String[0];
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		return new Object[0];
	}

	@Override
	public boolean connectsAudio(EnumFacing side) {
		if(hasWorldObj()) {
			IBlockState state = worldObj.getBlockState(getPos());
			return state.getValue(Computronics.speaker.rotation.FACING) != side;
		} else {
			return false;
		}
	}

	@Override
	public boolean canConnectPeripheralOnSide(EnumFacing side) {
		return false;
	}

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

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
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
}

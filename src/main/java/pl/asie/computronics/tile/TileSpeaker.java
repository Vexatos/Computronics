package pl.asie.computronics.tile;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import gnu.trove.set.hash.TIntHashSet;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.SidedEnvironment;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.audio.AudioPacket;
import pl.asie.computronics.api.audio.IAudioReceiver;
import pl.asie.computronics.audio.AudioUtils;
import pl.asie.computronics.cc.ISidedPeripheral;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.OCUtils;

import javax.annotation.Nullable;

@Optional.InterfaceList({
	@Optional.Interface(iface = "li.cil.oc.api.network.SidedEnvironment", modid = Mods.OpenComputers)
})
public class TileSpeaker extends TileEntityPeripheralBase implements IAudioReceiver, ISidedPeripheral, SidedEnvironment {

	private final TIntHashSet packetIds = new TIntHashSet();
	private long idTick = -1;

	public TileSpeaker() {
		super("speaker");
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
		if(!hasWorld() || idTick == world.getTotalWorldTime()) {
			if(packetIds.contains(packet.id)) {
				return;
			}
		} else {
			idTick = world.getTotalWorldTime();
			packetIds.clear();
		}

		packetIds.add(packet.id);
		packet.addReceiver(this);
	}

	@Override
	public String getID() {
		return AudioUtils.positionId(getPos());
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
		if(hasWorld()) {
			IBlockState state = world.getBlockState(getPos());
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

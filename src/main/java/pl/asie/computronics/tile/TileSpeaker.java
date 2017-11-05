package pl.asie.computronics.tile;

import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import gnu.trove.set.hash.TIntHashSet;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.SidedEnvironment;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.audio.AudioPacket;
import pl.asie.computronics.api.audio.IAudioReceiver;
import pl.asie.computronics.api.audio.IAudioSource;
import pl.asie.computronics.audio.AudioUtils;
import pl.asie.computronics.cc.ISidedPeripheral;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.OCUtils;

@Optional.InterfaceList({
	@Optional.Interface(iface = "li.cil.oc.api.network.SidedEnvironment", modid = Mods.OpenComputers)
})
public class TileSpeaker extends TileEntityPeripheralBase implements IAudioReceiver, ISidedPeripheral, SidedEnvironment {

	private final TIntHashSet packetIds = new TIntHashSet();
	private IAudioSource lastSource;

	public TileSpeaker() {
		super("speaker");
	}

	@Override
	public void updateEntity() {
		packetIds.clear();
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
		if(!packetIds.contains(packet.id)) {
			packetIds.add(packet.id);

			lastSource = packet.source;
			packet.addReceiver(this);
		}
	}

	@Override
	public String getID() {
		return AudioUtils.positionId(xCoord, yCoord, zCoord);
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
	public boolean connectsAudio(ForgeDirection side) {
		return Computronics.speaker.getFrontSide(getBlockMetadata()) != side.ordinal();
	}

	@Override
	public boolean canConnectPeripheralOnSide(int side) {
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

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Node sidedNode(ForgeDirection side) {
		return null;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public boolean canConnect(ForgeDirection side) {
		return false;
	}
}

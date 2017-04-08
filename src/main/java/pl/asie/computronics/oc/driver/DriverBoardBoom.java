package pl.asie.computronics.oc.driver;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import li.cil.oc.api.Network;
import li.cil.oc.api.component.RackBusConnectable;
import li.cil.oc.api.component.RackMountable;
import li.cil.oc.api.internal.Rack;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.commons.lang3.tuple.Pair;
import pl.asie.computronics.oc.IntegrationOpenComputers;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.OCUtils;
import pl.asie.computronics.util.boom.SelfDestruct;

import java.util.ArrayDeque;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;

/**
 * @author Vexatos
 */
public class DriverBoardBoom extends DriverCardBoom implements RackMountable {

	protected final Rack container;
	protected boolean needsUpdate;
	protected boolean isActive;

	public DriverBoardBoom(Rack container) {
		super(container);
		this.container = container;
	}

	@Override
	protected void createNode() {
		this.setNode(Network.newNode(this, Visibility.Network).
			withComponent("server_destruct", Visibility.Network).
			withConnector().
			create());
	}

	@Override
	public void update() {
		setActive(node.tryChangeBuffer(-Config.BOOM_BOARD_MAINTENANCE_COST));
		if(needsUpdate) {
			container.markChanged(container.indexOfMountable(this));
			needsUpdate = false;
		}
		if(!isActive) {
			setTime(-1);
			return;
		}
		super.update();
	}

	@Override
	protected void setTime(int time) {
		if(time != this.time) {
			super.setTime(time);
			needsUpdate = true;
		}
	}

	public void setActive(boolean active) {
		if(active != this.isActive) {
			this.isActive = active;
			needsUpdate = true;
		}
	}

	@Override
	protected void goBoom() {
		final Set<Rack> racks = new LinkedHashSet<Rack>();
		final Queue<Rack> toSearch = new ArrayDeque<Rack>();
		toSearch.add(container);
		racks.add(container);
		final Vec3 origin = Vec3.createVectorHelper(container.xPosition(), container.yPosition(), container.zPosition());
		Rack cur;
		while((cur = toSearch.poll()) != null) {
			final World world = cur.world();
			final int x = MathHelper.floor_double(cur.xPosition());
			final int y = MathHelper.floor_double(cur.yPosition());
			final int z = MathHelper.floor_double(cur.zPosition());
			for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
				final int rx = x + dir.offsetX;
				final int ry = y + dir.offsetY;
				final int rz = z + dir.offsetZ;

				if(origin.squareDistanceTo(rx, ry, rz) <= 256 &&
					world.blockExists(rx, ry, rz)) {
					TileEntity tile = world.getTileEntity(rx, ry, rz);
					if(tile instanceof Rack && racks.add((Rack) tile)) {
						toSearch.add((Rack) tile);
					}
				}
			}
		}
		final Queue<Set<Rack>> rackList = new ArrayDeque<Set<Rack>>(((racks.size() + 5) / 6));
		Iterator<Rack> itr = racks.iterator();
		while(itr.hasNext()) {
			Set<Rack> sub = new HashSet<Rack>(6);
			for(int i = 0; i < 6; i++) {
				if(!itr.hasNext()) {
					break;
				}
				Rack rack = itr.next();
				sub.add(rack);
			}
			rackList.add(sub);
		}
		IntegrationOpenComputers.boomBoardHandler.queue(container, rackList);
	}

	@Override
	public NBTTagCompound getData() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("t", this.time >= 0);
		tag.setBoolean("r", this.isActive);
		return tag;
	}

	@Override
	public void onDisconnect(final Node node) {
	}

	@Override
	public void onMessage(final Message message) {
		// NO-OP
	}

	@Override
	public void load(NBTTagCompound nbt) {
		super.load(nbt);
		if(nbt.getBoolean("active")) {
			setActive(nbt.getBoolean("active"));
		}
	}

	@Override
	public void save(NBTTagCompound nbt) {
		super.save(nbt);
		nbt.setBoolean("active", this.isActive);
	}

	@Override
	protected OCUtils.Device deviceInfo() {
		return new OCUtils.Device(
			DeviceClass.Generic,
			"Server-cleaning service",
			OCUtils.Vendors.HuggingCreeper,
			"SSD-Struct M4"
		);
	}

	@Override
	public int getConnectableCount() {
		return 0;
	}

	@Override
	public RackBusConnectable getConnectableAt(int index) {
		return null;
	}

	@Override
	public boolean onActivate(EntityPlayer player, float hitX, float hitY) {
		return false;
	}

	@Override
	public EnumSet<State> getCurrentState() {
		return EnumSet.noneOf(State.class);
	}

	public static class BoomHandler {

		private final Set<Pair<Rack, Queue<Set<Rack>>>> boomQueue = new HashSet<Pair<Rack, Queue<Set<Rack>>>>();

		@SubscribeEvent
		@Optional.Method(modid = Mods.OpenComputers)
		public void onServerTick(ServerTickEvent e) {
			if(e.phase != TickEvent.Phase.START || boomQueue.isEmpty()) {
				return;
			}
			Set<Pair<Rack, Queue<Set<Rack>>>> toRemove = new HashSet<Pair<Rack, Queue<Set<Rack>>>>();
			for(Pair<Rack, Queue<Set<Rack>>> lists : boomQueue) {
				if((lists.getKey().world().getTotalWorldTime() + lists.hashCode()) % 5 != 0) {
					continue; // Only explode every five ticks.
				}
				Set<Rack> current = lists.getValue().poll();
				for(Rack rack : current) {
					SelfDestruct.goBoom(rack.world(), rack.xPosition(), rack.yPosition(), rack.zPosition(), false);
				}
				if(lists.getValue().isEmpty()) {
					toRemove.add(lists);
				}
			}
			boomQueue.removeAll(toRemove);
		}

		@Optional.Method(modid = Mods.OpenComputers)
		public void queue(Rack owner, Queue<Set<Rack>> rackList) {
			boomQueue.add(Pair.of(owner, rackList));
		}
	}
}

package pl.asie.computronics.oc.driver;

import li.cil.oc.api.Network;
import li.cil.oc.api.internal.Rack;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.util.OCUtils;

/**
 * @author Vexatos
 */
public class DriverBoardSwitch extends RackMountableWithComponentConnector {

	protected final boolean[] switches = new boolean[4];
	protected final Rack host;
	protected boolean needsUpdate = false;

	public DriverBoardSwitch(Rack host) {
		this.host = host;
		this.setNode(Network.newNode(this, Visibility.Network).
			withComponent("switch_board", Visibility.Network).
			withConnector().
			create());
	}

	@Override
	public NBTTagCompound getData() {
		NBTTagCompound tag = new NBTTagCompound();
		byte switchData = 0;
		for(int i = 0; i < switches.length; i++) {
			switchData |= (switches[i] ? 1 : 0) << i;
		}
		tag.setByte("s", switchData);
		return tag;
	}

	// ----------------
	// - 00 00  00 00 -
	// ----------------
	protected static final int[] pixToSwitch = new int[] {
		-1, 0, 0, -1, 1, 1, -1, -1, 2, 2, -1, 3, 3, -1, -1
	};

	@Override
	public boolean onActivate(EntityPlayer player, float hitX, float hitY) {
		if(player.worldObj.isRemote) {
			return true;
		}
		int xPix = (int) (hitX * 14);
		int yPix = (int) (hitY * 3);
		if(yPix == 1) {
			if(xPix > 0 && xPix < pixToSwitch.length && pixToSwitch[xPix] >= 0) {
				flipSwitch(pixToSwitch[xPix]);
			}
		}
		return true;
	}

	protected void flipSwitch(int i) {
		switches[i] = !switches[i];
		node.sendToReachable("computer.signal", "switch_flipped", i + 1, switches[i]);
		needsUpdate = true;
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void update() {
		super.update();
		for(int i = 0; i < switches.length; i++) {
			if(switches[i] && !node.tryChangeBuffer(-Config.SWITCH_BOARD_MAINTENANCE_COST)) {
				setActive(i, false, false);
			}
		}
		if(needsUpdate) {
			host.markChanged(host.indexOfMountable(this));
			needsUpdate = false;
		}
	}

	public Boolean isActive(int index) {
		return index >= 0 && index < switches.length ? switches[index] : null;
	}

	public boolean isActiveB(int index) {
		Boolean active = isActive(index);
		return active != null ? active : false;
	}

	private void setActive(int index, boolean active, boolean signal) {
		if(switches[index] != active) {
			switches[index] = active;
			if(signal) {
				node.sendToReachable("computer.signal", "switch_flipped", index + 1, switches[index]);
			}
			needsUpdate = true;
		}
	}

	private int checkSwitch(int index) {
		Boolean active = isActive(index - 1);
		if(active == null) {
			throw new IllegalArgumentException("index out of range: " + index);
		}
		return index - 1;
	}

	@Callback(doc = "function(index:number, active:boolean):boolean; Activates or deactivates the specified switch. Returns true if the state changed.", direct = true)
	public Object[] setActive(Context context, Arguments args) {
		int index = checkSwitch(args.checkInteger(0));
		boolean active = args.checkBoolean(1);
		if(switches[index] != active) {
			setActive(index, active, true);
			return new Object[] { true };
		}
		return new Object[] { false };
	}

	@Callback(doc = "function(index:number):boolean; Returns true if the switch at the specified position is currently active", direct = true)
	public Object[] isActive(Context context, Arguments args) {
		return new Object[] { isActiveB(checkSwitch(args.checkInteger(0))) };
	}

	@Override
	public void load(NBTTagCompound tag) {
		super.load(tag);
		if(tag.hasKey("s")) {
			byte switchData = tag.getByte("s");
			for(int i = 0; i < switches.length; i++) {
				switches[i] = ((switchData >> i) & 1) == 1;
			}
		}
	}

	@Override
	public void save(NBTTagCompound tag) {
		super.save(tag);
		byte switchData = 0;
		for(int i = 0; i < switches.length; i++) {
			switchData |= (switches[i] ? 1 : 0) << i;
		}
		tag.setByte("s", switchData);
	}

	@Override
	protected OCUtils.Device deviceInfo() {
		return new OCUtils.Device(
			DeviceClass.Input,
			"Switch board",
			OCUtils.Vendors.Soluna,
			"Clickety-Clack Q3"
		);
	}
}

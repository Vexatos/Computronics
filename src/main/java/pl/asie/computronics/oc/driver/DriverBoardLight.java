package pl.asie.computronics.oc.driver;

import li.cil.oc.api.Network;
import li.cil.oc.api.component.RackBusConnectable;
import li.cil.oc.api.component.RackMountable;
import li.cil.oc.api.internal.Rack;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.oc.ManagedEnvironmentWithComponentConnector;
import pl.asie.computronics.reference.Config;

import java.util.EnumSet;

/**
 * @author Vexatos
 */
public class DriverBoardLight extends ManagedEnvironmentWithComponentConnector implements RackMountable {

	protected final Rack host;
	protected boolean needsUpdate;

	public DriverBoardLight(Rack host) {
		this.host = host;
		this.setNode(Network.newNode(this, Visibility.Network).
			withComponent("light_board", Visibility.Network).
			withConnector((Config.LIGHT_BOARD_COLOR_CHANGE_COST + Config.LIGHT_BOARD_COLOR_MAINTENANCE_COST) * 10).
			create());
	}

	public final Light[] lights = Light.createLights();

	public static class Light {

		protected int color = 0xC0C0C0;
		protected boolean isActive = false;
		public final int index;

		Light(int index) {
			this.index = index;
		}

		public static final int amount = 4;

		public static Light[] createLights() {
			Light[] lights = new Light[amount];
			for(int i = 0; i < amount; i++) {
				lights[i] = new Light(i + 1);
			}
			return lights;
		}
	}

	public Light getLight(int index) {
		return index >= 0 && index < lights.length ? lights[index] : null;
	}

	@Override
	public NBTTagCompound getData() {
		NBTTagCompound tag = new NBTTagCompound();
		for(Light light : lights) {
			tag.setBoolean("r_" + light.index, light.isActive);
			if(light.isActive) {
				tag.setInteger("c_" + light.index, light.color);
			}
		}
		return tag;
	}

	public void setColor(Light light, int color) {
		if(light.color != color) {
			light.color = color;
			needsUpdate = true;
		}
	}

	private void setActive(Light light, boolean active) {
		if(light.isActive != active) {
			light.isActive = active;
			needsUpdate = true;
		}
	}

	private Light checkLight(int index) {
		Light light = getLight(index - 1);
		if(light == null) {
			throw new IllegalArgumentException("index out of range");
		}
		return light;
	}

	@Callback(doc = "function(index:number, color:number):boolean; Sets the color of the specified light. Returns true on success, false and an error message otherwise", direct = true)
	public Object[] setColor(Context context, Arguments args) {
		Light light = checkLight(args.checkInteger(0));
		int color = args.checkInteger(1);
		if(color >= 0 && color <= 0xFFFFFF) {
			if(node.tryChangeBuffer(-Config.LIGHT_BOARD_COLOR_CHANGE_COST)) {
				setColor(light, color);
				return new Object[] { true };
			}
			return new Object[] { false, "not enough energy" };
		}
		return new Object[] { false, "number must be between 0 and 16777215" };
	}

	@Callback(doc = "function(index:number):number; Returns the color of the specified light on success, false and an error message otherwise", direct = true)
	public Object[] getColor(Context context, Arguments args) {
		return new Object[] { checkLight(args.checkInteger(0)).color };
	}

	@Callback(doc = "function(index:number, active:boolean):boolean; Turns the specified light on or off. Returns true on success, false and an error message otherwise", direct = true)
	public Object[] setActive(Context context, Arguments args) {
		Light light = checkLight(args.checkInteger(0));
		boolean active = args.checkBoolean(1);
		if(node.tryChangeBuffer(-Config.LIGHT_BOARD_COLOR_CHANGE_COST)) {
			setActive(light, active);
			return new Object[] { true };
		}
		return new Object[] { false, "not enough energy" };
	}

	@Callback(doc = "function(index:number):boolean; Returns true if the light at the specified position is currently active", direct = true)
	public Object[] isActive(Context context, Arguments args) {
		return new Object[] { checkLight(args.checkInteger(0)).isActive };
	}

	@Callback(value = "light_count", doc = "This represents the number of lights on the board.", direct = true, getter = true)
	public Object[] getLightCount(Context context, Arguments args) {
		return new Object[] { lights.length };
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void update() {
		for(Light light : lights) {
			if(light.isActive) {
				if(!node.tryChangeBuffer(-Config.LIGHT_BOARD_COLOR_MAINTENANCE_COST)) {
					setActive(light, false);
					break;
				}
			}
		}
		if(needsUpdate) {
			host.markChanged(host.indexOfMountable(this));
			needsUpdate = false;
		}
	}

	@Override
	public void load(NBTTagCompound tag) {
		super.load(tag);
		for(Light light : lights) {
			if(tag.hasKey("r_" + light.index)) {
				setActive(light, tag.getBoolean("r_" + light.index));
			}
			if(tag.hasKey("c_" + light.index)) {
				setColor(light, tag.getInteger("c_" + light.index));
			}
		}
	}

	@Override
	public void save(NBTTagCompound tag) {
		super.save(tag);
		for(Light light : lights) {
			tag.setBoolean("r_" + light.index, light.isActive);
			if(light.isActive) {
				tag.setInteger("c_" + light.index, light.color);
			}
		}
	}

	// Unused

	@Override
	public int getConnectableCount() {
		return 0;
	}

	@Override
	public RackBusConnectable getConnectableAt(int index) {
		return null;
	}

	@Override
	public boolean onActivate(EntityPlayer player, ForgeDirection side, float hitX, float hitY, float hitZ) {
		return false;
	}

	@Override
	public EnumSet<State> getCurrentState() {
		return EnumSet.noneOf(State.class);
	}
}

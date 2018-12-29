package pl.asie.computronics.oc.driver;

import li.cil.oc.api.Network;
import li.cil.oc.api.internal.Rack;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.util.OCUtils;
import pl.asie.lib.integration.Integration;

import javax.annotation.Nullable;

import static pl.asie.lib.util.WorldUtils.notifyBlockUpdate;

/**
 * @author Vexatos
 */
public class DriverBoardLight extends RackMountableWithComponentConnector {

	protected final Rack host;
	protected boolean needsUpdate = false;

	public DriverBoardLight(Rack host) {
		this.host = host;
		this.setNode(Network.newNode(this, Visibility.Network).
			withComponent("light_board", Visibility.Network).
			withConnector().
			create());
		mode = Mode.Default;
		lights = mode.createLights();
	}

	public static class Light {

		protected int color = 0xC0C0C0;
		protected boolean isActive = false;
		public final int index;

		Light(int index) {
			this.index = index;
		}
	}

	public enum Mode {
		Default(4) {
			// ----------------
			// - 00 00  00 00 -
			// ----------------
			@Override
			public float getU0(int index) {
				return ((index - 1) * 4) / 16f;
			}

			@Override
			public float getU1(int index, float u0) {
				return u0 + (4 / 16f);
			}
		},
		Regular(5) {
			// ----------------
			// -00 00 00 00 00-
			// ----------------
			@Override
			public float getU0(int index) {
				return ((index - 1) * 3) / 16f;
			}

			@Override
			public float getU1(int index, float u0) {
				return u0 + (3 / 16f);
			}
		},
		Five(10) {
			// ----------------
			// - 00000  00000 -
			// ----------------
			@Override
			public float getU0(int index) {
				return (index <= 5 ? (1 + index) : (3 + index)) / 16f;
			}

			@Override
			public float getU1(int index, float u0) {
				return u0 + (1 / 16f);
			}
		},
		Twelve(12) {
			// ----------------
			// - 000000000000 -
			// ----------------
			@Override
			public float getU0(int index) {
				return (index + 1) / 16f;
			}

			@Override
			public float getU1(int index, float u0) {
				return u0 + (1 / 16f);
			}
		},
		Total(42) {
			// -00000000000000-
			// -00000000000000-
			// -00000000000000-
			@Override
			public float getU0(int index) {
				return (((index - 1) % 14) + 1) / 16f;
			}

			@Override
			public float getU1(int index, float u0) {
				return u0 + (1 / 16f);
			}

			@Override
			public float getV0(int index, float v0, float v1) {
				index -= 1;
				if(index >= 14) {
					int diff = index / 14;
					v0 += diff / 16f;
				}
				return v0;
			}

			@Override
			public float getV1(int index, float v0, float v1) {
				return v0 + (1 / 16f);
			}

			@Override
			public Light[] createLights() {
				Light[] lights = super.createLights();
				for(Light light : lights) {
					light.color = 0x111111;
				}
				return lights;
			}
		};

		public final int index;
		public final int lightcount;
		@SideOnly(Side.CLIENT)
		public TextureAtlasSprite background;
		public final ResourceLocation foreground;
		public final String backgroundPath;
		public static final Mode[] VALUES = values();

		Mode(int lightcount) {
			this.index = ordinal() + 1;
			this.lightcount = lightcount;
			this.foreground = new ResourceLocation("computronics", "textures/blocks/light_board/mode_" + index + "_lights.png");
			this.backgroundPath = "computronics:blocks/light_board/mode_" + index;
		}

		public abstract float getU0(int index);

		public abstract float getU1(int index, float u0);

		public float getV0(int index, float v0, float v1) {
			return v0;
		}

		public float getV1(int index, float v0, float v1) {
			return v1;
		}

		public Light[] createLights() {
			Light[] lights = new Light[this.lightcount];
			for(int i = 0; i < this.lightcount; i++) {
				lights[i] = new Light(i + 1);
			}
			return lights;
		}

		@SideOnly(Side.CLIENT)
		public void registerIcons(TextureMap map) {
			background = map.registerSprite(new ResourceLocation(backgroundPath));
		}

		@Nullable
		public static Mode fromIndex(int index) {
			return index > 0 && index <= VALUES.length ? VALUES[index - 1] : null;
		}
	}

	public Mode mode;
	public Light[] lights;

	@Nullable
	public Light getLight(int index) {
		return index >= 0 && index < lights.length ? lights[index] : null;
	}

	@Override
	public NBTTagCompound getData() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("m", mode.index);
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

	private void setMode(@Nullable Mode mode) {
		if(mode == null) {
			mode = Mode.Default;
		}
		if(mode != this.mode) {
			this.mode = mode;
			this.lights = mode.createLights();
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
		if(tag.hasKey("m")) {
			setMode(Mode.fromIndex(tag.getInteger("m")));
			for(Light light : lights) {
				if(tag.hasKey("r_" + light.index)) {
					setActive(light, tag.getBoolean("r_" + light.index));
				}
				if(tag.hasKey("c_" + light.index)) {
					setColor(light, tag.getInteger("c_" + light.index));
				}
			}
		}
	}

	@Override
	public void save(NBTTagCompound tag) {
		super.save(tag);
		tag.setInteger("m", mode.index);
		for(Light light : lights) {
			tag.setBoolean("r_" + light.index, light.isActive);
			if(light.isActive) {
				tag.setInteger("c_" + light.index, light.color);
			}
		}
	}

	@Override
	public boolean onActivate(EntityPlayer player, EnumHand hand, ItemStack heldItem, float hitX, float hitY) {
		if(player.world.isRemote) {
			return true;
		}
		final BlockPos pos = new BlockPos(host.xPosition(), host.yPosition(), host.zPosition());
		if(!heldItem.isEmpty() && Integration.isTool(heldItem, player, hand, pos) && Integration.useTool(heldItem, player, hand, pos)) {
			int index = mode.index;
			if(index >= Mode.VALUES.length) {
				index = 0;
			}
			setMode(Mode.fromIndex(index + 1));
			host.markChanged(host.indexOfMountable(this));
			needsUpdate = false;
			notifyBlockUpdate(host.world(), pos);
			return true;
		}
		return false;
	}

	@Override
	protected OCUtils.Device deviceInfo() {
		return new OCUtils.Device(
			DeviceClass.Display,
			"Light board",
			OCUtils.Vendors.Lumiose,
			"LED-15 X"
		);
	}

}

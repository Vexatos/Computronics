package pl.asie.computronics.item;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.audio.AudioPacket;
import pl.asie.computronics.api.audio.IAudioReceiver;
import pl.asie.computronics.api.audio.IAudioSource;
import pl.asie.computronics.api.tape.IItemTapeStorage;
import pl.asie.computronics.audio.MachineSound;
import pl.asie.computronics.network.PacketType;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TapeDriveState;
import pl.asie.computronics.tile.TapeDriveState.State;
import pl.asie.computronics.util.StringUtil;
import pl.asie.lib.network.Packet;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author Vexatos
 */
public class ItemPortableTapeDrive extends Item {

	public static final class PortableDriveManager {

		public static final PortableDriveManager INSTANCE = new PortableDriveManager();

		private PortableDriveManager() {
		}

		private BiMap<String, TapeDrive> drivesServer = HashBiMap.create();
		private BiMap<String, TapeDrive> drivesClient = HashBiMap.create();

		private BiMap<String, TapeDrive> drives(boolean client) {
			return client ? drivesClient : drivesServer;
		}

		public TapeDrive getOrCreate(ItemStack stack, boolean client) {
			NBTTagCompound tag = stack.getTagCompound();
			String id;
			if(tag != null && tag.hasKey("tid")) {
				id = tag.getString("tid");
			} else {
				if(tag == null) {
					tag = new NBTTagCompound();
					stack.setTagCompound(tag);
				}
				id = UUID.randomUUID().toString();
				tag.setString("tid", id);
			}
			TapeDrive drive = drives(client).get(id);
			if(drive == null) {
				drive = new TapeDrive();
				drive.load(tag);
				add(id, drive, client);
			}
			return drive;
		}

		public void add(String id, TapeDrive drive, boolean client) {
			drives(client).put(id, drive);
		}

		public String getID(TapeDrive drive, boolean client) {
			return drives(client).inverse().get(drive);
		}

		public TapeDrive getTapeDrive(String id, boolean client) {
			return drives(client).get(id);
		}

		@SubscribeEvent
		public void onServerTick(ServerTickEvent event) {
			if(event.phase != TickEvent.Phase.END) {
				return;
			}
			Set<String> toRemove = new HashSet<String>();
			for(Map.Entry<String, TapeDrive> entry : drives(false).entrySet()) {
				if(entry.getValue().time > 5) {
					entry.getValue().switchState(State.STOPPED);
					toRemove.add(entry.getKey());
					try {
						Entity carrier = entry.getValue().carrier;
						if(carrier instanceof EntityPlayerMP) {
							Computronics.packet.sendTo(
								Computronics.packet.create(PacketType.PORTABLE_TAPE_STOP.ordinal()).writeString(entry.getKey()),
								((EntityPlayerMP) carrier)
							);
						}
					} catch(IOException e) {
						e.printStackTrace();
					}
					entry.getValue().carrier = null;
				} else {
					entry.getValue().time++;
				}
			}
			for(String s : toRemove) {
				drives(false).remove(s);
			}
		}

		@SideOnly(Side.CLIENT)
		public void stopTapeDrive(String id) {
			TapeDrive drive = drives(true).get(id);
			if(drive != null) {
				drive.switchState(State.STOPPED);
				drive.updateSound();
				drive.carrier = null;
				drives(true).remove(id);
			}
		}
	}

	public static class TapeDrive implements IAudioSource {

		protected World world;
		protected int x, y, z;
		protected Entity carrier;
		protected ItemStack self;
		private int time = 0;

		private String storageName = "";
		private TapeDriveState state = new TapeDriveState();
		private ItemStack inventory;

		public TapeDrive() {
		}

		public void updateCarrier(Entity carrier, ItemStack self) {
			this.world = carrier.worldObj;
			this.x = MathHelper.floor_double(carrier.posX);
			this.y = MathHelper.floor_double(carrier.posY);
			this.z = MathHelper.floor_double(carrier.posZ);
			this.carrier = carrier;
			this.self = self;
		}

		public State getEnumState() {
			return this.state.getState();
		}

		public void switchState(State s) {
			//System.out.println("Switchy switch to " + s.name());
			if(this.getEnumState() != s) {
				this.state.switchState(world, x, y, z, s);
				updateState();
			}
		}

		public void resetTime() {
			this.time = 0;
		}

		public void update() {
			if(world.isRemote) {
				updateSound();
			}
			State st = getEnumState();
			AudioPacket pkt = state.update(this, world, x, y, z);
			if(pkt != null) {
				internalSpeaker.receivePacket(pkt, ForgeDirection.UNKNOWN);

				pkt.sendPacket();
			}
			if(!world.isRemote && st != getEnumState()) {
				updateState();
			}
		}

		public NBTTagCompound getTag() {
			NBTTagCompound tag = self.getTagCompound();
			if(tag == null) {
				tag = new NBTTagCompound();
				self.setTagCompound(tag);
			}
			return tag;
		}

		protected void updateState() {
			if(world.isRemote) {
				return;
			}

			save(getTag());
			String id = PortableDriveManager.INSTANCE.getID(this, world.isRemote);
			if(id != null) {
				try {
					Packet packet = Computronics.packet.create(PacketType.PORTABLE_TAPE_STATE.ordinal())
						.writeString(id)
						.writeByte((byte) state.getState().ordinal());
					//.writeByte((byte)soundVolume);
					Computronics.packet.sendToAllAround(packet, carrier, 64.0D);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}

		@SideOnly(Side.CLIENT)
		private MachineSound sound;

		private ResourceLocation soundRes = new ResourceLocation(Mods.Computronics, "tape_rewind");

		public float getVolume() {
			return 1.0f;
		}

		public float getPitch() {
			return 1.0f;
		}

		public boolean shouldRepeat() {
			return true;
		}

		@SideOnly(Side.CLIENT)
		private void updateSound() {
			if(shouldPlaySound()) {
				if(sound == null) {
					sound = new MachineSound(soundRes, x + 0.5f, y + 0.5f, z + 0.5f, getVolume(), getPitch(), shouldRepeat()) {
						@Override
						public void update() {
							this.xPosF = TapeDrive.this.x;
							this.yPosF = TapeDrive.this.y;
							this.zPosF = TapeDrive.this.z;
						}
					};
					FMLClientHandler.instance().getClient().getSoundHandler().playSound(sound);
				}
			} else if(sound != null) {
				sound.endPlaying();
				sound = null;
			}
		}

		public boolean shouldPlaySound() {
			switch(getEnumState()) {
				case REWINDING:
				case FORWARDING:
					return true;
				default:
					return false;
			}
		}

		// Storage handling

		private void loadStorage() {
			if(world != null && world.isRemote) {
				return;
			}

			if(state.getStorage() != null) {
				unloadStorage();
			}
			ItemStack stack = this.inventory;
			if(stack != null) {
				// Get Storage.
				Item item = stack.getItem();
				if(item instanceof IItemTapeStorage) {
					state.setStorage(((IItemTapeStorage) item).getStorage(stack));
				}

				// Get possible label.
				if(stack.getTagCompound() != null) {
					NBTTagCompound tag = stack.getTagCompound();
					storageName = tag.hasKey("label") ? tag.getString("label") : "";
				} else {
					storageName = "";
				}
			}
		}

		public void saveStorage() {
			unloadStorage();
		}

		private void unloadStorage() {
			if(world.isRemote || state.getStorage() == null) {
				return;
			}

			switchState(State.STOPPED);
			try {
				state.getStorage().onStorageUnload();
			} catch(Exception e) {
				e.printStackTrace();
			}
			state.setStorage(null);
		}

		public void onInvUpdate() {
			if(this.inventory == null) {
				if(state.getStorage() != null) { // Tape was inserted
					// Play eject sound
					world.playSoundEffect(x, y, z, "computronics:tape_eject", 1, 0);
				}
				unloadStorage();
			} else {
				loadStorage();
				if(this.inventory.getItem() instanceof IItemTapeStorage) {
					// Play insert sound
					world.playSoundEffect(x, y, z, "computronics:tape_insert", 1, 0);
				}
			}
		}

		public void load(NBTTagCompound tag) {
			if(tag.hasKey("inv")) {
				this.inventory = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("inv"));
			}
			if(tag.hasKey("state")) {
				this.state.setState(State.VALUES[tag.getByte("state")]);
			}
			if(tag.hasKey("sp")) {
				this.state.packetSize = tag.getShort("sp");
			}
			if(tag.hasKey("vo")) {
				this.state.soundVolume = tag.getByte("vo");
			} else {
				this.state.soundVolume = 127;
			}
			loadStorage();
		}

		public void save(NBTTagCompound tag) {
			NBTTagCompound inv = new NBTTagCompound();
			if(inventory != null) {
				inventory.writeToNBT(inv);
			}
			tag.setTag("inv", inv);
			tag.setShort("sp", (short) this.state.packetSize);
			tag.setByte("state", (byte) this.state.getState().ordinal());
			if(this.state.soundVolume != 127) {
				tag.setByte("vo", (byte) this.state.soundVolume);
			}
		}

		private final IAudioReceiver internalSpeaker = new IAudioReceiver() {
			@Override
			public boolean connectsAudio(ForgeDirection side) {
				return true;
			}

			@Override
			public World getSoundWorld() {
				return world;
			}

			@Override
			public int getSoundX() {
				return x;
			}

			@Override
			public int getSoundY() {
				return y;
			}

			@Override
			public int getSoundZ() {
				return z;
			}

			@Override
			public int getSoundDistance() {
				return 4;
			}

			@Override
			public void receivePacket(AudioPacket packet, ForgeDirection direction) {
				packet.addReceiver(this);
			}
		};

		@Override
		public int getSourceId() {
			return state.getId();
		}

		@Override
		public boolean connectsAudio(ForgeDirection side) {
			return true;
		}

		public final IInventory fakeInventory = new IInventory() {
			@Override
			public int getSizeInventory() {
				return 1;
			}

			@Override
			public String getInventoryName() {
				return "portabletapedrive.inventory";
			}

			@Override
			public int getInventoryStackLimit() {
				return 1;
			}

			@Override
			public boolean isItemValidForSlot(int slot, ItemStack stack) {
				return false;
			}

			@Override
			public ItemStack getStackInSlot(int slot) {
				if(slot != 0) {
					return null;
				}
				return TapeDrive.this.inventory;
			}

			@Override
			public ItemStack decrStackSize(int slot, int amount) {
				if(slot != 0) {
					return null;
				}
				if(TapeDrive.this.inventory != null) {
					ItemStack stack;
					if(TapeDrive.this.inventory.stackSize <= amount) {
						stack = TapeDrive.this.inventory;
						TapeDrive.this.inventory = null;
						TapeDrive.this.onInvUpdate();
						return stack;
					} else {
						stack = TapeDrive.this.inventory.splitStack(amount);

						if(TapeDrive.this.inventory.stackSize == 0) {
							TapeDrive.this.inventory = null;
						}

						TapeDrive.this.onInvUpdate();

						return stack;
					}
				} else {
					return null;
				}
			}

			@Override
			public ItemStack getStackInSlotOnClosing(int slot) {
				ItemStack stack = getStackInSlot(slot);
				if(stack == null) {
					return null;
				}
				TapeDrive.this.inventory = null;
				TapeDrive.this.onInvUpdate();
				return stack;
			}

			@Override
			public void setInventorySlotContents(int slot, ItemStack stack) {
				if(slot != 0) {
					return;
				}
				TapeDrive.this.inventory = stack;
				TapeDrive.this.onInvUpdate();
			}

			@Override
			public boolean isUseableByPlayer(EntityPlayer player) {
				return true;
			}

			@Override
			public void openInventory() {

			}

			@Override
			public void closeInventory() {

			}

			@Override
			public boolean hasCustomInventoryName() {
				return false;
			}

			@Override
			public void markDirty() {

			}
		};
	}

	public ItemPortableTapeDrive() {
		super();
		this.setCreativeTab(Computronics.tab);
		this.setHasSubtypes(false);
		this.setUnlocalizedName("computronics.portableTapeDrive");
		this.setTextureName("computronics:portable_tape_drive");
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		this.setNoRepair();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean advanced) {
		info.add(StringUtil.localizeAndFormat("tooltip.computronics.tape.state",
			StringUtil.localize("tooltip.computronics.tape.state."
				+ TapeDriveState.State.VALUES[stack.getTagCompound().getByte("state")].name().toLowerCase(Locale.ENGLISH))));
		super.addInformation(stack, player, info, advanced);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity carrier, int slot, boolean isSelected) {
		super.onUpdate(stack, world, carrier, slot, isSelected);
		TapeDrive drive = PortableDriveManager.INSTANCE.getOrCreate(stack, world.isRemote);
		drive.resetTime();
		drive.updateCarrier(carrier, stack);
		drive.update();
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		TapeDrive drive = PortableDriveManager.INSTANCE.getOrCreate(stack, world.isRemote);
		drive.updateCarrier(player, stack);
		if(world.isRemote) {
			return super.onItemRightClick(stack, world, player);
		}
		if(player.isSneaking()) {
			player.openGui(Computronics.instance, Computronics.guiPortableTapeDrive.getGuiID(), world, 0, 0, 0);
		} else {
			drive.switchState(drive.getEnumState() != State.STOPPED ? State.STOPPED : State.PLAYING);
		}
		return super.onItemRightClick(stack, world, player);
	}
}

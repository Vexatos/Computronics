package pl.asie.computronics.tape;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.audio.AudioPacket;
import pl.asie.computronics.api.audio.IAudioReceiver;
import pl.asie.computronics.api.audio.IAudioSource;
import pl.asie.computronics.api.tape.IItemTapeStorage;
import pl.asie.computronics.audio.MachineSound;
import pl.asie.computronics.network.PacketType;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TapeDriveState;
import pl.asie.lib.network.Packet;

/**
 * @author Vexatos
 */
public class PortableTapeDrive implements IAudioSource {

	protected World world;
	protected Vec3 pos;
	protected Entity carrier;
	protected ItemStack self;
	protected int time = 0;

	private String storageName = "";
	private TapeDriveState state = new TapeDriveState();
	private ItemStack inventory;

	public PortableTapeDrive() {
	}

	public void updateCarrier(Entity carrier, ItemStack self) {
		this.world = carrier.worldObj;
		this.pos = Vec3.createVectorHelper(carrier.posX, carrier.posY, carrier.posZ);
		this.carrier = carrier;
		this.self = self;
	}

	public TapeDriveState.State getEnumState() {
		return this.state.getState();
	}

	public void switchState(TapeDriveState.State s) {
		//System.out.println("Switchy switch to " + s.name());
		if(this.getEnumState() != s) {
			this.state.switchState(world, s);
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
		TapeDriveState.State st = getEnumState();
		AudioPacket pkt = state.update(this, world);
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

	public ItemStack getSelf() {
		return self;
	}

	protected void updateState() {
		if(world.isRemote) {
			return;
		}

		save(getTag());
		sendState();
	}

	protected void sendState() {
		String id = PortableDriveManager.INSTANCE.getID(this, world.isRemote);
		if(id != null) {
			try {
				Packet packet = Computronics.packet.create(PacketType.PORTABLE_TAPE_STATE.ordinal())
					.writeString(id)
					.writeByte((byte) state.getState().ordinal())
					.writeInt(getSourceId());
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
	protected void updateSound() {
		if(shouldPlaySound()) {
			if(sound == null) {
				sound = new MachineSound(soundRes, (float) pos.xCoord, (float) pos.yCoord, (float) pos.zCoord, getVolume(), getPitch(), shouldRepeat()) {
					@Override
					public void update() {
						this.xPosF = (float) PortableTapeDrive.this.pos.xCoord;
						this.yPosF = (float) PortableTapeDrive.this.pos.yCoord;
						this.zPosF = (float) PortableTapeDrive.this.pos.zCoord;
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

		switchState(TapeDriveState.State.STOPPED);
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
				world.playSoundEffect(pos.xCoord, pos.yCoord, pos.zCoord, "computronics:tape_eject", 1, 0);
			}
			unloadStorage();
		} else {
			loadStorage();
			if(this.inventory.getItem() instanceof IItemTapeStorage) {
				// Play insert sound
				world.playSoundEffect(pos.xCoord, pos.yCoord, pos.zCoord, "computronics:tape_insert", 1, 0);
			}
		}
		save(getTag());
	}

	protected int clientId = -1;

	public void load(NBTTagCompound tag) {
		if(tag.hasKey("inv")) {
			this.inventory = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("inv"));
		}
		if(tag.hasKey("state")) {
			this.state.setState(TapeDriveState.State.VALUES[tag.getByte("state")]);
		}
		if(tag.hasKey("sp")) {
			this.state.packetSize = tag.getShort("sp");
		}
		if(tag.hasKey("vo")) {
			this.state.soundVolume = tag.getByte("vo");
		} else {
			this.state.soundVolume = 127;
		}
		if(tag.hasKey("cId")) {
			this.clientId = tag.getInteger("cId");
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
		tag.setInteger("cId", clientId);
	}

	private final IAudioReceiver internalSpeaker = new IAudioReceiver() {
		@Override
		public boolean connectsAudio(ForgeDirection side) {
			return false;
		}

		@Override
		public World getSoundWorld() {
			return world;
		}

		@Override
		public Vec3 getSoundPos() {
			return pos;
		}

		@Override
		public int getSoundDistance() {
			return Config.PORTABLE_TAPEDRIVE_DISTANCE;
		}

		@Override
		public void receivePacket(AudioPacket packet, ForgeDirection direction) {
			packet.addReceiver(this);
		}

		@Override
		public String getID() {
			return ""; // Not needed since there is always only this one receiver.
		}

	};

	@Override
	public int getSourceId() {
		return state.getId();
	}

	public int getSourceIdClient() {
		return clientId;
	}

	public void setSourceIdClient(int clientId) {
		this.clientId = clientId;
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
			return PortableTapeDrive.this.inventory;
		}

		@Override
		public ItemStack decrStackSize(int slot, int amount) {
			if(slot != 0) {
				return null;
			}
			if(PortableTapeDrive.this.inventory != null) {
				ItemStack stack;
				if(PortableTapeDrive.this.inventory.stackSize <= amount) {
					stack = PortableTapeDrive.this.inventory;
					PortableTapeDrive.this.inventory = null;
					PortableTapeDrive.this.onInvUpdate();
					return stack;
				} else {
					stack = PortableTapeDrive.this.inventory.splitStack(amount);

					if(PortableTapeDrive.this.inventory.stackSize == 0) {
						PortableTapeDrive.this.inventory = null;
					}

					PortableTapeDrive.this.onInvUpdate();

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
			PortableTapeDrive.this.inventory = null;
			PortableTapeDrive.this.onInvUpdate();
			return stack;
		}

		@Override
		public void setInventorySlotContents(int slot, ItemStack stack) {
			if(slot != 0) {
				return;
			}
			PortableTapeDrive.this.inventory = stack;
			PortableTapeDrive.this.onInvUpdate();
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

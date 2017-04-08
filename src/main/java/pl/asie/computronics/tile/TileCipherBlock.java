package pl.asie.computronics.tile;

import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.OCUtils;
import pl.asie.lib.api.tile.IBundledRedstoneProvider;
import pl.asie.lib.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class TileCipherBlock extends TileEntityPeripheralBase implements IBundledRedstoneProvider, ISidedInventory {
	private byte[] key = new byte[32];
	private byte[] iv = new byte[16];
	private SecretKeySpec skey;
	private Cipher cipher;
	private boolean isLocked = false;

	public TileCipherBlock() {
		super("cipher");
		this.createInventory(6);
		this.registerBundledRedstone(this);
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isLocked() {
		return isLocked;
	}

	@Override
	public boolean canUpdate() {
		return Config.MUST_UPDATE_TILE_ENTITIES;
	}

	public void updateKey() {
		byte[] realKey = new byte[16];
		for(int i = 0; i < 6; i++) {
			ItemStack stack = this.getStackInSlot(i);
			if(stack == null || stack.getItem() == null) {
				key[i * 5] = 0;
				key[i * 5 + 1] = 0;
			} else {
				key[i * 5] = (byte) (Item.getIdFromItem(stack.getItem()) & 255);
				key[i * 5 + 1] = (byte) (Item.getIdFromItem(stack.getItem()) >> 8);
			}

			if(stack == null) {
				key[i * 5 + 2] = 0;
				key[i * 5 + 3] = 0;
				iv[i * 2] = 0;
			} else {
				key[i * 5 + 2] = (byte) ((stack.getItemDamage() & 3) | (stack.stackSize << 2));
				key[i * 5 + 3] = (byte) (stack.getItemDamage() >> 2);
				iv[i * 2] = (byte) (stack.getItemDamage() ^ (stack.getItemDamage() >> 8));
			}

			if(stack == null || stack.getTagCompound() == null) {
				key[i * 5 + 4] = 0;
				iv[i * 2 + 1] = 0;
			} else {
				key[i * 5 + 4] = (byte) stack.getTagCompound().hashCode();
				iv[i * 2 + 1] = (byte) (stack.getTagCompound().hashCode() >> 8);
			}
		}
		for(int i = 0; i < 16; i++) {
			realKey[i] = (byte) (key[i] ^ key[i + 16]);
		}
		skey = new SecretKeySpec(realKey, "AES");
	}

	public String encrypt(String data) throws Exception {
		return encrypt(data.getBytes("UTF8"));
	}

	public String encrypt(byte[] data) throws Exception {
		if(cipher == null) {
			return "";
		}
		if(skey == null) {
			updateKey();
		}
		cipher.init(Cipher.ENCRYPT_MODE, skey, new IvParameterSpec(iv));
		return Base64.encodeBytes(cipher.doFinal(data));
	}

	public String decrypt(String data) throws Exception {
		if(cipher == null) {
			return "";
		}
		if(skey == null) {
			updateKey();
		}
		cipher.init(Cipher.DECRYPT_MODE, skey, new IvParameterSpec(iv));
		return new String(cipher.doFinal(Base64.decode(data)), "UTF8");
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	protected OCUtils.Device deviceInfo() {
		return new OCUtils.Device(
			DeviceClass.Processor,
			"Data encryption device",
			OCUtils.Vendors.Siekierka,
			"Cryptotron 5-X"
		);
	}

	@Callback(doc = "function(message:string):string; Encrypts the specified message", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] encrypt(Context context, Arguments args) throws Exception {
		if(args.count() >= 1) {
			if(args.isByteArray(0)) {
				return new Object[] { encrypt(args.checkByteArray(0)) };
			} else if(args.isString(0)) {
				return new Object[] { encrypt(args.checkString(0)) };
			}
		}
		return null;
	}

	@Callback(doc = "function(message:string):string; Decrypts the specified message", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] decrypt(Context context, Arguments args) throws Exception {
		if(args.count() >= 1 && args.isString(0)) {
			return new Object[] { decrypt(args.checkString(0)) };
		}
		return null;
	}

	@Callback(doc = "function():boolean; Returns whether the block is currently locked", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] isLocked(Context context, Arguments args) throws Exception {
		return new Object[] { isLocked };
	}

	@Callback(doc = "function(locked:boolean); Sets whether the block is currently locked")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] setLocked(Context context, Arguments args) throws Exception {
		if(args.count() == 1 && args.isBoolean(0)) {
			this.isLocked = args.checkBoolean(0);
		}
		return null;
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String[] getMethodNames() {
		return new String[] { "encrypt", "decrypt", "isLocked", "setLocked" };
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
		int method, Object[] arguments) throws LuaException,
		InterruptedException {
		try {
			if(arguments.length == 1 && (arguments[0] instanceof String)) {
				String message = ((String) arguments[0]);
				switch(method) {
					case 0:
						return new Object[] { encrypt(message) };
					case 1:
						return new Object[] { decrypt(message) };
				}
			} else if(arguments.length == 1 && (arguments[0] instanceof Boolean) && method == 3) {
				this.isLocked = (Boolean) arguments[0];
				return null;
			} else if(method == 2) {
				return new Object[] { this.isLocked };
			}
		} catch(Exception e) {
			throw new LuaException(e.getMessage());
		}
		return null;
	}

	private int bundledXORData;

	private int getBundledXORKey() {
		int key = 0;
		for(int i = 0; i < 6; i++) {
			ItemStack stack = this.getStackInSlot(i);
			if(stack == null || stack.getItem() == null) {
				continue;
			}

			int stackId = Item.getIdFromItem(stack.getItem());
			if(stackId < 4096) {
				stackId <<= 4;
			}
			key ^= stackId;
			key ^= stack.getItemDamage();
			key ^= stack.stackSize * (193 * i);
		}
		return key;
	}

	public int redNetSingleOutput = 0;
	public int[] redNetMultiOutput = new int[16];

	@Optional.Method(modid = Mods.MFR)
	private int getRedNetXORKey() {
		int key = 0;
		int amountOfItems = 0;
		for(int i = 0; i < 6; i++) {
			ItemStack stack = this.getStackInSlot(i);
			if(stack == null || stack.getItem() == null) {
				continue;
			}

			amountOfItems++;
			int keyPart = Item.getIdFromItem(stack.getItem());
			if(keyPart < 4096) {
				keyPart <<= 4;
			}
			keyPart ^= stack.getItemDamage();
			keyPart ^= stack.stackSize * (193 * i);
			key ^= (keyPart << 3);
		}
		key ^= (amountOfItems & 1) << 31;
		return key;
	}

	@Optional.Method(modid = Mods.MFR)
	public void updateRedNet(int in) {
		redNetSingleOutput = in ^ getRedNetXORKey();
	}

	@Optional.Method(modid = Mods.MFR)
	public void updateRedNet(int[] in) {
		redNetMultiOutput = in;
		int key = getRedNetXORKey();
		for(int i = 0; i < redNetMultiOutput.length; i++) {
			redNetMultiOutput[i] ^= key;
		}
	}

	private byte[] getBundledOutput() {
		int output = getBundledXORKey() ^ bundledXORData;
		byte[] out = new byte[16];
		for(int i = 0; i < 16; i++) {
			out[i] = (output & 1) > 0 ? (byte) 255 : 0;
			output >>= 1;
		}
		return out;
	}

	public void updateOutputWires() {
		worldObj.notifyBlockChange(xCoord, yCoord, zCoord, this.getBlockType());
	}

	@Override
	public void onSlotUpdate(int slot) {
		updateKey();
		updateOutputWires();
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if(tag.hasKey("cb_l") && Config.CIPHER_CAN_LOCK) {
			isLocked = tag.getBoolean("cb_l");
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if(isLocked) {
			tag.setBoolean("cb_l", true);
		}
	}

	@Override
	public void removeFromNBTForTransfer(NBTTagCompound data) {
		super.removeFromNBTForTransfer(data);
		data.removeTag("cb_l");
	}

	@Override
	public boolean canBundledConnectTo(int arg0, int arg1) {
		int s = Computronics.cipher.relToAbs(5, blockMetadata);
		return (arg0 == s || arg0 == (s ^ 1));
	}

	@Override
	public byte[] getBundledOutput(int side, int arg1) {
		blockMetadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		if(side == (Computronics.cipher.relToAbs(5, blockMetadata))) {
			return getBundledOutput();
		} else {
			return null;
		}
	}

	@Override
	public void onBundledInputChange(int arg0, int arg1, byte[] data) {
		if(data != null && arg0 == Computronics.cipher.relToAbs(4, blockMetadata)) {
			bundledXORData = 0;
			for(int i = 0; i < 16; i++) {
				bundledXORData |= (data[i] != 0) ? (1 << i) : 0;
			}
			updateOutputWires();
		}
	}

	// Security

	@Override
	public ItemStack getStackInSlot(int slot) {
		if(isLocked) {
			return null;
		} else {
			return super.getStackInSlot(slot);
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if(!isLocked) {
			super.setInventorySlotContents(slot, stack);
		}
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side) {
		return !isLocked && slot == side;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, int side) {
		return !isLocked && slot == side;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		if(isLocked) {
			return new int[] {};
		} else {
			return new int[] { side };
		}
	}
}

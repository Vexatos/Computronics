package pl.asie.computronics.tile;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.SimpleComponent;
import pl.asie.computronics.Computronics;
import pl.asie.lib.AsieLibMod;
import pl.asie.lib.block.TileEntityInventory;
import pl.asie.lib.util.Base64;

public class TileCipherBlock extends TileEntityPeripheralInventory {
	private byte[] key = new byte[32];
	private byte[] iv = new byte[16];
	private SecretKeySpec skey;
	private Cipher cipher;
	
	public TileCipherBlock() {
		super("cipher");
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean canUpdate() { return Computronics.MUST_UPDATE_TILE_ENTITIES; }
	
	public void updateKey() {
		byte[] realKey = new byte[16];
		for(int i = 0; i < 6; i++) {
			ItemStack stack = this.getStackInSlot(i);
			if(stack == null || stack.getItem() == null) {
				key[i*5] = 0;
				key[i*5 + 1] = 0;
			} else {
				key[i*5] = (byte)(Item.getIdFromItem(stack.getItem()) & 255);
				key[i*5 + 1] = (byte)(Item.getIdFromItem(stack.getItem()) >> 8);
			}
			
			if(stack == null) {
				key[i*5 + 2] = 0;
				key[i*5 + 3] = 0;
				iv[i * 2] = 0;
			} else {
				key[i*5 + 2] = (byte)((stack.getItemDamage() & 3) | (stack.stackSize << 2));
				key[i*5 + 3] = (byte)(stack.getItemDamage() >> 2);
				iv[i * 2] = (byte)(stack.getItemDamage() ^ (stack.getItemDamage() >> 8));
			}
			
			if(stack == null || stack.getTagCompound() == null) {
				key[i*5 + 4] = 0;
				iv[i * 2 + 1] = 0;
			} else {
				key[i*5 + 4] = (byte)stack.getTagCompound().hashCode();
				iv[i * 2 + 1] = (byte)(stack.getTagCompound().hashCode() >> 8);
			}
		}
		for(int i = 0; i < 16; i++) {
			realKey[i] = (byte)(key[i] ^ key[i + 16]);
		}
		skey = new SecretKeySpec(realKey, "AES");
	}
	
	public String encrypt(String data) throws Exception {
		return encrypt(data.getBytes("UTF8"));
	}
	public String encrypt(byte[] data) throws Exception {
		if(cipher == null) return "";
		if(skey == null) updateKey();
		cipher.init(Cipher.ENCRYPT_MODE, skey, new IvParameterSpec(iv));
		return Base64.encodeBytes(cipher.doFinal(data));
	}
	public String decrypt(String data) throws Exception {
		if(cipher == null) return "";
		if(skey == null) updateKey();
		cipher.init(Cipher.DECRYPT_MODE, skey, new IvParameterSpec(iv));
		return new String(cipher.doFinal(Base64.decode(data)), "UTF8");
	}
	
	@Override
	public int getSizeInventory() {
		return 6;
	}

	@Override
	public void onInventoryUpdate(int slot) {
		updateKey();
	}
	
	@Callback(direct = true)
    @Optional.Method(modid="OpenComputers")
	public Object[] encrypt(Context context, Arguments args) throws Exception {
		if(args.count() >= 1) {
			if(args.isByteArray(0))
				return new Object[]{encrypt(args.checkByteArray(0))};
			else if(args.isString(0))
				return new Object[]{encrypt(args.checkString(0))};
		}
		return null;
	}
	
	@Callback(direct = true)
    @Optional.Method(modid="OpenComputers")
	public Object[] decrypt(Context context, Arguments args) throws Exception {
		if(args.count() >= 1 && args.isString(0)) {
			return new Object[]{decrypt(args.checkString(0))};
		}
		return null;
	}

	@Override
    @Optional.Method(modid="ComputerCraft")
	public String[] getMethodNames() {
		return new String[]{"encrypt", "decrypt"};
	}

	@Override
    @Optional.Method(modid="ComputerCraft")
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
			InterruptedException {
		try {
			if(arguments.length == 1 && (arguments[0] instanceof String)) {
				String message = ((String)arguments[0]);
				switch(method) {
				case 0: return new Object[]{encrypt(message)};
				case 1: return new Object[]{decrypt(message)};
				}
			}
		} catch(Exception e) {
			throw new LuaException(e.getMessage());
		}
		return null;
	}

	@Override
    @Optional.Method(modid="nedocomputers")
	public short busRead(int addr) {
		return 0;
	}

	@Override
    @Optional.Method(modid="nedocomputers")
	public void busWrite(int addr, short data) {
	}
}

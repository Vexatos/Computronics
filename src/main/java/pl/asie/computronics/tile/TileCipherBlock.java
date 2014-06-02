package pl.asie.computronics.tile;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.item.ItemStack;
import openperipheral.api.Arg;
import openperipheral.api.Freeform;
import openperipheral.api.LuaCallable;
import openperipheral.api.LuaType;
import pl.asie.lib.block.TileEntityInventory;
import pl.asie.lib.util.Base64;
import cpw.mods.fml.common.Optional;

@Freeform
@Optional.Interface(iface = "li.cil.li.oc.network.SimpleComponent", modid = "OpenComputers")
public class TileCipherBlock extends TileEntityInventory implements SimpleComponent {
	private byte[] key = new byte[32];
	private byte[] iv = new byte[16];
	private SecretKeySpec skey;
	private Cipher cipher;
	
	public TileCipherBlock() {
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateKey() {
		byte[] realKey = new byte[16];
		for(int i = 0; i < 6; i++) {
			ItemStack stack = this.getStackInSlot(i);
			if(stack == null || stack.getItem() == null) {
				key[i*5] = 0;
				key[i*5 + 1] = 0;
			} else {
				key[i*5] = (byte)(stack.itemID & 255);
				key[i*5 + 1] = (byte)(stack.itemID >> 8);
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
	public String getComponentName() {
		return "cipher";
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
	public Object[] decrypt(Context context, Arguments args) throws Exception {
		if(args.count() >= 1 && args.isString(0)) {
			return new Object[]{decrypt(args.checkString(0))};
		}
		return null;
	}
	
	// OpenP
	
    @LuaCallable(name = "encrypt", description = "Encrypts the specified string.", returnTypes = {LuaType.STRING})
	public String encryptOP (
		@Arg(name = "text", type = LuaType.STRING, description = "String to encrypt") String text
	) {
    	try {
    		return encrypt(text);
    	} catch(Exception e) { return null; }
    }
    
    @LuaCallable(name = "decrypt", description = "Decrypts the specified string.", returnTypes = {LuaType.STRING})
	public String decryptOP(
		@Arg(name = "text", type = LuaType.STRING, description = "String to decrypt") String text
	) {
    	try {
    		return decrypt(text);
    	} catch(Exception e) { return null; }
    }
}

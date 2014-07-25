package pl.asie.computronics.tile;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import mods.immibis.redlogic.api.wiring.IBundledEmitter;
import mods.immibis.redlogic.api.wiring.IBundledUpdatable;
import mods.immibis.redlogic.api.wiring.IBundledWire;
import mods.immibis.redlogic.api.wiring.IConnectable;
import mods.immibis.redlogic.api.wiring.IWire;
import mrtjp.projectred.api.IBundledTile;
import mrtjp.projectred.api.ProjectRedAPI;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
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

@Optional.InterfaceList({
	@Optional.Interface(iface = "mods.immibis.redlogic.api.wiring.IBundledEmitter", modid = "RedLogic"),
	@Optional.Interface(iface = "mods.immibis.redlogic.api.wiring.IBundledUpdatable", modid = "RedLogic"),
	@Optional.Interface(iface = "mods.immibis.redlogic.api.wiring.IConnectable", modid = "RedLogic"),
	@Optional.Interface(iface = "mods.immibis.redlogic.api.wiring.IBundledTile", modid = "ProjRed|Core")
})
public class TileCipherBlock extends TileEntityPeripheralInventory implements IBundledEmitter, IBundledTile, IBundledUpdatable, IConnectable {
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
		updateOutputWires();
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
	
	private short[] _nedo_cipher = new short[16];
	private byte _nedo_status = 0;
	
	@Override
    @Optional.Method(modid="nedocomputers")
	public short busRead(int addr) {
		int a = ((addr & 0xFFFE)) >> 1;
		if(a < 16) return _nedo_cipher[a];
		else if(a == 16) return _nedo_status;
		else return 0;
	}

	@Override
    @Optional.Method(modid="nedocomputers")
	public void busWrite(int addr, short data) {
		if(cipher == null) return;
		
		int a = ((addr & 0xFFFE)) >> 1;
		if(a < 16) _nedo_cipher[a] = data;
		else if(a == 16) {
			if(skey == null) updateKey();
			try {
				if(data == 1) { // Encrypt
					cipher.init(Cipher.ENCRYPT_MODE, skey, new IvParameterSpec(iv));
				} else if(data == 2) { // Decrypt
					cipher.init(Cipher.DECRYPT_MODE, skey, new IvParameterSpec(iv));
				}
				byte[] _data = new byte[32];
				for(int i = 0; i < 16; i++) {
					_data[i*2] = (byte)(_nedo_cipher[i] & 255);
					_data[i*2+1] = (byte)(_nedo_cipher[i] >> 8);
				}
				byte[] _out = cipher.doFinal(_data);
				for(int i = 0; i < 16; i++) {
					_nedo_cipher[i] = (short)(_out[i*2] | (_out[i*2+1] << 8));
				}
				_nedo_status = 0;
			} catch(Exception e) {
				_nedo_status = 1;
			}
		}
	}
	
	private int bundledXORData;
	
	private int getBundledXORKey() {
		int key = 0;
		for(int i = 0; i < 6; i++) {
			ItemStack stack = this.getStackInSlot(i);
			if(stack == null || stack.getItem() == null) continue;
			
			int stackId = Item.getIdFromItem(stack.getItem());
			if(stackId < 4096) stackId <<= 4;
			key ^= stackId;
			key ^= stack.getItemDamage();
			key ^= stack.stackSize * (193 * i);
		}
		return key;
	}
	
	private byte[] getBundledOutput() {
		int output = getBundledXORKey() ^ bundledXORData;
		byte[] out = new byte[16];
		for(int i = 0; i < 16; i++) {
			out[i] = (output & 1) > 0 ? (byte)255 : 0;
			output >>= 1;
		}
		return out;
	}

	@Override // modid = "ProjectRed", but we can use it for more
	public boolean canConnectBundled(int side) {
		blockMetadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		return ((side == Computronics.cipher.relToAbs(4, blockMetadata)))
				|| ((side == Computronics.cipher.relToAbs(5, blockMetadata)));
	}
	
	private void updateOutputWires() {
		worldObj.notifyBlockChange(xCoord, yCoord, zCoord, this.blockType);
	}
	
	private void parseBundledInput(byte[] data) {
		if(data != null) {
			bundledXORData = 0;
			for(int i = 0; i < 16; i++) {
				bundledXORData |= (data[i] != 0) ? (1 << i) : 0;
			}
			updateOutputWires();
		}
	}

	@Override
	@Optional.Method(modid = "RedLogic")
	public void onBundledInputChanged() {
		ForgeDirection input = ForgeDirection.getOrientation(Computronics.cipher.relToAbs(4, blockMetadata));
		TileEntity inputTE = worldObj.getTileEntity(xCoord + input.offsetX, yCoord + input.offsetY, zCoord + input.offsetZ);
		if(inputTE instanceof IBundledWire) {
			IBundledWire inputWire = (IBundledWire)inputTE;
			parseBundledInput(inputWire.getBundledCableStrength(0, Computronics.cipher.relToAbs(4, blockMetadata) ^ 1));
		}
	}
	
	@Override
	@Optional.Method(modid = "RedLogic")
	public byte[] getBundledCableStrength(int blockFace, int toDirection) {
		blockMetadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		if(blockFace == 0 && (toDirection == (Computronics.cipher.relToAbs(5, blockMetadata)))) {
			return getBundledOutput();
		} else return new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	}

	@Override
	@Optional.Method(modid = "RedLogic")
	public boolean connects(IWire wire, int blockFace, int fromDirection) {
		if(blockFace != 0 || !(wire instanceof IBundledWire)) return false;
		return canConnectBundled(fromDirection);
	}

	@Override
	@Optional.Method(modid = "RedLogic")
	public boolean connectsAroundCorner(IWire wire, int blockFace,
			int fromDirection) {
		return false;
	}
	
	@Optional.Method(modid = "ProjRed|Core")
	public void onProjectRedBundledInputChanged() {
		blockMetadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		parseBundledInput(ProjectRedAPI.transmissionAPI.getBundledInput(worldObj, xCoord, yCoord, zCoord,
				Computronics.cipher.relToAbs(4, blockMetadata)));
	}

	@Override
	@Optional.Method(modid = "ProjRed|Core")
	public byte[] getBundledSignal(int side) {
		blockMetadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		if(side == (Computronics.cipher.relToAbs(5, blockMetadata))) {
			return getBundledOutput();
		} else return new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	}
}

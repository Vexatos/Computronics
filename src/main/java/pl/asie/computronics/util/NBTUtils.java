package pl.asie.computronics.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import pl.asie.lib.network.Packet;

import java.io.IOException;

/**
 * @author CovertJaguar, Vexatos
 */
public class NBTUtils {

	public static void writeItemStack(ItemStack stack, Packet packet) throws IOException {
		if(stack == null) {
			packet.writeShort((short) -1);
		} else {
			packet.writeShort((short) Item.getIdFromItem(stack.getItem()));
			packet.writeByte((byte) stack.stackSize);
			packet.writeShort((short) stack.getItemDamage());
			NBTTagCompound nbt = null;
			if(stack.getItem().isDamageable() || stack.getItem().getShareTag()) {
				nbt = stack.stackTagCompound;
			}
			writeNBT(nbt, packet);
		}

	}

	public static void writeNBT(NBTTagCompound nbt, Packet packet) throws IOException {
		if(nbt == null) {
			packet.writeShort((short) -1);
		} else {
			byte[] nbtData = CompressedStreamTools.compress(nbt);
			packet.writeShort((short) nbtData.length);
			packet.writeByteArrayData(nbtData);
		}

	}

	public static ItemStack readItemStack(Packet packet) throws IOException {
		ItemStack stack = null;
		short id = packet.readShort();
		if(id >= 0) {
			byte stackSize = packet.readByte();
			short damage = packet.readShort();
			stack = new ItemStack(Item.getItemById(id), stackSize, damage);
			stack.stackTagCompound = readNBT(packet);
		}

		return stack;
	}

	public static NBTTagCompound readNBT(Packet packet) throws IOException {
		short lenght = packet.readShort();
		if(lenght < 0) {
			return null;
		} else {
			byte[] nbtData = packet.readByteArrayData(lenght);
			return CompressedStreamTools.func_152457_a(nbtData, new NBTSizeTracker(2097152L));
		}
	}
}

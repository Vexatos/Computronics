package pl.asie.lib.network;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import pl.asie.lib.util.WorldUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.Type;

public class Packet implements IMessage {

	private final ByteBuf write;
	private ByteBuf read;
	private final Gson gson = new Gson();

	public Packet() {
		this.write = Unpooled.buffer();
	}

	// Custom read functions

	@Nullable
	public TileEntity readTileEntity() throws IOException {
		World world = null;
		int dimensionId = readInt();
		int x = readInt();
		int y = readInt();
		int z = readInt();
		return WorldUtils.getTileEntity(dimensionId, x, y, z);
	}

	@Nullable
	public TileEntity readTileEntityServer() throws IOException {
		World world = null;
		int dimensionId = readInt();
		int x = readInt();
		int y = readInt();
		int z = readInt();
		return WorldUtils.getTileEntityServer(dimensionId, x, y, z);
	}

	public byte[] readByteArray() throws IOException {
		return readByteArrayData(read.readUnsignedShort());
	}

	public byte[] readByteArrayData(int size) throws IOException {
		byte[] data = new byte[size];
		read.readBytes(data, 0, size);
		return data;
	}

	public Object readJSON(Type t) throws IOException {
		return gson.fromJson(ByteBufUtils.readUTF8String(read), t);
	}

	public Object readJSON(Class<?> t) throws IOException {
		return gson.fromJson(ByteBufUtils.readUTF8String(read), t);
	}

	// Forwarding existing read functions

	public byte readByte() throws IOException {
		return read.readByte();
	}

	public short readShort() throws IOException {
		return read.readShort();
	}

	public byte readSignedByte() throws IOException {
		return read.readByte();
	}

	public short readSignedShort() throws IOException {
		return read.readShort();
	}

	public int readUnsignedByte() throws IOException {
		return read.readUnsignedByte();
	}

	public int readUnsignedShort() throws IOException {
		return read.readUnsignedShort();
	}

	public int readInt() throws IOException {
		return read.readInt();
	}

	public long readLong() throws IOException {
		return read.readLong();
	}

	public double readDouble() throws IOException {
		return read.readDouble();
	}

	public float readFloat() throws IOException {
		return read.readFloat();
	}

	public String readString() throws IOException {
		return ByteBufUtils.readUTF8String(read);
	}

	// Custom write instructions

	public Packet writeTileLocation(TileEntity te) throws IOException, RuntimeException {
		if(te.getWorld() == null) {
			throw new RuntimeException("World does not exist!");
		}
		if(te.isInvalid()) {
			throw new RuntimeException("TileEntity is invalid!");
		}
		write.writeInt(te.getWorld().provider.getDimension());
		BlockPos pos = te.getPos();
		write.writeInt(pos.getX());
		write.writeInt(pos.getY());
		write.writeInt(pos.getZ());
		return this;
	}

	public Packet writeByteArray(byte[] array) throws IOException, RuntimeException {
		if(array.length > 65535) {
			throw new RuntimeException("Invalid array size!");
		}
		write.writeShort(array.length);
		write.writeBytes(array);
		return this;
	}

	public Packet writeByteArrayData(byte[] array) throws IOException {
		write.writeBytes(array);
		return this;
	}

	// Forwarding all write instructions I care about

	public Packet writeByte(byte v) throws IOException {
		write.writeByte(v);
		return this;
	}

	public Packet writeBoolean(boolean v) throws IOException {
		write.writeBoolean(v);
		return this;
	}

	public Packet writeString(String s) throws IOException {
		ByteBufUtils.writeUTF8String(this.write, s);
		return this;
	}

	public Packet writeShort(short v) throws IOException {
		write.writeShort(v);
		return this;
	}

	public Packet writeInt(int v) throws IOException {
		write.writeInt(v);
		return this;
	}

	public Packet writeDouble(double v) throws IOException {
		write.writeDouble(v);
		return this;
	}

	public Packet writeFloat(float v) throws IOException {
		write.writeFloat(v);
		return this;
	}

	public Packet writeLong(long v) throws IOException {
		write.writeLong(v);
		return this;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.read = buf;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBytes(this.write);
	}
}

package pl.asie.computronics.integration.enderio;

import crazypants.enderio.base.transceiver.Channel;
import crazypants.enderio.base.transceiver.ChannelType;
import crazypants.enderio.base.transceiver.PacketAddRemoveChannel;
import crazypants.enderio.base.transceiver.ServerChannelRegister;
import crazypants.enderio.machines.machine.transceiver.TileTransceiver;
import crazypants.enderio.machines.network.PacketHandler;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.integration.DriverSpecificTileEntity;
import pl.asie.computronics.integration.NamedManagedEnvironment;
import pl.asie.computronics.reference.Names;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;

/**
 * @author Vexatos
 */
public class DriverTransceiver {

	private static Object[] parseChannels(Collection<Channel> channelList) {
		LinkedHashMap<Integer, String> channelMap = new LinkedHashMap<Integer, String>();
		if(channelList != null) {
			int i = 1;
			for(Channel channel : channelList) {
				channelMap.put(i++, channel.getName());
			}
			return new Object[] { channelMap };
		}
		return new Object[] {};
	}

	private static Object[] getSendChannels(TileTransceiver tile, Object[] arguments) {
		Collection<Channel> channelList;
		try {
			channelList = tile.getSendChannels(ChannelType.valueOf(((String) arguments[0]).toUpperCase(Locale.ENGLISH)));
		} catch(IllegalArgumentException e) {
			throw new IllegalArgumentException("No valid channel type given");
		}
		return parseChannels(channelList);
	}

	private static Object[] setSendChannel(TileTransceiver tile, Object[] arguments) {
		Collection<Channel> channels;
		boolean shouldAdd = ((Boolean) arguments[2]);
		try {
			channels = shouldAdd
				? ServerChannelRegister.instance.getChannelsForType(ChannelType.valueOf(((String) arguments[0]).toUpperCase(Locale.ENGLISH)))
				: tile.getSendChannels(ChannelType.valueOf(((String) arguments[0]).toUpperCase(Locale.ENGLISH)));

		} catch(IllegalArgumentException e) {
			throw new IllegalArgumentException("No valid channel type given");
		}
		if(channels != null) {
			for(Channel channel : channels) {
				if(shouldAdd && channel.getName().equals(arguments[1].toString())) {
					tile.addSendChanel(channel);
					return new Object[] { true };
				} else if(!shouldAdd && channel.getName().equals(arguments[1].toString())) {
					tile.removeSendChanel(channel);
					return new Object[] { true };
				}
			}
		}
		return new Object[] { false };
	}

	private static Object[] getReceiveChannels(TileTransceiver tile, Object[] arguments) {
		Collection<Channel> channelList;
		try {
			channelList = tile.getRecieveChannels(ChannelType.valueOf(((String) arguments[0]).toUpperCase(Locale.ENGLISH)));
		} catch(IllegalArgumentException e) {
			throw new IllegalArgumentException("No valid channel type given");
		}
		return parseChannels(channelList);
	}

	private static Object[] setReceiveChannel(TileTransceiver tile, Object[] arguments) {
		Collection<Channel> channels;
		boolean shouldAdd = ((Boolean) arguments[2]);
		try {
			channels = shouldAdd
				? ServerChannelRegister.instance.getChannelsForType(ChannelType.valueOf(((String) arguments[0]).toUpperCase(Locale.ENGLISH)))
				: tile.getRecieveChannels(ChannelType.valueOf(((String) arguments[0]).toUpperCase(Locale.ENGLISH)));

		} catch(IllegalArgumentException e) {
			throw new IllegalArgumentException("No valid channel type given");
		}
		if(channels != null) {
			for(Channel channel : channels) {
				if(shouldAdd && channel.getName().equals(arguments[1].toString())) {
					tile.addRecieveChanel(channel);
					return new Object[] { true };
				} else if(!shouldAdd && channel.getName().equals(arguments[1].toString())) {
					tile.removeRecieveChanel(channel);
					return new Object[] { true };
				}
			}
		}
		return new Object[] { false };
	}

	private static Object[] addChannel(TileTransceiver tile, Object[] arguments) {
		Collection<Channel> channels;
		try {
			channels = ServerChannelRegister.instance.getChannelsForType(ChannelType.valueOf(((String) arguments[0]).toUpperCase(Locale.ENGLISH)));
		} catch(IllegalArgumentException e) {
			throw new IllegalArgumentException("No valid channel type given");
		}
		if(channels != null) {
			for(Channel channel : channels) {
				if(channel.getName().equals(arguments[1].toString())) {
					return new Object[] { false };
				}
			}
			Channel channel = new Channel(((String) arguments[1]), ChannelType.valueOf(((String) arguments[0]).toUpperCase(Locale.ENGLISH)));
			ServerChannelRegister.instance.addChannel(channel);
			PacketHandler.INSTANCE.sendToAll(new PacketAddRemoveChannel(channel, true));
			return new Object[] { true };
		}
		return new Object[] { false };
	}

	private static Object[] removeChannel(TileTransceiver tile, Object[] arguments) {
		Collection<Channel> channels;
		try {
			channels = ServerChannelRegister.instance.getChannelsForType(ChannelType.valueOf(((String) arguments[0]).toUpperCase(Locale.ENGLISH)));
		} catch(IllegalArgumentException e) {
			throw new IllegalArgumentException("No valid channel type given");
		}
		if(channels != null) {
			for(Channel channel : channels) {
				if(channel.getName().equals(arguments[1].toString())) {
					ServerChannelRegister.instance.removeChannel(channel);
					PacketHandler.INSTANCE.sendToAll(new PacketAddRemoveChannel(channel, false));
					return new Object[] { true };
				}
			}
		}
		return new Object[] { false };
	}

	private static Object[] isChannelExisting(TileTransceiver tile, Object[] arguments) {
		Collection<Channel> channels;
		try {
			channels = ServerChannelRegister.instance.getChannelsForType(ChannelType.valueOf(((String) arguments[0]).toUpperCase(Locale.ENGLISH)));
		} catch(IllegalArgumentException e) {
			throw new IllegalArgumentException("No valid channel type given");
		}
		if(channels != null) {
			for(Channel channel : channels) {
				if(channel.getName().equals(arguments[1].toString())) {
					return new Object[] { true };
				}
			}
		}
		return new Object[] { false };
	}

	private static Object[] getChannels(TileTransceiver tile, Object[] arguments) {
		Collection<Channel> channelList;
		try {
			channelList = ServerChannelRegister.instance.getChannelsForType(ChannelType.valueOf(((String) arguments[0]).toUpperCase(Locale.ENGLISH)));
		} catch(IllegalArgumentException e) {
			throw new IllegalArgumentException("No valid channel type given");
		}
		return parseChannels(channelList);
	}

	private static Object[] isSendChannel(TileTransceiver tile, Object[] arguments) {
		Collection<Channel> channels;
		try {
			channels = tile.getSendChannels(ChannelType.valueOf(((String) arguments[0]).toUpperCase(Locale.ENGLISH)));
		} catch(IllegalArgumentException e) {
			throw new IllegalArgumentException("No valid channel type given");
		}
		if(channels != null) {
			for(Channel channel : channels) {
				if(channel.getName().equals(arguments[1].toString())) {
					return new Object[] { true };
				}
			}
		}
		return new Object[] { false };
	}

	private static Object[] isReceiveChannel(TileTransceiver tile, Object[] arguments) {
		Collection<Channel> channels;
		try {
			channels = tile.getRecieveChannels(ChannelType.valueOf(((String) arguments[0]).toUpperCase(Locale.ENGLISH)));
		} catch(IllegalArgumentException e) {
			throw new IllegalArgumentException("No valid channel type given");
		}
		if(channels != null) {
			for(Channel channel : channels) {
				if(channel.getName().equals(arguments[1].toString())) {
					return new Object[] { true };
				}
			}
		}
		return new Object[] { false };
	}

	private static LinkedHashMap<Object, Object> types;

	private static Object[] types() {
		if(types == null) {
			types = new LinkedHashMap<Object, Object>();
			int i = 1;
			for(ChannelType type : ChannelType.values()) {
				final String name = type.name().toLowerCase(Locale.ENGLISH);
				types.put(name, i);
				types.put(i++, name);
			}
		}
		return new Object[] { types };
	}

	public static class OCDriver extends DriverSpecificTileEntity<TileTransceiver> {

		public static class InternalManagedEnvironment extends NamedManagedEnvironment<TileTransceiver> {

			public InternalManagedEnvironment(TileTransceiver tile) {
				super(tile, Names.EnderIO_Transceiver);
			}

			@Override
			public int priority() {
				return 4;
			}

			@Callback(doc = "function(channeltype:string):table; Returns a table of channels the transceiver sends to")
			public Object[] getSendChannels(Context c, Arguments a) {
				a.checkString(0);
				return DriverTransceiver.getSendChannels(tile, a.toArray());
			}

			@Callback(doc = "function(channeltype:string,name:string,shouldSend:boolean):boolean; Sets whether the transceiver should send to the specified channel")
			public Object[] setSendChannel(Context c, Arguments a) {
				a.checkString(0);
				a.checkString(1);
				a.checkBoolean(2);
				return DriverTransceiver.setSendChannel(tile, a.toArray());
			}

			@Callback(doc = "function(channeltype:string):table; Returns a table of channels the transceiver receives from")
			public Object[] getReceiveChannels(Context c, Arguments a) {
				a.checkString(0);
				return DriverTransceiver.getReceiveChannels(tile, a.toArray());
			}

			@Callback(doc = "function(channeltype:string,name:string,shouldReceive:boolean):boolean; Sets whether the transceiver should receive from the specified channel")
			public Object[] setReceiveChannel(Context c, Arguments a) {
				a.checkString(0);
				a.checkString(1);
				a.checkBoolean(2);
				return DriverTransceiver.setReceiveChannel(tile, a.toArray());
			}

			@Callback(doc = "function(channeltype:string,name:string):boolean; Adds a channel to the public channel list if possible")
			public Object[] addChannel(Context c, Arguments a) {
				a.checkString(0);
				a.checkString(1);
				return DriverTransceiver.addChannel(tile, a.toArray());
			}

			@Callback(doc = "function(channeltype:string,name:string):boolean; Removes a channel from the public channel list if possible")
			public Object[] removeChannel(Context c, Arguments a) {
				a.checkString(0);
				a.checkString(1);
				return DriverTransceiver.removeChannel(tile, a.toArray());
			}

			@Callback(doc = "function(channeltype:string,name:string):boolean; Returns whether the specified channel exists")
			public Object[] isChannelExisting(Context c, Arguments a) {
				a.checkString(0);
				a.checkString(1);
				return DriverTransceiver.isChannelExisting(tile, a.toArray());
			}

			@Callback(doc = "function(channeltype:string):table; Returns a table containing every public channel")
			public Object[] getChannels(Context c, Arguments a) {
				a.checkString(0);
				return DriverTransceiver.getChannels(tile, a.toArray());
			}

			@Callback(doc = "function(channeltype:string,name:string):boolean; Returns whether the transceiver sends to the specified channel")
			public Object[] isSendChannel(Context c, Arguments a) {
				a.checkString(0);
				a.checkString(1);
				return DriverTransceiver.isSendChannel(tile, a.toArray());
			}

			@Callback(doc = "function(channeltype:string,name:string):boolean; Returns whether the transceiver receives from the specified channel")
			public Object[] isReceiveChannel(Context c, Arguments a) {
				a.checkString(0);
				a.checkString(1);
				return DriverTransceiver.isReceiveChannel(tile, a.toArray());
			}

			@Callback(doc = "This is a bidirectional table containing every available channel type", getter = true)
			public Object[] channel_types(Context c, Arguments a) {
				return DriverTransceiver.types();
			}
		}

		public OCDriver() {
			super(TileTransceiver.class);
		}

		@Override
		public InternalManagedEnvironment createEnvironment(World world, BlockPos pos, EnumFacing side, TileTransceiver tile) {
			return new InternalManagedEnvironment(tile);
		}
	}

	public static class CCDriver extends CCMultiPeripheral<TileTransceiver> {

		public CCDriver() {
		}

		public CCDriver(TileTransceiver tile, World world, BlockPos pos) {
			super(tile, Names.EnderIO_Transceiver, world, pos);
		}

		@Override
		public int peripheralPriority() {
			return 4;
		}

		@Override
		public CCMultiPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
			TileEntity te = world.getTileEntity(pos);
			if(te != null && te instanceof TileTransceiver) {
				return new CCDriver((TileTransceiver) te, world, pos);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getSendChannels", "setSendChannel", "getReceiveChannels", "setReceiveChannel", "addChannel", "removeChannel", "isChannelExisting",
				"getChannels", "isSendChannel", "isReceiveChannel", "getChannelTypes" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			if(method != 10 && arguments.length < 1 || !(arguments[0] instanceof String)) {
				throw new LuaException("first argument needs to be a string");
			} else if(method == 10) {
				return DriverTransceiver.types();
			}
			try {
				switch(method) {
					case 0: {
						return DriverTransceiver.getSendChannels(tile, arguments);
					}
					case 1: {
						if(arguments.length < 2 || !(arguments[1] instanceof String)) {
							throw new LuaException("second argument needs to be a string");
						}
						if(arguments.length < 3 || !(arguments[2] instanceof Boolean)) {
							throw new LuaException("third argument needs to be a boolean");
						}
						return DriverTransceiver.setSendChannel(tile, arguments);
					}
					case 2: {
						return DriverTransceiver.getReceiveChannels(tile, arguments);
					}
					case 3: {
						if(arguments.length < 2 || !(arguments[1] instanceof String)) {
							throw new LuaException("second argument needs to be a string");
						}
						if(arguments.length < 3 || !(arguments[2] instanceof Boolean)) {
							throw new LuaException("third argument needs to be a boolean");
						}
						return DriverTransceiver.setReceiveChannel(tile, arguments);
					}
					case 4: {
						if(arguments.length < 2 || !(arguments[1] instanceof String)) {
							throw new LuaException("second argument needs to be a string");
						}
						return DriverTransceiver.addChannel(tile, arguments);
					}
					case 5: {
						if(arguments.length < 2 || !(arguments[1] instanceof String)) {
							throw new LuaException("second argument needs to be a string");
						}
						return DriverTransceiver.removeChannel(tile, arguments);
					}
					case 6: {
						if(arguments.length < 2 || !(arguments[1] instanceof String)) {
							throw new LuaException("second argument needs to be a string");
						}
						return DriverTransceiver.isChannelExisting(tile, arguments);
					}
					case 7: {
						return DriverTransceiver.getChannels(tile, arguments);
					}
					case 8: {
						if(arguments.length < 2 || !(arguments[1] instanceof String)) {
							throw new LuaException("second argument needs to be a string");
						}
						return DriverTransceiver.isSendChannel(tile, arguments);
					}
					case 9: {
						if(arguments.length < 2 || !(arguments[1] instanceof String)) {
							throw new LuaException("second argument needs to be a string");
						}
						return DriverTransceiver.isReceiveChannel(tile, arguments);
					}
				}
			} catch(IllegalArgumentException e) {
				throw new LuaException(e.getMessage());
			}
			return new Object[] {};
		}
	}
}

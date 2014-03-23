package openperipheral.api;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.FMLLog;

public class OpenPeripheralAPI {

	//public static final Logger logger;

	static {
		//logger = Logger.getLogger("OpenPeripheral API");
		//logger.setParent(FMLLog.getLogger());
	}

	public static boolean register(IPeripheralAdapter adapter) {
		return register(IPeripheralAdapter.class, adapter, "openperipheral.adapter.AdapterManager", "addPeripheralAdapter");
	}

	public static boolean register(IObjectAdapter adapter) {
		return register(IPeripheralAdapter.class, adapter, "openperipheral.adapter.AdapterManager", "addObjectAdapter");
	}

	public static boolean createAdapter(Class<? extends TileEntity> cls) {
		return register(Class.class, cls, "openperipheral.adapter.AdapterManager", "addInlinePeripheralAdapter");
	}

	public static boolean register(ITypeConverter converter) {
		return register(ITypeConverter.class, converter, "openperipheral.TypeConversionRegistry", "registerTypeConverter");
	}

	public static boolean register(IIntegrationModule module) {
		return register(IIntegrationModule.class, module, "openperipheral.IntegrationModuleRegistry", "registerModule");
	}

	private static boolean register(Class<?> type, Object obj, String klazzName, String methodName) {
		try {
			Class<?> klazz = Class.forName(klazzName);
			if (klazz != null) {
				Method method = klazz.getMethod(methodName, new Class[] { type });
				method.invoke(null, obj);
				return true;
			}
		} catch (Throwable t) {
			//logger.log(Level.WARNING, String.format("Exception while calling method '%s'", methodName), t);
		}

		return false;
	}

	public static IMultiReturn wrap(final Object... args) {
		return new IMultiReturn() {
			@Override
			public Object[] getObjects() {
				return args;
			}
		};
	}
}

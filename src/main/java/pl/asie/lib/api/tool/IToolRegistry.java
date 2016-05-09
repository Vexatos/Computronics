package pl.asie.lib.api.tool;

/**
 * Allows you to register instances of
 * {@link pl.asie.lib.api.tool.IToolProvider}.
 * <p/>
 * You need to send a method registering your instances of
 * {@link pl.asie.lib.api.tool.IToolProvider}
 * using {@link net.minecraftforge.fml.common.event.FMLInterModComms#sendMessage}.
 * <p/>
 * Note that method sent must have the following
 * signature:
 * <pre>
 *     public static void f({@link IToolProvider})
 * </pre>
 * Example:
 * <pre>
 *     FMLInterModComms.sendMessage("asielib", "addToolProvider",
 *     "com.example.examplemod.tool.ToolProviders.register");
 * </pre>
 */
public interface IToolRegistry extends Iterable<IToolProvider> {

	/**
	 * Registers a new {@link pl.asie.lib.api.tool.IToolProvider}.
	 */
	public void registerToolProvider(IToolProvider provider);
}

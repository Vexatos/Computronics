package pl.asie.computronics.api.multiperipheral;

/**
 * Allows you to register instances of
 * {@link pl.asie.computronics.api.multiperipheral.IMultiPeripheralProvider}.
 * <p/>
 * You need to send a method registering your instances of
 * {@link pl.asie.computronics.api.multiperipheral.IMultiPeripheralProvider}
 * using {@link cpw.mods.fml.common.event.FMLInterModComms#sendMessage}.
 * <p/>
 * Note that method sent must have the following
 * signature:
 * <pre>
 *     public static void f({@link IMultiPeripheralRegistry})
 * </pre>
 * Example:
 * <pre>
 *     FMLInterModComms.sendMessage("Computronics", "addmultiperipherals",
 *     "com.example.examplemod.cc.MultiPeripherals.register");
 * </pre>
 */
public interface IMultiPeripheralRegistry {

	/**
	 * Registers a new {@link pl.asie.computronics.api.multiperipheral.IMultiPeripheralProvider}.
	 * <p/>
	 * Needs to be called between Computronics' preInit and init phase.
	 */
	public void registerPeripheralProvider(IMultiPeripheralProvider provider);
}

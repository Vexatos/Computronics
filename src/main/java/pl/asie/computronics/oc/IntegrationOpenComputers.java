package pl.asie.computronics.oc;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.registry.GameRegistry;
import li.cil.oc.api.Driver;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Logger;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.integration.appeng.DriverSpatialIOPort;
import pl.asie.computronics.integration.betterstorage.DriverCrateStorageNew;
import pl.asie.computronics.integration.betterstorage.DriverCrateStorageOld;
import pl.asie.computronics.integration.enderio.DriverAbstractMachine;
import pl.asie.computronics.integration.enderio.DriverCapacitorBank;
import pl.asie.computronics.integration.enderio.DriverHasExperience;
import pl.asie.computronics.integration.enderio.DriverIOConfigurable;
import pl.asie.computronics.integration.enderio.DriverRedstoneControllable;
import pl.asie.computronics.integration.enderio.DriverTransceiver;
import pl.asie.computronics.integration.factorization.DriverChargeConductor;
import pl.asie.computronics.integration.fsp.DriverSteamTransporter;
import pl.asie.computronics.integration.gregtech.DriverBaseMetaTileEntity;
import pl.asie.computronics.integration.gregtech.DriverBatteryBuffer;
import pl.asie.computronics.integration.gregtech.DriverDeviceInformation;
import pl.asie.computronics.integration.gregtech.DriverDigitalChest;
import pl.asie.computronics.integration.gregtech.DriverMachine;
import pl.asie.computronics.integration.mfr.DriverDeepStorageUnit;
import pl.asie.computronics.integration.railcraft.DriverElectricGrid;
import pl.asie.computronics.integration.railcraft.DriverLimiterTrack;
import pl.asie.computronics.integration.railcraft.DriverLocomotiveTrack;
import pl.asie.computronics.integration.railcraft.DriverRoutingDetector;
import pl.asie.computronics.integration.railcraft.DriverRoutingSwitch;
import pl.asie.computronics.integration.railcraft.DriverRoutingTrack;
import pl.asie.computronics.integration.redlogic.DriverLamp;
import pl.asie.computronics.item.ItemOpenComputers;
import pl.asie.computronics.reference.Compat;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;

import static pl.asie.computronics.Computronics.camera;
import static pl.asie.computronics.Computronics.chatBox;
import static pl.asie.computronics.Computronics.radar;

/**
 * @author Vexatos
 */
public class IntegrationOpenComputers {

	private final Configuration config;
	private final Compat compat;
	private final Computronics computronics;
	private final Logger log;

	public static ItemOpenComputers itemRobotUpgrade;

	public IntegrationOpenComputers(Computronics computronics) {
		this.computronics = computronics;
		this.config = computronics.config.config;
		this.compat = computronics.compat;
		this.log = Computronics.log;
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public void preInit() {
		if(computronics.isEnabled("ocRobotUpgrades", true)) {
			itemRobotUpgrade = new ItemOpenComputers();
			GameRegistry.registerItem(itemRobotUpgrade, "computronics.robotUpgrade");
			Driver.add(itemRobotUpgrade);
		}

		// OpenComputers needs a hook in updateEntity in order to proprly register peripherals.
		// Fixes Iron Note Block, among others.
		// To ensure less TE ticks for those who don't use OC, we keep this tidbit around.
		Config.MUST_UPDATE_TILE_ENTITIES = true;
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public void init() {
		if(Loader.isModLoaded(Mods.RedLogic)) {
			if(compat.isCompatEnabled(Compat.RedLogic_Lamps)) {
				Driver.add(new DriverLamp.OCDriver());
			}
		}
		if(Loader.isModLoaded(Mods.BetterStorage)) {
			if(compat.isCompatEnabled(Compat.BetterStorage_Crates)) {
				try {
					Class.forName("net.mcft.copy.betterstorage.api.ICrateStorage");
					log.info("Using old (pre-0.10) BetterStorage crate API!");
					Driver.add(new DriverCrateStorageOld());
				} catch(Exception e) {
					//NO-OP
				}

				try {
					Class.forName("net.mcft.copy.betterstorage.api.crate.ICrateStorage");
					log.info("Using new (0.10+) BetterStorage crate API!");
					Driver.add(new DriverCrateStorageNew());
				} catch(Exception e) {
					//NO-OP
				}
			}
		}
		if(Loader.isModLoaded(Mods.MFR) || Loader.isModLoaded(Mods.JABBA)) {
			if(compat.isCompatEnabled(Compat.MFR_DSU)) {
				Driver.add(new DriverDeepStorageUnit.OCDriver());
			}
		}
		if(Loader.isModLoaded(Mods.FSP)) {
			if(compat.isCompatEnabled(Compat.FSP_Steam_Transporter)) {
				Driver.add(new DriverSteamTransporter.OCDriver());
			}
		}
		if(Loader.isModLoaded(Mods.Factorization)) {
			if(compat.isCompatEnabled(Compat.FZ_ChargePeripheral)) {
				Driver.add(new DriverChargeConductor.OCDriver());
			}
		}
		if(Loader.isModLoaded(Mods.Railcraft)) {
			if(compat.isCompatEnabled(Compat.Railcraft_Routing)) {
				Driver.add(new DriverRoutingTrack.OCDriver());
				Driver.add(new DriverRoutingDetector.OCDriver());
				Driver.add(new DriverRoutingSwitch.OCDriver());
				Driver.add(new DriverElectricGrid.OCDriver());
				Driver.add(new DriverLimiterTrack.OCDriver());
				Driver.add(new DriverLocomotiveTrack.OCDriver());
			}
		}
		if(Loader.isModLoaded(Mods.GregTech)) {
			if(compat.isCompatEnabled(Compat.GregTech_Machines)) {
				Driver.add(new DriverBaseMetaTileEntity());
				Driver.add(new DriverDeviceInformation());
				Driver.add(new DriverMachine());
				Driver.add(new DriverBatteryBuffer());
			}
			if(compat.isCompatEnabled(Compat.GregTech_DigitalChests)) {
				Driver.add(new DriverDigitalChest());
			}
		}
		if(Loader.isModLoaded(Mods.AE2)) {
			if(compat.isCompatEnabled(Compat.AE2_SpatialIO)) {
				Driver.add(new DriverSpatialIOPort.OCDriver());
			}
		}
		if(Loader.isModLoaded(Mods.EnderIO)) {
			if(compat.isCompatEnabled(Compat.EnderIO)) {
				Driver.add(new DriverRedstoneControllable.OCDriver());
				Driver.add(new DriverIOConfigurable.OCDriver());
				Driver.add(new DriverHasExperience.OCDriver());
				Driver.add(new DriverAbstractMachine.OCDriver());
				Driver.add(new DriverCapacitorBank.OCDriver());
				Driver.add(new DriverTransceiver.OCDriver());
			}
		}
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public void postInit() {
		if(computronics.isEnabled("ocRobotUpgrades", true)) {
			Block[] b = { camera, chatBox, radar };
			try {
				for(int i = 0; i < b.length; i++) {
					Block t = b[i];
					GameRegistry.addShapedRecipe(new ItemStack(itemRobotUpgrade, 1, i), "mcm", 'c', new ItemStack(t, 1, 0), 'm', li.cil.oc.api.Items.get("chip2").createItemStack(1));
					GameRegistry.addShapedRecipe(new ItemStack(itemRobotUpgrade, 1, i), "m", "c", "m", 'c', new ItemStack(t, 1, 0), 'm', li.cil.oc.api.Items.get("chip2").createItemStack(1));
				}
				GameRegistry.addShapedRecipe(new ItemStack(itemRobotUpgrade, 1, 3), "mf", " b", 'm', li.cil.oc.api.Items.get("chip2").createItemStack(1), 'f', Items.firework_charge, 'b', li.cil.oc.api.Items.get("card").createItemStack(1));
			} catch(Exception e) {
				log.error("Could not create robot upgrade recipes! You are most likely using OpenComputers 1.2 - please upgrade to 1.3.0+!");
				e.printStackTrace();
			}
		}
	}
}
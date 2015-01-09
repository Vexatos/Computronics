package pl.asie.computronics.cc;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModAPIManager;
import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.ComputerCraftAPI;
import net.minecraftforge.common.config.Configuration;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.cc.multiperipheral.MultiPeripheralProvider;
import pl.asie.computronics.integration.appeng.DriverSpatialIOPort;
import pl.asie.computronics.integration.buildcraft.DriverHeatable;
import pl.asie.computronics.integration.cofh.DriverEnergyHandler;
import pl.asie.computronics.integration.enderio.DriverAbstractMachine;
import pl.asie.computronics.integration.enderio.DriverAbstractPoweredMachine;
import pl.asie.computronics.integration.enderio.DriverCapacitorBank;
import pl.asie.computronics.integration.enderio.DriverCapacitorBankOld;
import pl.asie.computronics.integration.enderio.DriverHasExperience;
import pl.asie.computronics.integration.enderio.DriverIOConfigurable;
import pl.asie.computronics.integration.enderio.DriverPowerMonitor;
import pl.asie.computronics.integration.enderio.DriverPowerStorage;
import pl.asie.computronics.integration.enderio.DriverRedstoneControllable;
import pl.asie.computronics.integration.enderio.DriverTransceiver;
import pl.asie.computronics.integration.factorization.DriverChargeConductor;
import pl.asie.computronics.integration.fsp.DriverSteamTransporter;
import pl.asie.computronics.integration.mfr.DriverDeepStorageUnit;
import pl.asie.computronics.integration.openblocks.DriverBuildingGuide;
import pl.asie.computronics.integration.railcraft.DriverBoilerFirebox;
import pl.asie.computronics.integration.railcraft.DriverElectricGrid;
import pl.asie.computronics.integration.railcraft.DriverRoutingDetector;
import pl.asie.computronics.integration.railcraft.DriverRoutingSwitch;
import pl.asie.computronics.integration.railcraft.DriverSteamTurbine;
import pl.asie.computronics.integration.railcraft.track.DriverLauncherTrack;
import pl.asie.computronics.integration.railcraft.track.DriverLimiterTrack;
import pl.asie.computronics.integration.railcraft.track.DriverLocomotiveTrack;
import pl.asie.computronics.integration.railcraft.track.DriverPoweredTrack;
import pl.asie.computronics.integration.railcraft.track.DriverPrimingTrack;
import pl.asie.computronics.integration.railcraft.track.DriverRoutingTrack;
import pl.asie.computronics.integration.redlogic.CCBundledRedstoneProviderRedLogic;
import pl.asie.computronics.integration.redlogic.DriverLamp;
import pl.asie.computronics.reference.Compat;
import pl.asie.computronics.reference.Mods;

import static pl.asie.computronics.Computronics.itemTape;
import static pl.asie.computronics.Computronics.peripheralRegistry;
import static pl.asie.computronics.Computronics.registerMultiPeripheralProvider;

/**
 * @author Vexatos
 */
public class IntegrationComputerCraft {

	private final Configuration config;
	private final Compat compat;
	private final Computronics computronics;

	public IntegrationComputerCraft(Computronics computronics) {
		this.computronics = computronics;
		this.config = computronics.config.config;
		this.compat = computronics.compat;
	}

	@Optional.Method(modid = Mods.ComputerCraft)
	public void init() {
		if(Loader.isModLoaded(Mods.RedLogic)) {
			if(compat.isCompatEnabled(Compat.RedLogic_Lamps)) {
				registerMultiPeripheralProvider(new DriverLamp.CCDriver());
			}
			if(compat.isCompatEnabled(Compat.Bundled_Redstone)) {
				ComputerCraftAPI.registerBundledRedstoneProvider(new CCBundledRedstoneProviderRedLogic());
			}
		}
		if(Loader.isModLoaded(Mods.MFR) || Loader.isModLoaded(Mods.JABBA)) {
			if(compat.isCompatEnabled(Compat.MFR_DSU)) {
				registerMultiPeripheralProvider(new DriverDeepStorageUnit.CCDriver());
			}
		}
		if(Loader.isModLoaded(Mods.FSP)) {
			if(compat.isCompatEnabled(Compat.FSP_Steam_Transporter)) {
				registerMultiPeripheralProvider(new DriverSteamTransporter.CCDriver());
			}
		}
		if(Loader.isModLoaded(Mods.Factorization)) {
			if(compat.isCompatEnabled(Compat.FZ_ChargePeripheral)) {
				registerMultiPeripheralProvider(new DriverChargeConductor.CCDriver());
			}
		}

		if(Loader.isModLoaded(Mods.Railcraft)) {
			if(compat.isCompatEnabled(Compat.Railcraft_Routing)) {
				registerMultiPeripheralProvider(new DriverPoweredTrack.CCDriver());
				registerMultiPeripheralProvider(new DriverRoutingTrack.CCDriver());
				registerMultiPeripheralProvider(new DriverRoutingDetector.CCDriver());
				registerMultiPeripheralProvider(new DriverRoutingSwitch.CCDriver());
				registerMultiPeripheralProvider(new DriverElectricGrid.CCDriver());
				registerMultiPeripheralProvider(new DriverLimiterTrack.CCDriver());
				registerMultiPeripheralProvider(new DriverLocomotiveTrack.CCDriver());
				registerMultiPeripheralProvider(new DriverLauncherTrack.CCDriver());
				registerMultiPeripheralProvider(new DriverPrimingTrack.CCDriver());
				registerMultiPeripheralProvider(new DriverBoilerFirebox.CCDriver());
				registerMultiPeripheralProvider(new DriverSteamTurbine.CCDriver());
			}
		}

		if(Loader.isModLoaded(Mods.AE2)) {
			if(compat.isCompatEnabled(Compat.AE2_SpatialIO)) {
				registerMultiPeripheralProvider(new DriverSpatialIOPort.CCDriver());
			}
		}

		if(Loader.isModLoaded(Mods.EnderIO)) {
			if(compat.isCompatEnabled(Compat.EnderIO)) {
				registerMultiPeripheralProvider(new DriverEnergyHandler.CCDriver());
				registerMultiPeripheralProvider(new DriverRedstoneControllable.CCDriver());
				registerMultiPeripheralProvider(new DriverIOConfigurable.CCDriver());
				registerMultiPeripheralProvider(new DriverHasExperience.CCDriver());
				registerMultiPeripheralProvider(new DriverPowerStorage.CCDriver());
				registerMultiPeripheralProvider(new DriverAbstractMachine.CCDriver());
				registerMultiPeripheralProvider(new DriverAbstractPoweredMachine.CCDriver());
				registerMultiPeripheralProvider(new DriverPowerMonitor.CCDriver());
				registerMultiPeripheralProvider(new DriverCapacitorBank.CCDriver());
				registerMultiPeripheralProvider(new DriverCapacitorBankOld.CCDriver());
				registerMultiPeripheralProvider(new DriverTransceiver.CCDriver());
			}
		} else if(ModAPIManager.INSTANCE.hasAPI(Mods.API.CoFHAPI_Energy)
			&& compat.isCompatEnabled(Compat.RedstoneFlux)) {
			registerMultiPeripheralProvider(new DriverEnergyHandler.CCDriver());
		}

		if(Loader.isModLoaded(Mods.OpenBlocks)) {
			if(compat.isCompatEnabled(Compat.OpenBlocks)) {
				registerMultiPeripheralProvider(new DriverBuildingGuide.CCDriver());
			}
		}

		if(Mods.API.hasVersion(Mods.API.BuildCraftTiles, "[1.1,)")) {
			if(compat.isCompatEnabled(Compat.BuildCraft_Drivers)) {
				registerMultiPeripheralProvider(new DriverHeatable.CCDriver());
			}
		}

		registerMultiPeripheralProvider(new CCPeripheralProvider());

		ComputerCraftAPI.registerPeripheralProvider(new MultiPeripheralProvider(peripheralRegistry.peripheralProviders));

		if(itemTape != null) {
			ComputerCraftAPI.registerMediaProvider(itemTape);
		}

		if(computronics.isEnabled("ccTurtleUpgrades", true)) {
			ComputerCraftAPI.registerTurtleUpgrade(
				new SpeakingTurtleUpgrade(config.get("turtleUpgradeIDs", "speaking", 190).getInt()));
			ComputerCraftAPI.registerTurtleUpgrade(
				new RadarTurtleUpgrade(config.get("turtleUpgradeIDs", "radar", 191).getInt()));
			ComputerCraftAPI.registerTurtleUpgrade(
				new MusicalTurtleUpgrade(config.get("turtleUpgradeIDs", "musical", 192).getInt()));
			ComputerCraftAPI.registerTurtleUpgrade(
				new ParticleTurtleUpgrade(config.get("turtleUpgradeIDs", "particle", 193).getInt()));
		}
	}
}

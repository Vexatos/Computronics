package pl.asie.computronics.cc;

import dan200.computercraft.api.ComputerCraftAPI;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.cc.multiperipheral.MultiPeripheralProvider;
import pl.asie.computronics.integration.flamingo.DriverFlamingo;
import pl.asie.computronics.reference.Compat;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileTapeDrive;

import static pl.asie.computronics.Computronics.peripheralRegistry;
import static pl.asie.computronics.Computronics.registerMultiPeripheralProvider;

/**
 * @author Vexatos
 */
public class IntegrationComputerCraft {

	private final Configuration config;
	private final Compat compat;
	private final Computronics computronics;
	private MultiPeripheralProvider multiPeripheralProvider;

	public IntegrationComputerCraft(Computronics computronics) {
		this.computronics = computronics;
		this.config = computronics.config.config;
		this.compat = computronics.compat;
	}

	@Optional.Method(modid = Mods.ComputerCraft)
	public void init() {
		/*if(Mods.isLoaded(Mods.RedLogic)) {
			if(compat.isCompatEnabled(Compat.RedLogic_Lamps)) {
				registerMultiPeripheralProvider(new DriverLamp.CCDriver());
			}
		}
		if(Mods.isClassLoaded("powercrystals.minefactoryreloaded.api.IDeepStorageUnit")) {
			if(compat.isCompatEnabled(Compat.MFR_DSU)) {
				registerMultiPeripheralProvider(new DriverDeepStorageUnit.CCDriver());
			}
		}*/
		/*if(Mods.isLoaded(Mods.StorageDrawers)) {
			if(compat.isCompatEnabled(Compat.StorageDrawers)) {
				registerMultiPeripheralProvider(new DriverDrawerGroup.CCDriver());
			}
		}*//*
		if(Mods.isLoaded(Mods.FSP)) {
			if(compat.isCompatEnabled(Compat.FSP_Steam_Transporter)) {
				registerMultiPeripheralProvider(new DriverSteamTransporter.CCDriver());
			}
		}
		if(Mods.isLoaded(Mods.Factorization)) {
			if(compat.isCompatEnabled(Compat.FZ_ChargePeripheral)) {
				registerMultiPeripheralProvider(new DriverChargeConductor.CCDriver());
			}
		}

		if(Mods.isLoaded(Mods.Railcraft)) {
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

		if(Mods.isLoaded(Mods.ArmourersWorkshop)) {
			if(compat.isCompatEnabled(Compat.AW_Mannequins)) {
				registerMultiPeripheralProvider(new DriverMannequin.CCDriver());
			}
		}

		if(Mods.isLoaded(Mods.AE2)) {
			if(compat.isCompatEnabled(Compat.AE2_SpatialIO)) {
				registerMultiPeripheralProvider(new DriverSpatialIOPort.CCDriver());
			}
		}*/

		/*if(Mods.API.hasAPI(Mods.API.CoFHAPI_Energy)
			&& compat.isCompatEnabled(Compat.RedstoneFlux)) {
			registerMultiPeripheralProvider(new DriverEnergyHandler.CCDriver());
		}

		if(Mods.isLoaded(Mods.EnderIO)) {
			if(compat.isCompatEnabled(Compat.EnderIO)) {
				registerMultiPeripheralProvider(new DriverRedstoneControllable.CCDriver());
				registerMultiPeripheralProvider(new DriverIOConfigurable.CCDriver());
				registerMultiPeripheralProvider(new DriverHasExperience.CCDriver());
				registerMultiPeripheralProvider(new DriverPowerStorage.CCDriver());
				registerMultiPeripheralProvider(new DriverProgressTile.CCDriver());
				registerMultiPeripheralProvider(new DriverAbstractMachine.CCDriver());
				registerMultiPeripheralProvider(new DriverAbstractPoweredMachine.CCDriver());
				registerMultiPeripheralProvider(new DriverPowerMonitor.CCDriver());
				registerMultiPeripheralProvider(new DriverCapacitorBank.CCDriver());
				registerMultiPeripheralProvider(new DriverTransceiver.CCDriver());
				registerMultiPeripheralProvider(new DriverVacuumChest.CCDriver());
				registerMultiPeripheralProvider(new DriverWeatherObelisk.CCDriver());
				registerMultiPeripheralProvider(new DriverTelepad.CCDriver());
			}
		}*/

		/*if(Mods.API.hasAPI(Mods.API.DraconicEvolution)
			&& compat.isCompatEnabled(Compat.DraconicEvolution)) {
			registerMultiPeripheralProvider(new DriverExtendedRFStorage.CCDriver());
		}

		if(Mods.API.hasAPI(Mods.API.Mekanism_Energy)
			&& compat.isCompatEnabled(Compat.MekanismEnergy)) {
			registerMultiPeripheralProvider(new DriverStrictEnergyStorage.CCDriver());
		}*/

		/*if(Mods.hasVersion(Mods.API.BuildCraftTiles, Mods.Versions.BuildCraftTiles)) {
			if(compat.isCompatEnabled(Compat.BuildCraft_Drivers)) {
				registerMultiPeripheralProvider(new DriverHeatable.CCDriver());
			}
		}*/

		if(Mods.isLoaded(Mods.Flamingo)) {
			if(compat.isCompatEnabled(Compat.Flamingo)) {
				registerMultiPeripheralProvider(new DriverFlamingo.CCDriver());
			}
		}

		registerMultiPeripheralProvider(new CCPeripheralProvider());

		multiPeripheralProvider = new MultiPeripheralProvider(peripheralRegistry.peripheralProviders);

		ComputerCraftAPI.registerPeripheralProvider(multiPeripheralProvider);

		/*if(itemTape != null) {
			ComputerCraftAPI.registerMediaProvider(itemTape);
		}*/

		if(computronics.isEnabled("ccTurtleUpgrades", true)) {
			ComputerCraftAPI.registerTurtleUpgrade(
				new SpeakingTurtleUpgrade("computronics.turtle_speaking"));
			ComputerCraftAPI.registerTurtleUpgrade(
				new RadarTurtleUpgrade("computronics.turtle_radar"));
			ComputerCraftAPI.registerTurtleUpgrade(
				new MusicalTurtleUpgrade("computronics.turtle_noteblock"));
			ComputerCraftAPI.registerTurtleUpgrade(
				new ParticleTurtleUpgrade("computronics.turtle_fx"));
		}

		if(Computronics.tapeReader != null) {
			TileTapeDrive.initCCFilesystem();
		}
	}

	public void serverStart() {
		if(multiPeripheralProvider != null && Config.CC_ALWAYS_FIRST) {
			multiPeripheralProvider.sort();
		}
	}
}

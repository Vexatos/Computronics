package pl.asie.computronics.cc;

import dan200.computercraft.api.ComputerCraftAPI;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.block.BlockSoundBoard;
import pl.asie.computronics.cc.multiperipheral.MultiPeripheralProvider;
import pl.asie.computronics.integration.enderio.DriverAbstractMachine;
import pl.asie.computronics.integration.enderio.DriverAbstractPoweredMachine;
import pl.asie.computronics.integration.enderio.DriverCapacitorBank;
import pl.asie.computronics.integration.enderio.DriverHasExperience;
import pl.asie.computronics.integration.enderio.DriverIOConfigurable;
import pl.asie.computronics.integration.enderio.DriverPowerMonitor;
import pl.asie.computronics.integration.enderio.DriverPowerStorage;
import pl.asie.computronics.integration.enderio.DriverProgressTile;
import pl.asie.computronics.integration.enderio.DriverRedstoneControllable;
import pl.asie.computronics.integration.enderio.DriverTelepad;
import pl.asie.computronics.integration.enderio.DriverTransceiver;
import pl.asie.computronics.integration.enderio.DriverVacuumChest;
import pl.asie.computronics.integration.enderio.DriverWeatherObelisk;
import pl.asie.computronics.integration.flamingo.DriverFlamingo;
import pl.asie.computronics.integration.railcraft.driver.DriverBoilerFirebox;
import pl.asie.computronics.integration.railcraft.driver.DriverElectricGrid;
import pl.asie.computronics.integration.railcraft.driver.DriverRoutingActuator;
import pl.asie.computronics.integration.railcraft.driver.DriverRoutingDetector;
import pl.asie.computronics.integration.railcraft.driver.DriverSteamTurbine;
import pl.asie.computronics.integration.railcraft.driver.track.DriverLauncherTrack;
import pl.asie.computronics.integration.railcraft.driver.track.DriverLocomotiveTrack;
import pl.asie.computronics.integration.railcraft.driver.track.DriverMessengerTrack;
import pl.asie.computronics.integration.railcraft.driver.track.DriverPoweredTrack;
import pl.asie.computronics.integration.railcraft.driver.track.DriverPrimingTrack;
import pl.asie.computronics.integration.railcraft.driver.track.DriverRoutingTrack;
import pl.asie.computronics.integration.railcraft.driver.track.DriverThrottleTrack;
import pl.asie.computronics.integration.storagedrawers.DriverDrawerGroup;
import pl.asie.computronics.reference.Compat;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileSoundBoard;
import pl.asie.computronics.tile.TileTapeDrive;
import pl.asie.computronics.util.RecipeUtils;

import static pl.asie.computronics.Computronics.peripheralRegistry;
import static pl.asie.computronics.Computronics.registerMultiPeripheralProvider;

/**
 * @author Vexatos
 */
public class IntegrationComputerCraft {

	private final Compat compat;
	private final Computronics computronics;
	private MultiPeripheralProvider multiPeripheralProvider;

	public BlockSoundBoard soundBoard;

	public IntegrationComputerCraft(Computronics computronics) {
		this.computronics = computronics;
		this.compat = computronics.compat;
	}

	@Optional.Method(modid = Mods.ComputerCraft)
	public void preInit() {
		if(Config.CC_SOUND_BOARD) {
			soundBoard = new BlockSoundBoard();
			Computronics.instance.registerBlockWithTileEntity(soundBoard, TileSoundBoard.class, "sound_board");
		}
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
		if(Mods.isLoaded(Mods.StorageDrawers)) {
			if(compat.isCompatEnabled(Compat.StorageDrawers)) {
				registerMultiPeripheralProvider(new DriverDrawerGroup.CCDriver());
			}
		}/*
		if(Mods.isLoaded(Mods.FSP)) {
			if(compat.isCompatEnabled(Compat.FSP_Steam_Transporter)) {
				registerMultiPeripheralProvider(new DriverSteamTransporter.CCDriver());
			}
		}
		if(Mods.isLoaded(Mods.Factorization)) {
			if(compat.isCompatEnabled(Compat.FZ_ChargePeripheral)) {
				registerMultiPeripheralProvider(new DriverChargeConductor.CCDriver());
			}
		}*/

		if(Mods.isLoaded(Mods.Railcraft)) {
			if(compat.isCompatEnabled(Compat.Railcraft_Routing)) {
				registerMultiPeripheralProvider(new DriverPoweredTrack.CCDriver());
				registerMultiPeripheralProvider(new DriverRoutingTrack.CCDriver());
				registerMultiPeripheralProvider(new DriverRoutingDetector.CCDriver());
				registerMultiPeripheralProvider(new DriverRoutingActuator.CCDriver());
				registerMultiPeripheralProvider(new DriverElectricGrid.CCDriver());
				registerMultiPeripheralProvider(new DriverLocomotiveTrack.CCDriver());
				registerMultiPeripheralProvider(new DriverLauncherTrack.CCDriver());
				registerMultiPeripheralProvider(new DriverMessengerTrack.CCDriver());
				registerMultiPeripheralProvider(new DriverPrimingTrack.CCDriver());
				registerMultiPeripheralProvider(new DriverThrottleTrack.CCDriver());
				registerMultiPeripheralProvider(new DriverBoilerFirebox.CCDriver());
				registerMultiPeripheralProvider(new DriverSteamTurbine.CCDriver());
			}
		}

		/*if(Mods.isLoaded(Mods.ArmourersWorkshop)) {
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
		}*/

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
				if(Mods.isLoaded(Mods.EnderIO_Machines)) {
					registerMultiPeripheralProvider(new DriverTransceiver.CCDriver());
					registerMultiPeripheralProvider(new DriverVacuumChest.CCDriver());
					registerMultiPeripheralProvider(new DriverWeatherObelisk.CCDriver());
					registerMultiPeripheralProvider(new DriverTelepad.CCDriver());
				}
			}
		}

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
			ComputerCraftAPI.registerTurtleUpgrade(
				new CameraTurtleUpgrade("computronics.turtle_camera"));
		}

		if(Computronics.tapeReader != null) {
			TileTapeDrive.initCCFilesystem();
		}
	}

	@Optional.Method(modid = Mods.ComputerCraft)
	public void postInit() {
		if(Config.CC_SOUND_BOARD) {
			if(Computronics.ironNote != null && Computronics.speaker != null) {
				RecipeUtils.addShapedRecipe(new ItemStack(soundBoard, 1, 0),
					"ggg", "gcs", "ggg",
					'c', Computronics.ironNote,
					's', Computronics.speaker,
					'g', "ingotGold"
				);
			} else {
				Computronics.log.warn("Could not add Sound Board Recipe because Iron Note Block or Speaker is disabled in the config.");
			}
		}
	}

	public void serverStart() {
		if(multiPeripheralProvider != null && Config.CC_ALWAYS_FIRST) {
			multiPeripheralProvider.sort();
		}
	}
}

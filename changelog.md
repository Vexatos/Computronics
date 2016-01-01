==== 1.6.1 ====

  * ADDED: Support for TIS-3D!
    - Added a Colorful Module to display any colour you want, akin to a Colorful Lamp.
    - Added a Tape Reader Module which allows you to interface with an adjacent Tape Drive.
    - Added a Self-Destructing Module which allows you to dispose of your casingd with style.
    - Documented everything in the TIS-3D manual.
    - Each module can be individually disabled in the config file.
  * FIXED: Crash when the `openComputersBees` config option was set to `false`.

==== 1.6.0 ====

  * ADDED: Audio Cables!
    - Connects audio players (like Tape Drives) to audio receivers (Like Speakers)
    - If you connect one to a Tape Drive, it will stop playing itself and instead send the sound across the cables.
    - Can be coloured. When coloured, they won't connect to cables with a different colour and won't connect to coloured audio senders/receivers with a different colour.
    - They also support Immibis' Microblocks for separation, if you like covering up your cabling.
  * ADDED: Speakers!
    - Can be connected to Tape Drives or Cables
    - Plays any sound it receives through the cables
    - Allows you to play sound in multiple places at once using only a single Tape Drive.
  * ADDED: Audio API!
    - Now you can create your own audio packets! Sort of - it's a bit rough around the edges.
  * CHANGED: NedoComputers is no longer supported. Removed the EEPROM Reader and any kind of NedoComputers support there was.
  * FIXED: More crashes related to invalid crafting recipes.

==== 1.5.9 ====

  * ADDED: New config option (disabled by default) to make the normal Chat Box have no range limit and work interdimensionally unless you specify a distance.
  * CHANGED: The Creative Chat Box can now send messages interdimensionally and has no range limit anymore unless you specify a distance.
  * FIXED: Various crashes related to invalid crafting recipes.
  * FIXED: Crash when disabling the Digital Signal Receiver Box.
  * FIXED: Updated Railcraft integration to version 9.8.0.0; requires Railcraft 9.8.0.0 now.

==== 1.5.8 ====

  * ADDED: Some Nanomachine Beehaviour. Requires OpenComputers 1.5.18 now.
  * ADDED: Binary mode to the Colorful Lamp for MineFactory Reloaded. Shift-right click the lamp using a wrench or sledgehammer to make the lamp read every RedNet color frequency, each colour (apart from black) setting a specific bit of the lamp's colour (just like it works for Project: Red and RedLogic Bundled Cable). Shift-right clicking again will reset the mode.
  * CHANGED: Made the Deep Storage Unit driver for ComputerCraft work with Quantum Storage.
  * FIXED: Updated Forestry integration to version 4.0.8; requires Forestry 4.0.8 now.

==== 1.5.7 ====

  * ADDED: The Colorful Upgade for OpenComputers Robots!
    - Allows changing the colour of the Robot case to make your bots even prettier than before!
    - Only works in Robots.
  * CHANGED: The Particle Turtle now uses blaze powder instead of Firework Stars as those weren't working properly.
  * FIXED: Some issues with default peripheral priority.
  * FIXED: Errors in the multiperipheral system not being handled properly.
  * FIXED: Errors in the multiperipheral system not being written to log properly.

==== 1.5.6 ====

  * ADDED: Made most Computronics blocks dyable!
    - You can now right click most Computronics blocks with any kind of dye to change their colour!
    - Note: In case you change the colour of a block that existed in the world before you updated, it might not properly save the colour (this should only happen very rarely so you probably will not be affected at all). In this case just break and re-place the block!
  * ADDED: Flamingo integration!
    - Now you can make Flamingos wiggle using ComputerCraft or OpenComputers.
  * ADDED: Armourer's Workshop integration!
    - Now you can change rotation of body parts of mannequins using ComputerCraft or OpenComputers.
  * CHANGED: The encoding for the Advanced Cipher Block's keys have changed. You will need to re-generate keys you might be using. Sorry for the inconvenience, but the new encoding is a lot more compact and server-friendly.
    - **Huge thanks to makkarpov for helping me with improving the Advanced Cipher Block!**
  * FIXED: Creative Chat Boxes turning into normal chat boxes when placed.
  * FIXED: `setDistance()` not working on Creative Chat Boxes.

==== 1.5.5 ====

  * ADDED: StorageDrawers integration!
    - Now you can properly get the (max) item count stored in a specific drawer slot.
  * CHANGED: Doubled the default download rate of the built-in tape program
  * FIXED: Possible crash with Colorful Lamps
  * FIXED: Rare error with chat boxes kicking people from servers.
  * FIXED: Updated Forestry integration to version 3.6; requires Forestry 3.6 now.
  * MISC: Made secret bees more secret.
  * MISC: Added IChatListenerRegistry.isListenerRegistered to the API.

==== 1.5.4 ====

  * FIXED: Crash when OpenComputers is present but BuildCraft is not.

==== 1.5.3 ====

  * ADDED: The Chat Box and Camera now emit a comparator signal similar to the usually emitted redstone signal.
  * FIXED: The ability to place a Digital Signal Receiver Box on a side of a block when it's not supported.
  * FIXED: Computronics Blocks not having any functions provided to ComputerCraft when OpenPeripheral is there and OpenComputers is not.
  * FIXED: Crash when placing a Camera or Chat Box next to a Rail.

==== 1.5.2 ====

  * ADDED: BuildCraft Builder support for all Computronics blocks except for the Digital Receiver Box
  * FIXED: A certain achievement not getting triggered all the time
  * FIXED: Updated BuildCraft integration to 7.0.6. Requires BuildCraft 7.0.6 now.

==== 1.5.1 ====

  * ADDED: Note Block particle to a playing Iron Note Block or Musical Turtle.
  * CHANGED: the Digital Detector now returns a locomotive's colour numbers so that they can be used with the OpenComputers Colors API (so 0 being white, 1 being orange etc.)
  * FIXED: Shift-right clicking onto the Ticket Machine with an OpenComputers manual not opening the manual page.
  * FIXED: Crash with OpenComputers 1.5.11 and Computronics' Robot Upgrades and cards. Requires OpenComputers 1.5.11.25 now.
  * MISC: Slight improvements to the Digital Detector manual page

==== 1.5.0 ====

  * ADDED: All Computronics blocks now support the OpenComputers API documentation in NotEnoughItems.
  * ADDED: OpenComputers Component Documentation for every function any Computronics block provides
  * ADDED: Support for the OpenComputers manual!
    - Every block and item in Computronics can now be found in the manual
    - **A huge "Thank You!" to rashy for writing all the documentation!**
  * ADDED: Some Robot Upgrades now render on a robot if the robot contains them. Requires OpenComputers 1.5.9 now.
  * ADDED: The tape drive now plays a sound effect when forwarding or rewinding
  * ADDED: Ticket Machine for Railcraft
    - Allows printing single-use tickets using ComputerCraft or OpenComputers
    - Right click to open the customer GUI
      - Here you can select the ticket you would like to print and print tickets
    - Shift-right click with an empty hand
      - Now you can insert golden tickets for customers to select or take paper out
      - If you are the owner or an Op, you can also lock the machine so only the owner and Ops can open the maintenance GUI
    - You can prohibit selecting a ticket or printing tickets using the GUI using a computer
      - If set, tickets can only be selected/printed using a computer
    - Of course you can insert paper and extract printed tickets using any kind of automation
    - By default the Ticket Machine uses a little bit of RF for printing tickets, this can be disabled in the config
  * CHANGED: Made Camera, Chat Box and Tape Drive functions more consistent in behaviour (The ComputerCraft and OpenComputers functions should do the exact same now)
  * CHANGED: Got rid of the ComputerCraft bundled redstone support for RedLogic as it was not working anyway. Full ComputerCraft support in RedLogic has been proposed to immibis.
  * CHANGED: Radars now only output relative distance by default.
    - This can of course be changed in the config.
  * CHANGED: The Locomotive Relay now consumes a little Charge in the Electric Locomotive as well as a little OpenComputers energy if used by OpenComputers
    - Consuming charge can be disabled in the config
  * CHANGED: Renamed the EEPROM Reader component/peripheral name to "eeprom_reader" as it was conflicting with the OpenComputers EEPROM component.
  * CHANGED: Slightly tweaked the recipes of some Railcraft integration blocks and OpenComputers upgrades
  * CHANGED: Added config options for enabling each OpenComputers robot upgrade separately, removed the generic one.
  * CHANGED: The Locomotive Relay's `getMode()` now returns the mode in lower case.
  * FIXED: Updated OpenPeripheral integration to version 1.1 of OpenPeripheralCore (Requires version 1.1 now)
  * FIXED: Updated Forestry integration to version 3.5.3 (Requires version 3.5.3 now)
  * FIXED: Reworked the Locomotive Relay to properly work now and hopefully not randomly get unbound anymore.
    - Now you can clear a locomotive relay's bond by shift-rightclicking with an empty hand
  * FIXED: Reworked the Digital Detector to only send an event once for each passing minecart instead of sending one once per tick and minecart
    - The event will also now ship additional information if the minecart is a locomotive (primary colour, secondary colour and destination)
    - Thanks to marcin212 and Kubuxu for this!
  * FIXED: Computronics machine sounds now use the "Blocks" volume setting instead of the "Friendly Creatures" one
  * MISC: Refactored a lot of code, mainly related to GUIs and Railcraft integration.
  * MISC: Increased the bitrate on most Computronics textures, they should look much better now.

==== 1.4.6 ====

This one is semi-officially known as "The Cyther Update".
  * FIXED: A crash when the Iron Note Block has been disabled in the config.
  * FIXED: A crash with GregTech 6; depending on GregTech 5 for now until GregTech 6 is out of alpha.

==== 1.4.5 ====

This is semi-officially known as "The Kodos Update".
  * FIXED: The Creative Chat Box respecting the max range of the normal Chat Box
  * FIXED: Draconic Evolution and Mekanism Energy integration not working at all. Woops.

==== 1.4.4 ====

  * ADDED: Draconic Evolution integration!
    - You can now properly get the (max) energy stored from Energy Pylons, the values can be so huge it needed a special handler
    - You will need at least version v1.0.1-RC-1 of the mod for this to work
  * ADDED: Mekanism 8 integration!
    - You can now properly get the (max) energy stored from Mekanism machines like the Induction Matrix, the values can be so huge it needed a special handler
  * ADDED: getItems() function to Radar Block, Turtle and Robot Upgrade
    - Gives you information about all the items floating in the world around the Radar.
  * CHANGED: Digital Signal Reciver Box functions are now direct (thus they can be accessed much faster now)
  * CHANGED: Added getEnergyStored() / getMaxEnergyStored functions to blocks that only receive or provide RF and do not do both
  * FIXED: Updated OpenPeripheral integration to version 1.0 of OpenPeripheralCore (Requires version 1.0 now)
  * FIXED: Digital Locomotive Relay's getDestination() and setDestination() actually work properly again now!
  * FIXED: Bees now don't run on oil anymore
  * FIXED: A couple of issues that may have been appearing with Locomotive Relays

==== 1.4.3 ====

  * FIXED: Updated BuildCraft integration (requires BuildCraft 6.4.1 now)
    - This will make the Drone Docking station not crash anymore
  * FIXED: Server-side crash with Self-Destructing Card
  * FIXED: Some tooltips not being displayed properly


==== 1.4.2 ====

  * ADDED: Self-Destructing card
    - Does exactly what you think it does
    - Also comes with a built-in program for convenient self-destruction
  * ADDED: Optional argument to the Iron Note Block's / Musical Turtle's `playNote()` to specify the volume
    - Its definition is now `function([instrument:number or string,] note:number [, volume:number])`
    - `volume` may be a number between 0 and 1 (1 being default and the volume it was before)
  * ADDED: The Drone Docking Station now is able to charge docked drones when plugged onto a Kinesis Pipe!
  * FIXED: Particle Effects Card emitting particles when it doesn't have enough energy for that
  * FIXED: Crash happening when Tape length configuration entry has been misconfigured. Now it will print errors to the log instead
  * FIXED: A lot of Lua functions returning nil in ComputerCraft when they should return tables
  * FIXED: The Creative Chat Box dropping a normal Chat Box when broken. Now it will drop a creative one.
  * FIXED: Updated OpenComputers integration (requires OpenComputers 1.5 now)

==== 1.4.1 ====

  * FIXED: Updated Railcraft integration (requires Railcraft 9.5 now)
  * CHANGED: Some changes to the config file
    - It is recommended to regenerate the config file
  * CHANGED: Got rid of the EnderIO Capacitor Bank's getEnergyStoredForNetwork and getMaxEnergyStoredForNetwork functions
    - Their behaviour was the same is getEnergyStored/getMaxEnergyStored, so use those functions instead

==== 1.4.0 ====

  * ADDED: Tape Drives now output a comparator signal if they are currently playing
  * ADDED: Drone docking station + Docking upgrade for OpenComputers Drones
    - The Docking Station you put onto any BuildCraft pipe
    - Drones with the Docking Upgrade can dock with Docking Stations
    - If the Station is on an Item Transport pipe, the drone will be able to inject items into the pipe
    - Drones should have been able to be charged while docked with a station, if the station is on a Kinesis pipe,
      but that feature could not be added due to a bug in the BuildCraft power system
    - Requires Buildcraft 6.3
  * ADDED: Integration for the EnderIO Power Monitor
    - You can get literally everything shown in its GUI
    - You can also configure Engine Control using ComputerCraft/OpenComputers
    - Requires EnderIO 2.2.6.321 or later
  * ADDED: New chat API
    - Other mods may hook into this to listen to chat messages and react accordingly
  * CHANGED: Changes to MultiPeripheral system:
    - MultiPeripheral system can now recognize every ComputerCraft peripheral!
      - This can be disabled in the config
    - MultiPeripheral system is now almost always being recognized by ComputerCraft
      - This means that the basic behaviour of ComputerCraft's peripheral handling changed:
        - Previously ComputerCraft, when trying to access a peripheral and multiple mods are adding peripherals to the same block, would choose the peripheral that has been registered first
        - Now all Peripherals on the same block are merged into a single one using the MultiPeripheral system, meaning that there won't be peripheral conflicts anymore
      - This can be reverted to default behaviour in the config
    - Added a config option to disable OpenPeripheral integration
  * FIXED: Updated EnderIO and Applied Energistics 2 integration to work properly again.
  * FIXED: Rightclicking EEPROMs onto the EEPROM reader not working
  * FIXED: Extremely rare ArrayIndexOutOfBoundsException when playing and writing to a tape at the same time
  * FIXED: Tape Drive's isEnd() returning the opposite of what it should return
  * FIXED: Chat Box Upgrade not working in Tablets and Drones
  * FIXED: Some issues with the built-in tape program
  * MISC: Cleaned up some Driver code
    - Also got rid of the DSU driver for OpenComputers because it is inside OC natively now (the ComputerCraft peripheral is still there)

==== 1.3.4 ====

  * FIXED: Tape Drive not being started when calling play() using ComputerCraft
  * FIXED: Some more miscellaneous fixes
  * MISC: Updated Waila integration to 1.5.7

==== 1.3.3 ====

  * ADDED: New OpenComputers Cards:
    - Spoofing Card: Works the same as a Network Card, but allows you to specify the source address
    - Beep Card: Provides beep(); same as computer.beep(), just takes a table with frequency-duration pairs, allows playing up to 8 different sounds at once. See [this example implementation](https://github.com/OpenPrograms/Vexatos-Programs/blob/master/song/song.lua)
  * ADDED: OpenPeripheral integration:
    - Now OpenPeripheral peripherals aren't ignored anymore if Computronics is present
  * ADDED: More BuildCraft integration
    - Now you can get various things about the heat of any block which might overheat
  * ADDED: Forestry Bees:
    - Now there is a new Bee producing Grog for OpenComputers. Have fun acquiring it!
  * FIXED: Lamps ignoring bundled redstone if placed adjacent to each other
  * FIXED: Tape Drive GUI. Now it actually, really works
  * FIXED: Chat box messages not being consistently gray
  * MISC: Built-in tape program for OC now handles errors properly
  * MISC: Added a few more things to the config file
  * MISC: Updated Waila integration to 1.5.6
  * MISC: Updated EnderIO integration to 2.2.4
    - New Capacitor Banks can be properly accessed by CC and OC now!
    - for CC, getEnergyStored()/getMaxEnergyStored() has been fixed
    - for OC, you will need to update to the 1.4.3.283-dev build or a later version for it to be fixed
    - Also fixed getPowerPerTick() for powerable EnderIO devices

==== 1.3.2 ====

  * FIXED: Tapes not retaining their data
  * FIXED: Tape Drive File System not being persistent in OpenComputers
  * FIXED: Waila integration for Tape Drives. Should be a lot better now.
  * FIXED: Recipe crash when GregTech is installed but Railcraft is not

==== 1.3.1 ====

  *This is the mandatory hotfix release after a major relese*
  * FIXED: Updated to NedoComputers 0.5 API to fix a crash
  * FIXED: A crash happening with recipes.

==== 1.3.0 ====

  **Note: This update changed some config values, it is highly recommended to re-generate your config file!**
  * ADDED: Colored Lights support for the Colorful Lamp!
  * ADDED: More Railcraft Integration (Requires Railcraft 9.4.x now)
    - Digital Signal Receiver Box: Same as the Receiver Box driver has been, but also fires an "aspect_changed" event whenever the box aspect changes
    - Digital Detector: Fires a "minecart" event whenever a minecart passes, containing the type of cart as well as the name if the cart has got one
    - Now you can get and set the force of Launcher Tracks using ComputerCraft or OpenComputers!
    - Now you can get and set the fuse time of Priming Tracks using ComputerCraft or OpenComputers!
    - Now you can check isPowered() on every kind of track that can be powered by Redstone
    - Replicated the Steam Turbine Driver and the Boiler Firebox Driver added by OpenComputers for ComputerCraft
    - Added getDraw() for anything using Railcraft Charge
  * ADDED: Buildcraft Integration (Gate Statements)
    - 2 Triggers (Computer Running/Stopped) for the OpenComputers Computer Case
    - 2 Actions (Start/Stop Computer) for the OpenComputers Computer Case
    - 4 Triggers (Tape Drive Playing/Stopped/Rewinding/Forwarding) for the Tape Drive
    - 4 Actions (Start/Stop/Rewind/Forward Tape) for the Tape Drive
    - 2 Actions (Set/Reset Color) for the Colorful Lamp (Emerald Gate required for Set Color)
  * ADDED: EnderIO Integration for OpenComputers and ComputerCraft
    - You can get and set max Input/Output of Capacitor Banks
    - You can get/set the IO mode of certain sides of blocks with configurable IO
    - You can get the RF usage/production per tick of any machine
    - You can check whether a machine is currently active
    - You can get/set the redstone control mode of redstone-controllable devices
    - You can get/set send/receive channels of the Dimensional Transceiver
    - You can add/remove public channels to/from the Dimensional Transceiver channel list
    - You can get the (max) Experience (Levels) of any experience-storing EnderIO device
  * ADDED: Advanced Cipher Block
    - Allows you to encrypt and decrypt string messages using RSA
    - Also includes a key generator (with the two prime numbers to start with as paramters)
    - Also includes a random key generator (with the key's bit length as an optional paramter)
    - **Huge thanks to Kubuxu for helping me figure out a good algorithm!**
  * ADDED: More Waila integration
    - Now you can see the component address and bus ID of every block for OpenComputers and NedoComputers
    - Now you can see whether a Digital Locomotive Relay is bound or not
    - Now you can see the colour values of a Colorful Lamp
  * ADDED: New MultiPeripheral system
    - Like in OpenComputers, these peripherals are being merged into a single one in case a block has multiple Peripherals on it
    - This also comes with a new MultiPeripheral API for other mods to hook into in case they want
  * REMOVED: Removed the Signal Receiver Box Driver in favour of the Digital Signal Receiver Box
  * CHANGED: setLampColor(0) now turns off the Colorful Lamp.
  * CHANGED: Updated to AsieLib 0.3.3
  * FIXED: A strange error that can happen while chatting near a chat box.
  * FIXED: Power values and power consumtion for OC. Re-generate your config file!
  * FIXED: A problem in the Radar's table creation
  * FIXED: Some GregTech recipes not working
  * MISC: Lots of code cleanup done. It should be much more pretty now.

==== 1.2.0 ====

  * ADDED: More Railcraft Integration
    - You can now get/set the track mode in Locomotive Tracks using ComputerCraft/OpenComputers!
      - You can access `modes` in OpenComputers or `modes()` in ComputerCraft to get a table containing all possible modes
    - You can now get/set the speed limit of Limiter Tracks using ComputerCraft/OpenComputers!
    - Receiver Boxes now have an `aspects` table containing every Signal Aspect in Railcraft
      - You can access it from ComputerCraft using `aspects()`
  * ADDED: More Waila Integration
    - Tape Drives now show their current state in Waila
  * CHANGED: Moved to OpenComputers 1.4
  * FIXED: A pretty serious bug with the Cipher Block
  * FIXED: Waila integration
  * FIXED: Tape Drive's getState not properly returning the current state.
  * MISC: Major refactor of integration code.

==== 1.1.0 ====

  * ADDED: Applied Energistics 2 integration: The Spatial IO port can now be accessed using ComputerCraft and OpenComputers!
  * ADDED: getRoutingTableTitle and setRoutingTableTitle to the Routing Detector and Routing Switch Motor
  * ADDED: getName to the Locomotive Relay

==== 1.0.7 ====

  * FIXED: Robot upgrades crashing when not used inside robot.

==== 1.0.6 ====

  * CHANGED: Locomotive Relays should now remember the locomotive they are bound to through chunk unloading properly.
  * FIXED: GregTech Battery Buffer not being recognized by OpenComputers if placed after the adjacent adapter was.
  * FIXED: Electric Tracks not being regognized as peripherals/components

==== 1.0.5 ====

  * FIXED: Some weird Crash with GregTech

==== 1.0.4 ====

  * ADDED: You can now read charge and loss of Railcraft's Electric Blocks using OpenComputers or ComputerCraft!  
    - Note: Apparently the Electric Track can only be connected to using OpenComputers, the other electric blocks work fine with OpenComputers and ComputerCraft.
  * FIXED: Forge log getting spammed with errors regarding Applied Energistics 2
  * FIXED: Radar not working when used with ComputerCraft (Thanks to Grovert11 for finding the bug)

==== 1.0.3 ====

  * FIXED: Chat Boxes crashing when OpenComputers is not installed (thanks to Grovert11 for finding the bug)
  * FIXED: Chat Box Names not being set properly (thanks to Grovert11 for finding this as well)
  * FIXED: Locomotive Relay's getDestination returning the same as getMode (once again thanks to Grovert11)

==== 1.0.2 ====

  * FIXED: EVERYTHING!

==== 1.0.1 ====

  * FIXED: RailCraft stuff breaking everything without RailCraft installed.

==== 1.0.0 ====

  **Yes, this is what you should think it is, this is the version 1.0.0, so the first actual full release. Prepare for a lot of new stuff:**

  * ADDED: RailCraft integration, courtesy of Vexatos! Routing can now be fully controlled with computers!  
    - Routing Tracks can now be accessed using the Adapter Block  
    - Routing Detectors can now be accessed using the Adapter Block  
    - Routing Switch Motors can now be accessed using the Adapter Block  
    - Signal Receiver Boxes can now be accessed using the Adapter Block  
    - Added the Digital Locomotive Relay to access Electric Locomotives remotely  
    - Added the Digital Relay Sensor to bind a relay to a locomotive  
  * ADDED: GregTech integration  
    - Machine monitoring  
    - Digital Chests support  
    - Battery Buffer support (make sure to place the adapter after you placed the battery buffer!)  
    - Added a new virtually indestructable high-end tape using GregTech materials!  
    - Added a GregTech recipe mode for all the Computronics blocks and items (Thanks to SpwnX for helping with this)  
  * ADDED: New fancy Achievements for Computronics and its GregTech and Railcraft integration
  * ADDED: New French (AegisLesha) and Chinese (crafteverywhere) translations.
  * ADDED: ITapeStorage API - developers can now create their own compatible tapes and tape devices.
  * ADDED: tape.lua to tape drives - a utility program to write songs to tapes (from a hard drive or the Internet) and to play, stop, pause etc. songs
  * ADDED: BetterStorage 0.10+ compatibility for Storage Crate drivers.
  * ADDED: Radars now use energy uniformly for OC, CC turtles (coal) and ComputerCraft itself (MJ, RF or EU).
  * ADDED: Changed Cipher Block behaviour:  
    - Cipher Blocks can now be locked - their inventory is not accessible via automation or manually while they are locked. They can still be broken, though!  
    - Each side of the Cipher Block now corresponds to one of its six slots.  
  * REMOVED: Project: Red CC redstone integration - it's now improved and inside P:Red itself!
  * FIXED: Disabling the Chat Box will now disable the (potentially lag-inducing on large server) Chat Box event code.
  * FIXED: OC 1.2 will now give an error message when used. (YES, USE OC 1.3, IT'S BETTER)
  * MISC: Rewritten parts of the TE code to use the new AsieLib 0.3.0 methods.

==== 0.6.4 (13 August 2014 [1.7.2, 1.7.10]) ====

  * ADDED: You can set chat box names with getName() and setName().
  * ADDED: Improved German and Russian translations, by Vexatos and Adaptivity respectively.
  * FIXED: distanceUp() and distanceDown() in camera robots. For real. I actually tested it this time. It works.
  * FIXED: Potential CC issues with Lamps. (thanks GyroW)
  * FIXED: Wired Modems from CC can now be used on Lamps. (thanks GyroW)
  * FIXED: Radars and Radar Upgrades in OpenComputers now work again!
  * WARNING: Apparently, there never was a 0.6.3. Whatever.

==== 0.6.2 (1 August 2014 [1.7.2, 1.7.10]) ====

  * ADDED: Factorization Charge Conductor support!
  * ADDED: FSP Steam Transporter support!
  * ADDED: MFR RedNet support for Cipher Blocks!
  * WARNING: Mod compatibility configs have changed - if you disabled something added in 0.6.0 or 0.6.1, you might need to disable it again.

==== 0.6.1 (25 July 2014 [1.7.2, 1.7.10]) ====

  * ADDED: Deep Storage Unit API support for CC and OpenC (read: JABBA barrels)
  * FIXED: Fixed a boot crash when not having Project: Red installed. (This might feel ironic for some people who know me better.)

==== 0.6.0 (//Compatible Edition//) (25 July 2014 [1.7.2, 1.7.10]) ====

  * ADDED: Colorful Lamps! Supporting ComputerCraft, OpenComputers, NedoComputers, Project: Red, MineFactory Reloaded //and// RedLogic for setting their colour!
  * ADDED: Reading Bundled Cable output for RedLogic and Project:Red-compilant Bundled Cables in ComputerCraft is now possible! (Writing for RedLogic might come soon; writing for Project: Red is impossible due to P:Red's restrictive license)
  * ADDED: Cipher Blocks now support encryption via RedLogic and Project: Red bundled cables!
  * ADDED: Iron Note Blocks now support RedLogic, Project: Red and MFR for playback!
  * ADDED: BetterStorage crates can now have their contents read by OpenComputers!
  * ADDED: Chat Boxes now should emit a redstone pulse on every message sent on the chat.

==== 0.5.2 (24 July 2014 [1.7.2, 1.7.10]) ====

  * ADDED: EEPROM readers! Only active when NedoComputers is also installed, they let you read from and write to initialized NedoComputers EEPROMs.
  * ADDED: RedLogic lamps as peripherals! Because why not?
  * CHANGED: Iron Note Blocks now use the block under them for choosing the default instrument, instead of hardcoding a single instrument.
  * FIXED: Iron Note Blocks now emit Forge events for noteblocks. Sometimes.
  * FIXED: Cameras should now work a little bit better. They're still broken for distanceUp() and distanceDown(), but at least should not break for distance().

==== 0.5.1 (9 July 2014 [1.7.2, 1.7.10]) ====

  * ADDED: Cipher Block support for NedoComputers.
  * ADDED: Particle Turtle Upgrades for ComputerCraft.
  * FIXED: Allow setting bus IDs for NedoComputers. With NedoC installed, sneak-right-click with an empty hand on any Computronics peripheral.
  * FIXED: Robot Camera Upgrades now work again.
  * FIXED: The Tape Drive GUI finally works in 1.7.

==== 0.5.0 (8 July 2014 [1.7.2, 1.7.10]) ====

  * ADDED: Full ComputerCraft 1.64pr3 support using the native APIs!
  * ADDED: Partial NedoComputers support (Cameras, Iron Note Blocks, Tape Drives)
  * ADDED: Configuration options - configurable tape lengths
  * ADDED: Radars! They detect entities. And players. And... that's about it.
  * ADDED: Chat Box and Radar Upgrades for OpenComputers Robots.
  * ADDED: Speaking, Musical and Radar Turtle Upgrades (ComputerCraft)! [UNTESTED - waiting for CC for 1.7.10]
  * ADDED: Tapes can now have labels changed in CC Disk Drives. (Why not?)
  * ADDED: Chat Boxes can now have an optional second parameter to say() - the distance.
  * ADDED: Particle Effect Cards for OpenComputers - spawn particles all you want!
  * FIXED: Cameras should now work. Really. Please work ;_;
  * FIXED: Chat Boxes can now be connected using OpenComputers cables.
  * FIXED: Iron Note Blocks work in OC (again)
  * FIXED: Volume and speed settings in Tape Drives.
  * FIXED: For those of you who don't use OpenComputers, a few more peripherals will now become tickless! (Chat Boxes, Cipher Blocks)
  * REMOVED: OpenPeripheral support, we hook directly into ComputerCraft now!
  * REMOVED: A lot of junk code from ages ago.

==== 0.4.2 (30 May 2014 [1.7.2]) ====

  * [Techokami] New version of Nether Star Tape added: crafted with 4 Nether Stars and one tape track, it is capable of holding 128 minutes of audio, or enough data to properly accommodate a FAT16 partition.
  * [Techokami] Fixed crash bug related to Tape Drives (speed, volume settings).

==== 0.4.1 (28 May 2014 [1.7.2]) ====

  * [Techokami] Ported to 1.7.2.

==== 0.4.1 (27 April 2014 [1.6.4]) ====

  * Added: Russian translation by dangranos.
  * Fixed: Tape Drive GUIs now work again.

==== 0.4.0 (27 April 2014 [1.6.4]) ====

  * Warning: Needs AsieLib 0.2.3+
  * Added: Creative Chat Boxes! You can now pretend you're the NSA! (No distance limits, only one dimension, works with / commands too.)
  * Added: More tape types: Nether Star, Copper and Steel.
  * Added: GregTech compatibility.
  * Added: Chat Boxes now log /me commands.
  * Added: A few more configuration options.
  * Added: You can now set the volume of your Tape Drive using a computer.
  * Fixed: The Robot Camera Upgrade now functions properly.
  * Fixed: Proper CC 1.6 support now! I think.

==== 0.3.2 (17 April 2014 [1.6.4]) ====

  * Added: distanceUp() and distanceDown() for the Robot camera upgrade.
  * Fixed: Some fixes or other. I don't remember what exactly.

==== 0.3.1 (5 April 2014 [1.6.4]) ====

  * Warning: Needs AsieLib 0.2.1+
  * Added: Chat Box getDistance() and setDistance() in 1.6.4, for OC and CC.
  * Added: Cipher Block crafting recipe! I forgot!
  * Added: setSpeed() to Tape Drives, letting you set the speed from 0.25x to 2.0x!

==== 0.3.0 (5 April 2014) ====

  * WARNING: Needs AsieLib 0.2.0+ AND a snapshot version of OpenPeripheralCore (for ComputerCraft support)!
  * [1.7.2] Added: getDistance() and setDistance() to Chat Boxes, setting their listening distance (will backport to 1.6 in 0.3.1!)
  * Added: ComputerCraft 1.6+ support!
  * Added: Cipher Block - encrypts and decrypts messages!
  * Added: Cameras now look up and down in addition to the 4 sides.

==== 0.2.4 (23 March 2014) ====

  * Ported to Minecraft 1.7.2.
  * Fixed: Cameras giving mirrored output in two of the four directions.

==== 0.2.3 (17 March 2014) ====

  * Fixed: Bug where Chat Boxes not connected to an OC computer could crash the server.

==== 0.2.2 (15 March 2014) ====

  * Changed: Split off AsieLib into a separate mod.
  * Fixed: Now works on dedicated servers.
  * Fixed: Rewinding and fast-forwarding will now not repeat the rewind sound (and never stop it).

==== 0.2.1 (14th March 2014) ====

  * Added: getState() to get the current state of the Tape Drive.
  * Fixed: An NPE when clicking on a machine with an empty hand. (Yes, I don't know how I missed that one either)

==== 0.2.0 (14th March 2014 - Happy Pi Day!) ====

  * Added: A rewrite of the Tape Drive mechanics.
    * Tape Drives now remember their state on unload (persistence!).
    * [[http://i.imgur.com/FCFiPfd.png|A slick new GUI]] - makes it possible to use Tape Drives without computers!
    * Tape Drive playback can be controlled with redstone.
  * Added: Tape Drives and Cameras can now be rotated with BuildCraft or compatible wrenches.
  * Added: Iron Note Blocks support the two noteblock sounds that are in the Minecraft assets/ folder but were never used ("pling" and "bass", respectively).

==== 0.1.5 (12 March 2014) ====

  * Added: The tape eject sound is now played on... tape eject.
  * Fixed: Chat Boxes crashed if OpenComputers was not installed.

==== 0.1.4 (12 March 2014) ====

  * Added: Better Camera accuracy - it now gives correct shapes for blocks, as well as has higher accuracy for distance values. Thanks to Sangar!
  * Added: Camera Redstone support - Cameras now output a redstone signal related to the distance of the block in front of them.
  * Added: German translation (thanks, Vexatos!)

==== 0.1.3 (11 March 2014) ====

  * Added: Polish translation.
  * Fixed: Cameras now /really/ work properly.

==== 0.1.2 (11 March 2014) ====

  * Added: Wonderful new textures by **ping**.
  * Added: Chat Boxes! They let you send and receive messages.
  * Added: (OpenComputers only) Robot Camera Upgrades! You can now add Cameras to OC robots.
  * Tweaked: A custom creative tab.
  * Tweaked: Camera improvements!
    * (OpenComputers only) Cameras now output block data: a unique hash of the ID, as well as the light distance. The feature will be added to ComputerCraft at a later date.
    * The distance value's accuracy has been somewhat tweaked.

==== 0.1.1 (9 March 2014) ====

  * First official release.

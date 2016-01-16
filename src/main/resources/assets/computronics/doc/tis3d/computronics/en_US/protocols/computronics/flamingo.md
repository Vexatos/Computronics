# Pink Flamingo

![Yep, it's real.](block:flamingo:flamingo.flamingo)

Exploiting the undocumented ability to poke things of the serial port module, it is possible to make Flamingos wiggle. Writing any value to the serial port will result in the module making the Flamingo wiggle, as if a human had poked it. Reading from the module will result in it returning the wiggle strength, but the module is unable to handle particularly high values, resulting undefined behavior for those. 

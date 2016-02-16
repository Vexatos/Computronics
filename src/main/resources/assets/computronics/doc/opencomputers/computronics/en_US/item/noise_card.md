# Noise Card

![Boop. Beep. Bzzt.](item:computronics:computronics.ocParts@8)

The noise card, akin to the [beep card](beep_card.md), provides the `play()` function. In addition, it also allows setting each of the eight separate channels' sound modes to make the card play square, sine, triangle or sawtooth waves.

Furthermore, the card also provides a small internal buffer, allowing up to eight entries of frequency-duration pairs with an optional initial delay to be specified per channel. Calling `process()` will then process the entire buffer at once.

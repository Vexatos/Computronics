# Tape Drive

The tape drive is a block that allows for playback and recording of audio data. [Cassette tapes](../item/tape.md) can be placed into the tape drive, and come in durations of 2 to 128 minutes. Note that the tape drive isn't restricted to purely audio data - other types of data can be written to the [cassette](../item/tape.md).

Audio is recorded in the DFPWM format, due to the low filesize. Converting from MP3 or other common audio formats is tricky; however, it is possible to convert from WAV files to DFPWM using a program called [LionRay](http:/dl.dropboxusercontent.com/u/93572794/LionRay.jar). The converted audio file can then be written, byte-by-byte, to the cassette tape. 

The tape drive also has a `seek()` function, allowing fast-forwarding to a specific point on the [cassette tape](../item/tape.md). Providing a negative value to the `seek()` rewinds the [cassette tape](../item/tape.md) to an earlier point. 
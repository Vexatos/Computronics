package pl.asie.computronics.api.tape;

public interface ITapeStorage {
	/**
	 * @return The unique identifier of the ITapeStorage.
	 */
	public String getUniqueId();

	/**
	 * @return The name (note: not label, think of it more as a type) of the ITapeStorage.
	 */
	public String getName();

	/**
	 * @return The position the ITapeStorage is currently in.
	 */
	public int getPosition();

	/**
	 * @return The size of the ITapeStorage, in bytes.
	 */
	public int getSize();

	/**
	 * Sets the position of the tape. I would very much ask for you **NEVER** to interface this to players
	 * in any way, shape or form, due to it breaking the delicate balance of the ITapeStorage system.
	 * @param newPosition The new position of the tape
	 * @return The position the tape is now set to.
	 */
	public int setPosition(int newPosition);

	/**
	 * Seek amount bytes in the tape. Negative values indicate rewinding.
	 * @param amount The amount of bytes to seek.
	 * @return The amount of bytes seeked.
	 */
	public int seek(int amount);

	/**
	 * Read a single byte.
	 * @param simulate If true, do not automatically seek the tape. Like setPosition, this should NOT be interfaced to players.
	 * @return The byte read, automatically converted to an unsigned form (0-255).
	 */
	public int read(boolean simulate);

	/**
	 * Read intoArray.length bytes into intoArray
	 * @param intoArray The array into which the data should be read.
	 * @param simulate If true, do not automatically seek the tape. Like setPosition, this should NOT be interfaced to players.
	 * @return The amount of bytes read.
	 */
	public int read(byte[] intoArray, boolean simulate);

	/**
	 * Write a byte on the tape.
	 * @param b The byte to write.
	 */
	public void write(byte b);

	/**
	 * Write the array into the tape.
	 * @param array The array with the bytes to write.
	 * @return The amount of bytes written.
	 */
	public int write(byte[] array);

	/**
	 * Called when the storage is about to be unloaded - use this entrypoint to save the (modified) data.
	 */
	public void onStorageUnload();
}

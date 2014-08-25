package gregtech.api.interfaces.tileentity;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.fluids.IFluidHandler;

/**
 * This is a bunch of Functions my TileEntities provide, to make life much easier, and to get rid of internal TileEntity stuff.
 * 
 * This also makes access to adjacent TileEntities more Efficient.
 */
public interface IHasWorldObjectAndCoords {
	public World getWorld();
	public int getXCoord();
	public short getYCoord();
	public int getZCoord();
	
	public boolean isServerSide();
    public boolean isClientSide();
    
    public int getRandomNumber(int aRange);
    
	public TileEntity getTileEntity(int aX, int aY, int aZ);
    public TileEntity getTileEntityOffset(int aX, int aY, int aZ);
	public TileEntity getTileEntityAtSide(byte aSide);
	public TileEntity getTileEntityAtSideAndDistance(byte aSide, int aDistance);
	
	public IInventory getIInventory(int aX, int aY, int aZ);
    public IInventory getIInventoryOffset(int aX, int aY, int aZ);
	public IInventory getIInventoryAtSide(byte aSide);
	public IInventory getIInventoryAtSideAndDistance(byte aSide, int aDistance);
	
	public IFluidHandler getITankContainer(int aX, int aY, int aZ);
    public IFluidHandler getITankContainerOffset(int aX, int aY, int aZ);
	public IFluidHandler getITankContainerAtSide(byte aSide);
	public IFluidHandler getITankContainerAtSideAndDistance(byte aSide, int aDistance);
	
	public IGregTechTileEntity getIGregTechTileEntity(int aX, int aY, int aZ);
    public IGregTechTileEntity getIGregTechTileEntityOffset(int aX, int aY, int aZ);
	public IGregTechTileEntity getIGregTechTileEntityAtSide(byte aSide);
	public IGregTechTileEntity getIGregTechTileEntityAtSideAndDistance(byte aSide, int aDistance);
	
    public Block getBlock(int aX, int aY, int aZ);
    public Block getBlockOffset(int aX, int aY, int aZ);
    public Block getBlockAtSide(byte aSide);
    public Block getBlockAtSideAndDistance(byte aSide, int aDistance);
    
	public byte getMetaID(int aX, int aY, int aZ);
    public byte getMetaIDOffset(int aX, int aY, int aZ);
    public byte getMetaIDAtSide(byte aSide);
    public byte getMetaIDAtSideAndDistance(byte aSide, int aDistance);
    
	public byte getLightLevel(int aX, int aY, int aZ);
    public byte getLightLevelOffset(int aX, int aY, int aZ);
    public byte getLightLevelAtSide(byte aSide);
    public byte getLightLevelAtSideAndDistance(byte aSide, int aDistance);

	public boolean getOpacity(int aX, int aY, int aZ);
    public boolean getOpacityOffset(int aX, int aY, int aZ);
    public boolean getOpacityAtSide(byte aSide);
    public boolean getOpacityAtSideAndDistance(byte aSide, int aDistance);
    
	public boolean getSky(int aX, int aY, int aZ);
    public boolean getSkyOffset(int aX, int aY, int aZ);
    public boolean getSkyAtSide(byte aSide);
    public boolean getSkyAtSideAndDistance(byte aSide, int aDistance);

	public boolean getAir(int aX, int aY, int aZ);
    public boolean getAirOffset(int aX, int aY, int aZ);
    public boolean getAirAtSide(byte aSide);
    public boolean getAirAtSideAndDistance(byte aSide, int aDistance);
    
    public BiomeGenBase getBiome();
    public BiomeGenBase getBiome(int aX, int aZ);
    
    public int   getOffsetX(byte aSide, int aMultiplier);
    public short getOffsetY(byte aSide, int aMultiplier);
    public int   getOffsetZ(byte aSide, int aMultiplier);
    
    /**
     * Checks if the TileEntity is Invalid or Unloaded. Stupid Minecraft cannot do that btw.
     */
    public boolean isDead();
    
    /**
     * Sends a Block Event to the Client TileEntity, the byte Parameters are only for validation as Minecraft doesn't properly write Packet Data.
     */
    public void sendBlockEvent(byte aID, byte aValue);
    
	/**
	 * @return the Time this TileEntity has been loaded.
	 */
	public long getTimer();
	
    /**
     * Sets the Light Level of this Block on a Scale of 0 - 15
     * It could be that it doesn't work. This is just for convenience.
     */
    public void setLightValue(byte aLightValue);
    
    /**
     * Function of the regular TileEntity
     */
    public void writeToNBT(NBTTagCompound aNBT);
    
    /**
     * Function of the regular TileEntity
     */
    public void readFromNBT(NBTTagCompound aNBT);
    
    /**
     * Function of the regular TileEntity
     */
    public boolean isInvalidTileEntity();
    
	/**
	 * Opens the GUI with this ID of this MetaTileEntity
	 */
	public boolean openGUI(EntityPlayer aPlayer, int aID);
	
	/**
	 * Opens the GUI with the ID = 0 of this TileEntity
	 */
	public boolean openGUI(EntityPlayer aPlayer);
}
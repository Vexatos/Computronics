package pl.asie.computronics.block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileEEPROMReader;

public class BlockEEPROMReader extends BlockPeripheral {
	private IIcon mTopOff, mTopOn, mSide, mBottom;
	
	public BlockEEPROMReader() {
		super();
		this.setBlockName("computronics.eepromReader");
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEEPROMReader();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getAbsoluteIcon(int sideNumber, int metadata) {
		switch(sideNumber) {
		case 0: return mBottom;
		case 1: return (metadata > 0 ? mTopOn : mTopOff);
		default: return mSide;
		}
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int a, float _x, float _y, float _z) {
		if (!world.isRemote && !player.isSneaking()) {
			ItemStack h = player.getHeldItem();
			if(h != null && h.stackSize > 0 && h.getItem().equals(GameRegistry.findItem(Mods.NedoComputers, "EEPROM"))) {
				TileEEPROMReader te = (TileEEPROMReader)world.getTileEntity(x, y, z);
				if(te.getStackInSlot(0) == null && h.stackSize == 1) {
					te.setInventorySlotContents(0, h);
					player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
					return true;
				}
			} else if(h == null) {
				TileEEPROMReader te = (TileEEPROMReader)world.getTileEntity(x, y, z);
				if(te.getStackInSlot(0) != null) {
					ItemStack is = te.getStackInSlot(0);
					te.setInventorySlotContents(0, null);
					player.inventory.setInventorySlotContents(player.inventory.currentItem, is);
					return true;
				}
			}
		}
		return super.onBlockActivated(world, x, y, z, player, a, _x, _y, _z);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister r) {
		super.registerBlockIcons(r);
		mTopOff = r.registerIcon("computronics:eepromreader_top_nochip");
		mTopOn = r.registerIcon("computronics:eepromreader_top_chip");
		mSide = r.registerIcon("computronics:machine_side");
		mBottom = r.registerIcon("computronics:machine_bottom");
	}
}

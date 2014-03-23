package pl.asie.computronics.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.tile.TileChatBox;
import pl.asie.computronics.tile.TileIronNote;
import pl.asie.lib.block.BlockBase;

public class BlockChatBox extends BlockMachineSidedIcon {
	private IIcon mSide;
	
	public BlockChatBox() {
		super();
		this.setCreativeTab(Computronics.tab);
		this.setIconName("computronics:chatbox");
		this.setBlockName("computronics.chatBox");
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileChatBox();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getAbsoluteSideIcon(int sideNumber, int metadata) {
		return mSide;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister r) {
		super.registerBlockIcons(r);
		mSide = r.registerIcon("computronics:chatbox_side");
	}
}

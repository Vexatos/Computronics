package pl.asie.computronics.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.tile.TileChatBox;
import pl.asie.computronics.tile.TileIronNote;
import pl.asie.lib.block.BlockBase;

public class BlockChatBox extends BlockMachineSidedIcon {
	private Icon mSide;
	
	public BlockChatBox(int id) {
		super(id);
		this.setCreativeTab(Computronics.tab);
		this.setIconName("computronics:chatbox");
		this.setUnlocalizedName("computronics.chatBox");
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileChatBox();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getAbsoluteSideIcon(int sideNumber, int metadata) {
		return mSide;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister r) {
		super.registerIcons(r);
		mSide = r.registerIcon("computronics:chatbox_side");
	}
}

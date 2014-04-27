package pl.asie.computronics.block;

import java.util.logging.Level;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.tile.TileChatBoxBase;
import pl.asie.computronics.tile.TileChatBoxCC15;
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
	
	// I'm such a cheater.
	@Override
	public int getRenderColor(int meta) {
		return meta >= 8 ? 0xFF60FF : 0xFFFFFF;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		try {
			return Computronics.CHAT_BOX_CLASS.newInstance();
		} catch(Exception e) {
			Computronics.log.log(Level.SEVERE, "Could not instantiate ChatBox, falling back to CC 1.58 API!");
			e.printStackTrace();
			return new TileChatBoxCC15();
		}
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

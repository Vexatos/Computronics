package pl.asie.computronics.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.tile.TileChatBox;
import pl.asie.computronics.tile.TileIronNote;
import pl.asie.lib.block.BlockBase;

public class BlockChatBox extends BlockBase {
	public BlockChatBox(int id) {
		super(id, Material.iron, Computronics.instance);
		this.setIconName("computronics:chatbox");
		this.setUnlocalizedName("computronics.chatBox");
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileChatBox();
	}
}

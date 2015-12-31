package pl.asie.computronics.integration.glibyvc;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import pl.asie.computronics.Computronics;
import pl.asie.lib.block.BlockBase;

public class BlockMicrophoneReceiver extends BlockBase {
    @SideOnly(Side.CLIENT)
    protected IIcon mTop, mSide, mBottom;

    public BlockMicrophoneReceiver() {
        super(Material.iron, Computronics.instance);
        setBlockName("computronics.microphoneReceiver");
        setCreativeTab(Computronics.tab);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float par7, float par8, float par9) {
        if (world.isRemote) {
            return true;
        } else if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemMicrophone) {
            boolean bound = false;
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileMicrophoneReceiver) {
                bound = IntegrationGlibyVoiceChat.microphoneItem.bind(player.getCurrentEquippedItem(), (TileMicrophoneReceiver) tile);
            }

            player.addChatComponentMessage(new ChatComponentTranslation("chat.computronics.microphone.bound." + (bound ? "true" : "false")));
            return true;
        } else {
            return super.onBlockActivated(world, x, y, z, player, side, par7, par8, par9);
        }
    }


    @SideOnly(Side.CLIENT)
    public IIcon getAbsoluteIcon(int side, int metadata) {
        switch(side) {
            case 0: return mBottom;
            case 1: return mTop;
            default: return mSide;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister r) {
        mSide = r.registerIcon("computronics:mic_receiver");
        mTop = r.registerIcon("computronics:machine_top");
        mBottom = r.registerIcon("computronics:machine_bottom");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return new TileMicrophoneReceiver();
    }
}

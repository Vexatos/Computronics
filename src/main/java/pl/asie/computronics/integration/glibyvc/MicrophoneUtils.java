package pl.asie.computronics.integration.glibyvc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import net.minecraftforge.common.DimensionManager;

import pl.asie.computronics.reference.Config;

public final class MicrophoneUtils {
    private MicrophoneUtils() {

    }

    public static ItemStack getMicrophone(EntityPlayer player) {
        for (int i = 0; i < 4; i++) {
            if (player.getCurrentArmor(i) != null && player.getCurrentArmor(i).getItem() instanceof ItemMicrophone) {
                return player.getCurrentArmor(i);
            }
        }
        return null;
    }

    public static TileMicrophoneReceiver getMicrophoneReceiver(EntityPlayer player) {
        if (player == null) {
            return null;
        }

        ItemStack mic = getMicrophone(player);
        if (mic != null && mic.hasTagCompound() && mic.getTagCompound().hasKey("w")) {
            World w = DimensionManager.getWorld(mic.getTagCompound().getInteger("w"));
            if (w != null) {
                Vec3 pos = Vec3.createVectorHelper(
                        mic.getTagCompound().getInteger("x"),
                        mic.getTagCompound().getInteger("y"),
                        mic.getTagCompound().getInteger("z")
                );
                if (pos.squareDistanceTo(player.posX, player.posY, player.posZ) <= Config.GLIBY_RECEIVER_DISTANCE * Config.GLIBY_RECEIVER_DISTANCE) {
                    TileEntity t = w.getTileEntity(
                            mic.getTagCompound().getInteger("x"),
                            mic.getTagCompound().getInteger("y"),
                            mic.getTagCompound().getInteger("z")
                    );
                    if (t instanceof TileMicrophoneReceiver) {
                        return (TileMicrophoneReceiver) t;
                    }
                }
            }
        }

        return null;
    }
}

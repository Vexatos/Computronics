package pl.asie.computronics.integration.glibyvc;

import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;

import pl.asie.computronics.api.audio.IAudioConnection;
import pl.asie.computronics.api.audio.IAudioSource;

public class TileMicrophoneReceiver extends TileEntity implements IAudioConnection, IAudioSource {
    @Override
    public int getSourceId() {
        return -1;
    }

    @Override
    public boolean connectsAudio(ForgeDirection side) {
        return true;
    }
}

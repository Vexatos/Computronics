/*
 * Copyright (c) CovertJaguar, 2011 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at railcraft.wikispaces.com.
 */
package mods.railcraft.api.electricity;

import java.util.HashSet;
import java.util.Set;
import mods.railcraft.api.tracks.ITrackInstance;
import mods.railcraft.api.tracks.ITrackTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class GridTools {

    public static Set<IElectricGrid> getConnectedGridObjects(World world, int x, int y, int z) {
        Set<IElectricGrid> grid = new HashSet<IElectricGrid>();

        IElectricGrid object = getGridObjectFuzzyAt(world, x, y, z - 1);
        if (object != null)
            grid.add(object);

        object = getGridObjectFuzzyAt(world, x, y, z + 1);
        if (object != null)
            grid.add(object);

        object = getGridObjectFuzzyAt(world, x - 1, y, z);
        if (object != null)
            grid.add(object);

        object = getGridObjectFuzzyAt(world, x + 1, y, z);
        if (object != null)
            grid.add(object);

        return grid;
    }

    public static IElectricGrid getGridObjectFuzzyAt(World world, int x, int y, int z) {
        IElectricGrid object = getGridObjectAt(world, x, y, z);
        if (object != null)
            return object;
        object = getGridObjectAt(world, x, y + 1, z);
        if (object != null)
            return object;
        object = getGridObjectAt(world, x, y - 1, z);
        if (object != null)
            return object;
        return null;
    }

    public static IElectricGrid getGridObjectAt(World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile == null)
            return null;
        if (tile instanceof IElectricGrid)
            return (IElectricGrid) tile;
        if (tile instanceof ITrackTile) {
            ITrackInstance track = ((ITrackTile) tile).getTrackInstance();
            if (track instanceof IElectricGrid)
                return (IElectricGrid) track;
        }
        return null;
    }

}

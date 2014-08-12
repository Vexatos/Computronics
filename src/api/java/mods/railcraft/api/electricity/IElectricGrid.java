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
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * Any Electric Track needs to implement this interface on either the track
 * TileEntity or ITrackInstance object.
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public interface IElectricGrid {

    public static final double MAX_CHARGE = 500.0;
    public static final double LOSS = 0.99;
    public static final int SEARCH_INTERVAL = 64;
    public static final int BALANCE_INTERVAL = 4;
    public static final Random rand = new Random();

    public ChargeHandler getChargeHandler();

    public TileEntity getTile();

    public static class ChargeHandler {

        public enum ConnectType {

            TRACK {

                        @Override
                        public Set<IElectricGrid> getConnected(TileEntity tile) {
                            return GridTools.getConnectedGridObjects(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);
                        }

                    },
            WIRE {
                        @Override
                        public Set<IElectricGrid> getConnected(TileEntity tile) {
                            return GridTools.getConnectedGridObjects(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);
                        }

                    },
            FEEDER {
                        @Override
                        public Set<IElectricGrid> getConnected(TileEntity tile) {
                            return GridTools.getConnectedGridObjects(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);
                        }

                    };

            public abstract Set<IElectricGrid> getConnected(TileEntity tile);

        };

        private final IElectricGrid gridObject;
        private final ConnectType type;
        private final Set<ChargeHandler> neighbors = new HashSet<ChargeHandler>();
        private double charge = 0;
        private int clock = rand.nextInt();

        public ChargeHandler(IElectricGrid gridObject, ConnectType type) {
            this.gridObject = gridObject;
            this.type = type;
        }

        /**
         * Averages the charge between two tracks.
         *
         * @param other
         */
        public void balance(ChargeHandler other) {
            double total = charge + other.charge;
            charge = total / 2;
        }

        public boolean addCharge(double charge) {
            if (this.charge < MAX_CHARGE) {
                this.charge += charge;
                return true;
            }
            return false;
        }

        /**
         * Remove up to the requested amount of charge and returns the amount
         * removed.
         *
         * @param request
         * @return charge removed
         */
        public double removeCharge(double request) {
            if (charge >= request) {
                charge -= request;
                return request;
            }
            charge = 0.0;
            return charge;
        }

        public void tick() {
            clock++;
            charge += LOSS;

            if (clock % SEARCH_INTERVAL == 0) {
                neighbors.clear();
                Set<IElectricGrid> tracks = type.getConnected(gridObject.getTile());
                for (IElectricGrid t : tracks) {
                    neighbors.add(t.getChargeHandler());
                }
            }

            if (clock % BALANCE_INTERVAL == 0) {
                Iterator<ChargeHandler> it = neighbors.iterator();
                while (it.hasNext()) {
                    ChargeHandler ch = it.next();
                    if (ch.gridObject.getTile().isInvalid())
                        it.remove();
                }
                for (ChargeHandler t : neighbors) {
                    balance(t);
                }
            }
        }

        public void writeToNBT(NBTTagCompound nbt) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setDouble("charge", charge);
            nbt.setTag("chargeHandler", tag);
        }

        public void readFromNBT(NBTTagCompound nbt) {
            NBTTagCompound tag = nbt.getCompoundTag("chargeHandler");
            charge = tag.getDouble("charge");
        }

    }

}

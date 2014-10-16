package pl.asie.computronics.oc;

import li.cil.oc.api.Network;
import li.cil.oc.api.driver.EnvironmentHost;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.network.Connector;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.ManagedEnvironment;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.util.ParticleUtils;

import java.util.Random;

public class DriverCardFX extends ManagedEnvironment {
    
    protected final EnvironmentHost container;

    public DriverCardFX(EnvironmentHost container) {
        this.container = container;
        this.setNode(Network.newNode(this, Visibility.Neighbors).
                withComponent("particle").
                withConnector(Computronics.FX_ENERGY_COST * 32).
                create());
    }

    // We allow spawning particle effects. The parameters are the particle
    // name, the position relative to the block the card is in to spawn
    // the particle at, as well as - optionally - the initial velocity.

    @Callback(direct = true, limit = 16)
    public Object[] spawn(Context context, Arguments args) {
        String name = args.checkString(0);

        if (name.length() > Short.MAX_VALUE) {
            return new Object[]{false, "name too long"};
        }

        Random rng = container.world().rand;
        double x = container.xPosition() + 0.5 + args.checkDouble(1);
        double y = container.yPosition() + 0.5 + args.checkDouble(2);
        double z = container.zPosition() + 0.5 + args.checkDouble(3);
        double defaultv = (rng.nextDouble() * 0.1);
        if(args.count() >= 5) defaultv = args.checkDouble(4);
        double vx = defaultv * rng.nextGaussian();
        double vy = defaultv * rng.nextGaussian();
        double vz = defaultv * rng.nextGaussian();
        if(args.count() >= 7) {
        	vx = args.checkDouble(4);
        	vy = args.checkDouble(5);
        	vz = args.checkDouble(6);
        }
        ParticleUtils.sendParticlePacket(name, container.world(), x, y, z, vx, vy, vz);
		((Connector) this.node()).tryChangeBuffer(0 - Computronics.FX_ENERGY_COST);
        return new Object[]{true};
    }
}

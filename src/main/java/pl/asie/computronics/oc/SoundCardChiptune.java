package pl.asie.computronics.oc;

import net.minecraft.tileentity.TileEntity;
import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Robot;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.ManagedEnvironment;

public class SoundCardChiptune extends ManagedEnvironment {
	private final TileEntity entity;
	public SoundCardChiptune(TileEntity entity) {
		this.entity = entity;
		this.node = Network.newNode(this, Visibility.Network).withComponent("sound_card", Visibility.Neighbors).create();
	}
}

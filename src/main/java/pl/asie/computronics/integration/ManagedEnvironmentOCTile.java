package pl.asie.computronics.integration;

import li.cil.oc.api.Network;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.ManagedEnvironment;

public class ManagedEnvironmentOCTile<T> extends ManagedEnvironment {
	protected final T tile;
	
	public ManagedEnvironmentOCTile(final T tile, final String name) {
		this.tile = tile;
		node = Network.newNode(this, Visibility.Network).withComponent(name, Visibility.Network).create();
	}
}

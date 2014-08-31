package pl.asie.computronics.integration;

import java.util.List;

import net.mcft.copy.betterstorage.api.ICrateStorage;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import li.cil.oc.api.Network;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.ManagedEnvironment;

public class ManagedEnvironmentOCTile<T> extends ManagedEnvironment {
	protected final T tile;
	
	public ManagedEnvironmentOCTile(final T tile, final String name) {
		this.tile = tile;
		node = Network.newNode(this, Visibility.Network).withComponent(name, Visibility.Network).create();
	}
}

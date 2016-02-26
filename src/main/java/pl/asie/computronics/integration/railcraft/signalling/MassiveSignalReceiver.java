package pl.asie.computronics.integration.railcraft.signalling;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.api.core.WorldCoordinate;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.api.signals.SignalController;
import mods.railcraft.api.signals.SignalReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import pl.asie.computronics.util.collect.SimpleInvertibleDualMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Vexatos
 */
public class MassiveSignalReceiver extends SignalReceiver {

	private final Map<WorldCoordinate, SignalAspect> aspects = new HashMap<WorldCoordinate, SignalAspect>();
	private final SimpleInvertibleDualMap<String, WorldCoordinate> signalNames = SimpleInvertibleDualMap.create();
	private SignalAspect visualAspect;
	private SignalAspect mostRestrictive;

	public MassiveSignalReceiver(String locTag, TileEntity tile) {
		super(locTag, tile, 32);
	}

	public SignalAspect getAspectFor(WorldCoordinate coord) {
		return this.aspects.get(coord);
	}

	public SignalAspect getVisualAspect() {
		return this.visualAspect != null ? this.visualAspect : (this.visualAspect = this.getMostRestrictiveAspect());
	}

	public void setVisualAspect(SignalAspect aspect) {
		this.visualAspect = aspect;
	}

	public String getNameFor(WorldCoordinate coord) {
		return this.signalNames.inverse().get(coord);
	}

	public Collection<WorldCoordinate> getCoordsFor(String name) {
		return this.signalNames.get(name);
	}

	public SignalAspect getMostRestrictiveAspectFor(String name) {
		SignalAspect mostRestrictive = null;
		for(WorldCoordinate coord : this.signalNames.get(name)) {
			if(mostRestrictive == null) {
				mostRestrictive = this.aspects.get(coord);
			} else {
				mostRestrictive = SignalAspect.mostRestrictive(mostRestrictive, this.aspects.get(coord));
			}
		}
		return mostRestrictive;
	}

	public String getNameFor(SignalController con) {
		String name = this.signalNames.inverse().get(con.getCoords());
		if(name == null) {
			name = con.getName();
			this.signalNames.put(name, con.getCoords());
		}
		return name;
	}

	public SignalAspect getMostRestrictiveAspect() {
		if(this.mostRestrictive != null) {
			return this.mostRestrictive;
		}
		SignalAspect mostRestrictive = null;
		for(SignalAspect aspect : this.aspects.values()) {
			if(mostRestrictive == null) {
				mostRestrictive = aspect;
			} else {
				mostRestrictive = SignalAspect.mostRestrictive(mostRestrictive, aspect);
			}
		}
		return this.mostRestrictive = mostRestrictive != null ? mostRestrictive : SignalAspect.BLINK_RED;
	}

	public void onControllerAspectChange(SignalController con, SignalAspect aspect) {
		WorldCoordinate coords = con.getCoords();
		SignalAspect oldAspect = this.aspects.get(coords);
		if(oldAspect != aspect) {
			this.aspects.put(coords, aspect);
			this.mostRestrictive = null;
			super.onControllerAspectChange(con, aspect);
		}
		if(!signalNames.containsEntry(con.getName(), coords)) {
			signalNames.put(con.getName(), coords);
		}
	}

	@Override
	public void cleanPairings() {
		super.cleanPairings();
		Collection<WorldCoordinate> pairs = getPairs();
		if(this.aspects.keySet().retainAll(pairs)) {
			this.mostRestrictive = null;
		}
		this.signalNames.retainAllValues(pairs);
	}

	@Override
	public void clearPairings() {
		super.clearPairings();
		this.aspects.clear();
		this.mostRestrictive = null;
		this.signalNames.clear();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void removePair(int x, int y, int z) {
		super.removePair(x, y, z);
		Collection<WorldCoordinate> pairs = getPairs();
		if(this.aspects.keySet().retainAll(pairs)) {
			this.mostRestrictive = null;
		}
		this.signalNames.retainAllValues(pairs);
	}

	protected void saveNBT(NBTTagCompound data) {
		super.saveNBT(data);
		NBTTagList list = new NBTTagList();

		for(Map.Entry<WorldCoordinate, SignalAspect> entry : this.aspects.entrySet()) {
			NBTTagCompound tag = new NBTTagCompound();
			WorldCoordinate key = entry.getKey();
			tag.setIntArray("coords", new int[] { key.dimension, key.x, key.y, key.z });
			tag.setByte("aspect", (byte) entry.getValue().ordinal());
			String s = signalNames.inverse().get(key);
			if(s != null) {
				tag.setString("name", s);
			}
			list.appendTag(tag);
		}
		data.setTag("aspects", list);
	}

	protected void loadNBT(NBTTagCompound data) {
		super.loadNBT(data);
		NBTTagList list = data.getTagList("aspects", Constants.NBT.TAG_COMPOUND);

		for(byte entry = 0; entry < list.tagCount(); ++entry) {
			NBTTagCompound tag = list.getCompoundTagAt(entry);
			int[] c = tag.getIntArray("coords");
			WorldCoordinate coord = new WorldCoordinate(c[0], c[1], c[2], c[3]);
			this.aspects.put(coord, SignalAspect.fromOrdinal(data.getByte("aspect")));
			if(tag.hasKey("name")) {
				signalNames.put(tag.getString("name"), coord);
			}
		}
		this.mostRestrictive = null;
	}
}

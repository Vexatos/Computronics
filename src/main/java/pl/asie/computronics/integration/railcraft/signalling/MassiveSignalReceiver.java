package pl.asie.computronics.integration.railcraft.signalling;

import mods.railcraft.api.core.WorldCoordinate;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.api.signals.SignalController;
import mods.railcraft.api.signals.SignalReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Vexatos
 */
public class MassiveSignalReceiver extends SignalReceiver {

	private final Map<WorldCoordinate, SignalAspect> aspects = new HashMap<WorldCoordinate, SignalAspect>();

	public MassiveSignalReceiver(String locTag, TileEntity tile) {
		super(locTag, tile, 32);
	}

	public SignalAspect getAspectFor(WorldCoordinate coord) {
		return this.aspects.get(coord);
	}

	public void onControllerAspectChange(SignalController con, SignalAspect aspect) {
		WorldCoordinate coords = con.getCoords();
		SignalAspect oldAspect = this.aspects.get(coords);
		if(oldAspect != aspect) {
			this.aspects.put(coords, aspect);
			super.onControllerAspectChange(con, aspect);
		}
	}

	@Override
	public void cleanPairings() {
		super.cleanPairings();
		this.aspects.keySet().retainAll(getPairs());
	}

	protected void saveNBT(NBTTagCompound data) {
		super.saveNBT(data);
		NBTTagList list = new NBTTagList();

		for(Map.Entry<WorldCoordinate, SignalAspect> entry : this.aspects.entrySet()) {
			NBTTagCompound tag = new NBTTagCompound();
			WorldCoordinate key = entry.getKey();
			tag.setIntArray("coords", new int[] { key.dimension, key.x, key.y, key.z });
			tag.setByte("aspect", (byte) entry.getValue().ordinal());
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
			this.aspects.put(new WorldCoordinate(c[0], c[1], c[2], c[3]), SignalAspect.fromOrdinal(data.getByte("aspect")));
		}
	}
}

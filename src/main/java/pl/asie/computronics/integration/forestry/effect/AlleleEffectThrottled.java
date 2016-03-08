package pl.asie.computronics.integration.forestry.effect;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.genetics.IEffectData;
import forestry.core.genetics.EffectData;
import forestry.core.utils.EntityUtil;
import forestry.core.utils.vect.MutableVect;
import forestry.core.utils.vect.Vect;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import pl.asie.computronics.integration.forestry.IntegrationForestry;
import pl.asie.computronics.util.StringUtil;

import java.util.List;

/**
 * @author Vexatos
 */
public abstract class AlleleEffectThrottled implements IAlleleBeeEffect {
	private final int throttle;
	private final String uid;
	private final String unlocalizedName;

	public AlleleEffectThrottled(int throttle, String uid, String unlocalizedName) {
		this.throttle = throttle;
		this.uid = uid;
		this.unlocalizedName = unlocalizedName;
	}

	@Override
	public IEffectData doEffect(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		if(isThrottled(storedData, housing)) {
			return storedData;
		}
		return doEffectThrottled(genome, storedData, housing);
	}

	protected abstract IEffectData doEffectThrottled(IBeeGenome genome, IEffectData storedData, IBeeHousing housing);

	@Override
	public IEffectData doFX(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		return IntegrationForestry.effectNone.doFX(genome, storedData, housing);
	}

	private boolean isThrottled(IEffectData storedData, IBeeHousing housing) {

		int time = storedData.getInteger(0);
		time++;
		storedData.setInteger(0, time);

		if(time < throttle) {
			return true;
		}

		// Reset since we are done throttling.
		storedData.setInteger(0, 0);
		return false;
	}

	@Override
	public boolean isCombinable() {
		return false;
	}

	@Override
	public IEffectData validateStorage(IEffectData storedData) {
		if(storedData instanceof EffectData) {
			return storedData;
		}

		return new EffectData(1, 0);
	}

	@Override
	public String getUID() {
		return "computronics." + this.uid;
	}

	@Override
	public boolean isDominant() {
		return false;
	}

	@Override
	public String getName() {
		return StringUtil.localize(getUnlocalizedName());
	}

	@Override
	public String getUnlocalizedName() {
		return "computronics.allele.effect." + unlocalizedName;
	}

	public static AxisAlignedBB getBounding(IBeeGenome genome, IBeeHousing housing) {
		IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);
		float territoryModifier = beeModifier.getTerritoryModifier(genome, 1.0f);

		MutableVect area = new MutableVect(genome.getTerritory());
		area.multiply(territoryModifier);
		Vect offset = new Vect(area).multiply(-1 / 2.0f);

		Vect min = new Vect(housing.getCoordinates()).add(offset);
		Vect max = min.add(area);

		return AxisAlignedBB.getBoundingBox(min.x, min.y, min.z, max.x, max.y, max.z);
	}

	public static <T extends Entity> List<T> getEntitiesInRange(IBeeGenome genome, IBeeHousing housing, Class<T> entityClass) {
		AxisAlignedBB boundingBox = getBounding(genome, housing);
		return EntityUtil.getEntitiesWithinAABB(housing.getWorld(), entityClass, boundingBox);
	}
}

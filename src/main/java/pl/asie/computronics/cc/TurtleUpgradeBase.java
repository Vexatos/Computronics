package pl.asie.computronics.cc;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.api.turtle.TurtleVerb;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;
import java.util.HashMap;
import java.util.Map;

public abstract class TurtleUpgradeBase implements ITurtleUpgrade {

	private ResourceLocation upgradeID;

	public TurtleUpgradeBase(String id) {
		upgradeID = new ResourceLocation("computronics", id);
	}

	@Override
	public ResourceLocation getUpgradeID() {
		return upgradeID;
	}

	@Override
	public TurtleUpgradeType getType() {
		return TurtleUpgradeType.Peripheral;
	}

	@Override
	public TurtleCommandResult useTool(ITurtleAccess turtle, TurtleSide side,
		TurtleVerb verb, EnumFacing direction) {
		return null;
	}

	@Override
	public void update(ITurtleAccess turtle, TurtleSide side) {
	}

	@Override
	public int getLegacyUpgradeID() {
		return -1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Pair<IBakedModel, Matrix4f> getModel(ITurtleAccess turtle, TurtleSide side) {
		return Pair.of(Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(getCraftingItem()), getStandardBlockMatrixForSide(side));
	}

	private static final Map<TurtleSide, Matrix4f> standardBlockMatrixMap;

	static {
		standardBlockMatrixMap = new HashMap<TurtleSide, Matrix4f>();
		standardBlockMatrixMap.put(TurtleSide.Left, new Matrix4f(0.0F, 0.0F, -1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.5F, 0.0F, -1.0F, 0.0F, 1.40625F, 0.0F, 0.0F, 0.0F, 2F));
		standardBlockMatrixMap.put(TurtleSide.Right, new Matrix4f(0.0F, 0.0F, -1.0F, 2.0F, 1.0F, 0.0F, 0.0F, 0.5F, 0.0F, -1.0F, 0.0F, 1.40625F, 0.0F, 0.0F, 0.0F, 2F));
	}

	protected Matrix4f getStandardBlockMatrixForSide(TurtleSide side) {
		return standardBlockMatrixMap.get(side);
	}
}

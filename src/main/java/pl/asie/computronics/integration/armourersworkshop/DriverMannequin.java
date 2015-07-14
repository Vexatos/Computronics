package pl.asie.computronics.integration.armourersworkshop;

import java.lang.reflect.Field;

import net.minecraft.world.World;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverTileEntity;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;
import pl.asie.computronics.reference.Names;
import riskyken.armourersWorkshop.common.data.BipedRotations;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;

public class DriverMannequin {
	public static class OCDriver extends DriverTileEntity {

		public class InternalManagedEnvironment extends ManagedEnvironmentOCTile<TileEntityMannequin> {
			public InternalManagedEnvironment(TileEntityMannequin tile) {
				super(tile, Names.AW_Mannequin);
			}

			private BipedRotations.BipedPart getPart(String s) {
				try {
					Field f = BipedRotations.class.getField(s);
					Object o = f.get(tile.getBipedRotations());
					if (o instanceof BipedRotations.BipedPart) {
						return (BipedRotations.BipedPart) o;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			private void updateMannequin() {
				tile.markDirty();
				tile.getWorldObj().markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
			}

			@Callback(doc = "function(string, number, number, number); Set the rotation of the mannequin.")
			public Object[] setRotation(Context c, Arguments a) {
				if (a.isString(0) && a.isDouble(1)) {
					BipedRotations.BipedPart part = getPart(a.checkString(0));
					if (part != null) {
						part.rotationX = (float) a.checkDouble(1);
						part.rotationY = (float) (a.isDouble(2) ? a.checkDouble(2) : part.rotationX);
						part.rotationZ = (float) (a.isDouble(3) ? a.checkDouble(3) : part.rotationY);
						updateMannequin();
					}
				}
				return new Object[]{};
			}

			@Callback(doc = "function(string, number); Set the X rotation of the mannequin.")
			public Object[] setRotationX(Context c, Arguments a) {
				if (a.isString(0) && a.isDouble(1)) {
					BipedRotations.BipedPart part = getPart(a.checkString(0));
					if (part != null) {
						part.rotationX = (float) a.checkDouble(1);
						updateMannequin();
					}
				}
				return new Object[]{};
			}

			@Callback(doc = "function(string, number); Set the Y rotation of the mannequin.")
			public Object[] setRotationY(Context c, Arguments a) {
				if (a.isString(0) && a.isDouble(1)) {
					BipedRotations.BipedPart part = getPart(a.checkString(0));
					if (part != null) {
						part.rotationY = (float) a.checkDouble(1);
						updateMannequin();
					}
				}
				return new Object[]{};
			}

			@Callback(doc = "function(string, number); Set the Z rotation of the mannequin.")
			public Object[] setRotationZ(Context c, Arguments a) {
				if (a.isString(0) && a.isDouble(1)) {
					BipedRotations.BipedPart part = getPart(a.checkString(0));
					if (part != null) {
						part.rotationZ = (float) a.checkDouble(1);
						updateMannequin();
					}
				}
				return new Object[]{};
			}

			@Callback(doc = "function(string):number, number, number; Get the rotation of the mannequin.")
			public Object[] getRotation(Context c, Arguments a) {
				if (a.isString(0)) {
					BipedRotations.BipedPart part = getPart(a.checkString(0));
					if (part != null) {
						return new Object[]{part.rotationX, part.rotationY, part.rotationZ};
					}
				}
				return new Object[]{0.0, 0.0, 0.0};
			}

			@Callback(doc = "function(string):number; Get the X rotation of the mannequin.")
			public Object[] getRotationX(Context c, Arguments a) {
				if (a.isString(0)) {
					BipedRotations.BipedPart part = getPart(a.checkString(0));
					if (part != null) {
						return new Object[]{part.rotationX};
					}
				}
				return new Object[]{0.0};
			}

			@Callback(doc = "function(string):number; Get the Y rotation of the mannequin.")
			public Object[] getRotationY(Context c, Arguments a) {
				if (a.isString(0)) {
					BipedRotations.BipedPart part = getPart(a.checkString(0));
					if (part != null) {
						return new Object[]{part.rotationY};
					}
				}
				return new Object[]{0.0, 0.0, 0.0};
			}

			@Callback(doc = "function(string):number; Get the Z rotation of the mannequin.")
			public Object[] getRotationZ(Context c, Arguments a) {
				if (a.isString(0)) {
					BipedRotations.BipedPart part = getPart(a.checkString(0));
					if (part != null) {
						return new Object[]{part.rotationZ};
					}
				}
				return new Object[]{0.0, 0.0, 0.0};
			}
		}

		@Override
		public Class<?> getTileEntityClass() {
			return TileEntityMannequin.class;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
			return new InternalManagedEnvironment((TileEntityMannequin) world.getTileEntity(x, y, z));
		}
	}
}

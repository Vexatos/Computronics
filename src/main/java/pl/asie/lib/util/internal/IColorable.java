package pl.asie.lib.util.internal;

/**
 * @author Vexatos
 */
public interface IColorable {

	boolean canBeColored();

	int getColor();

	int getDefaultColor();

	void setColor(int color);
}

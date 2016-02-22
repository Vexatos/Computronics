package pl.asie.computronics.integration.buildcraft.statements.triggers;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;
import buildcraft.api.statements.ITriggerExternal;
import buildcraft.api.statements.StatementManager;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.tile.TapeDriveState.State;
import pl.asie.computronics.util.StringUtil;

/**
 * @author Vexatos
 */
public enum Triggers implements ITriggerExternal {
	Computer_Running("computer_running", new TriggerComputer.Running()),
	Computer_Stopped("computer_stopped", new TriggerComputer.Stopped()),
	TapeDrive_Playing("tape_drive_playing", new TriggerTapeDrive(State.PLAYING)),
	TapeDrive_Stopped("tape_drive_stopped", new TriggerTapeDrive(State.STOPPED)),
	TapeDrive_Rewinding("tape_drive_rewinding", new TriggerTapeDrive(State.REWINDING)),
	TapeDrive_Forwarding("tape_drive_forwarding", new TriggerTapeDrive(State.FORWARDING));

	public static final Triggers[] VALUES = values();
	private String tag;
	private IComputronicsTrigger trigger;
	private IIcon icon;

	Triggers(String tag, IComputronicsTrigger trigger) {
		this.tag = tag;
		this.trigger = trigger;
	}

	public static void initialize() {
		for(Triggers trigger : VALUES) {
			StatementManager.registerStatement(trigger);
		}
	}

	@Override
	public boolean isTriggerActive(TileEntity tile, ForgeDirection side, IStatementContainer container, IStatementParameter[] statements) {
		return trigger.isTriggerActive(tile, side, container, statements);
	}

	@Override
	public String getUniqueTag() {
		return "computroncis:trigger." + tag;
	}

	@Override
	public IIcon getIcon() {
		return this.icon;
	}

	@Override
	public void registerIcons(IIconRegister iconRegister) {
		this.icon = iconRegister.registerIcon("computronics:buildcraft/triggers/trigger." + this.tag);
	}

	@Override
	public int maxParameters() {
		return 0;
	}

	@Override
	public int minParameters() {
		return 0;
	}

	@Override
	public IStatementParameter createParameter(int i) {
		return null;
	}

	@Override
	public String getDescription() {
		return StringUtil.localize("tooltip.computronics.gate.trigger." + this.tag);
	}

	@Override
	public ITriggerExternal rotateLeft() {
		return this;
	}
}

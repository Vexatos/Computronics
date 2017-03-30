package pl.asie.computronics.integration.railcraft;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.IRailcraftBlock;
import mods.railcraft.common.blocks.IRailcraftBlockContainer;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.blocks.machine.wayobjects.boxes.BlockMachineSignalBox;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.integration.railcraft.tile.TileDigitalControllerBox;
import pl.asie.computronics.integration.railcraft.tile.TileDigitalReceiverBox;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Vexatos
 */
public enum SignalTypes implements IEnumMachine<SignalTypes> {
	DigitalReceiver("digital_receiver_box", Computronics.railcraft.digitalBox, TileDigitalReceiverBox.class),
	DigitalController("digital_controller_box", Computronics.railcraft.digitalBox, TileDigitalControllerBox.class);

	public static final SignalTypes[] VALUES = values();
	private final Definition def;
	private ToolTip tip;
	//private Block block;

	SignalTypes(String tag, Block block, Class<? extends TileMachineBase> tile) {
		//this.block = block;
		this.def = new Definition(null, tag, tile);
	}

	@Override
	public Definition getDef() {
		return this.def;
	}

	@Override
	public String getTag() {
		return "tile.computronics." + this.getBaseTag();
	}

	@Override
	public String getName() {
		return this.getBaseTag();
	}

	@Override
	public String getLocalizationTag() {
		return this.getTag();
	}

	@Nullable
	@Override
	public ToolTip getToolTip(ItemStack itemStack, EntityPlayer entityPlayer, boolean b) {
		if(this.tip != null) {
			return this.tip;
		} else {
			String tipTag = this.getLocalizationTag() + ".tips";
			if(LocalizationPlugin.hasTag(tipTag)) {
				this.tip = ToolTip.buildToolTip(tipTag);
			}

			return this.tip;
		}
	}

	@Override
	public boolean isEnabled() {
		return this.block() != null;
	}

	@Override
	public IRailcraftBlockContainer getContainer() {
		return new IRailcraftBlockContainer() {
			@Nullable
			@Override
			public IBlockState getState(@Nullable IVariantEnum variant) {
				return block() instanceof IRailcraftBlock ? ((IRailcraftBlock) block()).getState(variant) : this.getDefaultState();
			}

			@Nullable
			@Override
			public Block block() {
				return Computronics.railcraft.digitalBox;
			}

			@Nullable
			@Override
			public Item item() {
				Block block = block();
				return block != null ? Item.getItemFromBlock(block) : null;
			}

			@Nullable
			@Override
			public IBlockState getDefaultState() {
				return block() == null ? null : block().getDefaultState();
			}

			@Override
			public boolean isEqual(@Nullable ItemStack stack) {
				return stack != null && block() != null && InvTools.getBlockFromStack(stack) == block();
			}

			@Override
			public String getBaseTag() {
				return SignalTypes.this.getBaseTag();
			}

			@Override
			public Optional<IRailcraftBlock> getObject() {
				return Optional.ofNullable((IRailcraftBlock) block());
			}

			@Override
			public boolean isEnabled() {
				return SignalTypes.this.isEnabled();
			}

			@Override
			public boolean isLoaded() {
				return block() != null;
			}
		};
	}

	private static final List<SignalTypes> creativeList = new ArrayList<SignalTypes>();

	public static List<SignalTypes> getCreativeList() {
		return creativeList;
	}

	static {
		for(SignalTypes variant : VALUES) {
			variant.def.passesLight = true;
			creativeList.add(variant);
		}

		BlockMachineSignalBox.connectionsSenders.add(DigitalReceiver);
	}
}

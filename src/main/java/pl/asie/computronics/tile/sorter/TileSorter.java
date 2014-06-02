package pl.asie.computronics.tile.sorter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import pl.asie.computronics.api.ISortingOutputHandler;
import pl.asie.lib.block.TileEntityBase;
import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;

@Optional.InterfaceList({
	@Optional.Interface(iface = "buildcraft.api.transport.IPipeTile", modid = "BuildCraft|Core"),
	@Optional.Interface(iface = "buildcraft.api.transport.IPipeConnection", modid = "BuildCraft|Core"),
	@Optional.Interface(iface = "li.cil.li.oc.network.SimpleComponent", modid = "OpenComputers")
})
public class TileSorter extends TileEntityBase implements SimpleComponent, IPipeTile, IPipeConnection {
	private IInventory inventory = null;
	private ForgeDirection inputSide;
	private Mode mode = Mode.MANUAL;
	
	private TileEntity input, output;
	private ISortingOutputHandler outputHandler;
	
	private ArrayList<ISortingOutputHandler> outputHandlerList = new ArrayList<ISortingOutputHandler>();
	
	public TileSorter() {
		if(Loader.isModLoaded("BuildCraft|Core")) addSortingHandler(new SortingHandlerBC());
	}
	
	public void addSortingHandler(Object sortingHandler) {
		if(sortingHandler instanceof ISortingOutputHandler)
			outputHandlerList.add((ISortingOutputHandler)sortingHandler);
	}
	
	public enum Mode {
		MANUAL,
		AUTOMATIC
	};
	
	// Portable functions
	
	public int getInventorySize() {
		return inventory != null ? inventory.getSizeInventory() : 0;
	}
	
	public ItemStack getInventoryStack(int slot) {
		if(slot < 0 || slot >= getInventorySize()) return null;
		ItemStack stack = inventory.getStackInSlot(slot);
		if(inventory instanceof ISidedInventory) {
			ISidedInventory sided = (ISidedInventory)inventory;
			// Sided inventory checks
			if(!sided.canExtractItem(slot, stack, inputSide.ordinal())) return null;
		}
		if(stack == null || stack.getItem() == null || stack.stackSize == 0) stack = null;
		return stack;
	}
	
	public Map<String, Object> getInventorySlot(int slot) {
		ItemStack stack = getInventoryStack(slot);
		if(stack == null) return null;
		
		HashMap<String, Object> data = new HashMap<String, Object>();
		
		data.put("size", stack.stackSize);
		data.put("item", stack.getUnlocalizedName());
		data.put("damage", stack.getItemDamage());
		return data;
	}
	
	public boolean pull(int slot) {
		if(output == null || inventory == null) return false;
		ItemStack stack = getInventoryStack(slot);
		if(stack == null) return false;
		
		int amount = outputHandler.output(output, stack.copy(), false);
		if(amount > 0) inventory.decrStackSize(slot, amount);
		
		return amount > 0;
	}
	
	// Internal handlery
	
	// BuildCraft - input
	
	@Optional.Method(modid = "BuildCraft|Core")
	public boolean findInventoryBC(TileEntity te, ForgeDirection side) {
		if(te instanceof IPipeTile && ((IPipeTile)te).getPipeType() == PipeType.ITEM) {
			this.input = te;
			this.inputSide = side;
			return true;
		}
		return false;
	}
	
	@Override
	@Optional.Method(modid = "BuildCraft|Core")
	public PipeType getPipeType() {
		return PipeType.ITEM;
	}

	@Override
	@Optional.Method(modid = "BuildCraft|Core")
	public int injectItem(ItemStack stack, boolean doAdd, ForgeDirection from) {
		if(from == ForgeDirection.UP || from == ForgeDirection.DOWN) return 0;
		return outputHandler.output(output, stack, !doAdd);
	}

	@Override
	@Optional.Method(modid = "BuildCraft|Core")
	public boolean isPipeConnected(ForgeDirection with) {
		return (with != ForgeDirection.DOWN);
	}

	@Override
	@Optional.Method(modid = "BuildCraft|Core")
	public ConnectOverride overridePipeConnection(PipeType type,
			ForgeDirection with) {
		return ((with != ForgeDirection.DOWN) && (type == PipeType.ITEM)) ? ConnectOverride.CONNECT : ConnectOverride.DISCONNECT;
	}
	
	// Vanilla - input
	
	private boolean findInventory() {
		for(int i = 2; i < 6; i++) {
			ForgeDirection side = ForgeDirection.getOrientation(i);
			Block block = Block.blocksList[worldObj.getBlockId(xCoord + side.offsetX, yCoord + side.offsetY, zCoord + side.offsetZ)];
			int meta = worldObj.getBlockMetadata(xCoord + side.offsetX, yCoord + side.offsetY, zCoord + side.offsetZ);
			if(block != null && block.hasTileEntity(meta)) {
				TileEntity te = worldObj.getBlockTileEntity(xCoord + side.offsetX, yCoord + side.offsetY, zCoord + side.offsetZ);
				// Check for blocked mods
				if(te.getClass().getName().startsWith("li.cil.oc")) continue;
				
				// Check for known types
				if(Loader.isModLoaded("BuildCraft|Core")) {
					if(findInventoryBC(te, side)) return true;
				}
				if(te instanceof IInventory) {
					this.inventory = (IInventory)te;
					this.input = te;
					this.inputSide = side;
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean findOutput() {
		int yPos = yCoord + 1;
		Block block = Block.blocksList[worldObj.getBlockId(xCoord, yPos, zCoord)];
		int meta = worldObj.getBlockMetadata(xCoord, yPos, zCoord);
		if(block != null && block.hasTileEntity(meta)) {
			TileEntity tile = worldObj.getBlockTileEntity(xCoord, yPos, zCoord);
			for(ISortingOutputHandler handler: outputHandlerList)
				if(handler.isOutputtable(tile)) {
					this.outputHandler = handler;
					this.output = tile;
					return true;
				}
		}
		return false;
	}
	
	@Override
	public boolean canUpdate() { return false; }
	
	public void update() {
		this.inventory = null;
		this.input = null;
		this.output = null;
		
		boolean foundInventory = findInventory();
		boolean foundOutput = findOutput();
		this.mode = this.inventory != null ? Mode.MANUAL : Mode.AUTOMATIC;
		
		int newMeta = (foundInventory || foundOutput) ? 8 : 0;
		int oldMeta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord) & 7;
		try {
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, oldMeta | newMeta, 2);
		} catch(Exception e) {
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	// OpenComputers
	
	@Override
	@Optional.Method(modid="OpenComputers")
	public String getComponentName() {
		return "sorter";
	}
	
	@Callback(direct = true)
	@Optional.Method(modid="OpenComputers")
	public Object[] getInventorySize(Context ctx, Arguments args) {
		return new Object[]{getInventorySize()};
	}
	
	@Callback(direct = true)
	@Optional.Method(modid="OpenComputers")
	public Object[] getInventoryStack(Context ctx, Arguments args) {
		if(args.count() < 1 || !args.isInteger(0)) return null;
		return new Object[]{getInventorySlot(args.checkInteger(0))};
	}
	
	@Callback(direct = true)
	@Optional.Method(modid="OpenComputers")
	public Object[] pull(Context ctx, Arguments args) {
		if(args.count() < 1 || !args.isInteger(0)) return null;
		return new Object[]{pull(args.checkInteger(0))};
	}
	
	@Override
	public void validate() {
		super.validate();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	// Whoever decided BC should need these on 1.6.4 is silly to say the least.
	
	@Override
	public boolean isSolidOnSide(ForgeDirection side) {
		return true;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource,
			boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return null;
	}

	@Override
	public IPipe getPipe() {
		return null;
	}
}

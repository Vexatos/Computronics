package pl.asie.computronics.block;

public abstract class BlockMachineSidedIcon extends BlockPeripheral {

	//private String sidingType;

	public BlockMachineSidedIcon(String sidingType, String documentationName, Rotation rotation) {
		super(documentationName, rotation);
		/*this.sidingType = sidingType;
		if(sidingType.equals("bundled") && !Mods.isLoaded(Mods.RedLogic) && !Mods.isLoaded(Mods.ProjectRed)) {
			this.sidingType = "";
		}*/
	}

	public BlockMachineSidedIcon(String documentationName, Rotation rotation) {
		this("", documentationName, rotation);
	}
}

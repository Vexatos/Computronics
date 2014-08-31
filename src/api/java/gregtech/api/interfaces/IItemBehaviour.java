package gregtech.api.interfaces;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IItemBehaviour<E extends Item> {
	public boolean onLeftClickEntity(E aItem, ItemStack aStack, EntityPlayer aPlayer, Entity aEntity);
	public boolean onItemUse(E aItem, ItemStack aStack, EntityPlayer aPlayer, World aWorld, int aX, int aY, int aZ, int aSide, float hitX, float hitY, float hitZ);
	public boolean onItemUseFirst(E aItem, ItemStack aStack, EntityPlayer aPlayer, World aWorld, int aX, int aY, int aZ, int aSide, float hitX, float hitY, float hitZ);
    public ItemStack onItemRightClick(E aItem, ItemStack aStack, World aWorld, EntityPlayer aPlayer);
    public List<String> getAdditionalToolTips(List<String> aList, ItemStack aStack);
    public void onUpdate(ItemStack aStack, World aWorld, Entity aPlayer, int aTimer, boolean aIsInHand);
}
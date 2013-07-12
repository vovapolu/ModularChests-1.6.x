package vovapolu.modularchests.items;

import vovapolu.modularchests.ModularChestTileEntity;
import vovapolu.modularchests.ModularChests;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class StackSizeUpgradeItem extends ModularChestUpgradeItem {

	public StackSizeUpgradeItem(int id, String name, String aIconName, String aTextureName) {
		super(id, name, aIconName, aTextureName);
	}

	@Override
	public void onUseItem(ModularChestTileEntity tileEnity,
			EntityPlayer player, World world, int side) {
		if (tileEnity == null)
			return;
		tileEnity.setStackLimit(1);
		tileEnity.setInventoryFactor(ModularChests.inventoryFactor);
	}

	@Override
	public void onRemoveItem(ModularChestTileEntity tileEnity,
			EntityPlayer player, World world, int side) {	
		if (tileEnity == null)
			return;
		tileEnity.setStackLimit(64);
		tileEnity.setInventoryFactor(1);
	}

	@Override
	public boolean applyItemToStorage(ModularChestUpgradesStorage storage, EntityPlayer player, int side) {
		return storage.addGlobalItem(this, player);
	}

	@Override
	public boolean isValidSide(int side) {
		return true;
	}

	@Override
	public boolean isGlobalItem() { 
		return true;
	}

	@Override
	public boolean isUniqueItem() { 
		return true;
	}
	
	@Override
	public EnumChatFormatting getChatFormattingColor() {
		return EnumChatFormatting.RED;
	}

}

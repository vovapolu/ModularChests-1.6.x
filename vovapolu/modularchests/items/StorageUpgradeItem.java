package vovapolu.modularchests.items;

import vovapolu.modularchests.ModularChestTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class StorageUpgradeItem extends ModularChestUpgradeItem {
	
	private int addSize;

	public StorageUpgradeItem(int id, String name, String aIconName, String aTextureName, int newAddSize) {
		super(id, name, aIconName, aTextureName);	
		addSize = newAddSize;
	}

	@Override
	public void onUseItem(ModularChestTileEntity tileEnity,
			EntityPlayer player, World world, int side) {	
		if (tileEnity == null)
			return;
		tileEnity.addStorageSlots(addSize);
	}
	
	@Override
	public void onRemoveItem(ModularChestTileEntity tileEnity,
			EntityPlayer player, World world, int side) {
		if (tileEnity == null)
			return;
		tileEnity.removeStorageSlots(addSize);
	}
	
	public boolean isValidSide(int side)
	{
		return side < 4;
	}

	@Override
	public boolean applyItemToStorage(ModularChestUpgradesStorage storage, EntityPlayer player, int side) {	
		return storage.setSideItem(side, this, player);
	}

	@Override
	public boolean isGlobalItem() {
		return false;
	}

	@Override
	public boolean isUniqueItem() {
		return false;
	}
	
	@Override
	public EnumChatFormatting getChatFormattingColor() {
		return EnumChatFormatting.YELLOW;
	}
}

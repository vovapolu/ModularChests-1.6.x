package vovapolu.modularchests.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import vovapolu.modularchests.ModularChestTileEntity;
import vovapolu.modularchests.ScrollContainer;
import vovapolu.modularchests.guimodule.CraftModule;
import vovapolu.modularchests.guimodule.IGuiModule;

public class GUICraftUpgradeItem extends GUIUpgradeItem {

	public GUICraftUpgradeItem(int id, String name, String aIconName,
			String aTextureName) {
		super(id, name, aIconName, aTextureName);
	}

	@Override
	public IGuiModule getNewGuiModule() {	
		return new CraftModule();
	}

	@Override
	public void onUseItem(ModularChestTileEntity tileEntity,
			EntityPlayer player, World world, int side) {	
	}

	@Override
	public void onRemoveItem(ModularChestTileEntity tileEntity,
			EntityPlayer player, World world, int side) {
	}

	@Override
	public boolean applyItemToStorage(ModularChestUpgradesStorage storage,
			EntityPlayer player, int side) {
		return storage.setSideItem(side, this, player);
	}

	@Override
	public boolean isValidSide(int side) {
		return side == 5;
	}

	@Override
	public boolean isGlobalItem() {
		return false;
	}

	@Override
	public boolean isUniqueItem() {		 
		return true; //temporarily
	}

}

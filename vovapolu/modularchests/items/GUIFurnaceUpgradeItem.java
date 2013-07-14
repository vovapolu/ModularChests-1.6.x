package vovapolu.modularchests.items;

import java.awt.Stroke;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import vovapolu.modularchests.ModularChestTileEntity;
import vovapolu.modularchests.guimodule.FurnaceModule;
import vovapolu.modularchests.guimodule.IGuiModule;

public class GUIFurnaceUpgradeItem extends GUIUpgradeItem {

	public GUIFurnaceUpgradeItem(int id, String name, String aIconName,
			String aTextureName) {
		super(id, name, aIconName, aTextureName);
	}

	@Override
	public IGuiModule getNewGuiModule() {
		return new FurnaceModule();
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
		return side == 0;
	}

	@Override
	public boolean isGlobalItem() {
		return false;
	}

	@Override
	public boolean isUniqueItem() {
		return true;
	}

}

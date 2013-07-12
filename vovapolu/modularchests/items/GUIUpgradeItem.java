package vovapolu.modularchests.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import vovapolu.modularchests.ModularChestTileEntity;
import vovapolu.modularchests.ScrollContainer;
import vovapolu.modularchests.guimodule.IGuiModule;

public abstract class GUIUpgradeItem extends ModularChestUpgradeItem {

	public GUIUpgradeItem(int id, String name, String aIconName,
			String aTextureName) {
		super(id, name, aIconName, aTextureName);	
	}
	
	public abstract IGuiModule getNewGuiModule();
}

package vovapolu.modularchests.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CoreUpgradeItem extends Item {

	public CoreUpgradeItem(int id) {
		super(id);
		setMaxStackSize(16);
		setUnlocalizedName("CoreAddItem");
		setCreativeTab(CreativeTabs.tabMisc);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		 this.itemIcon = iconRegister.registerIcon("ModularChests:CoreAddItem");
	}
}

package vovapolu.modularchests.items;

import vovapolu.modularchests.ModularChestRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class ModularChestItemRender implements IItemRenderer {

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		double dx = 0.0, dy = 0.0, dz = 0.0;
		if(type != ItemRenderType.EQUIPPED && type != ItemRenderType.EQUIPPED_FIRST_PERSON)
		{
			dx = -0.5;
			dy = -0.5;
			dz = -0.5;
		}
		
		ModularChestUpgradesStorage storage = new ModularChestUpgradesStorage(null, null);
		if (item.hasTagCompound())
		{
			storage.readFromNBT(item.getTagCompound());
		}
		
		ModularChestRenderer.render(storage, dx, dy, dz, 0.0F, 0.0F, 3, 0.0F);
	}
}

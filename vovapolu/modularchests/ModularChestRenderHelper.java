package vovapolu.modularchests;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.ChestItemRenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;

import com.google.common.collect.Maps;


public class ModularChestRenderHelper extends ChestItemRenderHelper {
	private ModularChestTileEntity tileEntity;
	
    public ModularChestRenderHelper()
    {
    	tileEntity = new ModularChestTileEntity(1, 1);
    }

    @Override
    public void renderChest(Block block, int i, float f)
    {
        if (block == ModularChests.modularChestBlock)
        {
            TileEntityRenderer.instance.renderTileEntityAt(tileEntity, 0.0D, 0.0D, 0.0D, 0.0F);
        }
        else
        {
            super.renderChest(block, i, f);
        }
    }
}
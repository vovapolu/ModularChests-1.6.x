package vovapolu.modularchests.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.renderer.ChestItemRenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import vovapolu.modularchests.CommonProxy;
import vovapolu.modularchests.ModularChestRenderHelper;
import vovapolu.modularchests.ModularChestTileEntity;
import vovapolu.modularchests.client.gui.GuiScrollChest;

public class ClientProxy extends CommonProxy{
	@Override
    public void registerRenderInformation()
    {
        ChestItemRenderHelper.instance = new ModularChestRenderHelper();
    }

    @Override
    public World getClientWorld()
    {
        return FMLClientHandler.instance().getClient().theWorld;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {    	
        TileEntity te = world.getBlockTileEntity(x, y, z);
        if (te != null && te instanceof ModularChestTileEntity)
        {
            return new GuiScrollChest(player, (ModularChestTileEntity) te);
        }
        else
        {
            return null;
        }
    }
}

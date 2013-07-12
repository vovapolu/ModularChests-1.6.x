package vovapolu.modularchests;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class CommonProxy implements IGuiHandler{

	public void registerRenderInformation() {
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		return null;
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int X, int Y, int Z) {
		TileEntity te = world.getBlockTileEntity(X, Y, Z);
		if (te != null && te instanceof ModularChestTileEntity) {
			ModularChestTileEntity icte = (ModularChestTileEntity) te;		
			return new ScrollContainer(player, icte);
		} else {
			return null;
		}
	}

	public World getClientWorld() {
		return null;
	}
}

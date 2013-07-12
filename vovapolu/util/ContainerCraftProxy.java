package vovapolu.util;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class ContainerCraftProxy extends Container {

	private static final Container dummyContainer = new Container() {
		@Override
		public boolean canInteractWith(EntityPlayer entityplayer) {
			return false;
		}
	};
	
	private Container realContainer = dummyContainer;

	public void setRealContainer(Container realContainer)
	{
		this.realContainer = realContainer;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return realContainer.canInteractWith(entityplayer);
	}

	@Override
	public void onCraftMatrixChanged(IInventory par1IInventory) {
		realContainer.onCraftMatrixChanged(par1IInventory);
	}
	
	@Override
	public void detectAndSendChanges() {
		realContainer.detectAndSendChanges();
	}
	
	@Override
	public List getInventory() {
		return realContainer.getInventory();
	}
	
	//TODO add remaining methods
}

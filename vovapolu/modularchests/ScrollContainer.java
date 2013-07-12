package vovapolu.modularchests;

import java.awt.Point;
import java.util.ArrayList;

import vovapolu.modularchests.guimodule.ChestModule;
import vovapolu.modularchests.guimodule.CraftModule;
import vovapolu.modularchests.guimodule.IGuiModule;
import vovapolu.modularchests.guimodule.ModularChestGuiModuleHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ScrollContainer extends Container {

	protected ModularChestTileEntity tileEntity;
	public EntityPlayer player;
	public static final int slotsWidth = 9;
	public static final int slotsSmallWidth = 4;
	public static final int slotsHeight = 5;
	private int prevBeginSlot = -1;
	private int prevActiveModule = -1;
	private int shiftVal = 0;

	public ScrollContainer(EntityPlayer player,
			ModularChestTileEntity te) {
		this.player = player;
		tileEntity = te;
		//moduleHandler = new ModularChestGuiModuleHandler(tileEntity.upgradesStorage, this);
		te.openChest();		
		
		for (int column = 0; column < slotsWidth; column++)
			addSlotToContainer(new ScrollChestSlot(player.inventory, column,
					9 + 18 * column, 170));

		for (int row = 0; row < 3; row++)
			for (int column = 0; column < slotsWidth; column++)
				addSlotToContainer(new ScrollChestSlot(player.inventory, 9 + row
						* slotsWidth + column, 9 + 18 * column, 112 + 18 * row));

		for (int i = 0; i < te.getRealSizeInventory(); i++)
			addSlotToContainer(new ScrollChestSlot(tileEntity, i, -100, -100));
		
		ArrayList<Slot> slots;
		for (IGuiModule module: tileEntity.getModuleHandler().getModules())
		{
			slots = module.createSlots(this);
			module.createInventory(this);
			for(Slot slot: slots)
				addSlotToContainer(slot);
		}

		scrollTo(0.0F);
		updateModule();
	}

	void shiftSlots(int beginSlot) {
		shiftVal = beginSlot;
		beginSlot += 36;
		placeChestSlots();
	}
	
	public int getRealSlotsWidth()
	{
		if (tileEntity.getModuleHandler().getActiveModule().getClass() == ChestModule.class)
			return slotsWidth;
		else 
			return slotsSmallWidth;
	}
	
	public Point getChestStartPoint()
	{
		Point res = new Point();
		if (tileEntity.getModuleHandler().getActiveModule().getClass() == ChestModule.class)
		{
			res.x = 9;
			res.y = 18;
		}
		else
		{
			res.x = 99;
			res.y = 18;
		}
		
		return res;
	}
	
	public void placeChestSlots()
	{
		int slotsWidth = getRealSlotsWidth();
		Point startPoint = getChestStartPoint();
		
		int beginSlot = shiftVal + 36;
		for (int i = 36; i < 36 + tileEntity.getRealSizeInventory(); i++) {
			if (i >= beginSlot && i < beginSlot + slotsWidth * slotsHeight) {
				int column = (i - beginSlot) % slotsWidth;
				int row = (i - beginSlot) / slotsWidth;
				((Slot) inventorySlots.get(i)).xDisplayPosition = startPoint.x + 18 * column;
				((Slot) inventorySlots.get(i)).yDisplayPosition = startPoint.y + 18 * row;
			} else {
				((Slot) inventorySlots.get(i)).xDisplayPosition = -100;
				((Slot) inventorySlots.get(i)).yDisplayPosition = -100;
			}
		}
	}
	
	public void hideModuleSlots()
	{
		for (int i = 36 + tileEntity.getRealSizeInventory(); i < inventorySlots.size(); i++)
		{
			((Slot) inventorySlots.get(i)).xDisplayPosition = -100;
			((Slot) inventorySlots.get(i)).yDisplayPosition = -100;
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tileEntity.isUseableByPlayer(player);
	}

	@Override
	public void onContainerClosed(EntityPlayer entityplayer) {
		super.onContainerClosed(entityplayer);
		tileEntity.closeChest();
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
		ItemStack stack = null;
		Slot slotObject = (Slot) inventorySlots.get(slot);

		if (slotObject != null && slotObject.getHasStack()) {
			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();

			if (slot >= 36) {
				if (!this.mergeItemStack(stackInSlot, 0, 36, true)) {
					return null;
				}
			} else if (!tileEntity.mergeItemStack(stackInSlot)) {
				return null;
			}

			if (stackInSlot.stackSize == 0) {
				slotObject.putStack(null);
			} else {
				slotObject.onSlotChanged();
			}

			if (stackInSlot.stackSize == stack.stackSize) {
				return null;
			}
			slotObject.onPickupFromSlot(player, stackInSlot);
		}
		// updateSlots();
		return stack;
	}
	
	@Override
	public void onCraftMatrixChanged(IInventory par1IInventory) {
		if (tileEntity.getModuleHandler().getActiveModule() instanceof CraftModule)
			((CraftModule)tileEntity.getModuleHandler().getActiveModule()).onCraftMatrixChanged(player.worldObj);
	}
	
	public int getShiftSlot(float shiftVal)
	{
		int slotsCount = tileEntity.getRealSizeInventory();
		int slotsWidth = getRealSlotsWidth();
		
		int scrollSlotsHeight = (slotsCount + slotsWidth - 1) / slotsWidth
				- slotsHeight;
		return (int) (scrollSlotsHeight * shiftVal) * slotsWidth;
	}
	
	public int getShiftRow(float shiftVal)
	{
		return getShiftSlot(shiftVal) / getRealSlotsWidth();
	}	
	
	public float getShiftHeightOfRow()
	{
		int slotsCount = tileEntity.getRealSizeInventory();
		int slotsWidht = getRealSlotsWidth();
		
		int scrollSlotsHeight = (slotsCount + slotsWidth - 1) / slotsWidth
				- slotsHeight;
		return 1.0F / scrollSlotsHeight;
	}

	public boolean scrollTo(float val) {
		int beginslot = getShiftSlot(val);
		if (beginslot < 0)
			beginslot = 0;
		if (beginslot == prevBeginSlot)
			return false;
		prevBeginSlot = beginslot;
		shiftSlots(beginslot);
		return true;
	}
	
	public void updateModule()
	{
		placeChestSlots();
		hideModuleSlots();
		tileEntity.getModuleHandler().getActiveModule().placeSlots(this, 8, 17);
	}

	/*public void updateSlots() {
		for (int i = 0; i < slotsWidth * slotsHeight; i++) {
			Slot slot = (Slot) inventorySlots.get(i);
			slot.putStack(tileEntity.getStackInSlot(i));
			slot.onSlotChanged();
		}
	}*/

	@Override
	public void detectAndSendChanges() {
		if (tileEntity.getModuleHandler().getActiveModuleIndex() != prevActiveModule)
		{
			prevActiveModule = tileEntity.getModuleHandler().getActiveModuleIndex();
			updateModule();
		}
		super.detectAndSendChanges();
	}
	
	public boolean isValidSlot(int slot)
	{
		return shiftVal + slot < tileEntity.getRealSizeInventory();
	}
	
	public int getValidSlots()
	{
		return tileEntity.getRealSizeInventory() - shiftVal;
	}
	
	public static void hideSlot(Slot slot)
	{
		if (slot instanceof ScrollChestSlot)
			((ScrollChestSlot) slot).hideSlot();
		else 
		{
			slot.xDisplayPosition = -100;
			slot.yDisplayPosition = -100;
		}
	}
}

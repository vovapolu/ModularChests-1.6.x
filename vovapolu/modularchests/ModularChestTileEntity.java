package vovapolu.modularchests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import vovapolu.modularchests.block.ModularChestBaseBlock;
import vovapolu.modularchests.guimodule.IGuiModule;
import vovapolu.modularchests.guimodule.ModularChestGuiModuleHandler;
import vovapolu.modularchests.items.ModularChestUpgradeItem;
import vovapolu.modularchests.items.ModularChestUpgradesStorage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class ModularChestTileEntity extends TileEntity implements
		IInventory {

	private ArrayList<ItemStack> inv;
	private int displaySize;	
	private byte facing;
	private int numUsingPlayers;
	private int ticksSinceSync;
	
	public float lidAngle;
	public float prevLidAngle;
	
	private int stackLimit = 64;
	private int inventoryFactor = 1;
	
	public boolean isBreakable;
	public boolean isLocked;
	
	public ModularChestUpgradesStorage upgradesStorage;
	private ModularChestGuiModuleHandler moduleHandler;

	public ModularChestTileEntity(int elemSize, int aDisplaySize) {
		inv = new ArrayList<ItemStack>(elemSize);
		for (int i = 0; i < elemSize; i++)
			inv.add(null);
		displaySize = aDisplaySize;	
		moduleHandler = new ModularChestGuiModuleHandler();
		upgradesStorage = new ModularChestUpgradesStorage(this, moduleHandler);
	}

	public ModularChestTileEntity() {
		this(1, 1);
	}

	@Override
	public int getSizeInventory() {
		return inv.size();
	}
	
	public int getRealSizeInventory()
	{
		return inv.size();
	}
	
	public void setStackLimit(int newStackLimit)
	{
		if (newStackLimit > 64)
			newStackLimit = 64;
		if (newStackLimit < 1)
			newStackLimit = 1;
		stackLimit = newStackLimit;
		for (int i = 0; i < inv.size(); i++)
			if (inv.get(i) != null)
				if (inv.get(i).stackSize > stackLimit)
				{
					ModularChestBaseBlock.dropItem(new ItemStack(inv.get(i).getItem(), 
							inv.get(i).stackSize - stackLimit), worldObj, xCoord, yCoord, zCoord);
					inv.get(i).stackSize = stackLimit;
				}
	}
	
	public void setInventoryFactor(int newFactor)
	{
		if (newFactor > inventoryFactor)
			addSlots(getSizeInventory() * (newFactor - inventoryFactor));
		if (newFactor < inventoryFactor)
			removeSlots(getSizeInventory() * (inventoryFactor - newFactor));
		inventoryFactor = newFactor;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inv.get(slot);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if (slot < inv.size())
		{
			inv.set(slot, stack);
			if (stack != null && stack.stackSize > getInventoryStackLimit()) {
				stack.stackSize = getInventoryStackLimit();
			}
			onInventoryChanged();	
		}
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			if (stack.stackSize <= amt) {
				setInventorySlotContents(slot, null);
			} else {
				stack = stack.splitStack(amt);
				if (stack.stackSize == 0) {
					setInventorySlotContents(slot, null);
				}
			}
			onInventoryChanged();
		}
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			setInventorySlotContents(slot, null);
		}
		return stack;
	}

	@Override
	public int getInventoryStackLimit() {
		return stackLimit;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) == this
				&& player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) < 64 
				&& !isLocked;
	}
	
	public boolean isCanApplyItem(ModularChestUpgradeItem item)
	{
		return !isLocked && numUsingPlayers == 0;
	}

	@Override
	public void openChest() {
		if (numUsingPlayers < 0) {
			numUsingPlayers = 0;
		}

		++numUsingPlayers;
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType().blockID,
				1, numUsingPlayers);
	}

	@Override
	public void closeChest() {
		--numUsingPlayers;
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType().blockID,
				1, numUsingPlayers);
	}

	@Override
	public boolean receiveClientEvent(int id, int val) {
		if (id == 1) {
			numUsingPlayers = val;
			return true;
		} else {
			return super.receiveClientEvent(id, val);
		}
	}
	
	/**
	 * Read all data that writes in writeData
	 */
	
	public void readDataFromNBT(NBTTagCompound tagCompound)
	{
		int newSize = tagCompound.getInteger("ElemSize");
		displaySize = tagCompound.getInteger("DisplaySize");
		facing = tagCompound.getByte("Facing");
		stackLimit = tagCompound.getInteger("StackLimit");
		inventoryFactor = tagCompound.getInteger("Factor");
		isBreakable = tagCompound.getBoolean("isBreakable");
		isLocked = tagCompound.getBoolean("isLocked");
		
		NBTTagList tagList = tagCompound.getTagList("Inventory");
		inv = new ArrayList<ItemStack>(newSize);
		for (int i = 0; i < newSize; i++)
			inv.add(null);			
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) tagList.tagAt(i);
			int slot = tag.getInteger("Slot");
			if (slot >= 0 && slot < inv.size()) {
				inv.set(slot, ItemStack.loadItemStackFromNBT(tag));
			}
		}
		
		upgradesStorage.readFromNBT(tagCompound);
		moduleHandler.readFromNBT(tagCompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		readDataFromNBT(tagCompound);
	}
	
	/**
	 * Writes all data about tile entity expect coordinates 
	 */
	
	public void writeDataToNBT(NBTTagCompound tagCompound)
	{
		tagCompound.setInteger("ElemSize", inv.size());
		tagCompound.setInteger("DisplaySize", displaySize);
		tagCompound.setByte("Facing", facing);
		tagCompound.setInteger("StackLimit", stackLimit);
		tagCompound.setInteger("Factor", inventoryFactor);
		tagCompound.setBoolean("isBreakable", isBreakable);
		tagCompound.setBoolean("isLocked", isLocked);
		
		NBTTagList itemList = new NBTTagList();
		for (int i = 0; i < inv.size(); i++) {
			ItemStack stack = inv.get(i);
			if (stack != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("Slot", i);
				stack.writeToNBT(tag);
				itemList.appendTag(tag);
			}
		}
		tagCompound.setTag("Inventory", itemList);
		
		upgradesStorage.writeToNBT(tagCompound);
		moduleHandler.writeToNBT(tagCompound);
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		writeDataToNBT(tagCompound);
		}

	@Override
	public String getInvName() {
		return "vovapolu.modularchestinventory";
	}

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	public void setFacing(byte chestFacing) {
		facing = chestFacing;
	}

	public byte getFacing() {
		return facing;
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound data = new NBTTagCompound();
		writeToNBT(data);
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 2, data);
	}

	@Override
	public void onDataPacket(INetworkManager netManager,
			Packet132TileEntityData packet) {
		readFromNBT(packet.customParam1);
	}
	
	private void updateChestAngle()
	{
		float padding;

		if (!worldObj.isRemote && numUsingPlayers != 0
				&& (ticksSinceSync + xCoord + yCoord + zCoord) % 200 == 0) {
			numUsingPlayers = 0;
			padding = 5.0F;
			List list = worldObj.getEntitiesWithinAABB(
					EntityPlayer.class,
					AxisAlignedBB.getAABBPool().getAABB(
							(double) ((float) xCoord - padding),
							(double) ((float) yCoord - padding),
							(double) ((float) zCoord - padding),
							(double) ((float) (xCoord + 1) + padding),
							(double) ((float) (yCoord + 1) + padding),
							(double) ((float) (zCoord + 1) + padding)));
			Iterator iterator = list.iterator();

			while (iterator.hasNext()) {
				EntityPlayer entityplayer = (EntityPlayer) iterator.next();

				if (entityplayer.openContainer instanceof ScrollContainer)
					++numUsingPlayers;
			}
		}

		prevLidAngle = lidAngle;
		padding = 0.1F;
		double d0;			

		if (numUsingPlayers > 0 && lidAngle == 0.0F) {
			worldObj.playSoundEffect((double) xCoord + 0.5,
					(double) yCoord + 0.5D, (double) zCoord + 0.5D,
					"random.chestopen", 0.5F,
					worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}

		if (numUsingPlayers == 0 && lidAngle > 0.0F || numUsingPlayers > 0 && lidAngle < 1.0F) {
			float copyLidAngle = lidAngle;

			if (numUsingPlayers > 0) {
				lidAngle += padding;
			} else {
				lidAngle -= padding;
			}

			if (lidAngle > 1.0F) {
				lidAngle = 1.0F;
			}

			float f2 = 0.5F;

			if (lidAngle < 0.5F && copyLidAngle >= 0.5F) {
				worldObj.playSoundEffect((double) xCoord + 0.5D, (double) yCoord + 0.5D, (double) zCoord + 0.5D,
						"random.chestclosed", 0.5F,
						worldObj.rand.nextFloat() * 0.1F + 0.9F);
			}

			if (lidAngle < 0.0F) {
				lidAngle = 0.0F;
			}
		}
	}
	
	private void updateModules() {
		ArrayList<IGuiModule> modules = moduleHandler.getModules();
		for (IGuiModule module: modules)
		{
			if (module.isNeedToUpdate())
				module.updateModule(worldObj.isRemote);
		}
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		++ticksSinceSync;
		
		updateChestAngle();
		updateModules();
	}

	public boolean mergeItemStack(ItemStack stack)
	{		
	        int i = 0;
	        boolean changed = false;
	        
	        ItemStack nowStack;
	
	        if (stack.isStackable())
	        {
	            while (stack.stackSize > 0 &&  i < inv.size())
	            {
	                nowStack = inv.get(i);
	
	                if (nowStack != null && nowStack.itemID == stack.itemID && (!stack.getHasSubtypes() || stack.getItemDamage() == nowStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, nowStack))
	                {
	                    int sum = nowStack.stackSize + stack.stackSize;
	                    int limit = Math.min(stack.getMaxStackSize(), getInventoryStackLimit());
	                
	                    if (sum <= limit)
	                    {
	                        stack.stackSize = 0;
	                        nowStack.stackSize = sum;     
	                        changed = true;
	                    }
	                    else if (nowStack.stackSize < limit)
	                    {
	                        stack.stackSize -= limit - nowStack.stackSize;
	                        nowStack.stackSize = limit;
	                        changed = true;
	                    }
	                }
	
	                ++i;
	            }
	        }
	
	        if (stack.stackSize > 0)
	        {
	            i = 0;
	            while (i < inv.size())
	            {           
	                nowStack = inv.get(i);
	
	                if (nowStack == null)
	                {
	                	ItemStack newStack;
	                	if (stack.stackSize >= getInventoryStackLimit())            
	                		newStack = stack.splitStack(getInventoryStackLimit());
	                	else 
	                	{
	                		newStack = stack.copy();
	                		stack.stackSize = 0;
	                	}
	                    inv.set(i, newStack.copy());                                        
	                    changed = true;                    
	                    
	                    if (stack.stackSize == 0)
	                    	break;
	                }
	                ++i;
	            }
	        }
	        
	        return changed;
	}
	
	public void addSlots(int count)
	{
		for (int i = 0; i < count; i++)
			inv.add(null);
	}
	
	public void removeSlots(int count)
	{	
		if (count > inv.size() - 1)
			count = inv.size() - 1;
		ModularChestBaseBlock.dropItemsInRange(worldObj, xCoord, yCoord, zCoord, inv.size() - count, inv.size() - 1);
		for (int i = 0; i < count; i++)
			inv.remove(inv.size() - 1);
	}

	public void addStorageSlots(int addSize) {
		addSlots(addSize * inventoryFactor);
	}

	public void removeStorageSlots(int addSize) {
		removeSlots(addSize * inventoryFactor);
	}
	
	public ModularChestGuiModuleHandler getModuleHandler()
	{
		return moduleHandler;
	}
	
}

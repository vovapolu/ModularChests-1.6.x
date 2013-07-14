package vovapolu.modularchests.guimodule;

import java.awt.Point;
import java.util.ArrayList;

import net.minecraft.block.BlockFurnace;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import vovapolu.modularchests.ScrollChestSlot;
import vovapolu.modularchests.ScrollContainer;
import vovapolu.util.ImageUtils;

public class FurnaceModule implements IGuiModule {

	private static final Point iconPoint = new Point(20, 0);
	private int furnaceBurnTime;
	private int currentItemBurnTime;
	private int furnaceCookTime;
	private FurnaceInventory inventory = new FurnaceInventory();
	
	private ArrayList<Slot> slots = new ArrayList<Slot>();

	@Override
	public Point getIcon() {
		return iconPoint;
	}

	@Override
	public ArrayList<Slot> createSlots(ScrollContainer container, EntityPlayer player) {
		slots = new ArrayList<Slot>();
		slots.add(new ScrollChestSlot(inventory, 0, -100, -100));
		slots.add(new ScrollChestSlot(inventory, 1, -100, -100));
		slots.add(new SlotFurnace(player, inventory, 2, -100, -100));
		return slots;
	}

	@Override
	public void placeSlots(ScrollContainer container, int x, int y) {
		
		slots.get(0).xDisplayPosition = x + 13;
		slots.get(0).yDisplayPosition = y + 19;
		
		slots.get(1).xDisplayPosition = x + 13;
		slots.get(1).yDisplayPosition = y + 55;
		
		slots.get(2).xDisplayPosition = x + 56;
		slots.get(2).yDisplayPosition = y + 37;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("BurnTime", furnaceBurnTime);
		tag.setInteger("CookTime", furnaceCookTime);
		NBTTagList itemList = new NBTTagList();
		for (int i = 0; i < 3; i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack != null) {
				NBTTagCompound atag = new NBTTagCompound();
				atag.setByte("Slot", (byte) i);
				stack.writeToNBT(atag);
				itemList.appendTag(atag);
			}
		}
		tag.setTag("FurnaceInventory", itemList);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		furnaceBurnTime = tag.getInteger("BurnTime");
		furnaceCookTime = tag.getInteger("CookTime");
		currentItemBurnTime = TileEntityFurnace.getItemBurnTime(inventory.getStackInSlot(1));
		
		NBTTagList tagList = tag.getTagList("FurnaceInventory");	
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound atag = (NBTTagCompound) tagList.tagAt(i);
			byte slot = atag.getByte("Slot");
			if (slot >= 0 && slot < 3) {
				inventory.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(atag));
			}
		}
	}

	@Override
	public void createInventory(ScrollContainer container) {
	}

	@Override
	public void drawBackground(GuiContainer gui, int x, int y) {
		ImageUtils.bindTextureByName("textures/gui/furnaceModule.png");
		gui.drawTexturedModalRect(x, y, 0, 0, 90, 90);
		
		if (isBurning())
        {
            int height = getBurnTimeRemainingScaled(12);
            gui.drawTexturedModalRect(x + 14, y + 39 + 12 - height, 90, 12 - height, 14, height + 1);
        }
		
		int width = getCookProgressScaled(15);
        gui.drawTexturedModalRect(x + 31, y + 40, 90, 13, width + 1, 12);
	}

	@Override
	public void updateModule(boolean isRemote) {

        if (this.furnaceBurnTime > 0)
        {
            --this.furnaceBurnTime;
        }

        if (this.furnaceBurnTime == 0 && this.canSmelt())
        {
            this.currentItemBurnTime = this.furnaceBurnTime = 
            		TileEntityFurnace.getItemBurnTime(inventory.getStackInSlot(1));

            if (this.furnaceBurnTime > 0)
            {          
            	ItemStack fuel = inventory.getStackInSlot(1);
                if (fuel != null)
                {
                    --fuel.stackSize;

                    if (fuel.stackSize == 0)
                    {
                    	if (!isRemote)
                    		inventory.setInventorySlotContents(1, fuel.getItem().getContainerItemStack(fuel));
                    }
                }
            }
        }

        if (this.isBurning() && this.canSmelt())
        {
            ++this.furnaceCookTime;

            if (this.furnaceCookTime == 200)
            {
                this.furnaceCookTime = 0;
                if (!isRemote)
                	this.smeltItem();           
            }
        }
        else
        {
            this.furnaceCookTime = 0;
        }
    }
	
	public void smeltItem() {
		 
        if (this.canSmelt())
        {
            ItemStack itemstack = FurnaceRecipes.smelting().getSmeltingResult(inventory.getStackInSlot(0));

            if (inventory.getStackInSlot(2) == null)
            {
                inventory.setInventorySlotContents(2, itemstack.copy());
            }
            else if (inventory.getStackInSlot(2).isItemEqual(itemstack))
            {
                inventory.getStackInSlot(2).stackSize += itemstack.stackSize;
            }

            --inventory.getStackInSlot(0).stackSize;

            if (inventory.getStackInSlot(0).stackSize <= 0)
            {
                inventory.setInventorySlotContents(0, null);
            }
        }
    }
	
	public boolean isBurning()
    {
        return this.furnaceBurnTime > 0;
    }
	
	private boolean canSmelt()
    {
        if (inventory.getStackInSlot(0) == null)
        {
            return false;
        }
        else
        {
            ItemStack itemstack = FurnaceRecipes.smelting().getSmeltingResult(inventory.getStackInSlot(0));
            if (itemstack == null) 
            	return false;
            if (inventory.getStackInSlot(2) == null) 
            	return true;
            if (!inventory.getStackInSlot(2).isItemEqual(itemstack)) 
            	return false;
            int result = inventory.getStackInSlot(2).stackSize + itemstack.stackSize;
            return (result <= inventory.getInventoryStackLimit() && result <= itemstack.getMaxStackSize());
        }
    }
	
	public int getBurnTimeRemainingScaled(int scaleHeight)
    {
        if (this.currentItemBurnTime == 0)
        {
            this.currentItemBurnTime = 200;
        }

        return this.furnaceBurnTime * scaleHeight / this.currentItemBurnTime;
    }
	
	public int getCookProgressScaled(int width)
    {
        return this.furnaceCookTime * width / 200;
    }

	@Override
	public void updateSlots() {
	}

	@Override
	public boolean isNeedToUpdate() {
		return true;
	}

	@Override
	public ArrayList<ItemStack> itemsToDrop() {
		ArrayList<ItemStack> res = new ArrayList<ItemStack>();
		for (int i = 0; i < 3; i++)
			if (inventory.getStackInSlot(i) != null)
				res.add(inventory.getStackInSlot(i).copy());
		return res;
	}
}

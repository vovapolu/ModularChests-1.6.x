package vovapolu.modularchests.guimodule;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.rmi.CORBA.Util;

import vovapolu.modularchests.ScrollChestSlot;
import vovapolu.modularchests.ScrollContainer;
import vovapolu.util.ContainerCraftProxy;
import vovapolu.util.ImageUtils;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class CraftModule implements IGuiModule {
	
	private static final Point iconPoint = new Point(10, 0);
	private InventoryCraftResult craftResult = new InventoryCraftResult();
	private ContainerCraftProxy proxyContainer = new ContainerCraftProxy();
	private InventoryCrafting craftMatrix = new InventoryCrafting(proxyContainer, 3, 3);
	private ArrayList<Slot> slots;
	
	public CraftModule() {
	}

	@Override
	public Point getIcon() {
		return iconPoint;
	}

	@Override
	public BufferedImage getGuiImage() {
		return ImageUtils.loadImageByName("gui", "craftModule.png");
	}
	
	@Override
	public void createInventory(ScrollContainer container)
	{
		proxyContainer.setRealContainer(container);
	}

	@Override
	public ArrayList<Slot> createSlots(ScrollContainer container) {
		
		slots = new ArrayList<Slot>();
		slots.add(new SlotCrafting(container.player, this.craftMatrix, this.craftResult, 0, -100, -100));
	
		for (int i = 0; i < 9; i++)
			slots.add(new ScrollChestSlot(this.craftMatrix, i));
		
		return slots;
	}

	@Override
	public void placeSlots(ScrollContainer container, int x, int y) {
		slots.get(0).xDisplayPosition = x + 65;
		slots.get(0).yDisplayPosition = y + 37;
		
		for (int row = 0; row < 3; row++)
			for (int column = 0; column < 3; column++)
			{
				slots.get(row * 3 + column + 1).xDisplayPosition = x + 9 + column * 18;
				slots.get(row * 3 + column + 1).yDisplayPosition = y + 19 + row * 18;
			}
	}
	
	public void onCraftMatrixChanged(World world) {
		this.craftResult.setInventorySlotContents(0, 
				CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, world));
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		NBTTagList itemList = new NBTTagList();
		for (int i = 0; i < 9; i++) {
			ItemStack stack = craftMatrix.getStackInSlot(i);
			if (stack != null) {
				NBTTagCompound atag = new NBTTagCompound();
				atag.setByte("Slot", (byte) i);
				stack.writeToNBT(atag);
				itemList.appendTag(atag);
			}
		}
		tag.setTag("CraftInventory", itemList);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		NBTTagList tagList = tag.getTagList("CraftInventory");	
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound atag = (NBTTagCompound) tagList.tagAt(i);
			byte slot = atag.getByte("Slot");
			if (slot >= 0 && slot < 9) {
				craftMatrix.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(atag));
			}
		}
	}

	@Override
	public void updateModule() {
		proxyContainer.onCraftMatrixChanged(craftMatrix);
	}
	
}

package vovapolu.modularchests.guimodule;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import vovapolu.modularchests.ScrollContainer;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public interface IGuiModule {
	public Point getIcon();
	public ArrayList<Slot> createSlots(ScrollContainer container, EntityPlayer player);
	public void placeSlots(ScrollContainer container, int x, int y);
	public void writeToNBT(NBTTagCompound tag);
	public void readFromNBT(NBTTagCompound tag);
	public void createInventory(ScrollContainer container);
	public void drawBackground(GuiContainer gui, int x, int y);
	public void updateModule(boolean isRemote);
	public void updateSlots();
	public boolean isNeedToUpdate();
	public ArrayList<ItemStack> itemsToDrop();
}

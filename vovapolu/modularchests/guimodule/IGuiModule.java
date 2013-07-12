package vovapolu.modularchests.guimodule;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import vovapolu.modularchests.ScrollContainer;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;

public interface IGuiModule {
	public Point getIcon();
	public BufferedImage getGuiImage();
	public ArrayList<Slot> createSlots(ScrollContainer container);
	public void placeSlots(ScrollContainer container, int x, int y);
	public void writeToNBT(NBTTagCompound tag);
	public void readFromNBT(NBTTagCompound tag);
	public void createInventory(ScrollContainer container);
	public void updateModule();
}

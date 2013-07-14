package vovapolu.modularchests.guimodule;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

import vovapolu.modularchests.ScrollContainer;
import vovapolu.util.ImageUtils;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ChestModule implements IGuiModule {

	private static final Point iconPoint = new Point(0, 0);
	
	@Override
	public Point getIcon() {
		return iconPoint;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
	}

	@Override
	public ArrayList<Slot> createSlots(ScrollContainer container, EntityPlayer player) {
		return new ArrayList<Slot>();
	}

	@Override
	public void placeSlots(ScrollContainer container, int x, int y) {	
	}

	@Override
	public void createInventory(ScrollContainer container) {		
	}

	@Override
	public void updateModule(boolean isRemote) {
	}

	@Override
	public void drawBackground(GuiContainer gui, int x, int y) {
	}

	@Override
	public void updateSlots() {
	}

	@Override
	public boolean isNeedToUpdate() {
		return false;
	}

	@Override
	public ArrayList<ItemStack> itemsToDrop() {
		return new ArrayList<ItemStack>();
	}
}

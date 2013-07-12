package vovapolu.modularchests.client.gui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import vovapolu.modularchests.ModularChestTileEntity;

import cpw.mods.fml.common.Loader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;

public class ModularChestGuiMaker {
	public final static int slotsWidth = 9;
	private static BufferedImage slot, empty_slot, bottom_border, left_border,
			right_border, top_border, middle_border, left_top_corner,
			left_bottom_corner, right_top_corner, right_bottom_corner,
			inventory;

	private static final int hot_bar_padding = 4;
	private static final int x_padding_button1 = 79;
	private static final int x_padding_button2 = 132;
	private static final int y_button_padding = 6;

	public static final int buttonWidth = 30, buttonHeight = 20;

	static {
		try {
			slot = ImageIO.read(new File(
					"mods/ModularChests/textures/gui/slot.png"));
			// System.out.println("slot loaded");
			left_border = ImageIO.read(new File(
					"mods/ModularChests/textures/gui/left_border.png"));
			// System.out.println("left_border loaded");
			right_border = ImageIO.read(new File(
					"mods/ModularChests/textures/gui/right_border.png"));
			// System.out.println("right_border loaded");
			top_border = ImageIO.read(new File(
					"mods/ModularChests/textures/gui/top_border.png"));
			// System.out.println("top_border loaded");
			bottom_border = ImageIO.read(new File(
					"mods/ModularChests/textures/gui/bottom_border.png"));
			// System.out.println("bottom_border loaded");
			left_top_corner = ImageIO.read(new File(
					"mods/ModularChests/textures/gui/left_top_corner.png"));
			// System.out.println("left_top_corner loaded");
			left_bottom_corner = ImageIO.read(new File(
					"mods/ModularChests/textures/gui/left_bottom_corner.png"));
			// System.out.println("left_bottom_corner loaded");
			right_top_corner = ImageIO.read(new File(
					"mods/ModularChests/textures/gui/right_top_corner.png"));
			// System.out.println("right_top_corner loaded");
			right_bottom_corner = ImageIO.read(new File(
					"mods/ModularChests/textures/gui/right_bottom_corner.png"));
			// System.out.println("right_bottom_corner loaded");
			middle_border = ImageIO.read(new File(
					"mods/ModularChests/textures/gui/middle_border.png"));
			// System.out.println("middle_border loaded");
			inventory = ImageIO.read(new File(
					"mods/ModularChests/textures/gui/inventory.png"));
			// System.out.println("inventory loaded");
			empty_slot = ImageIO.read(new File(
					"mods/ModularChests/textures/gui/empty_slot.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	ModularChestGuiMaker() {
	}

	static public int getGuiWidth(int slotsCount) {
		return left_border.getWidth() + slotsWidth * slot.getWidth()
				+ right_border.getWidth();
	}

	static public int getGuiHeight(int slotsCount) {
		int slotsHeight = (slotsCount + slotsWidth - 1) / slotsWidth;
		return top_border.getHeight() + slotsHeight * slot.getHeight()
				+ middle_border.getHeight() + inventory.getHeight()
				+ bottom_border.getHeight();
	}

	static public int getSlotsHeight(int slotsCount) {
		return (slotsCount + slotsWidth - 1) / slotsWidth;
	}

	static public BufferedImage makeGui(int slotsCount) {
		int slotsHeight = getSlotsHeight(slotsCount);

		int width = getGuiWidth(slotsCount);
		int height = getGuiHeight(slotsCount);

		BufferedImage newGui = new BufferedImage(256, 256,
				BufferedImage.TYPE_INT_RGB);
		Graphics g = newGui.getGraphics();

		// Draw top border of gui
		g.drawImage(left_top_corner, 0, 0, null);
		for (int i = 0; i < slotsWidth * slot.getWidth(); i++)
			g.drawImage(top_border, i + left_top_corner.getWidth(), 0, null);
		g.drawImage(right_top_corner, width - right_top_corner.getWidth(), 0,
				null);

		// Draw left and right border of gui
		for (int y = top_border.getHeight(); y < height
				- bottom_border.getHeight(); y++) {
			g.drawImage(left_border, 0, y, null);
			g.drawImage(right_border, width - right_border.getWidth(), y, null);
		}

		// Draw slots
		for (int row = 0; row < slotsHeight; row++) {
			for (int column = 0; column < slotsWidth; column++)
				if (row * slotsWidth + column < slotsCount)
					g.drawImage(slot,
							left_border.getWidth() + column * slot.getWidth(),
							top_border.getHeight() + row * slot.getHeight(),
							null);
				else
					g.drawImage(empty_slot, left_border.getWidth() + column
							* slot.getWidth(), top_border.getHeight() + row
							* slot.getHeight(), null);
		}

		// Draw middle padding
		for (int i = 0; i < slotsWidth * slot.getWidth(); i++)
			g.drawImage(middle_border, left_border.getWidth() + i,
					top_border.getHeight() + slotsHeight * slot.getHeight(),
					null);

		// Draw player's inventory
		g.drawImage(inventory, left_border.getWidth(), top_border.getHeight()
				+ slotsHeight * slot.getHeight() + middle_border.getHeight(),
				null);

		// Draw bottom border of gui
		g.drawImage(left_bottom_corner, 0,
				height - left_bottom_corner.getHeight(), null);
		for (int i = 0; i < slotsWidth * slot.getWidth(); i++)
			g.drawImage(bottom_border, i + left_top_corner.getWidth(), height
					- bottom_border.getHeight(), null);
		g.drawImage(right_bottom_corner,
				width - right_bottom_corner.getWidth(), height
						- right_bottom_corner.getHeight(), null);

		g.dispose();

		return newGui;
	}

	static public Slot[] addSlots(int slotsCount,
			ModularChestTileEntity chestTileEntity,
			InventoryPlayer inventoryPlayer) {
		Slot[] res = new Slot[slotsCount + 36];

		// Add chest slots
		for (int row = 0; row < (slotsCount + slotsWidth - 1) / slotsWidth; row++)
			for (int column = 0; column < Math.min(slotsCount - row
					* slotsWidth, slotsWidth); column++)
				res[row * slotsWidth + column] = new Slot(chestTileEntity, row
						* slotsWidth + column, left_border.getWidth() + 1
						+ column * slot.getWidth(), top_border.getHeight() + 1
						+ row * slot.getHeight());

		// Add inventory slots
		for (int row = 0; row < 3; row++)
			for (int column = 0; column < slotsWidth; column++)
				res[slotsCount + row * slotsWidth + column + 9] = new Slot(
						inventoryPlayer, row * slotsWidth + column + 9,
						left_border.getWidth() + 1 + column * slot.getWidth(),
						top_border.getHeight() + 1 + getSlotsHeight(slotsCount)
								* slot.getHeight() + middle_border.getHeight()
								+ row * slot.getHeight());

		// Add hot bar slots
		for (int column = 0; column < slotsWidth; column++)
			res[slotsCount + column] = new Slot(inventoryPlayer, column,
					left_border.getWidth() + 1 + column * slot.getWidth(),
					top_border.getHeight() + 1 + getSlotsHeight(slotsCount)
							* slot.getHeight() + middle_border.getHeight() + 3
							* slot.getHeight() + hot_bar_padding);

		return res;
	}

	static public void drawText(int slotsCount, FontRenderer fontRenderer) {
		fontRenderer.drawString("Tiny", left_border.getWidth(),
				top_border.getHeight() - fontRenderer.FONT_HEIGHT, 0x404040);
		fontRenderer.drawString(
				StatCollector.translateToLocal("container.inventory"),
				left_border.getWidth(), top_border.getHeight()
						+ getSlotsHeight(slotsCount) * slot.getHeight()
						+ middle_border.getHeight() - fontRenderer.FONT_HEIGHT,
				0x404040);
		return;
	}

	static public void addButtons(List buttonList, int slotsCount, int x, int y)
	{
		buttonList.add(new GuiButton(0, x + x_padding_button1, y + top_border.getHeight() + 
				getSlotsHeight(slotsCount) * slot.getHeight() + y_button_padding, buttonWidth, buttonHeight, "+"));
		buttonList.add(new GuiButton(1, x + x_padding_button2, y + top_border.getHeight() + 
				getSlotsHeight(slotsCount) * slot.getHeight() + y_button_padding, buttonWidth, buttonHeight, "-"));
	}
}

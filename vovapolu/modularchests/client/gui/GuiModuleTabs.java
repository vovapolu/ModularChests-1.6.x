package vovapolu.modularchests.client.gui;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import cpw.mods.fml.client.FMLClientHandler;

import net.minecraft.client.gui.inventory.GuiContainer;

import vovapolu.modularchests.guimodule.IGuiModule;
import vovapolu.util.ImageUtils;

public class GuiModuleTabs {
	
	private final int tabsPerPage = 5;
	private final int tabWidth = 20, tabHeight = 18, tabActiveWidth = 23;
	private final int iconPosX = 4 + 2, iconPosY = 3 + 1;
	private static final String iconTextureFile = "textures/gui/tabsIcons.png";
	
	private int pageNum = 0;
	private int maxPage;
	private int tabsCount;
	private int activeModule = 0;
	private ArrayList<IGuiModule> modules;
	
	public GuiModuleTabs(ArrayList<IGuiModule> modules) {
		tabsCount = modules.size();
		maxPage = (tabsCount + tabsPerPage - 1) / tabsPerPage;
		this.modules = modules;
	}
	
	public int getActiveTab()
	{
		return activeModule;
	}
	
	public boolean setActiveTab(int numInPage)
	{
		numInPage += pageNum * tabsPerPage;
		if (numInPage < 0 || numInPage >= tabsCount)
			return false;
		if (activeModule == numInPage)
			return false;
		activeModule = numInPage;
		return true;
	}
	
	public void setPage(int num)
	{
		pageNum = num;
	}
	
	public void incrementPage()
	{
		pageNum++;
		if (pageNum >= maxPage)
			pageNum = maxPage - 1;
	}
	
	public void decrementPage()
	{
		pageNum--;
		if (pageNum < 0)
			pageNum = 0;
	}
	
	public void drawTabs(int x, int y, GuiContainer gui)
	{
		ImageUtils.bindTextureByName("textures/gui/scroll_container.png");
	
		int begin = pageNum * tabsPerPage, end = Math.min((pageNum + 1) * tabsPerPage, tabsCount);
		for (int i = begin; i < end; i++)
		{
			if (i == activeModule)
				gui.drawTexturedModalRect(x, y + (i - begin) * (tabHeight - 1), 195, 18, tabActiveWidth, tabHeight);
			else 
				gui.drawTexturedModalRect(x, y + (i - begin) * (tabHeight - 1), 195, 35, tabWidth, tabHeight);
		}
		
		ImageUtils.bindTextureByName(iconTextureFile);
		for (int i = begin; i < end; i++)
		{
			Point iconPoint = modules.get(i).getIcon();
			gui.drawTexturedModalRect(x + iconPosX, y + (i - begin) * (tabHeight - 1) + iconPosY, 
					iconPoint.x, iconPoint.y, 10, 10);
		}
		
	}
	
	public boolean handleMouseClick(int x, int y, int tabX, int tabY)
	{
		if (x < tabX || x >= tabX + tabWidth || y < tabY || y >= tabY + (tabHeight - 1) * tabsPerPage)
			return false;
		int tab = (y - tabY) / (tabHeight - 1);
		return setActiveTab(tab);
	}
}

package vovapolu.modularchests;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.model.AdvancedModelLoader;

import vovapolu.modularchests.items.ModularChestUpgradesStorage;
import vovapolu.util.ImageUtils;

public class ModularChestTextureMaker {
	private BufferedImage mainTexture;
	private BufferedImage resImage;	
	/**
	 * coordinates of first side on texture
	 */
	public static final int ySide = 33, xSide = 0;
	public static final int yTop = 2, xTop = 16;
	public static final int topWidth = 10, topHeight = 10;
	public static final int sideWidth = 14, sideHeight = 10;
	
	private ModularChestUpgradesStorage storage;
	
	public ModularChestTextureMaker(ModularChestUpgradesStorage aStorage) {
		mainTexture = ImageUtils.loadImageByName("model", "stoneChest.png");	
		storage = aStorage;	
	}
	
	public void setStorage(ModularChestUpgradesStorage aStorage)
	{
		storage = aStorage;
	}
	
	public BufferedImage getTexture()
	{
		if (storage == null)
			return null;
		//loadTextures();			
		if (resImage == null)
			resImage = new BufferedImage(mainTexture.getWidth(), mainTexture.getHeight(), mainTexture.getType());
				
		Graphics2D g = resImage.createGraphics();
		g.drawImage(mainTexture, 0, 0, null);
		
		//draw textures on sides
		int nowX = xSide, nowY = ySide;
		for (int i = 0; i < 4; i++)
			if (storage.getSideItem(i) != null)
				ImageUtils.drawCentered(g, (i ^ 1) * sideWidth + nowX, nowY,  // "i ^ 1" some kind of magic, but it works
						sideWidth, sideHeight, 
						ImageUtils.loadImageByName("model", storage.getSideItem(i).getTextureName()));		
		
		if (storage.getSideItem(5) != null)
			ImageUtils.drawCentered(g, xTop, yTop, topWidth, topHeight, 
					ImageUtils.loadImageByName("model", storage.getSideItem(5).getTextureName()));
		
		//draw textures of global upgrades
		for (int i = 0; i < storage.getGlobalItemsCount(); i++)
			g.drawImage(ImageUtils.loadImageByName("model", storage.getGlobalItem(i).getTextureName()), 0, 0, null);			
		
		g.dispose();
		
		return resImage;
	}
}

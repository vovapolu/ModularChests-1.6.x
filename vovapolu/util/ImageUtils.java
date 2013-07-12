package vovapolu.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;

import vovapolu.modularchests.ModularChestTextureMaker;

public final class ImageUtils {
	
	private static HashMap<String, BufferedImage> images = new HashMap<String, BufferedImage>();
	
	private ImageUtils() {
	}
	
	public static BufferedImage loadImageByName(String folder, String name)
	{
		if (images.containsKey(name))
		{
			return images.get(name);
		}
		else 
		{			
			try {		
				InputStream istream = ModularChestTextureMaker.class.getResourceAsStream("/assets/modularchests/textures/" + folder + "/" + name);
				BufferedImage image = ImageIO.read(istream);				
				images.put(name, image);
				return image;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static void bindTextureByImage(BufferedImage image)
	{
		int num = TextureUtil.func_110987_a(TextureUtil.func_110996_a(), image);		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, num);
	}
	
	public static void bindTextureByName(String name)
	{
		FMLClientHandler.instance().getClient().renderEngine.func_110577_a(
				new ResourceLocation("modularchests:" + name));
	}
	
	public static void drawCentered(Graphics2D g, int x, int y, int width, int height, BufferedImage img)
	{
		if (img == null)
			return;
		int posX = (width - img.getWidth()) / 2 + x;
		int posY = (height - img.getHeight()) / 2 + y;
		g.drawImage(img, posX, posY, null);
	}
}

package vovapolu.modularchests;

import java.util.HashMap;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import paulscode.sound.Vector3D;

import vovapolu.modularchests.items.ModularChestUpgradesStorage;
import vovapolu.util.ImageUtils;
import vovapolu.util.Point3i;

import com.google.common.primitives.SignedBytes;

import cpw.mods.fml.client.FMLClientHandler;

import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;

import static org.lwjgl.opengl.GL11.GL_COMPILE_AND_EXECUTE;
import static org.lwjgl.opengl.GL11.glCallList;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEndList;
import static org.lwjgl.opengl.GL11.glGenLists;
import static org.lwjgl.opengl.GL11.glNewList;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;

public class ModularChestRenderer extends TileEntitySpecialRenderer {

	static private ModelChest model = new ModelChest();
	static private Random random = new Random();
	static private ModularChestTextureMaker textureMaker = new ModularChestTextureMaker(null);
	
	public ModularChestRenderer() {		
	}
	
	static public void render(ModularChestUpgradesStorage storage, double x, double y, double z, 
			float prevAngle, float angle, int facing, float particalTick)
	{		
		Point3i point = new Point3i((int)x, (int)y, (int)z);
		textureMaker.setStorage(storage);
		
		//textureMaker.getTexture();
		ImageUtils.bindTextureByImage(textureMaker.getTexture());		
		//bindTextureByName("/mods/ModularChests/textures/model/stoneChest.png");
		glPushMatrix();
		glEnable(32826 /* GL_RESCALE_NORMAL_EXT */);
		glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		glTranslatef((float) x, (float) y + 1.0F, (float) z + 1.0F);
		glScalef(1.0F, -1F, -1F);
		glTranslatef(0.5F, 0.5F, 0.5F);
		int k = 0;
		if (facing == 2) {
			k = 180;
		}
		if (facing == 3) {
			k = 0;
		}
		if (facing == 4) {
			k = 90;
		}
		if (facing == 5) {
			k = -90;
		}
		glRotatef(k, 0.0F, 1.0F, 0.0F);
		glTranslatef(-0.5F, -0.5F, -0.5F);
		float lidangle = prevAngle + (angle - prevAngle) * particalTick;
		lidangle = 1.0F - lidangle;
		lidangle = 1.0F - lidangle * lidangle * lidangle;
		model.chestLid.rotateAngleX = -((lidangle * (float)Math.PI) / 2.0F);
		// Render the chest itself
		model.renderAll();
		glDisable(32826 /* GL_RESCALE_NORMAL_EXT */);
		glPopMatrix();
		glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	static public void render(ModularChestTileEntity tile, double x, double y,
			double z, float particalTick) {
		if (tile == null) {
			return;
		}
		int facing = 3;
		if (tile.getWorldObj() != null) {
			facing = tile.getFacing();
		}
		render(tile.upgradesStorage, x, y, z, tile.prevLidAngle, tile.lidAngle, facing, particalTick);
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y,
			double z, float f) {
		render((ModularChestTileEntity) tileentity, x, y, z, f);
	}
}

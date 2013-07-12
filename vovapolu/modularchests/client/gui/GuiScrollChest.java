package vovapolu.modularchests.client.gui;

import java.awt.Point;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cpw.mods.fml.common.network.PacketDispatcher;
import vovapolu.modularchests.ModularChestTileEntity;
import vovapolu.modularchests.ModularChests;
import vovapolu.modularchests.PacketHandler;
import vovapolu.modularchests.ScrollContainer;
import vovapolu.modularchests.guimodule.ModularChestGuiModuleHandler;
import vovapolu.util.ImageUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public class GuiScrollChest extends GuiContainer {
	
	private float scrollVal = 0.0F;
	private int xGui;
	private int yGui;
	private final int barX = 175, barY = 18, barWidth = 12, barHeight = 85;
	private final int pWidth = 12, pHeight = 15;
	private final int pauseTicks = 5;
	
	private ModularChestTileEntity tileEntity;
	private boolean wasClicking;
	private boolean isScrolling;
	private int ticksAfterUpdate = 0;
	private int prevInventorySize = -1;	
	private ScrollContainer container;
	private GuiModuleTabs tabs;
	
	public GuiScrollChest(EntityPlayer player,
			ModularChestTileEntity te) {
		super(new ScrollContainer(player, te));	
		
		tileEntity = te;
		Keyboard.enableRepeatEvents(true);
		container = (ScrollContainer)inventorySlots;
		tabs = new GuiModuleTabs(tileEntity.getModuleHandler().getModules());
		tabs.setActiveTab(tileEntity.getModuleHandler().getActiveModuleIndex());
	}

	@Override
	public void initGui() {
		xSize = 195;
		ySize = 194;
		xGui = (width - xSize) / 2;
		yGui = (height - ySize) / 2;
		super.initGui();
	}
	
	private boolean isOverScrollBar(int mouseX, int mouseY)
	{
		return mouseX >= xGui + barX && mouseX <= xGui + barX + barWidth 
				&& mouseY >= yGui + barY && mouseY <= yGui +barY + barHeight;
	}
	
	@Override
	public void handleKeyboardInput() {		
		
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
		{			
			scrollVal += container.getShiftHeightOfRow();
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_UP))
		{
			scrollVal -= container.getShiftHeightOfRow();
		}
		
		if (scrollVal > 1.0F)
			scrollVal = 1.0F;
		if (scrollVal < 0.0F)
			scrollVal = 0.0F;
		
		container.scrollTo(scrollVal);
		super.handleKeyboardInput();
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float particleTick) {
		
		if (tileEntity.getRealSizeInventory() != prevInventorySize)
		{
			prevInventorySize = tileEntity.getRealSizeInventory();
			wasClicking = false;
			scrollVal = 0.0F;
			container.scrollTo(this.scrollVal);
			super.drawScreen(mouseX, mouseY, particleTick);
			return;
		}
		
		boolean isClick = Mouse.isButtonDown(0);
        int xGui = this.guiLeft;
        int yGui = this.guiTop;
        int realBarX = xGui + barX;
        int realBarY = yGui + barY;

        if (!this.wasClicking && isClick && isOverScrollBar(mouseX, mouseY))
        {
            this.isScrolling = true;
        }

        if (!isClick)
        {
            this.isScrolling = false;
        }

        this.wasClicking = isClick;

        if (this.isScrolling)
        {
            this.scrollVal = ((float)(mouseY - realBarY) - (float)pHeight / 2) / 
            		((float)(barHeight) - (float)pHeight);

            if (this.scrollVal < 0.0F)
            {
                this.scrollVal = 0.0F;
            }

            if (this.scrollVal > 1.0F)
            {
                this.scrollVal = 1.0F;                
            }

           container.scrollTo(this.scrollVal);
        }

        super.drawScreen(mouseX, mouseY, particleTick);
	}
	
	@Override
	protected void mouseClicked(int x, int y, int mode) {
		super.mouseClicked(x, y, mode);
		if (tabs.handleMouseClick(x, y, xGui - 20, yGui + 6))
		{
			PacketDispatcher.sendPacketToServer(PacketHandler.createTABCHPacket(tileEntity, tabs));
			tileEntity.getModuleHandler().setActiveModule(tabs.getActiveTab());
			container.updateModule();
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		/*int nowRow = container.getShiftRow(scrollVal);
		final int xPad = -5;
		final int yPad = 20;
		for (int i = 0; i < 5; i++)
		{
			int stringWidth = this.mc.fontRenderer.getStringWidth(String.valueOf(nowRow + i + 1));
			this.mc.fontRenderer.drawString(String.valueOf(nowRow + i + 1), 
					-stringWidth + xPad, yPad + i * 18, 
					0xffffff, true);
		}*/
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float particleTick, int mouseX, int mouseY) {
		
		this.mc.renderEngine.func_110577_a(new ResourceLocation("modularchests:textures/gui/scroll_container.png"));
		//.bindTexture("/mods/ModularChests/textures/gui/scroll_container.png");
		this.drawTexturedModalRect(xGui, yGui, 0, 0, xSize, ySize);
		this.drawTexturedModalRect(xGui + barX, yGui + barY + 
				(int)((float)(barHeight - pHeight) * this.scrollVal), 
				195, 0, pWidth, pHeight);
		
		tabs.drawTabs(xGui - 20, yGui + 6, this);
		
		final int moduleWidth = 90, moduleHeight = 90;
		ImageUtils.bindTextureByImage(tileEntity.getModuleHandler().getActiveModule().getGuiImage());
		this.drawTexturedModalRect(xGui + 8, yGui + 17, 0, 0, moduleWidth, moduleHeight);
		
		this.mc.renderEngine.func_110577_a(new ResourceLocation("modularchests:textures/gui/scroll_container.png"));
		Point startPoint = container.getChestStartPoint();
		for (int row = 0; row < container.slotsHeight; row++)
			for (int column = 0; column < container.getRealSlotsWidth(); column++)
				if (!container.isValidSlot(row * container.getRealSlotsWidth() + column))	
					this.drawTexturedModalRect(xGui + startPoint.x - 1 + 18 * column, 
											   yGui + startPoint.y - 1 + 18 * row, 207, 0, 18, 18);
	}

}

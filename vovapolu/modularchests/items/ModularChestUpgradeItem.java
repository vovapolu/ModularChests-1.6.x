package vovapolu.modularchests.items;

import vovapolu.modularchests.ModularChestTileEntity;
import vovapolu.modularchests.ModularChests;
import vovapolu.modularchests.PacketHandler;
import vovapolu.modularchests.block.ModularChestBaseBlock;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public abstract class ModularChestUpgradeItem extends Item {	

	private String iconName;
	private String textureName;
	
	public ModularChestUpgradeItem(int id, String name, String aIconName, String aTextureName) {
		super(id);
		setMaxStackSize(16);
		setUnlocalizedName(name);
		setCreativeTab(CreativeTabs.tabMisc);
		iconName = aIconName;
		textureName = aTextureName;
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon("ModularChests:" + iconName);
    }
	
	public String getTextureName()
	{
		return textureName;
	}
	
	public abstract void onUseItem(ModularChestTileEntity tileEntity, EntityPlayer player, World world, int side);
	public abstract void onRemoveItem(ModularChestTileEntity tileEntity, EntityPlayer player, World world, int side);
	public abstract boolean applyItemToStorage(ModularChestUpgradesStorage storage, EntityPlayer player, int side);
	
	public String getItemInformation()
	{
		return LanguageRegistry.instance().getStringLocalization(this.getUnlocalizedName() + ".name");
	}
	
	public EnumChatFormatting getChatFormattingColor()
	{
		return EnumChatFormatting.WHITE;
	}
	
	public void onRemoveItem(ModularChestTileEntity tileEntity, EntityPlayer player, World world)
	{
		onRemoveItem(tileEntity, player, world, -1);
	}
	
	public void onUseItem(ModularChestTileEntity tileEntity, EntityPlayer player, World world)
	{
		onUseItem(tileEntity, player, world, -1);
	}
	
	public boolean applyItemToTileEntity(int side, ModularChestTileEntity tileEntity, EntityPlayer player)
	{
		if (isValidSide(side))
		{
			return this.applyItemToStorage(tileEntity.upgradesStorage, player, side);
		}
		else 
			return false;
	}
	
	public int applyFacingToSide(int side, byte facing)
	{
		if (side < 4)
		{
			if (facing == 2)
				side += 2;
			if (facing == 4)
				side -= 1;
			if (facing == 5)
				side += 1;
			return (side + 4) % 4;
		}
		else
			return side;
	}
	
	@Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int X, int Y, int Z, int side, float hitX, float hitY, float hitZ)
    {
		ModularChests.debugStream.println(hitX + " " + hitY + " " + hitZ);
        if (world.isRemote) return false;        
        TileEntity te = world.getBlockTileEntity(X, Y, Z);
        ModularChestTileEntity newChest;        
        if (te != null && te instanceof ModularChestTileEntity)
        {
            ModularChestTileEntity modularChest = (ModularChestTileEntity) te;
            if (!modularChest.isCanApplyItem(this))
            	return false;
            int realSide = applyFacingToSide(ModularChestBaseBlock.getClickSide(hitX, hitY, hitZ), 
    				modularChest.getFacing());
            ModularChestUpgradeItem sideItem = modularChest.upgradesStorage.getSideItem(realSide);
            
            if (!applyItemToTileEntity(realSide, modularChest, player))
            	return false;        
                
        	PacketDispatcher.sendPacketToAllPlayers(PacketHandler.createTEPacket(modularChest));      
        	stack.stackSize--;	  
        }
        else
        {
            return false;
        }               
        return true;
    }
	
	public abstract boolean isValidSide(int side);
	public abstract boolean isGlobalItem();
	public abstract boolean isUniqueItem();
	
}

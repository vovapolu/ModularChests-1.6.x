package vovapolu.modularchests.block;

import java.util.List;

import org.lwjgl.input.Keyboard;

import vovapolu.modularchests.items.ModularChestUpgradesStorage;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public class ModularChestItemBlock extends ItemBlock {

	public ModularChestItemBlock(int id) {
		super(id);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack item, EntityPlayer player, List list, boolean flag) {	
		super.addInformation(item, player, list, flag);
		if (item.hasTagCompound())
		{
			ModularChestUpgradesStorage storage = new ModularChestUpgradesStorage(null, null);
			storage.readFromNBT(item.getTagCompound());
			list.add(EnumChatFormatting.GRAY + "" + storage.getAllItemsCount() + " upgrades installed." + EnumChatFormatting.RESET);
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
			{
				String[] information = storage.getItemsInformation();
				for (String s: information)
					list.add(s);
			}
			else 
				list.add(EnumChatFormatting.DARK_GRAY + "[Press shift to display upgrades.]");
		}
	}
}

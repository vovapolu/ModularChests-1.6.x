package vovapolu.modularchests.items;

import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashSet;

import vovapolu.modularchests.ModularChestTileEntity;
import vovapolu.modularchests.block.ModularChestBaseBlock;
import vovapolu.modularchests.guimodule.IGuiModule;
import vovapolu.modularchests.guimodule.ModularChestGuiModuleHandler;

import cpw.mods.fml.common.registry.GameData;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ModularChestUpgradesStorage {
	private HashSet<Integer> itemsId = new HashSet<Integer>();
	private ModularChestUpgradeItem upgrades[] = new ModularChestUpgradeItem[6];
	private ArrayList<ModularChestUpgradeItem> globalUpgrades = new ArrayList<ModularChestUpgradeItem>();
	private ModularChestTileEntity tileEntity;
	private ModularChestGuiModuleHandler moduleHandler;

	public ModularChestUpgradesStorage(ModularChestTileEntity tileEntity, ModularChestGuiModuleHandler moduleHandler) {
		for (int i = 0; i < 6; i++)
			upgrades[i] = null;
		this.tileEntity = tileEntity;
		this.moduleHandler = moduleHandler;
	}
	
	public ModularChestUpgradeItem getSideItem(int side) {
		return upgrades[side];
	}
	
	private void onItemAddToStorage(ModularChestUpgradeItem item)
	{
		if (moduleHandler == null)
			return;
		if (item instanceof GUIUpgradeItem)
			moduleHandler.addModule(((GUIUpgradeItem) item).getNewGuiModule());
	}
	
	private void onItemRemoveFromStorage(ModularChestUpgradeItem item)
	{
		if (moduleHandler == null)
			return;
		//if (item instanceof GUIUpgradeItem)
			//moduleHandler.removeModule(((GUIUpgradeItem) item).getNewGuiModule());
	}

	public boolean setSideItem(int side, ModularChestUpgradeItem newItem, EntityPlayer player) {
		if (newItem.isUniqueItem() && itemsId.contains(newItem.itemID))
			return false;
		if (!newItem.isGlobalItem() && newItem.isValidSide(side))
		{
			if (upgrades[side] != null)
			{
				onItemRemoveFromStorage(upgrades[side]);
				itemsId.remove(upgrades[side].itemID);
				ModularChestBaseBlock.dropItem(new ItemStack(upgrades[side]), tileEntity.worldObj, 
						tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
				upgrades[side].onRemoveItem(tileEntity, player, tileEntity.worldObj, side);
			}
			onItemAddToStorage(newItem);
			upgrades[side] = newItem;
			itemsId.add(newItem.itemID);
			upgrades[side].onUseItem(tileEntity, player, tileEntity.worldObj, side);
			return true;
		}
		else 
			return false;
	}
	
	public boolean addGlobalItem(ModularChestUpgradeItem newItem, EntityPlayer player)
	{
		if (newItem.isUniqueItem() && itemsId.contains(newItem.itemID))
			return false;
		else 
		{
			onItemAddToStorage(newItem);
			globalUpgrades.add(newItem);
			itemsId.add(newItem.itemID);
			newItem.onUseItem(tileEntity, player, tileEntity.worldObj);
			return true;
		}
	}
	
	public boolean removeGlobalItem(ModularChestUpgradeItem item, EntityPlayer player)
	{
		if (item == null)
			return true;
		itemsId.remove(item.itemID);
		int index = globalUpgrades.indexOf(item);
		if (index >= 0 && index < globalUpgrades.size())
		{
			onItemRemoveFromStorage(globalUpgrades.get(index));
			globalUpgrades.get(index).onRemoveItem(tileEntity, player, tileEntity.worldObj);
			globalUpgrades.remove(index);
			return true;
		}
		else 
			return false;
	}
	
	public int getGlobalItemsCount()
	{
		return globalUpgrades.size();
	}
	
	public ModularChestUpgradeItem getGlobalItem(int num)
	{
		return globalUpgrades.get(num);
	}
	
	public int getAllItemsCount()
	{
		int sideCount = 0;
		for (int i = 0; i < 6; i++)
			if (upgrades[i] != null)
				sideCount++;
		return sideCount + globalUpgrades.size();
	}
	
	public ArrayList<GUIUpgradeItem> getGuiModuleItems()
	{
		ArrayList<GUIUpgradeItem> res = new ArrayList<GUIUpgradeItem>();
		for (int i = 0; i < 6; i++)
			if (upgrades[i] instanceof GUIUpgradeItem)
				res.add((GUIUpgradeItem) upgrades[i]);
		
		for(ModularChestUpgradeItem item: globalUpgrades)
			if (item instanceof GUIUpgradeItem)
				res.add((GUIUpgradeItem) item);
		
		return res;
	}
	
	public String[] getItemsInformation()
	{
		String[] res = new String[getAllItemsCount()];
		int size = 0;
		for (int i = 0; i < 6; i++)
			if (upgrades[i] != null)
				res[size++] = upgrades[i].getChatFormattingColor() + upgrades[i].getItemInformation();
		for (int i = 0; i < globalUpgrades.size(); i++)
			res[size++] = globalUpgrades.get(i).getChatFormattingColor() + globalUpgrades.get(i).getItemInformation();
		
		return res;
	}

	public void writeToNBT(NBTTagCompound tagCompound) {
		NBTTagList itemList = new NBTTagList();
		for (int i = 0; i < 6; i++) {
			if (upgrades[i] != null) {
				int itemId = upgrades[i].itemID;
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte) i);
				tag.setInteger("Id", itemId);
				itemList.appendTag(tag);
			}
		}
		tagCompound.setTag("Upgrades", itemList);

		tagCompound.setInteger("GlobalUpgradesSize", globalUpgrades.size());

		NBTTagList globalItemList = new NBTTagList();
		for (int i = 0; i < globalUpgrades.size(); i++) {
			if (globalUpgrades.get(i) != null) {
				int itemId = globalUpgrades.get(i).itemID;
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("Id", itemId);
				globalItemList.appendTag(tag);
			}
		}
		tagCompound.setTag("GlobalUpgrades", globalItemList);
	}

	public void readFromNBT(NBTTagCompound tagCompound) {
		itemsId.clear();
		NBTTagList tagList = tagCompound.getTagList("Upgrades");
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) tagList.tagAt(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < 6) {
				int id = tag.getInteger("Id");
				upgrades[slot] = (ModularChestUpgradeItem) Item.itemsList[id];
				itemsId.add(id);
			}
		}

		int size = tagCompound.getInteger("GlobalUpgradesSize");
		globalUpgrades.clear();

		tagList = tagCompound.getTagList("GlobalUpgrades");
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) tagList.tagAt(i);
			
			int id = tag.getInteger("Id");
			globalUpgrades.add((ModularChestUpgradeItem) Item.itemsList[id]);
			itemsId.add(id);
		}
	}
}

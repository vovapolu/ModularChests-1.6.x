package vovapolu.modularchests.guimodule;

import java.util.ArrayList;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import vovapolu.modularchests.ScrollContainer;
import vovapolu.modularchests.client.gui.GuiModuleTabs;
import vovapolu.modularchests.items.GUIUpgradeItem;
import vovapolu.modularchests.items.ModularChestUpgradeItem;
import vovapolu.modularchests.items.ModularChestUpgradesStorage;

public class ModularChestGuiModuleHandler {

	private ArrayList<IGuiModule> modules = new ArrayList<IGuiModule>();
	private int activeModuleIndex = 0;
	
	public ModularChestGuiModuleHandler() { 

		modules.add(new ChestModule());
		//modules.add(new CraftModule());
		/*ArrayList<GUIUpgradeItem> items = this.storage.getGuiModuleItems();
		for (GUIUpgradeItem item: items)
			modules.add(item.getNewGuiModule(this.container));*/
	}
	
	public ArrayList<IGuiModule> getModules()
	{
		return modules;
	}
	
	public void setActiveModule(int index)
	{
		activeModuleIndex = index;
	}
	
	public IGuiModule getActiveModule()
	{
		return modules.get(activeModuleIndex);
	}
	
	public int getActiveModuleIndex()
	{
		return activeModuleIndex;
	}
	
	public void addModule(IGuiModule module)
	{
		modules.add(module);
	}
	
	public void removeModule(IGuiModule module)
	{
		
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setInteger("ModulesSize", modules.size());
		NBTTagList itemList = new NBTTagList();
		for (int i = 0; i < modules.size(); i++) {
			NBTTagCompound atag = new NBTTagCompound();
			ModuleIO.writeModuleToNBT(modules.get(i), atag);
			itemList.appendTag(atag);
		}
		tag.setTag("Modules", itemList);
	}
	
	public void readFromNBT(NBTTagCompound tag)
	{
		int size = tag.getInteger("ModulessSize");
		modules.clear();

		NBTTagList tagList = tag.getTagList("Modules");
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound atag = (NBTTagCompound) tagList.tagAt(i);
			IGuiModule module = ModuleIO.readModuleFromNBT(atag);
			modules.add(module);
		}
	}
}

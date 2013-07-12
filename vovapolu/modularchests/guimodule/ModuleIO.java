package vovapolu.modularchests.guimodule;

import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;

public final class ModuleIO {
	
	private ModuleIO() {
	}
	
	private static int ids = 0;
	
	private static HashMap <Class<? extends IGuiModule>, Integer> classToId = 
			new HashMap<Class<? extends IGuiModule>, Integer>();
	private static HashMap <Integer, Class<? extends IGuiModule>> IdToClass = 
			new HashMap<Integer, Class<? extends IGuiModule>>();
	
	private static void registerModuleClass(Class<? extends IGuiModule> moduleClass)
	{
		classToId.put(moduleClass, ids);
		IdToClass.put(ids, moduleClass);
		ids++;
	}
	
	static{
		registerModuleClass(CraftModule.class);
		registerModuleClass(ChestModule.class);
	}
	
	public static void writeModuleToNBT(IGuiModule module, NBTTagCompound tag)
	{
		tag.setInteger("ModuleId", classToId.get(module.getClass()));
		module.writeToNBT(tag);
	}
	
	public static IGuiModule readModuleFromNBT(NBTTagCompound tag)
	{
		int id = tag.getInteger("ModuleId");
		Class<? extends IGuiModule> moduleClass = IdToClass.get(id);
		IGuiModule module = null;
		
		try {
			module = moduleClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		module.readFromNBT(tag);
		return module;
	}
}

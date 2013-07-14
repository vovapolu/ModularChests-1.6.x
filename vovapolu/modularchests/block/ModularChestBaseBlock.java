package vovapolu.modularchests.block;

import java.util.ArrayList;
import java.util.Random;

import vovapolu.modularchests.ModularChestTileEntity;
import vovapolu.modularchests.ModularChests;
import vovapolu.modularchests.guimodule.IGuiModule;
import vovapolu.modularchests.items.BreakableUpgradeItem;
import vovapolu.modularchests.items.ModularChestUpgradeItem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ModularChestBaseBlock extends BlockContainer {		
	private static final float minX = 0.0625F;
	private static final float minY = 0F;
	private static final float minZ = 0.0625F;
	private static final float maxX = 0.9375F;
	private static final float maxY = 0.875F;
	private static final float maxZ = 0.9375F;
	
	static private Random random = new Random();
	
	/** 
	 * @param clickX
	 * @param clickY
	 * @param clickZ
	 * @return 0 - south, 1 - west, 2 - north, 3 - east, 4 - down, 5 - up
	 */
	public static int getClickSide(float clickX, float clickY, float clickZ)
	{
		final float epsilon = 5.96e-08F;
		if (Math.abs(clickX - ModularChestBaseBlock.minX) < epsilon)
			return 1;
		if (Math.abs(clickX - ModularChestBaseBlock.maxX) < epsilon)
			return 3;
		if (Math.abs(clickY - ModularChestBaseBlock.minY) < epsilon)
			return 4;
		if (Math.abs(clickY - ModularChestBaseBlock.maxY) < epsilon)
			return 5;
		if (Math.abs(clickZ - ModularChestBaseBlock.minZ) < epsilon)
			return 2;
		if (Math.abs(clickZ - ModularChestBaseBlock.maxZ) < epsilon)
			return 0;
		return -1;
	}
	
	@Override
	public int idDropped(int par1, Random par2Random, int par3) {
		return -1; // drops nothing, all drops are explained in breakBlock
	}
	
	public ModularChestBaseBlock(int id, Material material) {
		super(id, material);
		setHardness(2.5F);
		setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
		setStepSound(Block.soundStoneFootstep);
		setCreativeTab(CreativeTabs.tabDecorations);
		setUnlocalizedName("modularChest");		
		this.getBlockBoundsMaxX();
	}
	
	@Override
	public void registerIcons(IconRegister iconRegister) {
		this.blockIcon = iconRegister.registerIcon("stone");
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int idk, float hitX, float hitY, float hitZ) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		ModularChestTileEntity mte = (ModularChestTileEntity)tileEntity;
		if (tileEntity == null	|| player.isSneaking() || (mte != null && mte.isLocked) || 
				(player.getCurrentEquippedItem() != null 
				&& player.getCurrentEquippedItem().getItem() instanceof ModularChestUpgradeItem)) {
			return true;
		}
		player.openGui(ModularChests.instance, 0, world, x, y, z);		
		return true;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof ModularChestTileEntity)
		{
			ModularChestTileEntity mte = (ModularChestTileEntity) te;
			if (!((ModularChestTileEntity) te).isBreakable)
			{
				dropUpgrades(world, x, y, z);
				dropItems(world, x, y, z);
				dropItem(new ItemStack(this), world, x, y, z);
				dropModules(world, x, y, z);
			}
			else 
			{
				ItemStack item = new ItemStack(this);
				NBTTagCompound tag = new NBTTagCompound();
				mte.upgradesStorage.removeGlobalItem(ModularChests.breakableUpgradeItem, null);
				mte.writeDataToNBT(tag);
				item.setTagCompound(tag);
				dropItem(item, world, x, y, z);
			}
		}
		else 
			dropItems(world, x, y, z);
		super.breakBlock(world, x, y, z, par5, par6);
	}
	
	public static void dropUpgrades(World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (!(tileEntity instanceof ModularChestTileEntity)) {
			return;
		}
		
		ModularChestTileEntity mte = (ModularChestTileEntity) tileEntity;
											
		for (int i = 0; i < 6; i++) {
			Item item = mte.upgradesStorage.getSideItem(i);
			if (item != null)
			{
				ItemStack itemstack = new ItemStack(item);
				dropItem(itemstack, world, x, y, z);
			}
		}
		
		for (int i = 0; i < mte.upgradesStorage.getGlobalItemsCount(); i++)
		{
			Item item = mte.upgradesStorage.getGlobalItem(i);
			if (item != null)
			{
				ItemStack itemstack = new ItemStack(item);
				dropItem(itemstack, world, x, y, z);
			}
		}
	}
	
	public static void dropModules(World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (!(tileEntity instanceof ModularChestTileEntity)) {
			return;
		}
		
		ModularChestTileEntity mte = (ModularChestTileEntity) tileEntity;
		ArrayList<IGuiModule> modules = mte.getModuleHandler().getModules();
		for (IGuiModule module: modules)
		{
			ArrayList<ItemStack> items = module.itemsToDrop();
			for (ItemStack item: items)
				dropItem(item, world, x, y, z);
		}
	}
	
	public static void dropItem(ItemStack item, World world, int x, int y, int z)
	{
		if (item != null && item.stackSize > 0) {
			float rx = random.nextFloat() * 0.8F + 0.1F;
			float ry = random.nextFloat() * 0.8F + 0.1F;
			float rz = random.nextFloat() * 0.8F + 0.1F;

			EntityItem entityItem = new EntityItem(world, x + rx, y + ry, z
					+ rz, new ItemStack(item.itemID, item.stackSize,
					item.getItemDamage()));

			if (item.hasTagCompound()) {
				entityItem.getEntityItem().setTagCompound(
						(NBTTagCompound) item.getTagCompound().copy());
			}

			float factor = 0.05F;
			entityItem.motionX = random.nextGaussian() * factor;
			entityItem.motionY = random.nextGaussian() * factor + 0.2F;
			entityItem.motionZ = random.nextGaussian() * factor;
			world.spawnEntityInWorld(entityItem);
			item.stackSize = 0;
		}
	}
	
	static public void dropItemsInRange(World world, int x, int y, int z, int l, int r)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (!(tileEntity instanceof IInventory)) {
			return;
		}
		IInventory inventory = (IInventory) tileEntity;

		for (int i = l; i < Math.min(r, inventory.getSizeInventory()); i++) {
			ItemStack item = inventory.getStackInSlot(i);
			dropItem(item, world, x, y, z);
		}
	}

	static public void dropItems(World world, int x, int y, int z) {

		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (!(tileEntity instanceof IInventory)) {
			return;
		}
		IInventory inventory = (IInventory) tileEntity;

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack item = inventory.getStackInSlot(i);
			dropItem(item, world, x, y, z);
		}
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new ModularChestTileEntity(9, 45);
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z,
			EntityLivingBase entityliving, ItemStack itemStack) {
		
		TileEntity te = world.getBlockTileEntity(x, y, z);
		ModularChestTileEntity mte = null;
		if (te != null && te instanceof ModularChestTileEntity) {
			mte = (ModularChestTileEntity) te;
		}
		
		if (itemStack.getItem().itemID == ModularChests.modularBlockId)
		{
			if (itemStack.hasTagCompound())
			{
				EntityPlayer player = (EntityPlayer)entityliving;
					
				mte.readDataFromNBT(itemStack.getTagCompound());
			}
		}
		
		byte chestFacing = 0;
		int facing = MathHelper
				.floor_double((double) ((entityliving.rotationYaw * 4F) / 360F) + 0.5D) & 3;
		if (facing == 0) {
			chestFacing = 2;
		}
		if (facing == 1) {
			chestFacing = 5;
		}
		if (facing == 2) {
			chestFacing = 3;
		}
		if (facing == 3) {
			chestFacing = 4;
		}
		
		if (mte != null)
		{
			mte.setFacing(chestFacing);
			world.markBlockForUpdate(x, y, z);
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return null;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return 22;
	}
}
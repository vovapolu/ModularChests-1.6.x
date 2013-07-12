package vovapolu.modularchests;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import vovapolu.modularchests.client.gui.GuiModuleTabs;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler implements IPacketHandler {

	static private long packetTime = 0;

	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {		

		DataInputStream istream = new DataInputStream(new ByteArrayInputStream(packet.data));

		if (packet.channel.equals("TE")) {
			try {
				int x, y, z;
				NBTTagCompound tag;
				x = istream.readInt();
				y = istream.readInt();
				z = istream.readInt();
				tag = Packet.readNBTTagCompound(istream);

				if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
					EntityPlayerMP playerMP = (EntityPlayerMP) player;
					TileEntity te = playerMP.worldObj.getBlockTileEntity(x, y,z);
					te.readFromNBT(tag);
				} else if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
					EntityPlayer playerSP = (EntityPlayer) player;
					TileEntity te = playerSP.worldObj.getBlockTileEntity(x, y,z);
					te.readFromNBT(tag);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		
		if (packet.channel.equals("TABCH")) {
			try {
				int x, y, z, activeTab;
				NBTTagCompound tag;
				x = istream.readInt();
				y = istream.readInt();
				z = istream.readInt();
				activeTab = istream.readInt();

				TileEntity te = null;
		
				if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
					EntityPlayerMP playerMP = (EntityPlayerMP) player;
					te = playerMP.worldObj.getBlockTileEntity(x, y,z);
					
				} else if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
					EntityPlayer playerSP = (EntityPlayer) player;
					te = playerSP.worldObj.getBlockTileEntity(x, y,z);
				}
				
				if (te instanceof ModularChestTileEntity)
				{
					((ModularChestTileEntity) te).getModuleHandler().setActiveModule(activeTab);
					//((ModularChestTileEntity) te).getModuleHandler().getActiveModule().updateModule();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public static Packet250CustomPayload createTEPacket(TileEntity tileEntity) {
		Packet250CustomPayload packet = new Packet250CustomPayload();

		ByteArrayOutputStream array = new ByteArrayOutputStream();
		DataOutputStream ostream = new DataOutputStream(array);

		try {			
			int x = tileEntity.xCoord, y = tileEntity.yCoord, z = tileEntity.zCoord;
			NBTTagCompound tag = new NBTTagCompound();
			tileEntity.writeToNBT(tag);
			byte[] abyte = CompressedStreamTools.compress(tag);
			
			ostream.writeInt(x);
			ostream.writeInt(y);
			ostream.writeInt(z);
			ostream.writeShort((short) abyte.length);
			ostream.write(abyte);
			ostream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		packet.channel = "TE";
		packet.data = array.toByteArray();
		packet.length = array.size();
		
		return packet;
	}
	
	public static Packet250CustomPayload createTABCHPacket(TileEntity tileEntity, GuiModuleTabs tabs) {
		Packet250CustomPayload packet = new Packet250CustomPayload();

		ByteArrayOutputStream array = new ByteArrayOutputStream();
		DataOutputStream ostream = new DataOutputStream(array);

		try {			
			int x = tileEntity.xCoord, y = tileEntity.yCoord, z = tileEntity.zCoord;
			NBTTagCompound tag = new NBTTagCompound();
			
			ostream.writeInt(x);
			ostream.writeInt(y);
			ostream.writeInt(z);
			ostream.writeInt(tabs.getActiveTab());
			ostream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		packet.channel = "TABCH";
		packet.data = array.toByteArray();
		packet.length = array.size();
		
		return packet;
	}

}

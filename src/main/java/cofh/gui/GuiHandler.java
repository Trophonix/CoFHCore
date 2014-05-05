package cofh.gui;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class GuiHandler implements IGuiHandler {

	private final Map guiMap = new HashMap();
	private final Map guiTileMap = new HashMap();
	private final Map containerMap = new HashMap();
	private final Map containerTileMap = new HashMap();

	private int guiIdCounter = 0;

	/* Non-Tile Entity Guis */
	public int registerClientGui(Class gui, Class container) {

		guiIdCounter++;
		guiMap.put(guiIdCounter, gui);
		containerMap.put(guiIdCounter, container);
		return guiIdCounter;
	}

	public int registerServerGui(Class container) {

		guiIdCounter++;
		containerMap.put(guiIdCounter, container);
		return guiIdCounter;
	}

	/* Tile Entity Guis */
	public int registerClientGuiTile(Class gui, Class container) {

		guiIdCounter++;
		guiTileMap.put(guiIdCounter, gui);
		containerTileMap.put(guiIdCounter, container);
		return guiIdCounter;
	}

	public int registerServerGuiTile(Class container) {

		guiIdCounter++;
		containerTileMap.put(guiIdCounter, container);
		return guiIdCounter;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (guiTileMap.containsKey(id)) {
			if (!world.blockExists(x, y, z)) {
				return null;
			}
			TileEntity tile = world.getTileEntity(x, y, z);
			try {
				Class<? extends GuiScreen> guiClass = (Class<? extends GuiScreen>) guiTileMap.get(id);
				Constructor guiConstructor = guiClass.getDeclaredConstructor(new Class[] { InventoryPlayer.class, TileEntity.class });
				return guiConstructor.newInstance(player.inventory, tile);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		} else {
			try {
				Class<? extends GuiScreen> guiClass = (Class<? extends GuiScreen>) guiMap.get(id);
				Constructor guiConstructor = guiClass.getDeclaredConstructor(new Class[] { InventoryPlayer.class });
				return guiConstructor.newInstance(player.inventory);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (containerTileMap.containsKey(id)) {
			if (!world.blockExists(x, y, z)) {
				return null;
			}
			TileEntity tile = world.getTileEntity(x, y, z);

			try {
				Class<? extends Container> containerClass = (Class<? extends Container>) containerTileMap.get(id);
				Constructor containerConstructor = containerClass.getDeclaredConstructor(new Class[] { InventoryPlayer.class, TileEntity.class });
				return containerConstructor.newInstance(player.inventory, tile);

			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		} else {
			try {
				Class<? extends Container> containerClass = (Class<? extends Container>) containerMap.get(id);
				Constructor containerConstructor = containerClass.getDeclaredConstructor(new Class[] { InventoryPlayer.class });
				return containerConstructor.newInstance(player.inventory);

			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}
	}

}

package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.apiimpl.network.item.NetworkItemWirelessFluidGrid;
import com.raoulvdberge.refinedstorage.apiimpl.network.item.NetworkItemWirelessGrid;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.IGuiReaderWriter;
import com.raoulvdberge.refinedstorage.container.*;
import com.raoulvdberge.refinedstorage.gui.grid.GridDisplayDummy;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.integration.mcmp.IntegrationMCMP;
import com.raoulvdberge.refinedstorage.tile.*;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.TileCraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.WirelessCraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.grid.IGrid;
import com.raoulvdberge.refinedstorage.tile.grid.TileGrid;
import com.raoulvdberge.refinedstorage.tile.grid.WirelessFluidGrid;
import com.raoulvdberge.refinedstorage.tile.grid.WirelessGrid;
import mcmultipart.api.multipart.IMultipartTile;
import mcmultipart.api.slot.EnumCenterSlot;
import mcmultipart.block.TileMultipartContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import java.util.Optional;

public class GuiHandler implements IGuiHandler {
    private Container getContainer(int ID, EntityPlayer player, TileEntity tile) {
        switch (ID) {
            case RSGui.CONTROLLER:
                return new ContainerController((TileController) tile, player);
            case RSGui.GRID:
                return new ContainerGrid(((TileGrid) tile).getNode(), new GridDisplayDummy(), (TileGrid) tile, player);
            case RSGui.DISK_DRIVE:
                return new ContainerDiskDrive((TileDiskDrive) tile, player);
            case RSGui.IMPORTER:
                return new ContainerImporter((TileImporter) tile, player);
            case RSGui.EXPORTER:
                return new ContainerExporter((TileExporter) tile, player);
            case RSGui.DETECTOR:
                return new ContainerDetector((TileDetector) tile, player);
            case RSGui.SOLDERER:
                return new ContainerSolderer((TileSolderer) tile, player);
            case RSGui.DESTRUCTOR:
                return new ContainerDestructor((TileDestructor) tile, player);
            case RSGui.CONSTRUCTOR:
                return new ContainerConstructor((TileConstructor) tile, player);
            case RSGui.STORAGE:
                return new ContainerStorage((TileStorage) tile, player);
            case RSGui.EXTERNAL_STORAGE:
                return new ContainerExternalStorage((TileExternalStorage) tile, player);
            case RSGui.RELAY:
                return new ContainerRelay((TileRelay) tile, player);
            case RSGui.INTERFACE:
                return new ContainerInterface((TileInterface) tile, player);
            case RSGui.CRAFTING_MONITOR:
                return new ContainerCraftingMonitor(((TileCraftingMonitor) tile).getNode(), (TileCraftingMonitor) tile, player);
            case RSGui.WIRELESS_TRANSMITTER:
                return new ContainerWirelessTransmitter((TileWirelessTransmitter) tile, player);
            case RSGui.CRAFTER:
                return new ContainerCrafter((TileCrafter) tile, player);
            case RSGui.PROCESSING_PATTERN_ENCODER:
                return new ContainerProcessingPatternEncoder((TileProcessingPatternEncoder) tile, player);
            case RSGui.NETWORK_TRANSMITTER:
                return new ContainerNetworkTransmitter((TileNetworkTransmitter) tile, player);
            case RSGui.FLUID_INTERFACE:
                return new ContainerFluidInterface((TileFluidInterface) tile, player);
            case RSGui.FLUID_STORAGE:
                return new ContainerFluidStorage((TileFluidStorage) tile, player);
            case RSGui.DISK_MANIPULATOR:
                return new ContainerDiskManipulator((TileDiskManipulator) tile, player);
            case RSGui.READER_WRITER:
                return new ContainerReaderWriter((IGuiReaderWriter) ((TileNode) tile).getNode(), (TileBase) tile, player);
            case RSGui.SECURITY_MANAGER:
                return new ContainerSecurityManager((TileSecurityManager) tile, player);
            case RSGui.STORAGE_MONITOR:
                return new ContainerStorageMonitor((TileStorageMonitor) tile, player);
            default:
                return null;
        }
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == RSGui.WIRELESS_GRID) {
            return getWirelessGridContainer(player, x, y, z);
        } else if (ID == RSGui.FILTER) {
            return getFilterContainer(player, x);
        } else if (ID == RSGui.WIRELESS_CRAFTING_MONITOR) {
            return getWirelessCraftingMonitorContainer(player, x, y);
        }

        return getContainer(ID, player, unwrapMultipart(world.getTileEntity(new BlockPos(x, y, z))));
    }

    private TileEntity unwrapMultipart(TileEntity tile) {
        if (IntegrationMCMP.isLoaded() && tile instanceof TileMultipartContainer.Ticking) {
            Optional<IMultipartTile> multipartTile = ((TileMultipartContainer.Ticking) tile).getPartTile(EnumCenterSlot.CENTER);

            if (multipartTile.isPresent()) {
                return multipartTile.get().getTileEntity();
            }
        }

        return tile;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = unwrapMultipart(world.getTileEntity(new BlockPos(x, y, z)));

        switch (ID) {
            case RSGui.CONTROLLER:
                return new GuiController((ContainerController) getContainer(ID, player, tile), (TileController) tile);
            case RSGui.GRID:
                IGrid grid = ((TileGrid) tile).getNode();
                GuiGrid gui = new GuiGrid(null, grid);
                gui.inventorySlots = new ContainerGrid(grid, gui, null, player);
                return gui;
            case RSGui.WIRELESS_GRID:
                return getWirelessGridGui(player, x, y, z);
            case RSGui.DISK_DRIVE:
                return new GuiStorage((ContainerDiskDrive) getContainer(ID, player, tile), ((TileDiskDrive) tile).getNode(), "gui/disk_drive.png");
            case RSGui.IMPORTER:
                return new GuiImporter((ContainerImporter) getContainer(ID, player, tile));
            case RSGui.EXPORTER:
                return new GuiExporter((ContainerExporter) getContainer(ID, player, tile));
            case RSGui.DETECTOR:
                return new GuiDetector((ContainerDetector) getContainer(ID, player, tile));
            case RSGui.SOLDERER:
                return new GuiSolderer((ContainerSolderer) getContainer(ID, player, tile));
            case RSGui.DESTRUCTOR:
                return new GuiDestructor((ContainerDestructor) getContainer(ID, player, tile));
            case RSGui.CONSTRUCTOR:
                return new GuiConstructor((ContainerConstructor) getContainer(ID, player, tile));
            case RSGui.STORAGE:
                return new GuiStorage((ContainerStorage) getContainer(ID, player, tile), ((TileStorage) tile).getNode());
            case RSGui.EXTERNAL_STORAGE:
                return new GuiStorage((ContainerExternalStorage) getContainer(ID, player, tile), ((TileExternalStorage) tile).getNode());
            case RSGui.RELAY:
                return new GuiRelay((ContainerRelay) getContainer(ID, player, tile));
            case RSGui.INTERFACE:
                return new GuiInterface((ContainerInterface) getContainer(ID, player, tile));
            case RSGui.CRAFTING_MONITOR:
                return new GuiCraftingMonitor((ContainerCraftingMonitor) getContainer(ID, player, tile), ((TileCraftingMonitor) tile).getNode());
            case RSGui.WIRELESS_TRANSMITTER:
                return new GuiWirelessTransmitter((ContainerWirelessTransmitter) getContainer(ID, player, tile));
            case RSGui.CRAFTER:
                return new GuiCrafter((ContainerCrafter) getContainer(ID, player, tile));
            case RSGui.PROCESSING_PATTERN_ENCODER:
                return new GuiProcessingPatternEncoder((ContainerProcessingPatternEncoder) getContainer(ID, player, tile), (TileProcessingPatternEncoder) tile);
            case RSGui.FILTER:
                return new GuiFilter(getFilterContainer(player, x));
            case RSGui.NETWORK_TRANSMITTER:
                return new GuiNetworkTransmitter((ContainerNetworkTransmitter) getContainer(ID, player, tile), (TileNetworkTransmitter) tile);
            case RSGui.FLUID_INTERFACE:
                return new GuiFluidInterface((ContainerFluidInterface) getContainer(ID, player, tile));
            case RSGui.FLUID_STORAGE:
                return new GuiStorage((ContainerFluidStorage) getContainer(ID, player, tile), ((TileFluidStorage) tile).getNode());
            case RSGui.DISK_MANIPULATOR:
                return new GuiDiskManipulator((ContainerDiskManipulator) getContainer(ID, player, tile));
            case RSGui.WIRELESS_CRAFTING_MONITOR:
                return getWirelessCraftingMonitorGui(player, x, y);
            case RSGui.READER_WRITER:
                return new GuiReaderWriter((ContainerReaderWriter) getContainer(ID, player, tile), (IGuiReaderWriter) ((TileNode) tile).getNode());
            case RSGui.SECURITY_MANAGER:
                return new GuiSecurityManager((ContainerSecurityManager) getContainer(ID, player, tile), (TileSecurityManager) tile);
            case RSGui.STORAGE_MONITOR:
                return new GuiStorageMonitor((ContainerStorageMonitor) getContainer(ID, player, tile));
            default:
                return null;
        }
    }

    private IGrid getWirelessGrid(EntityPlayer player, int hand, int controllerDimension, int type) {
        ItemStack stack = player.getHeldItem(EnumHand.values()[hand]);

        switch (type) {
            case NetworkItemWirelessGrid.GRID_TYPE:
                return new WirelessGrid(controllerDimension, stack);
            case NetworkItemWirelessFluidGrid.GRID_TYPE:
                return new WirelessFluidGrid(controllerDimension, stack);
            default:
                return null;
        }
    }

    private GuiGrid getWirelessGridGui(EntityPlayer player, int hand, int controllerDimension, int type) {
        IGrid grid = getWirelessGrid(player, hand, controllerDimension, type);

        GuiGrid gui = new GuiGrid(null, grid);
        gui.inventorySlots = new ContainerGrid(grid, gui, null, player);
        return gui;
    }

    private ContainerGrid getWirelessGridContainer(EntityPlayer player, int hand, int controllerDimension, int type) {
        return new ContainerGrid(getWirelessGrid(player, hand, controllerDimension, type), new GridDisplayDummy(), null, player);
    }

    private WirelessCraftingMonitor getWirelessCraftingMonitor(EntityPlayer player, int hand, int controllerDimension) {
        return new WirelessCraftingMonitor(controllerDimension, player.getHeldItem(EnumHand.values()[hand]));
    }

    private GuiCraftingMonitor getWirelessCraftingMonitorGui(EntityPlayer player, int hand, int controllerDimension) {
        WirelessCraftingMonitor craftingMonitor = getWirelessCraftingMonitor(player, hand, controllerDimension);

        return new GuiCraftingMonitor(new ContainerCraftingMonitor(craftingMonitor, null, player), craftingMonitor);
    }

    private ContainerCraftingMonitor getWirelessCraftingMonitorContainer(EntityPlayer player, int hand, int controllerDimension) {
        return new ContainerCraftingMonitor(getWirelessCraftingMonitor(player, hand, controllerDimension), null, player);
    }

    private ContainerFilter getFilterContainer(EntityPlayer player, int hand) {
        return new ContainerFilter(player, player.getHeldItem(EnumHand.values()[hand]));
    }
}

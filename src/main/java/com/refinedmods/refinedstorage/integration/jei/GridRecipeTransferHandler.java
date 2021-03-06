package com.refinedmods.refinedstorage.integration.jei;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.container.GridContainer;
import com.refinedmods.refinedstorage.network.grid.GridProcessingTransferMessage;
import com.refinedmods.refinedstorage.network.grid.GridTransferMessage;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredient;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class GridRecipeTransferHandler implements IRecipeTransferHandler<GridContainer> {
    public static final long TRANSFER_SCROLLBAR_DELAY_MS = 200;
    public static long LAST_TRANSFER_TIME;

    @Override
    public Class<GridContainer> getContainerClass() {
        return GridContainer.class;
    }

    @Override
    public IRecipeTransferError transferRecipe(@Nonnull GridContainer container, @Nonnull IRecipeLayout recipeLayout, @Nonnull PlayerEntity player, boolean maxTransfer, boolean doTransfer) {
        IGrid grid = container.getGrid();

        if (doTransfer) {
            LAST_TRANSFER_TIME = System.currentTimeMillis();

            if (grid.getGridType() == GridType.PATTERN && !isCraftingRecipe(recipeLayout.getRecipeCategory())) {
                List<ItemStack> inputs = new LinkedList<>();
                List<ItemStack> outputs = new LinkedList<>();

                List<FluidStack> fluidInputs = new LinkedList<>();
                List<FluidStack> fluidOutputs = new LinkedList<>();

                for (IGuiIngredient<ItemStack> guiIngredient : recipeLayout.getItemStacks().getGuiIngredients().values()) {
                    if (guiIngredient != null && guiIngredient.getDisplayedIngredient() != null) {
                        ItemStack ingredient = guiIngredient.getDisplayedIngredient().copy();

                        if (guiIngredient.isInput()) {
                            inputs.add(ingredient);
                        } else {
                            outputs.add(ingredient);
                        }
                    }
                }

                for (IGuiIngredient<FluidStack> guiIngredient : recipeLayout.getFluidStacks().getGuiIngredients().values()) {
                    if (guiIngredient != null && guiIngredient.getDisplayedIngredient() != null) {
                        FluidStack ingredient = guiIngredient.getDisplayedIngredient().copy();

                        if (guiIngredient.isInput()) {
                            fluidInputs.add(ingredient);
                        } else {
                            fluidOutputs.add(ingredient);
                        }
                    }
                }

                RS.NETWORK_HANDLER.sendToServer(new GridProcessingTransferMessage(inputs, outputs, fluidInputs, fluidOutputs));
            } else {
                RS.NETWORK_HANDLER.sendToServer(new GridTransferMessage(
                    recipeLayout.getItemStacks().getGuiIngredients(),
                    container.inventorySlots.stream().filter(s -> s.inventory instanceof CraftingInventory).collect(Collectors.toList())
                ));
            }
        }

        return null;
    }

    private boolean isCraftingRecipe(IRecipeCategory<?> recipeCategory) {
        return recipeCategory.getUid().equals(VanillaRecipeCategoryUid.CRAFTING);
    }
}

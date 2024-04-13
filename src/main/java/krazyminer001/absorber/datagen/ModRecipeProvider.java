package krazyminer001.absorber.datagen;

import krazyminer001.absorber.blocks.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.RecipeCategory;

import java.util.ArrayList;
import java.util.List;

public class ModRecipeProvider extends FabricRecipeProvider {

    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        offerSmelting(exporter, List.of(ModBlocks.FILLED_ABSORBER), RecipeCategory.BUILDING_BLOCKS, ModBlocks.ABSORBER, 0.15f, 200, "");

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.ABSORBER)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .input('A', Blocks.DARK_PRISMARINE)
                .input('B', Blocks.PRISMARINE)
                .input('C', Blocks.SPONGE)
                .criterion(FabricRecipeProvider.hasItem(Blocks.SPONGE), FabricRecipeProvider.conditionsFromItem(Blocks.SPONGE))
                .offerTo(exporter);
    }
}

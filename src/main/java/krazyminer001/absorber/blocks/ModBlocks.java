package krazyminer001.absorber.blocks;

import krazyminer001.absorber.TheAbsorber;
import krazyminer001.absorber.blocks.custom.AbsorberBlock;
import krazyminer001.absorber.blocks.custom.WetAbsorberBlock;
import krazyminer001.absorber.sounds.ModSoundGroups;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block ABSORBER = registerBlock("absorber",
            new AbsorberBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(ModSoundGroups.ABSORBER)));
    public static final Block FILLED_ABSORBER = registerBlock("filled_absorber",
            new WetAbsorberBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(ModSoundGroups.FILLED_ABSORBER)));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(TheAbsorber.ModID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, Identifier.of(TheAbsorber.ModID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks() {
        TheAbsorber.LOGGER.info("Registering Mod Blocks");
    }
}

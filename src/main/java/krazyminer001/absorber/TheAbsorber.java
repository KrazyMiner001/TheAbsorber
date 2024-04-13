package krazyminer001.absorber;

import krazyminer001.absorber.blocks.ModBlocks;
import krazyminer001.absorber.sounds.ModSounds;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TheAbsorber implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("absorber");
	public static final String ModID = "absorber";

	@Override
	public void onInitialize() {
		ModBlocks.registerModBlocks();
		ModSounds.registerModSounds();

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(content -> {
			content.add(ModBlocks.ABSORBER);
			content.add(ModBlocks.FILLED_ABSORBER);
		});
	}
}
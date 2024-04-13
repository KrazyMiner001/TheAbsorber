package krazyminer001.absorber.sounds;

import krazyminer001.absorber.TheAbsorber;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final SoundEvent BLOCK_ABSORBER_ABSORB = registerSound("block.absorber.absorb");
    public static final SoundEvent BLOCK_ABSORBER_BREAK = registerSound("block.absorber.break");
    public static final SoundEvent BLOCK_ABSORBER_PLACE = registerSound("block.absorber.place");
    public static final SoundEvent BLOCK_ABSORBER_HIT = registerSound("block.absorber.hit");
    public static final SoundEvent BLOCK_ABSORBER_FALL = registerSound("block.absorber.fall");

    public static final SoundEvent BLOCK_ABSORBER_STEP = registerSound("block.absorber.step");
    public static final SoundEvent BLOCK_FILLED_ABSORBER_BREAK = registerSound("block.filled_absorber.break");
    public static final SoundEvent BLOCK_FILLED_ABSORBER_PLACE = registerSound("block.filled_absorber.place");
    public static final SoundEvent BLOCK_FILLED_ABSORBER_HIT = registerSound("block.filled_absorber.hit");
    public static final SoundEvent BLOCK_FILLED_ABSORBER_FALL = registerSound("block.filled_absorber.fall");

    public static final SoundEvent BLOCK_FILLED_ABSORBER_STEP = registerSound("block.filled_absorber.step");

    private static SoundEvent registerSound(String name) {
        Identifier soundId = new Identifier(TheAbsorber.ModID, name);
        return Registry.register(Registries.SOUND_EVENT, soundId, SoundEvent.of(soundId));
    }

    public static void registerModSounds() {
        TheAbsorber.LOGGER.info("Registering Mod Sounds");
    }
}

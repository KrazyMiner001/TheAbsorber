package krazyminer001.absorber.sounds;

import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;

public class ModSoundGroups {
    public static final BlockSoundGroup ABSORBER = new BlockSoundGroup(1f, 1f, ModSounds.BLOCK_ABSORBER_BREAK, ModSounds.BLOCK_ABSORBER_STEP, ModSounds.BLOCK_ABSORBER_PLACE, ModSounds.BLOCK_ABSORBER_HIT, ModSounds.BLOCK_ABSORBER_FALL);
    public static final BlockSoundGroup FILLED_ABSORBER = new BlockSoundGroup(1f, 1f, ModSounds.BLOCK_FILLED_ABSORBER_BREAK, ModSounds.BLOCK_FILLED_ABSORBER_STEP, ModSounds.BLOCK_FILLED_ABSORBER_PLACE, ModSounds.BLOCK_FILLED_ABSORBER_HIT, ModSounds.BLOCK_FILLED_ABSORBER_FALL);
}

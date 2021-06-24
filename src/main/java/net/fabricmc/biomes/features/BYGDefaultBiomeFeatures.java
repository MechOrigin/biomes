package net.fabricmc.biomes.features;

import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.gen.GenerationStep;

public class BYGDefaultBiomeFeatures {

    public static void addLargeLake(GenerationSettings.Builder gen) {
        gen.feature(GenerationStep.Feature.RAW_GENERATION, BYGConfiguredFeatures.WIDE_WATER_LAKE);
    }
}

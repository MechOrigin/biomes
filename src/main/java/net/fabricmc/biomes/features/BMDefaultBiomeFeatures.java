package net.fabricmc.biomes.features;

import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.gen.GenerationStep;

public class BMDefaultBiomeFeatures {

    public static void addLargeLake(GenerationSettings.Builder gen) {
        gen.feature(GenerationStep.Feature.RAW_GENERATION, 
        BMConfiguredFeatures.WIDE_WATER_LAKE);
    }

}

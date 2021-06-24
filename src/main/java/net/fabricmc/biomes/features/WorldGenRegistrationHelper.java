package net.fabricmc.biomes.features;

import net.fabricmc.biomes.BiomesMod;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class WorldGenRegistrationHelper {

    public static <C extends FeatureConfig, F extends Feature<C>> F createFeature(String id, F feature) {
        Identifier bygID = new Identifier(BiomesMod.MOD_ID, id);
        if (Registry.FEATURE.getIds().contains(bygID))
            throw new IllegalStateException("Feature ID: \"" + bygID.toString() + "\" already exists in the Features registry!");

        Registry.register(Registry.FEATURE, bygID, feature);
        BYGFeatures.features.add(feature);
        return feature;
    }

    public static <FC extends FeatureConfig, F extends Feature<FC>, CF extends ConfiguredFeature<FC, F>> CF createConfiguredFeature(String id, CF configuredFeature) {
        Identifier bygID = new Identifier(BiomesMod.MOD_ID, id);
        if (BuiltinRegistries.CONFIGURED_FEATURE.getIds().contains(bygID))
            throw new IllegalStateException("Configured Feature ID: \"" + bygID.toString() + "\" already exists in the Configured Features registry!");

        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, bygID, configuredFeature);
        return configuredFeature;
    }
}
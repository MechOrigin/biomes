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
        Identifier bmID = new Identifier(BiomesMod.MOD_ID, id);
        if (Registry.FEATURE.getIds().contains(bmID))
            throw new IllegalStateException("Feature ID: \"" + bmID.toString() + "\" already exists in registry.");

        Registry.register(Registry.FEATURE, bmID, feature);
        BMFeatures.features.add(feature);
        return feature;
    }

    public static <FC extends FeatureConfig, F extends Feature<FC>, CF extends ConfiguredFeature<FC, F>> CF createConfiguredFeature(String id, CF configuredFeature) {
        Identifier bmID = new Identifier(BiomesMod.MOD_ID, id);
        if (BuiltinRegistries.CONFIGURED_FEATURE.getIds().contains(bmID))
            throw new IllegalStateException("Configured Feature ID: \"" + bmID.toString() + "\" already exists in registry.");

        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, bmID, configuredFeature);
        return configuredFeature;
    }
}
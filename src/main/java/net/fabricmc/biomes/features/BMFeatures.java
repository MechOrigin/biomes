package net.fabricmc.biomes.features;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.gen.feature.Feature;

import static net.fabricmc.biomes.features.WorldGenRegistrationHelper.createFeature;


public class BMFeatures {

    public static List<Feature<?>> features = new ArrayList<>();

    //Wide Lakes
    public static final Feature<SimpleBlockProviderConfig> WIDE_LAKE = createFeature("wide_lake", new WideLake(SimpleBlockProviderConfig.CODEC.stable()));

    public static void init() {
        
    }

}



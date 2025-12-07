package net.abnormal.anabnormalcircumstance.datagen;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import net.abnormal.anabnormalcircumstance.recipe.SpellScrollRecipeSerializer;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

public class SpellScrollRecipeProvider extends FabricRecipeProvider {

    public SpellScrollRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {

        String[] spells = new String[] {
                "hydro_water_veil", "hydro_healing_fluids", "hydro_divers_grace",
                "hydro_control_weather", "hydro_cleansing_fluids", "hydro_bile_water",
                "hydro_icicle_shatter",

                "pyro_fire_aspect", "pyro_cinder_cloaking", "pyro_fireball",
                "pyro_flame_charge", "pyro_pyroclasm_wave", "pyro_blood_pact",
                "pyro_phoenix_rebirth", "pyro_molten_fury",

                "geo_rock_blast", "geo_quick_claws", "geo_growth",
                "geo_rock_polish", "geo_stomp", "geo_seismic_crown",
                "geo_earthquake", "geo_immovable_object",

                "aero_light_as_a_feather", "aero_soaring_stride", "aero_silent_step",
                "aero_updrafts", "aero_gale_winds", "aero_hurricanes_call",
                "aero_stormlords_will"
        };

        for (String spell : spells) {
            JsonObject root = new JsonObject();
            root.addProperty("type", "anabnormalcircumstance:spell_scroll");

            JsonArray pattern = new JsonArray();
            pattern.add("#Q#");
            pattern.add("ZXZ");
            pattern.add("QTQ");
            root.add("pattern", pattern);

            JsonObject key = new JsonObject();

            JsonObject manaGem = new JsonObject();
            manaGem.addProperty("item", "anabnormalcircumstance:mana_gem");
            key.add("#", manaGem);

            JsonObject prismShard = new JsonObject();
            prismShard.addProperty("item", "minecraft:prismarine_shard");
            key.add("Q", prismShard);

            JsonObject waterBucket = new JsonObject();
            waterBucket.addProperty("item", "minecraft:water_bucket");
            key.add("T", waterBucket);

            JsonObject scroll = new JsonObject();
            scroll.addProperty("item", "anabnormalcircumstance:spell_scroll");
            key.add("X", scroll);

            JsonObject crystals = new JsonObject();
            crystals.addProperty("item", "minecraft:prismarine_crystals");
            key.add("Z", crystals);

            root.add("key", key);

            JsonObject result = new JsonObject();
            result.addProperty("item", "anabnormalcircumstance:spell_scroll");
            result.addProperty("count", 1);
            result.addProperty("nbt", "{spell_id:\"anabnormalcircumstance:" + spell + "\"}");
            root.add("result", result);

            exporter.accept(new SimpleRecipeJsonProvider(
                    root,
                    new Identifier("anabnormalcircumstance", spell + "_scroll")
            ));
        }
    }

    private static final class SimpleRecipeJsonProvider implements RecipeJsonProvider {

        private final JsonObject json;
        private final Identifier id;

        public SimpleRecipeJsonProvider(JsonObject json, Identifier id) {
            this.json = json;
            this.id = id;
        }

        @Override
        public void serialize(JsonObject jsonOut) {
            for (Map.Entry<String, com.google.gson.JsonElement> e : json.entrySet()) {
                jsonOut.add(e.getKey(), e.getValue());
            }
        }

        @Override
        public Identifier getRecipeId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return SpellScrollRecipeSerializer.INSTANCE;
        }

        @Nullable
        @Override
        public JsonObject toAdvancementJson() {
            return null;
        }

        @Nullable
        @Override
        public Identifier getAdvancementId() {
            return null;
        }
    }
}

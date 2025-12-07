package net.abnormal.anabnormalcircumstance.recipe;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;

public class SpellScrollRecipeSerializer implements RecipeSerializer<SpellScrollRecipe> {

    public static final SpellScrollRecipeSerializer INSTANCE = new SpellScrollRecipeSerializer();
    public static final Identifier ID = new Identifier("anabnormalcircumstance", "spell_scroll");

    @Override
    public SpellScrollRecipe read(Identifier id, JsonObject json) {
        // Let vanilla parse the shaped recipe structure
        ShapedRecipe inner = ShapedRecipe.Serializer.SHAPED.read(id, json);

        // If NBT is specified in the result, parse and create an override ItemStack to pass to the wrapper recipe
        if (json.has("result") && json.getAsJsonObject("result").has("nbt")) {
            JsonObject resultObj = json.getAsJsonObject("result");
            String nbtString = resultObj.get("nbt").getAsString();

            NbtCompound nbt;
            try {
                nbt = StringNbtReader.parse(nbtString);
            } catch (Exception e) {
                throw new IllegalStateException("Invalid NBT in spell scroll recipe: " + nbtString, e);
            }

            ItemStack output = inner.getOutput(DynamicRegistryManager.EMPTY).copy();
            output.setNbt(nbt);

            // Return wrapper recipe with override output (no mutation of ShapedRecipe)
            return new SpellScrollRecipe(inner, output);
        }

        return new SpellScrollRecipe(inner);
    }

    @Override
    public SpellScrollRecipe read(Identifier id, net.minecraft.network.PacketByteBuf buf) {
        ShapedRecipe inner = ShapedRecipe.Serializer.SHAPED.read(id, buf);
        // Packet data does not include NBT override here; keep behavior consistent with write()
        return new SpellScrollRecipe(inner);
    }

    @Override
    public void write(net.minecraft.network.PacketByteBuf buf, SpellScrollRecipe recipe) {
        ShapedRecipe.Serializer.SHAPED.write(buf, recipe.getInner());
    }
}
package net.abnormal.anabnormalcircumstance.magic;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;


// Base immutable Spell definition.
// Concrete spells implement server-side behavior in cast(ServerPlayerEntity caster).

public abstract class Spell {
    private final Identifier id;
    private final SpellElement element;
    private final SpellTier tier;
    private final int manaCost;
    private final int cooldownTicks; // stored in ticks
    private final Identifier icon;
    private String displayName = "";
    private String description = "";

    protected Spell(Identifier id, SpellElement element, SpellTier tier, int manaCost, int cooldownSeconds, Identifier icon, String displayName, String description) {
        this.id = id;
        this.element = element;
        this.tier = tier;
        this.manaCost = manaCost;
        this.cooldownTicks = Math.max(0, cooldownSeconds * 20);
        this.icon = icon;
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public Identifier getId() { return id; }
    public SpellElement getElement() { return element; }
    public SpellTier getTier() { return tier; }
    public int getManaCost() { return manaCost; }
    public int getCooldownTicks() { return cooldownTicks; }
    public Identifier getIcon() { return icon; }
    public String getDescription() { return description; }

    public abstract boolean cast(ServerPlayerEntity caster);


}

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

    protected Spell(Identifier id, SpellElement element, SpellTier tier, int manaCost, int cooldownSeconds, Identifier icon, String displayName) {
        this.id = id;
        this.element = element;
        this.tier = tier;
        this.manaCost = manaCost;
        this.cooldownTicks = Math.max(0, cooldownSeconds * 20);
        this.icon = icon;
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }
    public Identifier getId() { return id; }
    public SpellElement getElement() { return element; }
    public SpellTier getTier() { return tier; }
    public int getManaCost() { return manaCost; }
    public int getCooldownTicks() { return cooldownTicks; }
    public Identifier getIcon() { return icon; }

    /**
     * Execute the spell effect. Called server-side only.
     * Return true if the spell had successful effect (will result in mana spent & cooldown applied).
     * Implementations MUST perform all world changes on server thread.
     */
    public abstract boolean cast(ServerPlayerEntity caster);
}

package me.alfie.totem_of_recalling;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config {

    public static final Config CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    public final ModConfigSpec.ConfigValue<Integer> EXP_LEVELS_COST;
    public final ModConfigSpec.ConfigValue<Integer> COOLDOWN_SECONDS;
    public final ModConfigSpec.ConfigValue<Integer> WAIT_SECONDS;
    public final ModConfigSpec.ConfigValue<Boolean> REQUIRE_STANDING_STILL;

    static  {
        Pair<Config, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(Config::new);

        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

    public Config(ModConfigSpec.Builder builder) {
        EXP_LEVELS_COST = builder
                .comment("Amount of experience levels it costs to use the totem of recalling. Note there is an enchantment that reduces this cost.")
                .translation("totemofrecalling.config.exp_levels_cost")
                .define("Experience Levels Cost", 10);

        COOLDOWN_SECONDS = builder
                .comment("Base cooldown in seconds after using the totem of recalling. Note: there is an enchantment that reduces this time.")
                .translation("totemofrecalling.config.cooldown_seconds")
                .define("Cooldown Seconds", 1200);

        WAIT_SECONDS = builder
                .comment("Base seconds the player has to wait before teleporting. 0 teleports instantly. Note: there is an enchantment that reduces this time.")
                .translation("totemofrecalling.config.wait_seconds")
                .define("Wait Seconds", 10);

        REQUIRE_STANDING_STILL = builder
                .comment("If true, moving during the wait time cancels the recall. Note: there is an enchantment that removes this requirement.")
                .translation("totemofrecalling.config.require_standing_still")
                .define("Require Standing Still", true);
    }
}

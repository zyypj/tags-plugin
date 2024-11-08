package gg.discord.mrkk.tadeu.zytags.systems;

import me.clip.placeholderapi.PlaceholderAPI;

public class SkillsIntegration {

    public String getTopPlayer() {
        String result = PlaceholderAPI.setBracketPlaceholders(null, "%auraskills_lb_power_1_name%");
        return result != null ? result : "&cNinguém";
    }

    public int getTopValue() {
        String result = PlaceholderAPI.setBracketPlaceholders(null, "%auraskills_lb_power_1_value%");
        try {
            return result != null ? Integer.parseInt(result) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

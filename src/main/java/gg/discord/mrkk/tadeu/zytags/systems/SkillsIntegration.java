package gg.discord.mrkk.tadeu.zytags.systems;

import me.clip.placeholderapi.PlaceholderAPI;

public class SkillsIntegration {

    public String getTopPlayer() {
        String result = PlaceholderAPI.setPlaceholders(null, "%auraskills_lb_power_1_name%");
        if (result.equals("%auraskills_lb_power_1_name%")) {
            return "&cPlaceholder inválido";
        }
        return result;
    }

    public int getTopValue() {
        String result = PlaceholderAPI.setPlaceholders(null, "%auraskills_lb_power_1_value%");
        try {
            if (result.equals("%auraskills_lb_power_1_value%")) {
                return 0;
            }
            return Integer.parseInt(result);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

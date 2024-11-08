package gg.discord.mrkk.tadeu.zytags.systems;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.Objects;

public class TimeIntegration {

    private final CMI cmi;

    public TimeIntegration() {
        this.cmi = CMI.getInstance();
    }

    public long getOnlineTime(Player player) {
        CMIUser user = cmi.getPlayerManager().getUser(player);

        if (user == null) {
            return 0;
        }

        return user.getTotalPlayTime();
    }

    public String getTopPlayer() {
        return cmi.getPlayerManager().getAllUsers().values().stream()
                .filter(Objects::nonNull)
                .max(Comparator.comparingLong(CMIUser::getTotalPlayTime))
                .map(CMIUser::getName)
                .orElse("&cNinguém");
    }
}

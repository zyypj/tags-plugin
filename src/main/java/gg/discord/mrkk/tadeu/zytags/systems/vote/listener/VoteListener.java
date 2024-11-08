package gg.discord.mrkk.tadeu.zytags.systems.vote.listener;

import gg.discord.mrkk.tadeu.zytags.Main;
import gg.discord.mrkk.tadeu.zytags.systems.VoteIntegration;
import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class VoteListener implements Listener {

    private final VoteIntegration voteIntegration;

    public VoteListener(Main plugin) {
        this.voteIntegration = plugin.getVoteIntegration();
    }

    @EventHandler
    public void onVoteEvent(VotifierEvent event) {
        Player player = Bukkit.getPlayer(event.getVote().getUsername());

        if (player != null) {
            voteIntegration.addVote(player.getUniqueId());
        }
    }
}

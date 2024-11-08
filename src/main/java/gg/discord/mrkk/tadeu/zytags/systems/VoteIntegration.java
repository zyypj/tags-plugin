package gg.discord.mrkk.tadeu.zytags.systems;

import gg.discord.mrkk.tadeu.zytags.Main;
import gg.discord.mrkk.tadeu.zytags.storage.MySQL;
import gg.discord.mrkk.tadeu.zytags.systems.vote.cache.VoteCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class VoteIntegration {

    private final VoteCache voteCache;
    private final MySQL database;

    public VoteIntegration(Main plugin) {
        this.voteCache = plugin.getVoteCache();
        this.database = plugin.getStorage();
    }

    public void addVote(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        assert player != null;
        voteCache.addVote(player);
    }

    public int getVotes(UUID playerUUID) {
        int votesFromCache = voteCache.getVotes(playerUUID);
        int votesFromDatabase = getVotesFromDatabase(playerUUID);

        return Math.max(votesFromCache, votesFromDatabase) + Math.min(votesFromCache, votesFromDatabase);
    }

    private int getVotesFromDatabase(UUID playerUUID) {
        String sql = "SELECT votes FROM player_votes WHERE player_uuid = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, playerUUID.toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("votes");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Obtém o UUID do jogador com o maior número de votos.
     *
     * @return UUID do jogador com o maior número de votos ou null se não houver jogadores.
     */
    public UUID getTopPlayerUUID() {
        UUID topPlayerUUID = null;
        int highestVotes = -1;

        // Verifica todos os jogadores no cache
        for (Map.Entry<UUID, Integer> entry : voteCache.getAllVotes().entrySet()) {
            UUID playerUUID = entry.getKey();
            int votes = getVotes(playerUUID);

            if (votes > highestVotes) {
                highestVotes = votes;
                topPlayerUUID = playerUUID;
            }
        }

        // Verifica os votos do banco de dados, caso necessário
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT player_uuid, votes FROM player_votes")) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UUID playerUUID = UUID.fromString(rs.getString("player_uuid"));
                int votes = rs.getInt("votes");

                // Considera o máximo entre cache e database
                votes = Math.max(votes, voteCache.getVotes(playerUUID));

                if (votes > highestVotes) {
                    highestVotes = votes;
                    topPlayerUUID = playerUUID;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return topPlayerUUID;
    }
}
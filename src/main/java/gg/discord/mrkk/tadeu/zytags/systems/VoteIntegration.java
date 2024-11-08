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
    // Exemplo simplificado de método em VoteIntegration
    public UUID getTopPlayerUUID() {
        try (PreparedStatement statement = database.getConnection().prepareStatement("SELECT player_uuid FROM player_votes ORDER BY votes DESC LIMIT 1")) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return UUID.fromString(resultSet.getString("player_uuid"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
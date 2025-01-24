package me.kiiya.hotbarmanager.database.providers;

import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.database.Database;
import me.kiiya.hotbarmanager.api.hotbar.Category;
import me.kiiya.hotbarmanager.utils.Support;
import me.kiiya.hotbarmanager.utils.Utility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SQLite implements Database {
    private Connection connection;

    public SQLite() {
        Utility.info("&eUsing SQLite as a database provider!");
        Utility.info("&eConnecting to SQLite database...");
        getConnection();
        Utility.info("&aSuccessfully connected!");
        Utility.info("&eCreating tables...");
        createTables();
        Utility.info("&aTables created successfully");
        Utility.info("&aSQLite loaded correctly!");
    }

    @Override
    public void createPlayerData(Player player, List<Category> defaultSlots) {
        String path = player.getUniqueId().toString();

        try (Connection c = getConnection()) {
            PreparedStatement check = c.prepareStatement("SELECT player FROM bedwars_hotbar_manager WHERE player=?");
            check.setString(1, path);
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                String str = rs.getString("player");
                if (str != null) {
                    check.close();
                    c.close();
                    return;
                }
            }
            String sql = "INSERT INTO bedwars_hotbar_manager(player, slot0, slot1, slot2, slot3, slot4, slot5, slot6, slot7, slot8) VALUES (?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, path);
            for (int i = 2; i <= 10; i++) {
                Category slot = defaultSlots.get(i-2);
                if (slot == null) slot = Category.NONE;
                ps.setString(i, slot.toString());
            }
            ps.executeUpdate();
            ps.close();
            c.close();
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public HashMap<String, String> getData(Player player) {
        HashMap<String, String> data = new HashMap<>();
        String path = player.getUniqueId().toString();
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM bedwars_hotbar_manager WHERE player=?")) {
            ps.setString(1, path);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data.put("slot0", rs.getString("slot0"));
                    data.put("slot1", rs.getString("slot1"));
                    data.put("slot2", rs.getString("slot2"));
                    data.put("slot3", rs.getString("slot3"));
                    data.put("slot4", rs.getString("slot4"));
                    data.put("slot5", rs.getString("slot5"));
                    data.put("slot6", rs.getString("slot6"));
                    data.put("slot7", rs.getString("slot7"));
                    data.put("slot8", rs.getString("slot8"));
                    rs.close();
                    ps.close();
                    c.close();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return data;
    }

    @Override
    public String getData(Player player, String column) {
        if (!isValidColumn(column)) {
            throw new IllegalArgumentException("Invalid column name");
        }

        String path = player.getUniqueId().toString();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT " + column + " FROM bedwars_hotbar_manager WHERE player=?")) {
            ps.setString(1, path);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String result = rs.getString(column);
                    rs.close();
                    ps.close();
                    conn.close();
                    return result;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e); // Consider logging the exception instead of throwing
        }
        return null;
    }


    @Override
    public void setData(Player player, String column, String value) {
        String path = player.getUniqueId().toString();
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE bedwars_hotbar_manager SET " + column + "=? WHERE player=?")) {
            ps.setString(1, value);
            ps.setString(2, path);
            ps.executeUpdate();
            ps.close();
            c.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createTables() {
        this.connection = getConnection();
        try {
            Statement s = this.connection.createStatement();
            s.executeUpdate("CREATE TABLE IF NOT EXISTS bedwars_hotbar_manager(`player` varchar(200), `slot0` varchar(200),`slot1` varchar(200),`slot2` varchar(200),`slot3` varchar(200),`slot4` varchar(200),`slot5` varchar(200),`slot6` varchar(200),`slot7` varchar(200),`slot8` varchar(200), PRIMARY KEY (`player`));");
            s.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        String path;
        if (HotbarManager.getSupport() == Support.BEDWARS1058) path = Bukkit.getPluginManager().getPlugin("BedWars1058").getDataFolder() + "/Cache/";
        else path = Bukkit.getPluginManager().getPlugin("BedWars2023").getDataFolder() + "/Cache/";

        File dataFolder = new File(path, "hotbar_manager.db");
        if (!dataFolder.exists()) {
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            if (this.connection != null && !this.connection.isClosed())
                return this.connection;
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return this.connection;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isValidColumn(String column) {
        List<String> validColumns = Arrays.asList("slot0", "slot1", "slot2", "slot3", "slot4", "slot5", "slot6", "slot7", "slot8");
        return validColumns.contains(column);
    }
}

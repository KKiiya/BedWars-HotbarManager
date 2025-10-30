package me.kiiya.hotbarmanager.database.providers;

import com.zaxxer.hikari.HikariDataSource;
import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.database.Database;
import me.kiiya.hotbarmanager.api.hotbar.Category;
import me.kiiya.hotbarmanager.utils.Support;
import me.kiiya.hotbarmanager.utils.Utility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MySQL implements Database {
    private final String host;
    private final int port;
    private final String database;
    private final String user;
    private final String pass;
    private final boolean ssl;
    private HikariDataSource db;

    public MySQL() {
        Utility.info("&eUsing MySQL as database provider!");
        if (HotbarManager.getSupport() == Support.BEDWARS1058) {
            this.host = HotbarManager.getBW1058Api().getConfigs().getMainConfig().getString("database.host");
            this.database = HotbarManager.getBW1058Api().getConfigs().getMainConfig().getString("database.database");
            this.user = HotbarManager.getBW1058Api().getConfigs().getMainConfig().getString("database.user");
            this.pass = HotbarManager.getBW1058Api().getConfigs().getMainConfig().getString("database.pass");
            this.port = HotbarManager.getBW1058Api().getConfigs().getMainConfig().getInt("database.port");
            this.ssl = HotbarManager.getBW1058Api().getConfigs().getMainConfig().getBoolean("database.ssl");
        } else if (HotbarManager.getSupport() == Support.BEDWARS2023) {
            this.host = HotbarManager.getBW2023Api().getConfigs().getMainConfig().getString("database.host");
            this.database = HotbarManager.getBW2023Api().getConfigs().getMainConfig().getString("database.database");
            this.user = HotbarManager.getBW2023Api().getConfigs().getMainConfig().getString("database.user");
            this.pass = HotbarManager.getBW2023Api().getConfigs().getMainConfig().getString("database.pass");
            this.port = HotbarManager.getBW2023Api().getConfigs().getMainConfig().getInt("database.port");
            this.ssl = HotbarManager.getBW2023Api().getConfigs().getMainConfig().getBoolean("database.ssl");
        } else if (HotbarManager.getSupport() == Support.BEDWARSPROXY2023) {
            this.host = Bukkit.getPluginManager().getPlugin("BWProxy2023").getConfig().getString("database.host");
            this.database = Bukkit.getPluginManager().getPlugin("BWProxy2023").getConfig().getString("database.database");
            this.user = Bukkit.getPluginManager().getPlugin("BWProxy2023").getConfig().getString("database.user");
            this.pass = Bukkit.getPluginManager().getPlugin("BWProxy2023").getConfig().getString("database.pass");
            this.port = Bukkit.getPluginManager().getPlugin("BWProxy2023").getConfig().getInt("database.port");
            this.ssl = Bukkit.getPluginManager().getPlugin("BWProxy2023").getConfig().getBoolean("database.ssl");
        } else {
            this.host = Bukkit.getPluginManager().getPlugin("BedWarsProxy").getConfig().getString("database.host");
            this.database = Bukkit.getPluginManager().getPlugin("BedWarsProxy").getConfig().getString("database.database");
            this.user = Bukkit.getPluginManager().getPlugin("BedWarsProxy").getConfig().getString("database.user");
            this.pass = Bukkit.getPluginManager().getPlugin("BedWarsProxy").getConfig().getString("database.pass");
            this.port = Bukkit.getPluginManager().getPlugin("BedWarsProxy").getConfig().getInt("database.port");
            this.ssl = Bukkit.getPluginManager().getPlugin("BedWarsProxy").getConfig().getBoolean("database.ssl");
        }

        connect();
        createTables();
        Utility.info("&aMySQL loaded correctly!");
    }

    private void connect() {
        Utility.info("&eConnecting to MySQL database...");
        db = new HikariDataSource();
        db.setPoolName("HotbarManager-Pool");
        db.setMaximumPoolSize(50); // Increase pool size
        db.setConnectionTimeout(60000L);
        db.setMaxLifetime(1800000L);
        db.setIdleTimeout(60000L);
        if (HotbarManager.getVersion().equals("1.8.8") ||
            HotbarManager.getVersion().equals("1.12.2")) db.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        else db.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
        db.addDataSourceProperty("serverName", this.host);
        db.addDataSourceProperty("databaseName", this.database);
        db.addDataSourceProperty("port", this.port);
        db.addDataSourceProperty("user", this.user);
        db.addDataSourceProperty("password", this.pass);
        db.addDataSourceProperty("useSSL", this.ssl);
        Utility.info("&aSuccessfully connected!");
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
    public Connection getConnection() throws SQLException {
        return db.getConnection();
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
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + column + " FROM bedwars_hotbar_manager WHERE player=?")) {
            ps.setString(1, path);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String result = rs.getString(column);
                    rs.close();
                    ps.close();
                    c.close();
                    return result;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void setData(Player player, String column, String value) {
        if (!isValidColumn(column)) {
            throw new IllegalArgumentException("Invalid column name");
        }

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
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement("CREATE TABLE IF NOT EXISTS bedwars_hotbar_manager(player varchar(200) PRIMARY KEY, slot0 varchar(200), slot1 varchar(200), slot2 varchar(200), slot3 varchar(200), slot4 varchar(200), slot5 varchar(200), slot6 varchar(200), slot7 varchar(200), slot8 varchar(200))");){
            Utility.info("&eCreating tables...");
            ps.executeUpdate();
            Utility.info("&aTables created successfully!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isValidColumn(String column) {
        List<String> validColumns = Arrays.asList("slot0", "slot1", "slot2", "slot3", "slot4", "slot5", "slot6", "slot7", "slot8");
        return validColumns.contains(column);
    }
}

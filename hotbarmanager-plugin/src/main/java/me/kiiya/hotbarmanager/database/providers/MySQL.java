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
import java.util.List;

public class MySQL implements Database {
    private final String host;
    private final int port;
    private final String database;
    private final String user;
    private final String pass;
    private HikariDataSource db;

    public MySQL() {
        Utility.info("&eUsing MySQL as database provider!");
        if (HotbarManager.getSupport() == Support.BEDWARS1058) {
            this.host = HotbarManager.getBW1058Api().getConfigs().getMainConfig().getString("database.host");
            this.database = HotbarManager.getBW1058Api().getConfigs().getMainConfig().getString("database.database");
            this.user = HotbarManager.getBW1058Api().getConfigs().getMainConfig().getString("database.user");
            this.pass = HotbarManager.getBW1058Api().getConfigs().getMainConfig().getString("database.pass");
            this.port = HotbarManager.getBW1058Api().getConfigs().getMainConfig().getInt("database.port");
        } else if (HotbarManager.getSupport() == Support.BEDWARS2023) {
            this.host = HotbarManager.getBW2023Api().getConfigs().getMainConfig().getString("database.host");
            this.database = HotbarManager.getBW2023Api().getConfigs().getMainConfig().getString("database.database");
            this.user = HotbarManager.getBW2023Api().getConfigs().getMainConfig().getString("database.user");
            this.pass = HotbarManager.getBW2023Api().getConfigs().getMainConfig().getString("database.pass");
            this.port = HotbarManager.getBW2023Api().getConfigs().getMainConfig().getInt("database.port");
        } else if (HotbarManager.getSupport() == Support.BEDWARSPROXY2023) {
            this.host = Bukkit.getPluginManager().getPlugin("BWProxy2023").getConfig().getString("database.host");
            this.database = Bukkit.getPluginManager().getPlugin("BWProxy2023").getConfig().getString("database.database");
            this.user = Bukkit.getPluginManager().getPlugin("BWProxy2023").getConfig().getString("database.user");
            this.pass = Bukkit.getPluginManager().getPlugin("BWProxy2023").getConfig().getString("database.pass");
            this.port = Bukkit.getPluginManager().getPlugin("BWProxy2023").getConfig().getInt("database.port");
        } else {
            this.host = Bukkit.getPluginManager().getPlugin("BedWarsProxy").getConfig().getString("database.host");
            this.database = Bukkit.getPluginManager().getPlugin("BedWarsProxy").getConfig().getString("database.database");
            this.user = Bukkit.getPluginManager().getPlugin("BedWarsProxy").getConfig().getString("database.user");
            this.pass = Bukkit.getPluginManager().getPlugin("BedWarsProxy").getConfig().getString("database.pass");
            this.port = Bukkit.getPluginManager().getPlugin("BedWarsProxy").getConfig().getInt("database.port");
        }

        connect();
        createTables();
        Utility.info("&aMySQL loaded correctly!");
    }

    private void connect() {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        Utility.info("&eConnecting to MySQL database...");
        db = new HikariDataSource();
        db.setPoolName("HotbarManager-Pool");
        db.setConnectionTimeout(480000000L);
        db.setMaximumPoolSize(10);
        if (version.contains("v1_8") || version.contains("v1_12")) db.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        else db.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
        db.addDataSourceProperty("serverName", this.host);
        db.addDataSourceProperty("databaseName", this.database);
        db.addDataSourceProperty("port", this.port);
        db.addDataSourceProperty("user", this.user);
        db.addDataSourceProperty("password", this.pass);
        Utility.info("&aSuccessfully connected!");
    }

    @Override
    public void createPlayerData(Player player, List<Category> defaultSlots) {
        String path = player.getUniqueId().toString();
        Bukkit.getScheduler().runTaskAsynchronously(HotbarManager.getInstance(), () -> {
            try {
                Connection c = db.getConnection();

                PreparedStatement check = c.prepareStatement("SELECT player FROM bedwars_hotbar_manager WHERE player=?");
                check.setString(1, path);
                ResultSet rs = check.executeQuery();
                if (rs.next()) {
                    String str = rs.getString("player");
                    if (str != null) return;
                    c.close();
                }
                else {
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
                }
            } catch(SQLException e){
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Connection getConnection() throws SQLException {
        return db.getConnection();
    }

    @Override
    public String getData(Player player, String column) {
        String path = player.getUniqueId().toString();
        try {
            Connection c = db.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT " + column + " FROM bedwars_hotbar_manager WHERE player=?");
            ps.setString(1, path);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String str = rs.getString(column);
                c.close();
                return str;
            }
            rs.close();
            ps.close();
            c.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void setData(Player player, String column, String value) {
        String path = player.getUniqueId().toString();
        try {
            Connection c = db.getConnection();
            PreparedStatement ps = c.prepareStatement("UPDATE bedwars_hotbar_manager SET " + column + "=? WHERE player=?");
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
        try {
            Utility.info("&eCreating tables...");
            Connection c = db.getConnection();
            PreparedStatement ps = c.prepareStatement("CREATE TABLE IF NOT EXISTS bedwars_hotbar_manager(player varchar(200) PRIMARY KEY, slot0 varchar(200), slot1 varchar(200), slot2 varchar(200), slot3 varchar(200), slot4 varchar(200), slot5 varchar(200), slot6 varchar(200), slot7 varchar(200), slot8 varchar(200))");
            ps.executeUpdate();
            ps.close();
            c.close();
            Utility.info("&aTables created successfully!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

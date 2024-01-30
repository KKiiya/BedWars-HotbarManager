package me.kiiya.hotbarmanager.database.providers;

import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.database.Database;
import me.kiiya.hotbarmanager.utils.Support;
import me.kiiya.hotbarmanager.utils.Utility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.io.File;
import java.io.IOException;
import java.sql.*;

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
    public void createPlayerData(Player p) {
        Connection connection = null;
        PreparedStatement ps = null;
        String path = p.getUniqueId().toString();
        try {
            connection = getConnection();
            ps = connection.prepareStatement("SELECT * FROM bedwars_hotbar_manager WHERE player = '" + path + "'");
            ResultSet rs = ps.executeQuery();
            String player = null;
            if (rs.next()) player = rs.getString("player");
            if (player != null) {
            }
            else {
                connection = getConnection();
                ps = connection.prepareStatement("INSERT INTO bedwars_hotbar_manager(player, slot0, slot1, slot2, slot3, slot4, slot5, slot6, slot7, slot8) VALUES (?,?,?,?,?,?,?,?,?,?)");
                ps.setString(1, path);
                ps.setString(2, "MELEE");
                ps.setString(3, "NONE");
                ps.setString(4, "NONE");
                ps.setString(5, "NONE");
                ps.setString(6, "NONE");
                ps.setString(7, "NONE");
                ps.setString(8, "NONE");
                ps.setString(9, "NONE");
                ps.setString(10, "COMPASS");
                ps.executeUpdate();
            }
        } catch(SQLException e){
            throw new RuntimeException(e);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getData(Player player, String column) {
        String path = player.getUniqueId().toString();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement("SELECT * FROM bedwars_hotbar_manager WHERE player = '" + path + "';");
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getString(column);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void setData(Player player, String column, String value) {
        String path = player.getUniqueId().toString();
        try {
            Connection c = getConnection();
            try {
                PreparedStatement ps = c.prepareStatement("UPDATE bedwars_hotbar_manager SET " + column + "=? WHERE player=?");
                ps.setString(1, value);
                ps.setString(2, path);
                ps.executeUpdate();
                ps.close();
                c.close();
            } catch (Throwable throwable) {
                if (c != null)
                    try {
                        c.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                throw throwable;
            }
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

    public void close(PreparedStatement ps, ResultSet rs) {
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Connection getConnection() {
        String path;
        if (HotbarManager.getSupport() == Support.BEDWARS1058) path = Bukkit.getPluginManager().getPlugin("BedWars1058").getDataFolder() + "/Cache/";
        else path = Bukkit.getPluginManager().getPlugin("BedWars2023").getDataFolder() + "/Cache/";

        File dataFolder = new File(path, "hotbar_manager.db");
        if (!dataFolder.exists())
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
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
}

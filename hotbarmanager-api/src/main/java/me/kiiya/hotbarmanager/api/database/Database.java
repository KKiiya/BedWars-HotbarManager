package me.kiiya.hotbarmanager.api.database;

import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.SQLException;

public interface Database {
    /**
     * Get the data of a player
     * @param player - Player you want to get the data from
     * @param column - Column you are searching the data from
     * @return - String
     */
    String getData(Player player, String column);

    /**
     * Set the data of a player
     * @param player - Player you want to set the data for
     * @param column - Column you want to set the data for
     * @param value - Value you want to write
     */
    void setData(Player player, String column, String value);

    /**
     * Create the data of a player
     * This can only be done ONCE
     * <p>
     * Doing it more than once won't do anything
     * @param player - Player you want to create the data to
     */
    void createPlayerData(Player player);

    /**
     * Get the database connection
     * @return - Connection
     */
    Connection getConnection() throws SQLException;
}

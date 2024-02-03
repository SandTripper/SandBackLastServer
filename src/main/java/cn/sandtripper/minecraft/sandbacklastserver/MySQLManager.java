package cn.sandtripper.minecraft.sandbacklastserver;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


public class MySQLManager {

    private final HikariDataSource dataSource;

    private JavaPlugin plugin;

    public MySQLManager(JavaPlugin plugin, String host, int port, String username, String password, String database, String jdbcOption, int minimumIdle, int maximumPoolSize) {
        this.plugin = plugin;

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + jdbcOption);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(10); // 设置连接池最大连接数

        dataSource = new HikariDataSource(config);
        try {
            initDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initDatabase() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS player_last(\n" +
                    "    player_name VARCHAR(256) PRIMARY KEY,\n" +
                    "    server VARCHAR(256) NOT NULL\n" +
                    ")ENGINE=InnoDB DEFAULT CHARSET=utf8;";
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean savePlayerLast(String server, String playerName) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO player_last(player_name, server)\n" +
                     "VALUES (?,?)\n" +
                     "ON DUPLICATE KEY UPDATE\n" +
                     "server = VALUES(server);\n")) {
            pstmt.setString(1, server);
            pstmt.setString(2, playerName);
            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getPlayerLast(String playerName) {
        String res = null;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT server FROM player_last WHERE player_name = ?;");) {
            pstmt.setString(1, playerName);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                res = rs.getString("server");
            } else {
                res = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public void closeDataSource() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

}


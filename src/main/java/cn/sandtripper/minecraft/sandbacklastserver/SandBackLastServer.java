package cn.sandtripper.minecraft.sandbacklastserver;


import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class SandBackLastServer extends JavaPlugin {

    boolean isLobby;
    boolean isSendWhenLogin;
    UnLobbyManager unLobbyManager;
    LobbyManager lobbyManager;
    MySQLManager mySQLManager;
    ConfigReader databaseConfigReader;


    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();

        this.databaseConfigReader = new ConfigReader(this, "database.yml");
        this.databaseConfigReader.saveDefaultConfig();

        initTmpData();
        if (isLobby) {
            this.lobbyManager = new LobbyManager(this);
        } else {
            this.unLobbyManager = new UnLobbyManager(this);
        }

        getCommand("SandBackLastServer").setExecutor(new CommandHandler(this));
        getCommand("SandBackLastServer").setTabCompleter(new MyTabCompleter(this));

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        if (Bukkit.getPluginManager().getPlugin("AuthMe") != null) {
            getLogger().info("\033[32m检测到 AuthMe\033[0m");
            getServer().getPluginManager().registerEvents(new LoginListener(this), this);
        } else {
            getLogger().info("\033[33m未检测到 AuthMe，禁用插件 AuthMe 有关的功能\033[0m");
        }

        int pluginId = 20882;
        Metrics metrics = new Metrics(this, pluginId);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (this.mySQLManager != null) {
            this.mySQLManager.closeDataSource();
        }
        if (isLobby) {
            this.lobbyManager.disable();
        } else {
            this.unLobbyManager.disable();
        }
    }

    private void initTmpData() {
        FileConfiguration config = getConfig();
        FileConfiguration databaseConfig = databaseConfigReader.getConfig();

        this.isLobby = config.getBoolean("is-lobby");
        this.isSendWhenLogin = config.getBoolean("is-send-when-login");

        String host = databaseConfig.getString("mysql.host");
        int port = databaseConfig.getInt("mysql.port");
        String username = databaseConfig.getString("mysql.username");
        String password = databaseConfig.getString("mysql.password");
        String database = databaseConfig.getString("mysql.database");
        String jdbcOption = databaseConfig.getString("mysql.jdbc-option");
        int minimumIdle = databaseConfig.getInt("HikariCP.minimum-idle");
        int maximumPoolSize = databaseConfig.getInt("HikariCP.maximum-pool-size");
        this.mySQLManager = new MySQLManager(this, host, port, username, password, database, jdbcOption, minimumIdle, maximumPoolSize);
    }

    public void reload() {
        reloadConfig();
        databaseConfigReader.reloadConfig();

        this.mySQLManager.closeDataSource();

        boolean oldIsLobby = isLobby;
        initTmpData();

        if (isLobby != oldIsLobby) {
            if (isLobby) {
                this.unLobbyManager.disable();
                this.unLobbyManager = null;
                this.lobbyManager = new LobbyManager(this);
            } else {
                this.lobbyManager.disable();
                this.lobbyManager = null;
                this.unLobbyManager = new UnLobbyManager(this);
            }
        } else {
            if (isLobby) {
                this.lobbyManager.reload();
            } else {
                this.unLobbyManager.reload();
            }
        }


    }
}

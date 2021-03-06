package cf.dejf.DEJFVPNBlocker;


import org.bukkit.util.config.Configuration;
import java.io.File;
import java.util.*;

import static cf.dejf.DEJFVPNBlocker.DEJFVPNBlocker.*;

public class ConfigManager {

    public static void save() {
        Configuration config = getPluginConfig("config.cfg");
        config.setProperty("whitelistedIps", whitelistedIps);
        config.setProperty("blacklistedIps", blacklistedIps);
        config.save();
    }

    public static void saveDatabase() {
        Configuration database = getPluginConfig("ipdatabase.yml");
        Map<String, Map<String, Object>> serializedIpLog = new HashMap<>();
        for(String ip : ipLog.keySet()) {
            IPLogEntry ipLogEntry = ipLog.get(ip);
            Map<String, Object> serializedIpLogEntry = new HashMap<>();
            serializedIpLogEntry.put("vpn", ipLogEntry.isVPN());
            serializedIpLogEntry.put("checkDate", ipLogEntry.getCheckDate().getTime());
            serializedIpLog.put("" + ip.replace(".", "-"), serializedIpLogEntry);
        }
        database.setProperty("ipLog", serializedIpLog);
        database.save();
    }

    public static void load() {
        Configuration config = getPluginConfig("config.cfg");

        if(config.getProperty("whitelistedIps") == null) {
            List<String> defaultWhitelistedIps = new ArrayList<>();
            defaultWhitelistedIps.add("0.0.0.0");
            defaultWhitelistedIps.add("127.0.0.1");
            config.setProperty("whitelistedIps", defaultWhitelistedIps);
            whitelistedIps = defaultWhitelistedIps;
            config.save();
        } else {
            whitelistedIps = (List<String>) config.getProperty("whitelistedIps");
        }

        if(config.getProperty("blacklistedIps") == null) {
            blacklistedIps = new ArrayList<>();
            blacklistedIps.add("Your blacklisted IP here");
            config.setProperty("blacklistedIps", blacklistedIps);
            config.save();
        } else {
            blacklistedIps = (List<String>) config.getProperty("blacklistedIps");
        }

        if(config.getProperty("kickMessage") != null) {
            config.setProperty("vpnKickMessage", config.getProperty("kickMessage"));
            vpnKickMessage = (String) config.getProperty("kickMessage");
            config.removeProperty("kickMessage");
            config.save();
        }

        if(config.getProperty("vpnKickMessage") == null) {
            String defaultVpnKickMessage = "VPN connections aren't allowed!";
            config.setProperty("vpnKickMessage", defaultVpnKickMessage);
            vpnKickMessage = defaultVpnKickMessage;
            config.save();
        } else {
            vpnKickMessage = (String) config.getProperty("vpnKickMessage");
        }

        if(config.getProperty("blacklistKickMessage") == null) {
            String defaultBlacklistKickMessage = "Your IP has been blacklisted!";
            config.setProperty("blacklistKickMessage", defaultBlacklistKickMessage);
            blacklistKickMessage = defaultBlacklistKickMessage;
            config.save();
        } else {
            blacklistKickMessage = (String) config.getProperty("blacklistKickMessage");
        }

        if(config.getProperty("databaseClearIntervalSeconds") == null) {
            int defaultDatabaseClearIntervalSeconds = 604800;
            config.setProperty("databaseClearIntervalSeconds", defaultDatabaseClearIntervalSeconds);
            databaseClearIntervalSeconds = defaultDatabaseClearIntervalSeconds;
            config.save();
        } else {
            databaseClearIntervalSeconds = (int) config.getProperty("databaseClearIntervalSeconds");
        }

        Configuration database = getPluginConfig("ipdatabase.yml");

        if(database.getKeys("ipLog") == null) {
            ipLog = new HashMap<>();
        } else {
            ipLog = new HashMap<>();
            List<String> checkedIps = database.getKeys("ipLog");
            for (String ip : checkedIps) {
                boolean isVPN = database.getNode("ipLog").getNode(ip).getBoolean("vpn", false);
                Date checkDate = new Date((long) database.getNode("ipLog").getNode(ip).getProperty("checkDate"));
                IPLogEntry ipLogEntry = new IPLogEntry(isVPN, checkDate);
                DEJFVPNBlocker.ipLog.put(ip.replace("-", "."), ipLogEntry);
            }
        }
    }

    private static Map<String, Configuration> configDict = new HashMap<>();

    public static Configuration getPluginConfig(String config) {
        if (configDict.containsKey(config))
            return configDict.get(config);
        File file = new File(DEJFVPNBlocker.getInstance().getDataFolder(), config);
        Configuration c = new Configuration(file);
        c.load();
        configDict.put(config, c);
        return c;
    }

    /*
    public static Configuration getPlayerConfig(String playerName) {
        return getPluginConfig(playerName+".config");
    }
     */

    public static Object get(Configuration c, String object) {
        return c.getProperty(object);
    }

    public static void addDefault(Configuration c, String object, Object value) {
        if (c.getProperty(object) == null)
            addTo(c, object, value);
    }

    public static void addTo(Configuration c, String object, Object value) {
        c.setProperty(object, value);
        c.save();
    }

    public static boolean contains(Configuration c, String object) {
        Object s = c.getProperty(object);
        return s != null;
    }

    public static void remove(Configuration c, String object) {
        c.removeProperty(object);
        c.save();
    }
}

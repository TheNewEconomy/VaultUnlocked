/* This file is part of Vault.

    Vault is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Vault is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Vault.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.milkbowl.vault;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

public class Vault extends JavaPlugin {

    private static final String VAULT_BUKKIT_URL = "https://dev.bukkit.org/projects/Vault";
    private Logger log;
    private String newVersionTitle = "";
    private double newVersion = 0;
    private double currentVersion = 0;
    private String currentVersionTitle = "";
    private ScheduledExecutorService asyncTaskTimer;

    @Override
    public void onDisable() {
        // Remove all Service Registrations
        getServer().getServicesManager().unregisterAll(this);
        if (asyncTaskTimer != null) {
            asyncTaskTimer.shutdownNow();
        }
    }

    @Override
    public void onEnable() {
        log = this.getLogger();
        currentVersionTitle = getDescription().getVersion().split("-")[0];
        currentVersion = Double.parseDouble(currentVersionTitle.replaceFirst("\\.", ""));
        // set defaults
        getConfig().addDefault("update-check", true);
        getConfig().options().copyDefaults(true);
        saveConfig();
        // Load Vault Addons

        getCommand("vault-info").setExecutor(this);
        getCommand("vault-convert").setExecutor(this);
        getServer().getPluginManager().registerEvents(new VaultListener(), this);

        // Load up the Plugin metrics
        Metrics metrics = new Metrics(this, 22252);
        findCustomData(metrics);

        log.info(String.format("Enabled Version %s", getDescription().getVersion()));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (!sender.hasPermission("vault.admin")) {
            sender.sendMessage("You do not have permission to use that command!");
            return true;
        }

        if (command.getName().equalsIgnoreCase("vault-info")) {
            infoCommand(sender);
            return true;
        } else if (command.getName().equalsIgnoreCase("vault-convert")) {
            convertCommand(sender, args);
            return true;
        } else {
            // Show help
            sender.sendMessage("VaultUnlocked Commands:");
            sender.sendMessage("  /vault-info - Displays information about Vault");
            sender.sendMessage("  /vault-convert [economy1] [economy2] - Converts from one Economy to another");
            return true;
        }
    }

    private void convertCommand(CommandSender sender, String[] args) {
        final Collection<RegisteredServiceProvider<Economy>> econs = this.getServer().getServicesManager().getRegistrations(Economy.class);

        if (econs == null || econs.size() < 2) {

            sender.sendMessage("You must have at least 2 economies loaded to convert.");
            return;

        } else if (args.length != 2) {
            sender.sendMessage("You must specify only the economy to convert from and the economy to convert to. (names should not contain spaces)");
            return;
        }
        Economy econ1 = null;
        Economy econ2 = null;

        final StringBuilder economies = new StringBuilder();
        for (RegisteredServiceProvider<Economy> econ : econs) {

            final String econName = econ.getProvider().getName().replace(" ", "");
            if (econName.equalsIgnoreCase(args[0])) {

                econ1 = econ.getProvider();
            } else if (econName.equalsIgnoreCase(args[1])) {

                econ2 = econ.getProvider();
            }

            if (economies.length() > 0) {

            	economies.append(", ");
            }
            economies.append(econName);
        }

        if (econ1 == null) {

            sender.sendMessage("Could not find " + args[0] + " loaded on the server, check your spelling.");
            sender.sendMessage("Valid economies are: " + economies);
            return;
        } else if (econ2 == null) {

            sender.sendMessage("Could not find " + args[1] + " loaded on the server, check your spelling.");
            sender.sendMessage("Valid economies are: " + economies);
            return;
        }

        sender.sendMessage("This may take some time to convert, expect server lag.");
        for (OfflinePlayer op : Bukkit.getServer().getOfflinePlayers()) {
            if (econ1.hasAccount(op)) {
                if (econ2.hasAccount(op)) {
                    continue;
                }
                econ2.createPlayerAccount(op);
                double diff = econ1.getBalance(op) - econ2.getBalance(op);
                if (diff > 0) {
                	econ2.depositPlayer(op, diff);
                } else if (diff < 0) {
                	econ2.withdrawPlayer(op, -diff);
                }
                
            }
        }
        sender.sendMessage("Conversion complete, please verify the data before using it.");
    }

    private void infoCommand(CommandSender sender) {
        // Get String of Registered Economy Services
        final StringBuilder registeredEcons = new StringBuilder();
        final Collection<RegisteredServiceProvider<Economy>> econs = this.getServer().getServicesManager().getRegistrations(Economy.class);
        for (RegisteredServiceProvider<Economy> econ : econs) {

            if(registeredEcons.length() > 0) registeredEcons.append(", ");
            registeredEcons.append(econ.getProvider().getName());
        }

        //VaultUnlocked Plugins
        final StringBuilder registeredModernEcons = new StringBuilder();
        final Collection<RegisteredServiceProvider<net.milkbowl.vault2.economy.Economy>> econs2 = this.getServer().getServicesManager().getRegistrations(net.milkbowl.vault2.economy.Economy.class);
        for (RegisteredServiceProvider<net.milkbowl.vault2.economy.Economy> econ : econs2) {

            if(registeredModernEcons.length() > 0) registeredModernEcons.append(", ");
            registeredModernEcons.append(econ.getProvider().getName());
        }

        // Get String of Registered Permission Services
        final StringBuilder registeredPerms = new StringBuilder();
        final Collection<RegisteredServiceProvider<Permission>> perms = this.getServer().getServicesManager().getRegistrations(Permission.class);
        for (RegisteredServiceProvider<Permission> perm : perms) {

            if(registeredPerms.length() > 0) registeredPerms.append(", ");
            registeredPerms.append(perm.getProvider().getName());
        }

        //VaultUnlocked Plugins
        final StringBuilder registeredModernPerms = new StringBuilder();
        final Collection<RegisteredServiceProvider<net.milkbowl.vault2.permission.Permission>> perms2 = this.getServer().getServicesManager().getRegistrations(net.milkbowl.vault2.permission.Permission.class);
        for (RegisteredServiceProvider<net.milkbowl.vault2.permission.Permission> perm : perms2) {

            if(registeredModernPerms.length() > 0) registeredModernPerms.append(", ");
            registeredModernPerms.append(perm.getProvider().getName());
        }

        final StringBuilder registeredChats = new StringBuilder();
        final Collection<RegisteredServiceProvider<Chat>> chats = this.getServer().getServicesManager().getRegistrations(Chat.class);
        for (RegisteredServiceProvider<Chat> chat : chats) {

            if(registeredChats.length() > 0) registeredChats.append(", ");
            registeredChats.append(chat.getProvider().getName());
        }

        //VaultUnlocked Plugins
        final StringBuilder registeredModernChats = new StringBuilder();
        final Collection<RegisteredServiceProvider<net.milkbowl.vault2.chat.Chat>> chats2 = this.getServer().getServicesManager().getRegistrations(net.milkbowl.vault2.chat.Chat.class);
        for (RegisteredServiceProvider<net.milkbowl.vault2.chat.Chat> chat : chats2) {

            if(registeredModernChats.length() > 0) registeredModernChats.append(", ");
            registeredModernChats.append(chat.getProvider().getName());
        }

        // Get Economy & Permission primary Services
        Economy econ = null;
        final RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            econ = rsp.getProvider();
        }

        //VaultUnlocked
        net.milkbowl.vault2.economy.Economy econ2 = null;
        final RegisteredServiceProvider<net.milkbowl.vault2.economy.Economy> rsp2 = getServer().getServicesManager().getRegistration(net.milkbowl.vault2.economy.Economy.class);
        if (rsp2 != null) {
            econ2 = rsp2.getProvider();
        }

        Permission perm = null;
        final RegisteredServiceProvider<Permission> rspp = getServer().getServicesManager().getRegistration(Permission.class);
        if (rspp != null) {
            perm = rspp.getProvider();
        }

        //VaultUnlocked
        net.milkbowl.vault2.permission.Permission perm2 = null;
        final RegisteredServiceProvider<net.milkbowl.vault2.permission.Permission> rspp2 = getServer().getServicesManager().getRegistration(net.milkbowl.vault2.permission.Permission.class);
        if (rspp2 != null) {
            perm2 = rspp2.getProvider();
        }

        Chat chat = null;
        final RegisteredServiceProvider<Chat> rspc = getServer().getServicesManager().getRegistration(Chat.class);
        if (rspc != null) {
            chat = rspc.getProvider();
        }

        //VaultUnlocked
        net.milkbowl.vault2.chat.Chat chat2 = null;
        final RegisteredServiceProvider<net.milkbowl.vault2.chat.Chat> rspc2 = getServer().getServicesManager().getRegistration(net.milkbowl.vault2.chat.Chat.class);
        if (rspc2 != null) {
            chat2 = rspc2.getProvider();
        }

        // Send user some info!
        sender.sendMessage(String.format("[%s] Vault v%s Information", getDescription().getName(), getDescription().getVersion()));
        sender.sendMessage(String.format("[%s] Economy Legacy: %s%s", getDescription().getName(), (econ == null)? "None" : econ.getName(), (registeredEcons.length() == 0)? "" : " [" + registeredEcons + "]"));
        sender.sendMessage(String.format("[%s] Economy Modern: %s%s", getDescription().getName(), (econ2 == null)? "None" : econ2.getName(), (registeredModernEcons.length() == 0)? "" : " [" + registeredModernEcons + "]"));
        sender.sendMessage(String.format("[%s] Permission Legacy: %s%s", getDescription().getName(), (perm == null)? "None" : perm.getName(), (registeredPerms.length() == 0)? "" : " [" + registeredPerms + "]"));
        sender.sendMessage(String.format("[%s] Permission Modern: %s%s", getDescription().getName(), (perm2 == null)? "None" : perm2.getName(), (registeredModernPerms.length() == 0)? "" : " [" + registeredModernPerms + "]"));
        sender.sendMessage(String.format("[%s] Chat Legacy: %s%s", getDescription().getName(), (chat == null)? "None" : chat.getName(), (registeredChats.length() == 0)? "" : " [" + registeredChats + "]"));
        sender.sendMessage(String.format("[%s] Chat Modern: %s%s", getDescription().getName(), (chat2 == null)? "None" : chat2.getName(), (registeredModernChats.length() == 0)? "" : " [" + registeredChats + "]"));
    }

    /**
     * Determines if all packages in a String array are within the Classpath
     * This is the best way to determine if a specific plugin exists and will be
     * loaded. If the plugin package isn't loaded, we shouldn't bother waiting
     * for it!
     * @param packages String Array of package names to check
     * @return Success or Failure
     */
    private static boolean packagesExists(String...packages) {
        try {
            for (String pkg : packages) {
                Class.forName(pkg);
            }
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }

    public double updateCheck(double currentVersion) {
        try {
            URL url = new URL("https://api.curseforge.com/servermods/files?projectids=33184");
            URLConnection conn = url.openConnection();
            conn.setReadTimeout(5000);
            conn.addRequestProperty("User-Agent", "VaultUnlocked Update Checker");
            conn.setDoOutput(true);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final String response = reader.readLine();
            final JSONArray array = (JSONArray) JSONValue.parse(response);

            if (array.isEmpty()) {
                this.getLogger().warning("No files found, or Feed URL is bad.");
                return currentVersion;
            }
            // Pull the last version from the JSON
            newVersionTitle = ((String) ((JSONObject) array.get(array.size() - 1)).get("name")).replace("Vault", "").trim();
            return Double.parseDouble(newVersionTitle.replaceFirst("\\.", "").trim());
        } catch (Exception ignore) {
            log.info("There was an issue attempting to check for the latest version.");
        }
        return currentVersion;
    }

    private void findCustomData(Metrics metrics) {
        // Create our Economy Graph and Add our Economy plotters
        final RegisteredServiceProvider<Economy> rspEcon = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        Economy econ = null;
        if (rspEcon != null) {
            econ = rspEcon.getProvider();
        }

        final String econName = econ != null ? econ.getName() : "No Economy";
        metrics.addCustomChart(new SimplePie("economy", ()->econName));

        // Create our Permission Graph and Add our permission Plotters

        final RegisteredServiceProvider<Permission> rspPerm = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        Permission perm = null;
        if (rspPerm != null) {
            perm = rspPerm.getProvider();
        }
        final String permName = perm != null ? perm.getName() : "No Economy";
        metrics.addCustomChart(new SimplePie("permission", ()->permName));

        // Create our Chat Graph and Add our chat Plotters
        final RegisteredServiceProvider<Chat> rspChat = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        Chat chat = null;
        if (rspChat != null) {
            chat = rspChat.getProvider();
        }

        final String chatName = chat != null ? chat.getName() : "No Chat";
        metrics.addCustomChart(new SimplePie("chat", ()->chatName));
    }

    public class VaultListener implements Listener {

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlayerJoin(PlayerJoinEvent event) {

            final Player player = event.getPlayer();
            if(player.hasPermission("vault.update")) {
                try {
                    if (newVersion > currentVersion) {
                        player.sendMessage("VaultUnlocked " +  newVersionTitle + " is out! You are running " + currentVersionTitle);
                        player.sendMessage("Update VaultUnlocked at: " + VAULT_BUKKIT_URL);
                    }
                } catch (Exception ignore) {}
            }
        }

    }
}

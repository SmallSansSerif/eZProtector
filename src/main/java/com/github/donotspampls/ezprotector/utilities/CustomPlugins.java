/*
 * eZProtector - Copyright (C) 2018 DoNotSpamPls
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.donotspampls.ezprotector.utilities;

import com.github.donotspampls.ezprotector.Main;
import com.google.common.base.Joiner;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CustomPlugins {

    /**
     * Intercepts the /plugins command and swaps the output with a fake one
     *
     * @param event The command event from which other information is gathered.
     */
    public static void executeCustom(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage();

        String[] plu = new String[]{"/pl", "/plugins"};
            for (String aList : plu) {
                // The command that is being tested at the moment
                if (command.split(" ")[0].equalsIgnoreCase(aList)) {
                    if (player.hasPermission("ezprotector.bypass.command.plugins")) {
                        return;
                    }

                    event.setCancelled(true);

                    String[] plugins = Main.getPlugin().getConfig().getString("custom-plugins.plugins").split(", ");
                    String pluginsList = Joiner.on(ChatColor.WHITE + ", " + ChatColor.GREEN).join(plugins);

                    // Create a fake /plugins output message using the string array above.
                    String customPlugins = ChatColor.WHITE + "Plugins (" + plugins.length + "): " + ChatColor.GREEN + pluginsList;

                    player.sendMessage(customPlugins);
                }
        }
    }

    /**
     * Intercepts the /plugins command and blocks it for the player who executed it.
     *
     * @param event The command event from which other information is gathered.
     */
    public static void executeBlock(PlayerCommandPreprocessEvent event) {
        FileConfiguration config = Main.getPlugin().getConfig();
        Player player = event.getPlayer();

        if (!player.hasPermission("ezprotector.bypass.command.plugins")) {
            String[] plu = new String[]{"/pl", "/plugins"};
            for (String aList : plu) {

                if (event.getMessage().split(" ")[0].equalsIgnoreCase(aList)) {
                    event.setCancelled(true);
                    String errorMessage = config.getString("custom-plugins.error-message");

                    if (!errorMessage.trim().isEmpty()) player.sendMessage(MessageUtil.placeholders(errorMessage, player, errorMessage, aList));

                    if (config.getBoolean("custom-plugins.punish-player.enabled")) {
                        String punishCommand = config.getString("custom-plugins.punish-player.command");
                        // Replace placeholder with the error message in the config
                        errorMessage = config.getString("custom-version.error-message");
                        ExecutionUtil.executeConsoleCommand(MessageUtil.placeholders(punishCommand, player, errorMessage, aList));
                    }

                    if (config.getBoolean("custom-plugins.notify-admins.enabled")) {
                        String notifyMessage = MessageUtil.placeholders(config.getString("custom-plugins.notify-admins.message"), player, null, aList);
                        ExecutionUtil.notifyAdmins(notifyMessage, "ezprotector.notify.command.plugins");
                    }
                }
            }
        }
    }

}

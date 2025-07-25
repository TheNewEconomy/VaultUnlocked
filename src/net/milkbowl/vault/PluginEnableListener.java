package net.milkbowl.vault;
/*
 * IslandSurvival
 * Copyright (C) 2025 Daniel "creatorfromhell" Vidmar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import net.milkbowl.vault.papi.EconomyPlaceholders;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;

/**
 * PluginEnableListener
 *
 * @author creatorfromhell
 * @since 0.0.1.0
 */
public class PluginEnableListener implements Listener {

  @EventHandler
  public void onEvent(final PluginEnableEvent event) {

    if(event.getPlugin().getName().equalsIgnoreCase("placeholderapi")) {
      new EconomyPlaceholders().register();
    }
  }
}
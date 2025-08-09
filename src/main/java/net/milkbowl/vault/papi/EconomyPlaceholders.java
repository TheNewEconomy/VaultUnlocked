package net.milkbowl.vault.papi;

/*
 *  This file is part of VaultUnlocked.
 *
 *  Copyright (C) 2025 Daniel "creatorfromhell" Vidmar
 *
 *  VaultUnlocked is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  VaultUnlocked is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with VaultUnlocked.  If not, see <http://www.gnu.org/licenses/>.
 */

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault2.economy.AccountPermission;
import net.milkbowl.vault2.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Placeholders
 *
 * @author creatorfromhell
 * @since 2.14.0
 */
public class EconomyPlaceholders extends PlaceholderExpansion {

  @Override
  public @NotNull String getIdentifier() {

    return "vaultunlocked";
  }

  @Override
  public @NotNull String getAuthor() {

    return "creatorfromhell";
  }

  @Override
  public @NotNull String getVersion() {

    return "2.13.1";
  }

  @Override
  public boolean persist() {

    return true;
  }

  @Override
  public @Nullable String onRequest(final OfflinePlayer player, @NotNull final String params) {

    final Optional<Economy> economyOpt = Vault.instance().modernProvider();
    if(!economyOpt.isPresent()) {
      return null;
    }

    final Economy economy = economyOpt.get();
    final String[] args = params.toLowerCase().split("_");
    final UUID uuid = player != null ? player.getUniqueId() : null;
    final String pluginName = "VaultUnlocked";

    try {

      switch(args[0]) {
        case "balance":

          //%vaultunlocked_balance%
          if(uuid == null) {
            return null;
          }

          //%vaultunlocked_balance_currency_<currency>%
          if(args.length == 3 && args[1].equals("currency")) {
            final String currency = decode(args[2]);
            return economy.balance(pluginName, uuid, "world", currency).toPlainString();
          }

          //%vaultunlocked_balance_currency_<currency>_world_<world>%
          if(args.length == 5 && args[1].equals("currency") && args[3].equals("world")) {
            final String currency = decode(args[2]);
            final String world = args[4];
            return economy.balance(pluginName, uuid, world, currency).toPlainString();
          }

          //%vaultunlocked_balance_<world>%
          if(args.length == 2) {
            return economy.balance(pluginName, uuid, args[1]).toPlainString();
          }

          //%vaultunlocked_balance%
          return economy.balance(pluginName, uuid).toPlainString();

        case "balanceformatted":
          //%vaultunlocked_balanceformatted%
          if(uuid == null) {
            return null;
          }

          //%vaultunlocked_balanceformatted_currency_<currency>%
          if(args.length == 3 && args[1].equals("currency")) {
            final String currency = decode(args[2]);
            final BigDecimal amount = economy.balance(pluginName, uuid, "world", currency);
            return economy.format(pluginName, amount, currency);
          }

          //%vaultunlocked_balanceformatted_currency_<currency>_world_<world>%
          if(args.length == 5 && args[1].equals("currency") && args[3].equals("world")) {
            final String currency = decode(args[2]);
            final String world = args[4];
            final BigDecimal amount = economy.balance(pluginName, uuid, world, currency);
            return economy.format(pluginName, amount, currency);
          }

          //%vaultunlocked_balanceformatted%
          return economy.format(pluginName, economy.balance(pluginName, uuid));

        case "account":
          if(args.length < 2) {
            return null;
          }

          final UUID accountId = UUID.fromString(args[1]);

          //%vaultunlocked_account_<uuid>%
          if(args.length == 2) {

            return economy.balance(pluginName, accountId).toPlainString();
          }

          //%vaultunlocked_account_<uuid>_currency_<currency>%
          if(args.length == 4 && args[2].equals("currency")) {
            final String currency = decode(args[3]);

            return economy.balance(pluginName, accountId, "world", currency).toPlainString();
          }

          //%vaultunlocked_account_<uuid>_currency_<currency>_formatted%
          if(args.length == 5 && args[2].equals("currency") && args[4].equals("formatted")) {
            final String currency = decode(args[3]);

            final BigDecimal balance = economy.balance(pluginName, accountId, "world", currency);

            return economy.format(pluginName, balance, currency);
          }

          //%vaultunlocked_account_<uuid>_currency_<currency>_world_<world>%
          if(args.length == 6 && args[2].equals("currency") && args[4].equals("world")) {
            final String currency = decode(args[3]);
            final String world = args[5];

            return economy.balance(pluginName, accountId, world, currency).toPlainString();
          }

          //%vaultunlocked_account_<uuid>_currency_<currency>_world_<world>_formatted%
          if(args.length == 7 && args[2].equals("currency") && args[4].equals("world") && args[6].equals("formatted")) {
            final String currency = decode(args[3]);
            final String world = args[5];
            final BigDecimal balance = economy.balance(pluginName, accountId, world, currency);

            return economy.format(pluginName, balance, currency);
          }
          break;

        case "can":
          if(args.length != 3 || uuid == null) {
            return null;
          }

          if(!economy.hasSharedAccountSupport()) {

            return "no";
          }

          final String action = args[1];
          final UUID targetAccountId = UUID.fromString(args[2]);

          switch (action) {

            //%vaultunlocked_can_deposit_<uuid>%
            case "deposit":
              return booleanToYesNo(economy.hasAccountPermission(pluginName, targetAccountId, uuid, AccountPermission.DEPOSIT));

            //%vaultunlocked_can_withdraw_<uuid>%
            case "withdraw":
              return booleanToYesNo(economy.hasAccountPermission(pluginName, targetAccountId, uuid, AccountPermission.WITHDRAW));

            //%vaultunlocked_can_balance_<uuid>%
            case "balance":
              return booleanToYesNo(economy.hasAccountPermission(pluginName, targetAccountId, uuid, AccountPermission.BALANCE));

            //%vaultunlocked_can_transfer_<uuid>%
            case "transfer":
              return booleanToYesNo(economy.hasAccountPermission(pluginName, targetAccountId, uuid, AccountPermission.TRANSFER_OWNERSHIP));

            //%vaultunlocked_can_invite_<uuid>%
            case "invite":
              return booleanToYesNo(economy.hasAccountPermission(pluginName, targetAccountId, uuid, AccountPermission.INVITE_MEMBER));

            //%vaultunlocked_can_remove_<uuid>%
            case "remove":
              return booleanToYesNo(economy.hasAccountPermission(pluginName, targetAccountId, uuid, AccountPermission.REMOVE_MEMBER));

            //%vaultunlocked_can_modify_<uuid>%
            case "modify":
              return booleanToYesNo(economy.hasAccountPermission(pluginName, targetAccountId, uuid, AccountPermission.CHANGE_MEMBER_PERMISSION));

            //%vaultunlocked_can_delete_<uuid>%
            case "delete":
              return booleanToYesNo(economy.hasAccountPermission(pluginName, targetAccountId, uuid, AccountPermission.DELETE));
          }
          break;

        case "accounts":
          if(uuid == null) {
            return null;
          }

          //%vaultunlocked_accounts%
          if(args.length == 1) {
            final List<String> accounts = economy.accountsMemberOf(pluginName, uuid);
            final StringBuilder joined = new StringBuilder();
            for (int i = 0; i < accounts.size(); i++) {
              if(i > 0) joined.append(", ");
              joined.append(accounts.get(i));
            }
            return joined.toString();
          }

          //%vaultunlocked_accounts_count%
          if(args.length == 2 && args[1].equals("count")) {
            final List<String> accounts = economy.accountsMemberOf(pluginName, uuid);
            return String.valueOf(accounts.size());
          }
          break;

        //%vaultunlocked_currency%
        case "currency":
          return economy.defaultCurrencyNameSingular(pluginName);

        //%vaultunlocked_currencyplural%
        case "currencyplural":
          return economy.defaultCurrencyNamePlural(pluginName);
      }
    } catch(final Exception ignore) {
      return null;
    }

    return null;
  }

  private String booleanToYesNo(final boolean bool) {

    return (bool)? "yes" : "no";
  }

  private String decode(final String input) {
    return input.replace("%20", " ")
            .replace("%24", "$")
            .replace("%2B", "+")
            .replace("%26", "&")
            .replace("%2F", "/")
            .replace("%3D", "=");
  }
}
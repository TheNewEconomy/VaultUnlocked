# Changelog File

## 2.11.0
- Separated command permissions into two separate permissions(thanks to Mickey42302)
  - vault.admin and vault.update
- Javadocs are here! https://theneweconomy.github.io/VaultUnlockedAPI/javadoc/
- 1.21.5 support


## 2.12.0
- 1.21.6 support

## 2.13
- Added support for 1.21.7

# 2.14
- Added a few new methods for grabbing accounts that specific accounts have access to.
```java

    /**
     * Retrieves a list of account IDs owned by the specified account ID.
     *
     * @param pluginName the name of the plugin
     * @param accountID the unique identifier of the account
     * @return a list of account names owned by the specified account ID
     *
     * @since 2.14
     */
    List<String> accountsOwnedBy(@NotNull final String pluginName, @NotNull final UUID accountID)

    /**
     * Retrieves a list of account IDs that the specified account is a member of.
     *
     * @param pluginName the name of the plugin
     * @param accountID the UUID of the account to check membership for
     * @return a List of String values representing the accounts that the account is a member of
     *
     * @since 2.14
     */
    List<String> accountsMemberOf(@NotNull final String pluginName, @NotNull final UUID accountID)

    /**
     * Retrieves a list of account IDs that the specified account has the specified permissions for.
     *
     * @param pluginName the name of the plugin
     * @param accountID the UUID of the account to check access for
     * @param permissions variable number of permissions to check for
     * @return a list of accounts that the account has the specified permissions to
     *
     * @since 2.14
     */
    List<String> accountsAccessTo(@NotNull final String pluginName, @NotNull final UUID accountID, @NotNull final AccountPermission... permissions)

```

## 📘 VaultUnlocked PlaceholderAPI Placeholders (UUID-Based)

Note: These only work if your economy plugin supports the modern API of VaultUnlocked.

### 💰 Personal Balance (Player Required)

| Placeholder                                                                 | Description                                                             |
|-----------------------------------------------------------------------------|-------------------------------------------------------------------------|
| `%vaultunlocked_balance%`                                                  | Player’s balance in the default world                                  |
| `%vaultunlocked_balance_<world>%`                                          | Player’s balance in a specific world                                   |
| `%vaultunlocked_balance_currency_<currency>%`                              | Balance in a specific currency (default world)                         |
| `%vaultunlocked_balance_currency_<currency>_world_<world>%`                | Balance in a specific currency and world                               |
| `%vaultunlocked_balanceformatted%`                                         | Formatted balance (e.g., `$1,234.56`)                                  |
| `%vaultunlocked_balanceformatted_currency_<currency>%`                     | Formatted balance in a specific currency                               |
| `%vaultunlocked_balanceformatted_currency_<currency>_world_<world>%`       | Formatted balance in a specific currency and world                     |

### 🏦 Shared Account Balance (Account UUID Required)

| Placeholder                                                                                         | Description                                                             |
|-----------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------|
| `%vaultunlocked_account_<uuid>%`                                                                    | Shared account balance (default currency/world)                        |
| `%vaultunlocked_account_<uuid>_status%`                                                             | Whether the player has access to the shared account                    |
| `%vaultunlocked_account_<uuid>_currency_<currency>%`                                                | Shared account balance in a specific currency                          |
| `%vaultunlocked_account_<uuid>_currency_<currency>_world_<world>%`                                  | Shared account balance in a specific currency and world                |
| `%vaultunlocked_account_<uuid>_currency_<currency>_formatted%`                                      | Formatted balance in a specific currency                               |
| `%vaultunlocked_account_<uuid>_currency_<currency>_world_<world>_formatted%`                        | Formatted balance in a specific currency and world                     |


### ✅ Capability Checks (Player & Account UUID Required)

Please note: The response is yes or no, for true or false respectively.

| Placeholder                              | Description                                                    |
|------------------------------------------|----------------------------------------------------------------|
| `%vaultunlocked_can_deposit_<uuid>%`     | Whether the player can deposit into the account with UUID.     |
| `%vaultunlocked_can_withdraw_<uuid>%`    | Whether the player can withdraw from the account with UUID.    |
| `%vaultunlocked_can_balance_<uuid>%`     | Whether the player can view the balance of the account.        |
| `%vaultunlocked_can_transfer_<uuid>%`    | Whether the player can transfer ownership of the account.      |
| `%vaultunlocked_can_invite_<uuid>%`      | Whether the player can invite members to the account.          |
| `%vaultunlocked_can_remove_<uuid>%`      | Whether the player can remove members from the account.        |
| `%vaultunlocked_can_modify_<uuid>%`      | Whether the player can modify member permissions.              |
| `%vaultunlocked_can_delete_<uuid>%`      | Whether the player can delete the account.                     |

### 📂 Account List / Info (Player Required)

| Placeholder                         | Description                                             |
|-------------------------------------|---------------------------------------------------------|
| `%vaultunlocked_accounts%`         | List of accessible shared account UUIDs                |
| `%vaultunlocked_accounts_count%`   | Count of accessible shared accounts                    |

### 💱 Currency Metadata (No Player Required)

| Placeholder                           | Description                             |
|---------------------------------------|-----------------------------------------|
| `%vaultunlocked_currency%`            | Singular form of the default currency   |
| `%vaultunlocked_currencyplural%`      | Plural form of the default currency     |

### 🔡 Encoding Notes for Currency Names

If a currency contains spaces or symbols, encode them:

| Character | Use `%` Encoding |
|-----------|------------------|
| Space     | `%20`            |
| `$`       | `%24`            |
| `+`       | `%2B`            |
| `&`       | `%26`            |
| `/`       | `%2F`            |
| `=`       | `%3D`            |

## 2.15.1
- Fixed issue where the softdepend for PlaceholderAPI was missing causing a console message.

## 2.16.0
- Added support for MC 1.21.9

## 2.17.0
# 2.17
- Added support for 1.21.10
- Added clarity to documentation for default behavior if the provider doesn't support share accounts.
- Added clarity to documentation for default behavior if the provider does not support multi-currency.
- Added clarity to documentation for default behavior if the provider doesn't support multiple worlds.
- Deprecated ```List<String> accountsAccessTo(@NotNull final String pluginName, @NotNull final UUID accountID, @NotNull final AccountPermission... permissions)```
    - This method will be removed in a future release as the string return allows ambiguity.
- Deprecated ```List<String> accountsMemberOf(@NotNull final String pluginName, @NotNull final UUID accountID)```
    - This method will be removed in a future release as the string return allows ambiguity.
- Deprecated ```List<String> accountsOwnedBy(@NotNull final String pluginName, @NotNull final UUID accountID)```
    - This method will be removed in a future release as the string return allows ambiguity.
- Added ```List<UUID> accountsWithAccessTo(@NotNull final String pluginName, @NotNull final UUID accountID, @NotNull final AccountPermission... permissions)```
    - This method is a replacement for the now deprecated ```List<String> accountsAccessTo(@NotNull final String pluginName, @NotNull final UUID accountID, @NotNull final AccountPermission... permissions)```
- Added ```List<UUID> accountsWithMembershipTo(@NotNull final String pluginName, @NotNull final UUID accountID)```
    - This method is a replacement for the now deprecated ```List<String> accountsMemberOf(@NotNull final String pluginName, @NotNull final UUID accountID)```
- Added ```List<UUID> accountsWithOwnerOf(@NotNull final String pluginName, @NotNull final UUID accountID)```
    - This method is a replacement for the now deprecated ```List<String> accountsOwnedBy(@NotNull final String pluginName, @NotNull final UUID accountID)```
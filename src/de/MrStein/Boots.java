package de.MrStein;

import de.MrStein.Stuff.Configs;
import de.MrStein.Stuff.Itemz;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.logging.Level;

public class Boots extends JavaPlugin implements Listener {

    /*
     * Feuer Schuhe
	 * Jetpack Schuhe
	 * Regenbogen Schuhe
	 * Sonic Schuhe
	 * Eis Schuhe
	 * Musik Schuhe
	 * Enderman Schuhe
	 * Schnitzel Schuhe
	 * Love Schuhe
	 * */

    public static String version;
    private static Plugin plugin;
    private static Configs boots;
    private static Configs players;
    public static Configs config;
    private static Configs lang;
    private HashMap<ItemStack, Effect> bootslist = new HashMap<>();
    private HashMap<ItemStack, Double> bootslistspeed = new HashMap<>();
    private HashMap<ItemStack, PotionEffect> bootslistpotion = new HashMap<>();
    private HashMap<ItemStack, List<String[]>> bootslisteffects = new HashMap<>();
    private HashMap<ItemStack, String> bootspermission = new HashMap<>();
    private static HashMap<ItemStack, String> bootconname = new HashMap<>();
    private HashMap<Player, ItemStack> bootcache = new HashMap<>();
    private int[] frame = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    private String INV_TITLE = "";
    private List<Material> ignoredblocks = new ArrayList<>();
    private static Economy econ = null;
    private Random ran = new Random();
    private ItemStack item;

    public void onEnable() {
        plugin = this;
        version = getServer().getClass().getPackage().getName();
        version = version.substring(version.lastIndexOf(".") + 1);
        boots = new Configs(plugin, "boots.yml");
        boots.saveDefault();
        players = new Configs(plugin, "players.yml");
        players.saveDefault();
        config = new Configs(plugin, "config.yml");
        config.saveDefault();
        lang = new Configs(plugin, "language.yml");
        lang.saveDefault();
        getServer().getPluginManager().registerEvents(this, this);
        updateBoots();
        item = new Itemz(Material.matchMaterial(config.get().getString("items.mainmenu.item"))).setDamage((short)config.get().getInt("items.mainmenu.damage")).setDisplayName(ChatColor.translateAlternateColorCodes('&', config.get().getString("items.mainmenu.name"))).build();
        INV_TITLE = ChatColor.translateAlternateColorCodes('&', lang.get().getString("bootstitle"));
        new BukkitRunnable() {
            public void run() {
                for (Player p : getServer().getOnlinePlayers()) {
                    if (p.getInventory().getBoots() != null && bootslist.containsKey(p.getInventory().getBoots())) {
                        if (bootslistpotion.containsKey(p.getInventory().getBoots())) {
                            p.addPotionEffect(bootslistpotion.get(p.getInventory().getBoots()));
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 6);
        if (!setupEconomy()) {
            getLogger().severe("Could not find Vault Plugin");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    public void onDisable() {
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    @EventHandler
    public void move(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (p.getInventory().getBoots() != null && bootslist.containsKey(p.getInventory().getBoots())) {
            if (p.getLocation().getX() == e.getTo().getX() && p.getLocation().getY() == e.getTo().getY() && p.getLocation().getZ() == e.getTo().getZ()) return;
            p.getWorld().spigot().playEffect(p.getLocation(), bootslist.get(p.getInventory().getBoots()), 0, 0, 0, 0, 0, bootslistspeed.get(p.getInventory().getBoots()).floatValue(), 1, 40);
        }
    }

    @EventHandler
    public void sneak(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        if (!p.isSneaking() && p.getInventory().getBoots() != null && bootslist.containsKey(p.getInventory().getBoots()) && bootslisteffects.containsKey(p.getInventory().getBoots())) {
            new BukkitRunnable() {
                public void run() {
                    for (String[] values : bootslisteffects.get(p.getInventory().getBoots())) {
                        if (values[0].equals("onground")) {
                            if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR) {
                                if (values[1].equals("velocity")) {
                                    if (values[2].equals("manual")) {
                                        p.setVelocity(new Vector(Double.parseDouble(values[3]), Double.parseDouble(values[4]), Double.parseDouble(values[5])));
                                    } else {
                                        Vector v = p.getLocation().getDirection().multiply(Double.parseDouble(values[3])).setY(Double.parseDouble(values[4]));
                                        p.setVelocity(v);
                                    }
                                } else if (values[1].equals("effect")) {
                                    if (values.length > 4) {
                                        p.getWorld().spigot().playEffect(p.getLocation(), Effect.valueOf(values[2]), 0, 0, Float.parseFloat(values[4]), Float.parseFloat(values[5]), Float.parseFloat(values[6]), Float.parseFloat(values[3]), 1, 40);
                                    } else {
                                        p.getWorld().spigot().playEffect(p.getLocation(), Effect.valueOf(values[2]), 0, 0, 0, 0, 0, Float.parseFloat(values[3]), 1, 40);
                                    }
                                } else if (values[1].equals("item")) {
                                    String[] ranid = values[3].split("");
                                    if (values[3].contains(",") && values[3].split(",").length != 0) {
                                        ranid = values[3].split(",");
                                    }
                                    int l = ran.nextInt(ranid.length);
                                    Item item = p.getWorld().dropItem(p.getLocation(), new Itemz(Material.matchMaterial(values[2])).setDamage(Short.parseShort("" + ranid[l])).build());
                                    item.setPickupDelay(Integer.MAX_VALUE);
                                    new BukkitRunnable() {
                                        public void run() {
                                            item.remove();
                                        }
                                    }.runTaskLater(plugin, 10);
                                }
                            }
                        } else if (values[0].equals("sneaking")) {
                            if (values[1].equals("velocity")) {
                                if (values[2].equals("manual")) {
                                    p.setVelocity(new Vector(Double.parseDouble(values[3]), Double.parseDouble(values[4]), Double.parseDouble(values[5])));
                                } else {
                                    Vector v = p.getLocation().getDirection().multiply(Double.parseDouble(values[3])).setY(Double.parseDouble(values[4]));
                                    p.setVelocity(v);
                                }
                            } else if (values[1].equals("effect")) {
                                if (values.length > 4) {
                                    p.getWorld().spigot().playEffect(p.getLocation(), Effect.valueOf(values[2]), 0, 0, Float.parseFloat(values[4]), Float.parseFloat(values[5]), Float.parseFloat(values[6]), Float.parseFloat(values[3]), 1, 40);
                                } else {
                                    p.getWorld().spigot().playEffect(p.getLocation(), Effect.valueOf(values[2]), 0, 0, 0, 0, 0, Float.parseFloat(values[3]), 1, 40);
                                }
                            } else if (values[1].equals("item")) {
                                String[] ranid = values[3].split("");
                                if (values[3].contains(",") && values[3].split(",").length != 0) {
                                    ranid = values[3].split(",");
                                }
                                int l = ran.nextInt(ranid.length);
                                Item item = p.getWorld().dropItem(p.getLocation(), new Itemz(Material.matchMaterial(values[2])).setDamage(Short.parseShort(ranid[l])).build());
                                item.setPickupDelay(Integer.MAX_VALUE);
                                new BukkitRunnable() {
                                    public void run() {
                                        item.remove();
                                    }
                                }.runTaskLater(plugin, 10);
                            }
                        }
                        if (!p.isSneaking()) {
                            this.cancel();
                        }
                    }
                }
            }.runTaskTimer(plugin, 0, 2);
        }
    }

    @EventHandler
    public void open(PlayerInteractEvent e) {
        if((e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && e.isBlockInHand() && e.getItem() != null && e.getItem().hasItemMeta() && e.getItem().getItemMeta().hasDisplayName() && e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(item.getItemMeta().getDisplayName())) {
            openBoots(e.getPlayer());
        }
    }

    @EventHandler
    public void click(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if(e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName() && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(item.getItemMeta().getDisplayName())) {
            e.setCancelled(true);
            p.updateInventory();
        }
        if (e.getSlotType() == InventoryType.SlotType.ARMOR && bootslist.containsKey(e.getCurrentItem())) {
            if(bootslistpotion.containsKey(e.getCurrentItem())) {
                p.removePotionEffect(bootslistpotion.get(e.getCurrentItem()).getType());
            }
            e.setCancelled(true);
            p.sendMessage(format(lang.get().getString("putoff")).replace("{boots}", e.getCurrentItem().getItemMeta().getDisplayName()));
            p.getPlayer().getInventory().setBoots(new ItemStack(Material.AIR));
            p.setItemOnCursor(new ItemStack(Material.AIR));
            p.updateInventory();
        }
        try {
            if (e.getInventory().getTitle().equalsIgnoreCase(INV_TITLE)) {
                e.setCancelled(true);
                if (e.getCurrentItem().getType() != Material.AIR && e.getCurrentItem().getType() != Material.matchMaterial(config.get().getString("items.menuframe.item"))) {
                    if (e.getCurrentItem().getItemMeta().getLore().contains("§aOwned")) {
                        ItemStack stack = new ItemStack(e.getCurrentItem());
                        ItemMeta meta = stack.getItemMeta();
                        meta.setLore(null);
                        stack.setItemMeta(meta);
                        for (PotionEffect eff : p.getActivePotionEffects()) {
                            p.removePotionEffect(eff.getType());
                        }
                        p.getInventory().setBoots(stack);
                        p.sendMessage(format(lang.get().getString("puton")).replace("{boots}", e.getCurrentItem().getItemMeta().getDisplayName()));
                        p.updateInventory();
                        p.closeInventory();
                    } else {
                        p.closeInventory();
                        Inventory inv = Bukkit.createInventory(p, 27, format(lang.get().getString("buyinvtitle")).replace("{boots}", e.getCurrentItem().getItemMeta().getDisplayName()));
                        for (int i = 0; i < frame.length; i++) {
                            if (frame[i] == 1)
                                inv.setItem(i, new Itemz(Material.matchMaterial(config.get().getString("items.menuframe.item"))).setDamage((short) config.get().getInt("items.menuframe.damage")).setDisplayName("§r").build());
                        }
                        inv.setItem(16, new Itemz(Material.STAINED_CLAY).setDamage((short) 5).setDisplayName(format(lang.get().getString("buyinvyes"))).build());
                        inv.setItem(10, new Itemz(Material.STAINED_CLAY).setDamage((short) 14).setDisplayName(format(lang.get().getString("buyinvno"))).build());
                        ItemStack stack = new ItemStack(e.getCurrentItem());
                        ItemMeta meta = stack.getItemMeta();
                        meta.setLore(null);
                        stack.setItemMeta(meta);
                        inv.setItem(13, stack);
                        bootcache.put(p, stack);
                        p.openInventory(inv);
                    }
                }
            }
            if (e.getInventory().getTitle().equalsIgnoreCase(format(lang.get().getString("buyinvtitle")).replace("{boots}", bootcache.get(p).getItemMeta().getDisplayName()))) {
                e.setCancelled(true);
                if (e.getCurrentItem().getType() == Material.STAINED_CLAY && e.getCurrentItem().getItemMeta().getDisplayName().equals(format(lang.get().getString("buyinvyes")))) {
                    EconomyResponse r = econ.withdrawPlayer(p, boots.get().getInt("boots." + bootconname.get(bootcache.get(p)) + ".options.price"));
                    if (r.transactionSuccess()) {
                        List<String> list = new ArrayList<>();
                        if (players.get().getStringList("players." + p.getUniqueId() + ".bought") != null) {
                            list = players.get().getStringList("players." + p.getUniqueId() + ".bought");
                        }
                        list.add(bootconname.get(bootcache.get(p)));
                        players.get().set("players." + p.getUniqueId() + ".bought", list);
                        players.save();
                        openBoots(p);
                    } else {
                        p.playSound(p.getLocation(), Sound.ITEM_SHIELD_BREAK, 1.0f, 0.0f);
                        p.sendMessage(format(lang.get().getString("notenoughmoney")));
                        return;
                    }
                }
                if (e.getCurrentItem().getType() == Material.STAINED_CLAY && e.getCurrentItem().getItemMeta().getDisplayName().equals(format(lang.get().getString("buyinvno")))) {
                    openBoots(p);
                }
            }
        } catch (Exception ex) {}
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if(p.getInventory().getItem(config.get().getInt("items.mainmenu.slot")) == null || p.getInventory().getItem(config.get().getInt("items.mainmenu.slot")).getType() != Material.matchMaterial(config.get().getString("items.mainmenu.item"))) {
            give(p);
        }
        if(!players.get().isString("players." + p.getUniqueId() + ".playername") || players.get().getString("players." + p.getUniqueId() + ".playername") != p.getName()) {
            players.get().set("players." + p.getUniqueId() + ".playername", p.getName());
            players.save();
        }
    }

    public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
        if (c.getName().equalsIgnoreCase("boots")) {
            if (args.length == 0) {
                if (s instanceof ConsoleCommandSender) {
                    s.sendMessage("§cThis command cannot be executed by Console");
                    return false;
                }
                Player p = (Player) s;
                openBoots(p);
                return true;
            }
            if (args[0].equalsIgnoreCase("reload") && s.hasPermission("boots.reload")) {
                long time = System.currentTimeMillis();
                boots.reload();
                config.reload();
                lang.reload();
                players.save();
                updateBoots();
                s.sendMessage("§7Configs and Boots reloaded in §e" + (System.currentTimeMillis() - time) + "ms");
                return true;
            }
            Player p = (Player) s;
            if (args[0].equalsIgnoreCase("ii")) {
                p.sendMessage("its a secret...");
                return true;
            }
            if(args[0].equalsIgnoreCase("give") && p.hasPermission("boots.give")) {
                give(p);
                p.updateInventory();
            }
            return true;
        }
        return false;
    }

    private void openBoots(Player p) {
        Inventory inv = getServer().createInventory(null, 3 * 9, INV_TITLE);
        for (int i = 0; i < frame.length; i++) {
            if (frame[i] == 1)
                inv.setItem(i, new Itemz(Material.matchMaterial(config.get().getString("items.menuframe.item"))).setDamage((short) config.get().getInt("items.menuframe.damage")).setDisplayName("§r").build());
        }
        for (ItemStack stack : getBoots(p)) {
            inv.addItem(stack);
        }
        p.openInventory(inv);
    }

    private static List<ItemStack> getBoots(Player p) {
        List<ItemStack> list = new ArrayList<>();
        for (String key : boots.get().getConfigurationSection("boots").getKeys(false)) {
            Itemz item = new Itemz(Material.matchMaterial(boots.get().getString("boots." + key + ".item")));
            if (item.build().getType() == Material.LEATHER_BOOTS) {
                item.rgbColor(boots.get().getIntegerList("boots." + key + ".color").get(0), boots.get().getIntegerList("boots." + key + ".color").get(1), boots.get().getIntegerList("boots." + key + ".color").get(2));
            }
            item.setDisplayName(ChatColor.translateAlternateColorCodes('&', boots.get().getString("boots." + key + ".name")));
            /*if (players.get().getString("players." + p.getUniqueId()) == null) {
                players.get().set("players." + p.getUniqueId());
            } */
            if (players.get().getStringList("players." + p.getUniqueId() + ".bought").contains(bootconname.get(item.build())) || boots.get().getInt("boots." + bootconname.get(item.build()) + ".options.price") == 0) {
                item.addLore(format(lang.get().getString("bought")));
            } else {
                item.addLore(format(lang.get().getString("price")).replace("{price}", "" + boots.get().getInt("boots." + bootconname.get(item.build()) + ".options.price")));
            }
            if(!boots.get().isString("boots." + key + ".options.permission") || p.hasPermission(boots.get().getString("boots." + key + ".options.permission"))) {
                list.add(item.build());
            }
        }
        return list;
    }

    private void updateBoots() {
        int counter = 0;
        for (String key : boots.get().getConfigurationSection("boots").getKeys(false)) {
            if (boots.get().getString("boots." + key + ".item").toUpperCase().contains("BOOTS")) {
                try {
                    Itemz item = new Itemz(Material.matchMaterial(boots.get().getString("boots." + key + ".item")));
                    if (boots.get().isList("boots." + key + ".color")) {
                        item.rgbColor(boots.get().getIntegerList("boots." + key + ".color").get(0), boots.get().getIntegerList("boots." + key + ".color").get(1), boots.get().getIntegerList("boots." + key + ".color").get(2));
                    }
                    item.setDisplayName(ChatColor.translateAlternateColorCodes('&', boots.get().getString("boots." + key + ".name")));
                    bootslist.put(item.build(), Effect.valueOf(boots.get().getString("boots." + key + ".effect")));
                    bootslistspeed.put(item.build(), boots.get().getDouble("boots." + key + ".speed"));
                    if (boots.get().isString("boots." + key + ".options.potioneffect.type") && boots.get().isInt("boots." + key + ".options.potioneffect.amplifier")) {
                        bootslistpotion.put(item.build(), new PotionEffect(PotionEffectType.getByName(boots.get().getString("boots." + key + ".options.potioneffect.type")), Integer.MAX_VALUE, boots.get().getInt("boots." + key + ".options.potioneffect.amplifier"), false, false));
                    }
                    if (boots.get().isList("boots." + key + ".options.effects")) {
                        List<String[]> list = new ArrayList<>();
                        for (String str : boots.get().getStringList("boots." + key + ".options.effects")) {
                            list.add(str.split(":"));
                        }
                        bootslisteffects.put(item.build(), list);
                    }
                    if (boots.get().isString("boots." + key + ".options.permission")) {
                        bootspermission.put(item.build(), boots.get().getString("boots." + key + ".options.permission"));
                    }
                    bootconname.put(item.build(), key);
                    counter++;
                } catch (Exception e) {
                    getLogger().warning("Invalid Boots: " + key);
                }
            } else {
                getLogger().warning("Invalid Boots: " + key);
            }
        }
        getLogger().info("Successfully loaded " + counter + " Boots");
        try {
            for (String material : config.get().getStringList("ignoredblocks")) {
                ignoredblocks.add(Material.matchMaterial(material));
            }
        }catch(Exception e) {
            getLogger().log(Level.WARNING, "Failed to load Ignored Blocks List!", e);
        }
    }

    private void give(Player p) {
        p.getInventory().setItem(config.get().getInt("items.mainmenu.slot"), item);
    }

    public double round(double value, int places) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private static String format(String string) {
        string = ChatColor.translateAlternateColorCodes('&', string);
        string = string.replace("{nl}", "\n").replace("ae", "ä").replace("oe", "ö").replace("ue", "ü").replace("{sz}", "ẞ");
        return string;
    }

}
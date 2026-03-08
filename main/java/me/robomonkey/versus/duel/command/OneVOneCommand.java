package me.robomonkey.versus.duel.command;

import me.robomonkey.versus.Versus;
import me.robomonkey.versus.command.AbstractCommand;
import me.robomonkey.versus.duel.DuelManager;
import me.robomonkey.versus.kit.Kit;
import me.robomonkey.versus.kit.KitManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

public class OneVOneCommand extends AbstractCommand implements CommandExecutor, Listener {

    private static final List<Player> queue = new ArrayList<>();
    private static final Kit DEFAULT_KIT = KitManager.getInstance().getDefaultKit();

    public OneVOneCommand() {
        super("1v1", "versus.duel");

        if (Versus.getInstance().getCommand("1v1") != null) {
            Versus.getInstance().getCommand("1v1").setExecutor(this);
        }

        setPlayersOnly(true);
        setPermissionRequired(false);
        setArgumentRequired(false);
        setUsage("/1v1");
        setDescription("Join the 1v1 queue.");

        Versus.getInstance().getServer().getPluginManager().registerEvents(this, Versus.getInstance());
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {

        if (!(sender instanceof Player player)) return;

        if (DuelManager.getInstance().isDueling(player)) {
            player.sendMessage("§cYou are already in a duel!");
            return;
        }

        if (queue.contains(player)) {
            player.sendMessage("§cYou are already in the queue!");
            return;
        }

        queue.add(player);
        player.sendTitle("§cFinding opponent...", "§cPlease wait!", 10, 999999, 10);

        tryStartMatch();
    }

    private void tryStartMatch() {

        if (queue.size() < 2) return;

        Player p1 = queue.get(0);
        Player p2 = queue.get(1);

        new BukkitRunnable() {

            int countdown = 3;

            @Override
            public void run() {

                if (!queue.contains(p1) || !queue.contains(p2)) {
                    cancel();
                    return;
                }

                if (!p1.isOnline() || !p2.isOnline()) {
                    cancelQueue(p1);
                    cancelQueue(p2);
                    cancel();
                    return;
                }

                if (countdown > 0) {

                    String title = "§aOpponent found!";
                    String subtitle = "§aTeleporting in " + countdown + "...";

                    p1.sendTitle(title, subtitle, 5, 20, 5);
                    p2.sendTitle(title, subtitle, 5, 20, 5);

                    countdown--;
                    return;
                }

                queue.remove(p1);
                queue.remove(p2);

                DuelManager.getInstance().setupDuel(p1, p2);

                cancel();
            }

        }.runTaskTimer(Versus.getInstance(), 0L, 20L);
    }

    public static boolean cancelQueue(Player player) {

        if (queue.remove(player)) {
            player.sendMessage("§cYou left the queue. Come back anytime to duel!");
            return true;
        }

        return false;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        cancelQueue(player);

        if (DuelManager.getInstance().isDueling(player)) {
            DuelManager.getInstance().registerDuelistDeath(player, false);
        }
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        callCommand(sender, args);
        return true;
    }
}
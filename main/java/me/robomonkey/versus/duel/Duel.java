package me.robomonkey.versus.duel;

import me.robomonkey.versus.Versus;
import me.robomonkey.versus.arena.Arena;
import me.robomonkey.versus.util.EffectUtil;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Duel {

    private final List<Player> players = new ArrayList<>();
    private final Arena arena;
    private DuelState state = DuelState.IDLE;
    private UUID winnerId;
    private UUID loserId;

    private boolean isPublic = true;
    private boolean fightMusicEnabled = true;
    private boolean victoryMusicEnabled = true;
    private boolean victoryEffectsEnabled = true;
    private boolean fireworksEnabled = true;
    private Color fireworkColor = Color.ORANGE;
    private Sound fightMusic = Sound.BLOCK_NOTE_BLOCK_PLING;
    private Sound victorySong = Sound.ENTITY_PLAYER_LEVELUP;

    public Duel(Arena arena, Player... duelists) {
        Collections.addAll(players, duelists);
        this.arena = arena;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Arena getArena() {
        return arena;
    }

    public DuelState getState() {
        return state;
    }

    public void setState(DuelState state) {
        this.state = state;
    }

    public Player getWinner() {
        return winnerId == null ? null : Bukkit.getPlayer(winnerId);
    }

    public Player getLoser() {
        return loserId == null ? null : Bukkit.getPlayer(loserId);
    }

    public void end(Player winner, Player loser) {
        if (winner != null) winnerId = winner.getUniqueId();
        if (loser != null) loserId = loser.getUniqueId();
        setState(DuelState.ENDED);
    }

    /**
     * Spustí 3‑2‑1 odpočet s titulky "Duel starting in X seconds" a na konci "Duel started"
     */
    public void startCountdown(Runnable onCountdownFinish) {
        setState(DuelState.COUNTDOWN);
        players.forEach(EffectUtil::freezePlayer);

        new BukkitRunnable() {
            int countdown = 3;

            @Override
            public void run() {
                for (Player p : players) {
                    if (p == null || !p.isOnline()) continue;

                    if (countdown > 0) {
                        String subtitle = "§aDuel starting in " + countdown + " seconds...";
                        String title = "§a";
                        p.sendTitle(title, subtitle, 5, 20, 5);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                    } else {
                        String title = "§aDuel started";
                        p.sendTitle(title, "", 0, 40, 10);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                        EffectUtil.unfreezePlayer(p);
                    }
                }

                if (countdown <= 0) {
                    cancel();
                    onCountdownFinish.run();
                }

                countdown--;
            }
        }.runTaskTimer(Versus.getInstance(), 0L, 20L);
    }

    public void cancelCountdown() {
        players.forEach(EffectUtil::unfreezePlayer);
    }

    public Color getFireworkColor() {
        return fireworkColor;
    }

    public void setFireworkColor(Color fireworkColor) {
        this.fireworkColor = fireworkColor;
    }

    public void spectate(Player player) {
    }
}
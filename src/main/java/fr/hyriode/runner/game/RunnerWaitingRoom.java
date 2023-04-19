package fr.hyriode.runner.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.waitingroom.HyriWaitingRoom;
import fr.hyriode.hyrame.utils.DurationFormatter;
import fr.hyriode.hyrame.utils.Symbols;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.RunnerStatistics;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.function.Function;

/**
 * Project: Hyriode
 * Created by Akkashi
 * on 17/07/2022 at 12:15
 */
public class RunnerWaitingRoom extends HyriWaitingRoom {

    private static final Function<String, HyriLanguageMessage> LANG_DATA = name -> HyriLanguageMessage.get("waiting-room.npc.data." + name);

    public RunnerWaitingRoom(HyriGame<?> game, HyriRunner plugin) {
        super(game, Material.DIAMOND_BOOTS, plugin.getConfiguration().getWaitingRoom());
        this.clearBlocks = false;

        this.addStatistics(21, RunnerGameType.SOLO);
        this.addStatistics(23, RunnerGameType.DOUBLES);
    }

    private void addStatistics(int slot, RunnerGameType gameType) {
        final NPCCategory normal = new NPCCategory(HyriLanguageMessage.from(gameType.getDisplayName()));

        normal.addData(new NPCData(LANG_DATA.apply("kills"), account -> String.valueOf(this.getStatistics(gameType, account).getKills())));
        normal.addData(new NPCData(LANG_DATA.apply("deaths"), account -> String.valueOf(this.getStatistics(gameType, account).getDeaths())));
        normal.addData(NPCData.voidData());
        normal.addData(new NPCData(LANG_DATA.apply("successful-runs"), account -> String.valueOf(this.getStatistics(gameType, account).getSuccessfulRuns())));
        normal.addData(new NPCData(LANG_DATA.apply("victories"), account -> String.valueOf(this.getStatistics(gameType, account).getVictories())));
        normal.addData(NPCData.voidData());
        normal.addData(new NPCData(LANG_DATA.apply("games-played"), account -> String.valueOf(this.getStatistics(gameType, account).getGamesPlayed())));
        normal.addData(new NPCData(LANG_DATA.apply("played-time"), account -> this.formatPlayedTime(account, account.getStatistics().getPlayTime(HyriAPI.get().getServer().getType()))));

        this.addNPCCategory(slot, normal);
    }

    private String formatPlayedTime(IHyriPlayer account, long playedTime) {
        return playedTime < 1000 ? ChatColor.RED + Symbols.CROSS_STYLIZED_BOLD : new DurationFormatter()
                .withSeconds(false)
                .format(account.getSettings().getLanguage(), playedTime);
    }

    private RunnerStatistics.Data getStatistics(RunnerGameType gameType, IHyriPlayer account) {
        return ((RunnerGamePlayer) this.game.getPlayer(account.getUniqueId())).getStatistics().getData(gameType);
    }

}

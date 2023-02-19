package fr.hyriode.runner.game;

import fr.hyriode.api.language.HyriLanguage;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.waitingroom.HyriWaitingRoom;
import fr.hyriode.hyrame.utils.DurationFormatter;
import fr.hyriode.hyrame.utils.LocationWrapper;
import fr.hyriode.runner.api.RunnerStatistics;
import org.bukkit.Material;

import java.util.function.Function;

/**
 * Project: Hyriode
 * Created by Akkashi
 * on 17/07/2022 at 12:15
 */
public class RunnerWaitingRoom extends HyriWaitingRoom {

    private static final Function<String, HyriLanguageMessage> LANG_DATA = name -> HyriLanguageMessage.get("waiting-room.npc.data." + name);

    public RunnerWaitingRoom(HyriGame<?> game) {
        super(game, Material.DIAMOND_BOOTS, createConfig());

        this.addStatistics(30, RunnerGameType.SOLO);
        this.addStatistics(32, RunnerGameType.DOUBLES);
    }

    private void addStatistics(int slot, RunnerGameType gameType) {
        final NPCCategory normal = new NPCCategory(new HyriLanguageMessage("").addValue(HyriLanguage.EN, gameType.getDisplayName()));

        normal.addData(new NPCData(LANG_DATA.apply("kills"), account -> String.valueOf(this.getStatistics(gameType, account).getKills())));
        normal.addData(new NPCData(LANG_DATA.apply("final-kills"), account -> String.valueOf(this.getStatistics(gameType, account).getFinalKills())));
        normal.addData(new NPCData(LANG_DATA.apply("deaths"), account -> String.valueOf(this.getStatistics(gameType, account).getDeaths())));
        normal.addData(NPCData.voidData());
        normal.addData(new NPCData(LANG_DATA.apply("successful-runs"), account -> String.valueOf(this.getStatistics(gameType, account).getSuccessfulRuns())));
        normal.addData(new NPCData(LANG_DATA.apply("victories"), account -> String.valueOf(this.getStatistics(gameType, account).getVictories())));
        normal.addData(NPCData.voidData());
        normal.addData(new NPCData(LANG_DATA.apply("games-played"), account -> String.valueOf(this.getStatistics(gameType, account).getGamesPlayed())));
        normal.addData(new NPCData(LANG_DATA.apply("played-time"), account -> this.formatPlayedTime(account, this.getStatistics(gameType, account).getPlayedTime())));

        this.addNPCCategory(slot, normal);
    }

    private String formatPlayedTime(IHyriPlayer account, long playedTime) {
        return new DurationFormatter()
                .withSeconds(false)
                .format(account.getSettings().getLanguage(), playedTime);
    }

    private RunnerStatistics.Data getStatistics(RunnerGameType gameType, IHyriPlayer account) {
        return ((RunnerGamePlayer) this.game.getPlayer(account.getUniqueId())).getStatistics().getData(gameType);
    }

    private static Config createConfig() {
        return new Config(new LocationWrapper(150.5, 131, 60.5, -90, 0), new LocationWrapper(180, 222, 80), new LocationWrapper(130, 110, 40), new LocationWrapper(5.5F, 200, -2.5F, 90, 0));
    }

}
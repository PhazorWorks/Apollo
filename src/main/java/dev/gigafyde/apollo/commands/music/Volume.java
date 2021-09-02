package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.SongUtils;

public class Volume extends Command {
    public Volume() {
        this.name = "volume";
        this.triggers = new String[]{"volume", "vol"};
        this.guildOnly = true;
    }

    public void execute(CommandEvent event) {
        if (!SongUtils.passedVoiceChannelChecks(event)) return;
        if (event.getArgument().isEmpty()) {
            int vol = (int) (event.getClient().getLavalink().getLink(event.getGuild()).getPlayer().getFilters().getVolume() * 100);
            event.getTrigger().reply("\uD83D\uDD0A **Current volume is: " + vol + "%**").mentionRepliedUser(true).queue();
        } else {
            try {
                float volume = (float) (Integer.parseInt(event.getArgument()) * 0.01);
                event.getClient().getLavalink().getLink(event.getGuild()).getPlayer().getFilters().setVolume(volume).commit();
                event.getTrigger().reply("\uD83D\uDD0A **Volume set to: " + (event.getClient().getLavalink().getLink(event.getGuild()).getPlayer().getFilters().getVolume() * 100) + "%**").mentionRepliedUser(true).queue();
            } catch (NumberFormatException ignored) {
                event.getTrigger().reply("\u274C **Invalid number**").mentionRepliedUser(true).queue();
            }
        }
    }
}

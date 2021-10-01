package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Emoji;
import dev.gigafyde.apollo.utils.SongUtils;
import lavalink.client.player.LavalinkPlayer;

public class Volume extends Command {
    public Volume() {
        this.name = "volume";
        this.triggers = new String[]{"volume", "vol"};
        this.guildOnly = true;
    }

    protected void execute(CommandEvent event) {
        if (!SongUtils.passedVoiceChannelChecks(event)) return;
        LavalinkPlayer player = event.getClient().getLavalink().getLink(event.getGuild()).getPlayer();
        if (event.getArgument().isEmpty()) {
            int volume = (int) (player.getFilters().getVolume() * 100); // Get volume and convert from float to int
            event.getMessage().reply(Emoji.VOLUME + " **Current volume is: " + volume + "%**").mentionRepliedUser(true).queue();
        } else {
            try {
                float volume = (float) (Integer.parseInt(event.getArgument()) * 0.01); // Get volume as int and convert to float
                player.getFilters().setVolume(volume).commit();
                event.getMessage().reply(Emoji.VOLUME + "  **Volume set to: " + ((int) (volume * 100)) + "%**").mentionRepliedUser(true).queue();
            } catch (NumberFormatException ignored) {
                event.getMessage().reply(Emoji.ERROR + " **Invalid number**").mentionRepliedUser(true).queue();
            }
        }
    }
}

package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Emoji;
import dev.gigafyde.apollo.utils.SongUtils;
import java.util.Objects;
import lavalink.client.player.LavalinkPlayer;

public class Volume extends Command {
    private CommandEvent event;
    private LavalinkPlayer player;

    public Volume() {
        this.name = "volume";
        this.triggers = new String[]{"volume", "vol"};
        this.guildOnly = true;
    }

    protected void execute(CommandEvent event) {
        this.event = event;
        switch (event.getCommandType()) {
            case REGULAR -> {
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                player = event.getClient().getLavalink().getLink(event.getGuild()).getPlayer();
                if (event.getArgument().isEmpty()) {
                    event.getMessage().reply(Emoji.VOLUME + " **Current volume is: " + getVolume() + "%**").mentionRepliedUser(true).queue();
                    return;
                }
                setVolume(event.getArgument());
            }
            case SLASH -> {
                event.deferReply().queue();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                player = event.getClient().getLavalink().getLink(event.getGuild()).getPlayer();
                if (event.getOptions().size() == 0) {
                    event.getHook().editOriginal(Emoji.VOLUME + " **Current volume is: " + getVolume() + "%**").queue();
                    return;
                }
                setVolume(Objects.requireNonNull(event.getOption("input")).getAsString());
            }
            case CONTEXT -> {
                //This command doesn't benefit from having context menu's, so we'll leave this empty.
            }
        }
    }

    private float getVolume() {
        return player.getFilters().getVolume() * 100;
    }

    private void setVolume(String input) {
        try {
            float volume = (float) (Integer.parseInt(input) * 0.01); // Get volume as int and convert to float
            if (volume > 1) volume = 1;
            player.getFilters().setVolume(volume).commit(); //send off the volume change to lavalink
            switch (event.getCommandType()) {
                case REGULAR -> event.getMessage().reply(Emoji.VOLUME + "  **Volume set to: " + ((int) (volume * 100)) + "%**").mentionRepliedUser(true).queue();
                case SLASH -> event.getHook().editOriginal(Emoji.VOLUME + "  **Volume set to: " + ((int) (volume * 100)) + "%**").queue();
            }
        } catch (NumberFormatException ignored) {
            switch (event.getCommandType()) {
                case REGULAR -> event.getMessage().reply("Invalid number").mentionRepliedUser(true).queue();
                case SLASH -> event.getHook().editOriginal("Invalid number, please try again using numbers only.").queue();
            }
        }
    }
}

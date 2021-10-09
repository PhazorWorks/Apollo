package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Constants;
import dev.gigafyde.apollo.utils.Emoji;
import dev.gigafyde.apollo.utils.SongUtils;
import lavalink.client.player.LavalinkPlayer;

import java.util.Objects;

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
                    event.send(Emoji.VOLUME + " Current volume is: " + getVolume() + "%");
                    return;
                }
                setVolume(event.getArgument());
            }
            case SLASH -> {
                event.deferReply().queue();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                player = event.getClient().getLavalink().getLink(event.getGuild()).getPlayer();
                if (event.getOptions().size() == 0) {
                    event.send(Emoji.VOLUME + " Current volume is: " + getVolume() + "%");
                    return;
                }
                setVolume(Objects.requireNonNull(event.getOption("input")).getAsString());
            }
        }
    }

    private float getVolume() {
        return player.getFilters().getVolume() * 100;
    }

    private void setVolume(String input) {
        try {
            if (!event.getSelfMember().getVoiceState().inVoiceChannel()) {
                event.sendError(Constants.botNotInVC);
                return;
            }
            if (!SongUtils.userConnectedToBotVC(event)) return;
            float volume = (float) (Integer.parseInt(input) * 0.01); // Get volume as int and convert to float
            if (volume > 1) volume = 1;
            player.getFilters().setVolume(volume).commit(); //send off the volume change to lavalink
            event.send(Emoji.VOLUME + "  Volume set to: " + ((int) (volume * 100)) + "%.");
        } catch (NumberFormatException ignored) {
            event.sendError(Constants.invalidInt);
        }
    }
}

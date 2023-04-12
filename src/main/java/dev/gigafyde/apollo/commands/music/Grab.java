package dev.gigafyde.apollo.commands.music;

import dev.gigafyde.apollo.Main;
import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dev.gigafyde.apollo.utils.SongUtils;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.*;

public class Grab extends Command {
    private CommandEvent event;

    public Grab() {
        this.name = "grab";
        this.description = "sends the current song";
        this.triggers = new String[]{"grab", "save", "link"};
        this.guildOnly = true;
    }

    protected void execute(CommandEvent event) {
        this.event = event;
        switch (event.getCommandType()) {
            case MESSAGE -> grab();
            case SLASH -> {
                event.deferReply().queue();
                grab();
            }
        }
    }

    protected void grab() {
        TrackScheduler scheduler = event.getClient().getGuildMusicManager(Objects.requireNonNull(event.getGuild())).scheduler;
        if (scheduler == null) {
            event.sendError(Constants.requireActivePlayerCommand);
            return;
        }

        if (scheduler.getPlayer().getPlayingTrack() != null) {
            String uri = scheduler.getPlayer().getPlayingTrack().getInfo().uri;
            User author = event.getAuthor();
            if (event.getArgument().isEmpty()) {
                switch (event.getCommandType()) {
                    case MESSAGE ->
                            event.getMessage().reply("Currently playing: " + uri).queue();
                    case SLASH ->
                            event.getHook().editOriginal("Currently playing: " + uri).queue();
                }
            } else {
                try {
                    switch (event.getCommandType()) {
                        case MESSAGE -> {
                            author.openPrivateChannel().complete().sendMessage("Here is a copy of the currently playing track\n" + uri).complete();
                            event.getMessage().addReaction(Emoji.fromUnicode(String.valueOf(dev.gigafyde.apollo.utils.Emoji.SUCCESS))).queue();
                        }
                        case SLASH -> event.send("Here is a copy of the currently playing track\n" + uri);
                    }
                } catch (Exception e) {
                    event.sendError("Hi there, I tried to send the link to you privately, but it seems that failed, so I'm sending it here instead.\n" + uri + "\n" + e);
                }
            }
        }
    }
}
package dev.gigafyde.apollo.commands.music;

import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Constants;
import dev.gigafyde.apollo.utils.Emoji;
import java.util.Objects;
import net.dv8tion.jda.api.entities.User;

public class Grab extends Command {
    private CommandEvent event;

    public Grab() {
        this.name = "grab";
        this.description = "sends the currently playing song to your dm";
        this.triggers = new String[]{"grab", "save"};
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
        TrackScheduler scheduler = event.getClient().getMusicManager().getScheduler(Objects.requireNonNull(event.getGuild()));
        if (scheduler == null) {
            event.sendError(Constants.requireActivePlayerCommand);
            return;
        }
        if (scheduler.getPlayer().getPlayingTrack() != null) {
            String uri = scheduler.getPlayer().getPlayingTrack().getInfo().uri;
            User author = event.getAuthor();
            try {
                switch (event.getCommandType()) {
                    case MESSAGE -> {
                        author.openPrivateChannel().complete().sendMessage("Here is a copy of the currently playing track\n" + uri).complete();
                        event.getMessage().addReaction(Emoji.SUCCESS.toString()).queue();
                    }
                    case SLASH -> event.send("Here is a copy of the currently playing track\n" + uri);
                }
            } catch (Exception e) {
                event.sendError("Hi there, I tried to send the link to you privately, but it seems that failed, so I'm sending it here instead.\n" + uri + "\n" + e);
            }
        } else {
            event.sendError(Constants.requireActivePlayerCommand);
        }
    }
}

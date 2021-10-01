package dev.gigafyde.apollo.commands.music;

import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Emoji;
import dev.gigafyde.apollo.utils.SongUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class Grab extends Command {

    private Message message;
    private InteractionHook hook;
    private CommandEvent event;

    public Grab() {
        this.name = "grab";
        this.description = "send the currently playing song to your dm's";
        this.triggers = new String[]{"grab", "save"};
        this.guildOnly = true;
    }

    protected void execute(CommandEvent event) {
        this.event = event;
        switch (event.getCommandType()) {
            case REGULAR -> {
                message = event.getMessage();
                grab();
            }
            case SLASH -> {
                hook = event.getHook();
                event.deferReply().queue();
                grab();
            }
        }
    }

    protected void grab() {
        if (!SongUtils.passedVoiceChannelChecks(event)) return;
        TrackScheduler scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
        if (scheduler.getPlayer().getPlayingTrack() != null) {
            String uri = scheduler.getPlayer().getPlayingTrack().getInfo().uri;
            User author = event.getAuthor();
            try {
                switch (event.getCommandType()) {
                    case REGULAR -> {
                        author.openPrivateChannel().complete().sendMessage("Here is a copy of the currently playing track\n" + uri).complete();
                        message.addReaction(Emoji.SUCCESS.toString()).queue();
                    }
                    case SLASH -> {
                        send("Here is a copy of the currently playing track\\n\" + uri");
                    }
                }
            } catch (Exception e) {
                sendError("Hi there, I tried to send the link to you privately, but it seems that failed, so I'm sending it here instead.\n" + uri);
            }
        } else {
            sendError("Nothing is currently playing, so there was nothing to grab.");
        }
    }

    protected void sendError(String error) {
        switch (event.getCommandType()) {
            case REGULAR -> message.reply(error).mentionRepliedUser(true).queue();
            case SLASH -> hook.editOriginal(error).queue();
        }
    }

    protected void send(String content) {
        switch (event.getCommandType()) {
            case REGULAR -> message.reply(content).queue();
            case SLASH -> hook.editOriginal(content).queue();
        }
    }
}

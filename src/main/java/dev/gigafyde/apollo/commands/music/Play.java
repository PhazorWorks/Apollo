package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */


import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.Main;
import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.core.handlers.SongCallBack;
import dev.gigafyde.apollo.core.handlers.SongCallBackListener;
import dev.gigafyde.apollo.core.handlers.SongHandler;
import dev.gigafyde.apollo.utils.SongUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static dev.gigafyde.apollo.core.handlers.SpotifyHandler.handleSpotify;

public class Play extends Command implements SongCallBack {

    private static final Logger log = LoggerFactory.getLogger("Play");
    private TrackScheduler scheduler;
    private User author;
    private Message message;
    private InteractionHook hook;
    private CommandEvent event;

    public Play() {
        this.name = "play";
        this.description = "Allows you to play a song of your choice";
        this.triggers = new String[]{"play", "p", "Add to Queue"};
        this.hidden = false;
        this.ownerOnly = false;
        this.guildOnly = true;
    }

    protected void execute(CommandEvent event) {
        this.event = event;
        switch (event.getCommandType()) {
            case REGULAR -> {
                author = event.getAuthor();
                message = event.getMessage();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                VoiceChannel vc = Objects.requireNonNull(event.getMember().getVoiceState()).getChannel();
                assert vc != null;
                scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
                if (scheduler == null) scheduler = event.getClient().getMusicManager().addScheduler(vc, false);
                if (event.getArgument().isEmpty()) {
                    if (!event.getAttachments().isEmpty()) {
                        processArgument(event.getAttachments().get(0).getUrl());
                    } else {
                        message.reply("**Please provide a search query.**").queue();
                    }
                    return;
                }
                processArgument(event.getArgument());
                try {
                    event.getMessage().suppressEmbeds(true).queue();
                } catch (Exception ignored) {
                    // Do nothing
                }
            }
            case SLASH -> {
                author = event.getAuthor();
                event.getHook().getInteraction().deferReply(false).queue();
                hook = event.getHook();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                VoiceChannel vc = Objects.requireNonNull(event.getGuild().getMember(event.getUser()).getVoiceState()).getChannel();
                assert vc != null;
                scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
                if (scheduler == null) scheduler = event.getClient().getMusicManager().addScheduler(vc, false);
                String args = event.getOption("query").getAsString();
                processArgument(args);
            }
            case CONTEXT -> {
                event.deferReply().setEphemeral(true).queue();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                VoiceChannel vc = Objects.requireNonNull(event.getGuild().getMember(event.getUser()).getVoiceState()).getChannel();
                assert vc != null;
                scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
                if (scheduler == null) scheduler = event.getClient().getMusicManager().addScheduler(vc, false);
                String args = event.getTargetMessage().getContentRaw();
                processArgument(args);
            }
        }
    }

    private void processArgument(String arguments) {
        SongCallBackListener.addListener(this); //setup callback
        arguments = SongUtils.extractUrl(arguments);
        if (arguments.contains("spotify")) {
            handleSpotify(scheduler, arguments);
            return;
        }
        if (SongUtils.isValidURL(arguments)) {
            String[] split = arguments.split("&list="); // Prevent accidentally queueing an entire playlist
            SongHandler.loadHandler(scheduler, SongUtils.getStrippedSongUrl(split[0]), false, true);
        } else {
            SongHandler.loadHandler(scheduler, arguments, true, true);
        }
    }

    public void trackHasLoaded(AudioTrack track) {
        switch (event.getCommandType()) {
            case REGULAR -> {
                if (Main.USE_IMAGE_API) {
                    try {
                        message.reply(SongUtils.generateAndSendImage(track, author.getAsTag()), "thumbnail.png").mentionRepliedUser(false).queue();
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        message.reply("Queued " + track.getInfo().title).mentionRepliedUser(false).queue();
                    }
                } else {
                    message.reply("Queued " + track.getInfo().title).mentionRepliedUser(false).queue();
                }
            }
            case SLASH -> {
                if (Main.USE_IMAGE_API) {
                    try {
                        hook.editOriginal(SongUtils.generateAndSendImage(track, author.getAsTag()), "thumbnail.png").queue();
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        hook.editOriginal("Queued " + track.getInfo().title).queue();
                    }
                } else {
                    hook.editOriginal("Queued " + track.getInfo().title).queue();
                }
            }
            case CONTEXT -> {
                event.getHook().editOriginal("Queued " + track.getInfo().title).queue();
            }
        }
        SongCallBackListener.removeListener(this);
    }

    public void playlistLoaded(AudioPlaylist playlist, int added, int amount) {
        switch (event.getCommandType()) {
            case REGULAR -> message.reply(String.format("**Added %s of %s from the playlist!**", added, amount)).mentionRepliedUser(false).queue();
            case SLASH -> hook.editOriginal(String.format("**Added %s of %s from the playlist!**", added, amount)).queue();
            case CONTEXT -> event.getHook().editOriginal(String.format("**Added %s of %s from the playlist!**", added, amount)).queue();
        }
        SongCallBackListener.removeListener(this);
    }

    public void noMatches() {
        switch (event.getCommandType()) {
            case REGULAR -> message.reply("No matches!").queue();
            case SLASH -> hook.editOriginal("No matches!").queue();
            case CONTEXT -> event.getHook().editOriginal("No matches!").queue();
        }
    }

    public void trackLoadingFailed(Exception e) {

    }

    public void spotifyUnsupported() {
        switch (event.getCommandType()) {
            case REGULAR -> message.reply("Invalid/Unsupported Spotify URL!").queue();
            case SLASH -> hook.editOriginal("Invalid/Unsupported Spotify URL!").queue();
            case CONTEXT -> event.getHook().editOriginal("Invalid/Unsupported Spotify URL!").queue();
        }
    }

    public void spotifyFailed(Exception e) {
        switch (event.getCommandType()) {
            case REGULAR -> message.reply("Spotify Lookup failed! Aborting " + e.getMessage()).queue();
            case SLASH -> hook.editOriginal("Spotify Lookup failed! Aborting! " + e.getMessage()).queue();
            case CONTEXT -> event.getHook().editOriginal("Spotify Lookup failed! Aborting! " + e.getMessage()).queue();
        }
    }
}

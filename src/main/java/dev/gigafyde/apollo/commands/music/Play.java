package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */


import com.sedmelluq.discord.lavaplayer.track.*;
import dev.gigafyde.apollo.*;
import dev.gigafyde.apollo.core.*;
import dev.gigafyde.apollo.core.command.*;
import dev.gigafyde.apollo.core.handlers.*;
import dev.gigafyde.apollo.utils.*;
import java.util.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.*;
import net.dv8tion.jda.api.interactions.*;
import net.dv8tion.jda.api.utils.FileUpload;
import org.slf4j.*;
import static dev.gigafyde.apollo.core.handlers.SpotifyHandler.*;

public class Play extends Command implements SongCallBack {

    private static final Logger log = LoggerFactory.getLogger("Play");
    private TrackScheduler scheduler;
    private User author;
    private Message message;
    private InteractionHook hook;
    private CommandEvent event;
    private MessageChannelUnion boundChannel;

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
            case MESSAGE -> {
                author = event.getAuthor();
                message = event.getMessage();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                VoiceChannel vc = event.getGuild().getVoiceChannelById(event.getMember().getVoiceState().getChannel().getIdLong());
                event.getGuild().getAudioManager().openAudioConnection(vc);
                assert vc != null;
                scheduler = event.getClient().getMusicManager().getGuildMusicManager(Objects.requireNonNull(event.getGuild())).scheduler;
                scheduler.setBoundChannel(event.getChannel());
                boundChannel = scheduler.getBoundChannel();
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
                //event.getHook().getInteraction().deferReply(false).queue();
                hook = event.getHook();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                VoiceChannel vc = event.getGuild().getVoiceChannelById(event.getMember().getVoiceState().getChannel().getIdLong());
                event.getGuild().getAudioManager().openAudioConnection(vc);
                assert vc != null;
                scheduler = event.getClient().getMusicManager().getGuildMusicManager(Objects.requireNonNull(event.getGuild())).scheduler;
                scheduler.setBoundChannel(event.getChannel());
                boundChannel = scheduler.getBoundChannel();
                String args = Objects.requireNonNull(event.getOption("query")).getAsString();
                processArgument(args);
            }
            case USER -> {
                event.deferReply().setEphemeral(true).queue();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                VoiceChannel vc = event.getGuild().getVoiceChannelById(event.getMember().getVoiceState().getChannel().getIdLong());
                event.getGuild().getAudioManager().openAudioConnection(vc);
                assert vc != null;
                scheduler = event.getClient().getMusicManager().getGuildMusicManager(Objects.requireNonNull(event.getGuild())).scheduler;
                boundChannel = scheduler.getBoundChannel();
                String args = event.getTarget().getContentRaw();
                processArgument(args);
            }
        }
    }

    private void processArgument(String arguments) {
        SongCallBackListener.addListener(this); //setup callback
        arguments = SongUtils.extractUrl(arguments);
        // this is disabled as web server is not needed
       // if (arguments.contains("spotify")) {
       //     handleSpotify(scheduler, arguments, author);
       //     return;
       // }
        if (SongUtils.isValidURL(arguments)) {
            if (Main.PLAYLISTS_WEB_SERVER != null) {
                if (arguments.contains(Main.PLAYLISTS_WEB_SERVER)) {
                    Playlists.loadSharePlaylist(arguments, event);
                    return;
                }
            }
            String[] split = arguments.split("&list="); // Prevent accidentally queueing an entire playlist
            SongHandler.loadHandler(scheduler, SongUtils.getStrippedSongUrl(split[0]), false, true, event.getAuthor().getAsTag());
        } else {
            SongHandler.loadHandler(scheduler, arguments, true, true, event.getAuthor().getAsTag());
        }
    }

    public void trackHasLoaded(AudioTrack track) {
        track.setUserData(author.getAsTag());
        switch (event.getCommandType()) {
            case MESSAGE -> {
                if (Main.USE_IMAGE_API) {
                    try {
                        message.replyFiles(FileUpload.fromData(Objects.requireNonNull(SongUtils.generateAndSendImage(track, author.getAsTag())), "thumbnail.png")).mentionRepliedUser(false).queue();
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
                        hook.editOriginalAttachments(FileUpload.fromData(Objects.requireNonNull(SongUtils.generateAndSendImage(track, author.getAsTag())), "thumbnail.png")).queue();
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        hook.editOriginal("Queued " + track.getInfo().title).queue();
                    }
                } else {
                    hook.editOriginal("Queued " + track.getInfo().title).queue();
                }
            }
            case USER -> {
                event.getHook().editOriginal("Queued " + track.getInfo().title).queue();
                if (boundChannel != null)
                    boundChannel.sendFiles(FileUpload.fromData(Objects.requireNonNull(SongUtils.generateAndSendImage(track, event.getAuthor().getAsTag())), "thumbnail.png")).queue();
            }
        }
        SongCallBackListener.removeListener(this);
    }

    public void playlistLoaded(AudioPlaylist playlist, int added, int amount) {
        switch (event.getCommandType()) {
            case MESSAGE -> message.reply(String.format("**Added `%s` of `%s` songs from playlist `%s`**", added, amount, playlist.getName())).mentionRepliedUser(false).queue();
            case SLASH -> hook.editOriginal(String.format("**Added `%s` of `%s` songs from playlist `%s`**", added, amount, playlist.getName())).queue();
            case USER -> {
                event.getHook().editOriginal(String.format("**Added `%s` of `%s` songs from playlist `%s`**", added, amount, playlist.getName())).queue();
                if (boundChannel != null)
                    boundChannel.sendMessage(String.format("**Added `%s` of `%s` songs from playlist `%s` (Requested by %s)**", added, amount, playlist.getName(), event.getAuthor().getAsTag())).queue();
            }
        }
        SongCallBackListener.removeListener(this);
    }

    public void noMatches() {
        switch (event.getCommandType()) {
            case MESSAGE -> message.reply("No matches!").queue();
            case SLASH -> hook.editOriginal("No matches!").queue();
            case USER -> event.getHook().editOriginal("No matches!").queue();
        }
        SongCallBackListener.removeListener(this);
    }

    public void trackLoadingFailed(Exception e) {
        switch (event.getCommandType()) {
            case MESSAGE -> message.reply("Loading failed: " + e.getMessage()).queue();
            case SLASH -> hook.editOriginal("Loading failed: " + e.getMessage()).queue();
            case USER -> event.getHook().editOriginal("Loading failed: " + e.getMessage()).queue();
        }
        SongCallBackListener.removeListener(this);
    }

    public void spotifyUnsupported() {
        switch (event.getCommandType()) {
            case MESSAGE -> message.reply("Invalid/Unsupported Spotify URL!").queue();
            case SLASH -> hook.editOriginal("Invalid/Unsupported Spotify URL!").queue();
            case USER -> event.getHook().editOriginal("Invalid/Unsupported Spotify URL!").queue();
        }
        SongCallBackListener.removeListener(this);
    }

    public void spotifyFailed(Exception e) {
        switch (event.getCommandType()) {
            case MESSAGE -> message.reply("Spotify Lookup failed! Aborting " + e.getMessage()).queue();
            case SLASH -> hook.editOriginal("Spotify Lookup failed! Aborting! " + e.getMessage()).queue();
            case USER -> event.getHook().editOriginal("Spotify Lookup failed! Aborting! " + e.getMessage()).queue();
        }
        SongCallBackListener.removeListener(this);
    }
}

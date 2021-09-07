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
import dev.gigafyde.apollo.core.command.SlashEvent;
import dev.gigafyde.apollo.core.handlers.SongCallBack;
import dev.gigafyde.apollo.core.handlers.SongCallBackListener;
import dev.gigafyde.apollo.core.handlers.SongHandler;
import dev.gigafyde.apollo.utils.SongUtils;
import java.util.Objects;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static dev.gigafyde.apollo.core.handlers.SpotifyHandler.handleSpotify;

public class Play extends Command implements SongCallBack {

    public Play() {
        this.name = "play";
        this.description = "Allows you to play a song of your choice";
        this.triggers = new String[]{"play", "p"};
        this.hidden = false;
        this.ownerOnly = false;
        this.guildOnly = true;
    }

    private TrackScheduler scheduler;
    private User author;
    private Message message;
    private InteractionHook hook;

    private static final Logger log = LoggerFactory.getLogger("Play");

    protected void execute(CommandEvent event) {
        author = event.getAuthor();
        message = event.getMessage();
        if (!SongUtils.passedVoiceChannelChecks(event)) return;
        VoiceChannel vc = Objects.requireNonNull(event.getMember().getVoiceState()).getChannel();
        assert vc != null;
        scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
        if (event.getArgument().isEmpty()) {
            event.getMessage().reply("**Please provide a search query.**").mentionRepliedUser(true).queue();
            return;
        }
        processArgument(event.getArgument());
    }

    protected void executeSlash(SlashEvent event) {
        author = event.getAuthor();
        event.getSlashCommandEvent().deferReply(false).queue();
        hook = event.getSlashCommandEvent().getHook();
        VoiceChannel vc = Objects.requireNonNull(event.getGuild().getMember(event.getUser()).getVoiceState()).getChannel();
        assert vc != null;
        scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
        String args = event.getSlashCommandEvent().getOption("args").getAsString();
        processArgument(args);
    }

    private void processArgument(String arguments) {
        handleCallback();
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

    private void handleCallback() {
        SongCallBackListener.addListener(this);
    }

    public void trackHasLoaded(AudioTrack track) {
        if (hook != null) {
            if (Main.USE_IMAGE_GEN) {
                try {
                    hook.editOriginal("").addFile(SongUtils.generateAndSendImage(track, author.getAsTag()), "thumbnail.png").queue();
                } catch (Exception e) {
                    log.error(e.getMessage());
                    hook.editOriginal("Queued " + track.getInfo().title).queue();
                }
            } else {
                hook.editOriginal("Queued " + track.getInfo().title).queue();
            }
        } else {
            if (Main.USE_IMAGE_GEN) {
                try {
                    message.reply(SongUtils.generateAndSendImage(track, author.getAsTag()), "thumbnail.png").mentionRepliedUser(true).queue();
                } catch (Exception e) {
                    log.error(e.getMessage());
                    message.reply("Queued " + track.getInfo().title).queue();
                }
            } else {
                message.reply("Queued " + track.getInfo().title).mentionRepliedUser(true).queue();
            }
        }
    }

    public void playlistLoaded(AudioPlaylist playlist, int added, int amount) {
        if (hook != null) {
            hook.editOriginal(String.format("**Added %s of %s from the playlist!**", added, amount)).queue();
        } else {
            message.reply(String.format("**Added %s of %s from the playlist!**", added, amount)).queue();
        }
    }

    public void noMatches() {
        if (hook != null) {
            hook.editOriginal("No matches!").queue();
        } else {
            message.reply("No matches!").mentionRepliedUser(true).queue();
        }
    }

    public void trackLoadingFailed(Exception e) {

    }

    public void spotifyUnsupported() {
        if (hook != null) {
            hook.editOriginal("Invalid/Unsupported Spotify URL!").queue();
        } else {
            message.reply("Invalid/Unsupported Spotify URL!").mentionRepliedUser(true).queue();
        }
    }

    public void spotifyFailed(Exception e) {
        if (hook != null) {
            hook.editOriginal("Spotify Lookup failed! Aborting! " + e.getMessage()).queue();
        } else {
            message.reply("Spotify Lookup failed! Aborting " + e.getMessage()).mentionRepliedUser(true).queue();
        }
    }
}

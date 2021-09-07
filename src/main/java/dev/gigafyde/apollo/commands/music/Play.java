package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */


import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.core.command.SlashEvent;
import dev.gigafyde.apollo.core.handlers.SongCallBackListener;
import dev.gigafyde.apollo.core.handlers.SongHandler;
import dev.gigafyde.apollo.utils.SongUtils;
import java.util.Objects;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static dev.gigafyde.apollo.core.handlers.SpotifyHandler.handleSpotify;

public class Play extends Command implements SongCallBackListener {

    public Play() {
        this.name = "play";
        this.description = "Allows you to play a song of your choice";
        this.triggers = new String[]{"play", "p"};
        this.hidden = false;
        this.ownerOnly = false;
        this.guildOnly = true;
    }

    private TrackScheduler scheduler;
    private MessageChannel channel;
    InteractionHook hook;

    private static final Logger log = LoggerFactory.getLogger("Play");

    protected void execute(CommandEvent event) {
        channel = event.getChannel();
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
        channel = event.getChannel();
        event.getSlashCommandEvent().deferReply(false).queue();
        hook = event.getSlashCommandEvent().getHook();
        VoiceChannel vc = Objects.requireNonNull(event.getGuild().getMember(event.getUser()).getVoiceState()).getChannel();
        assert vc != null;
        scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
        String args = event.getSlashCommandEvent().getOptionsByName("args").toString();
        processArgument(args);
    }

    private void handleCallback(){
        SongHandler songHandler = new SongHandler();
        songHandler.addListener(this);
    }

    private void processArgument(String arguments) {
        handleCallback();
        if (arguments.contains("spotify")) {
            handleSpotify(scheduler, channel, arguments);
        }
        if (SongUtils.isValidURL(arguments)) {
            String[] split = arguments.split("&list="); // Prevent accidentally queueing an entire playlist
            SongHandler.loadHandler(scheduler, channel, SongUtils.getStrippedSongUrl(split[0]), false, true);
        } else {
            SongHandler.loadHandler(scheduler, channel, arguments, true, true);

        }
    }

    @Override
    public void trackHasLoaded(AudioTrack track) {
        channel.sendMessage("Queued " + track.getInfo().title).queue();
        log.warn("Callback called!");
    }
}

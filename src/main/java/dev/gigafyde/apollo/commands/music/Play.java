package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */


import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.core.command.SlashEvent;
import dev.gigafyde.apollo.core.handlers.SongHandler;
import dev.gigafyde.apollo.utils.SongUtils;
import java.util.Objects;
import javax.sound.midi.Track;
import net.dv8tion.jda.api.entities.VoiceChannel;
import static dev.gigafyde.apollo.core.handlers.SpotifyHandler.handleSpotify;

public class Play extends Command {


    public Play() {
        this.name = "play";
        this.description = "Allows you to play a song of your choice";
        this.triggers = new String[]{"play", "p"};
        this.hidden = false;
        this.ownerOnly = false;
        this.guildOnly = true;
    }

    private TrackScheduler scheduler;

    protected void execute(CommandEvent event) {
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
        VoiceChannel vc = Objects.requireNonNull(event.getGuild().getMember(event.getAuthor()).getVoiceState()).getChannel();
        assert vc != null;
        scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
//        TrackScheduler scheduler = event.getClient().getMusicManager().addScheduler(vc, false);
        String args = event.getSlashCommandEvent().getOptionsByName("args").toString();
        processArgument(args);
    }

    private void processArgument(String arguments) {
        if (arguments.contains("spotify")) {
            handleSpotify(scheduler, arguments);
        }
        if (SongUtils.isValidURL(arguments)) {
            String[] split = arguments.split("&list="); // Prevent accidentally queueing an entire playlist
            SongHandler.loadHandler(scheduler, SongUtils.getStrippedSongUrl(split[0]), false, true);
        } else {
            SongHandler.loadHandler(scheduler, arguments, true, true);

        }
    }

}

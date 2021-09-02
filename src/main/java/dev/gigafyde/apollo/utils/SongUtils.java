package dev.gigafyde.apollo.utils;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.core.MusicManager;
import dev.gigafyde.apollo.core.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.EnumSet;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SongUtils {
    public static boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
        return true;
    }

    public static boolean passedVoiceChannelChecks(CommandEvent event) {
        MusicManager musicManager = event.getClient().getMusicManager();
        VoiceChannel vc = Objects.requireNonNull(event.getMember().getVoiceState()).getChannel();
        if (vc == null) {
            event.getMessage().reply("**Please join a voice channel first!**").mentionRepliedUser(true).queue();
            return false;
        }
        EnumSet<Permission> voicePermissions = event.getSelfMember().getPermissions(vc);
        if (!voicePermissions.contains(Permission.VIEW_CHANNEL)) {
            event.getMessage().reply("**I am unable to see this voice channel!**").mentionRepliedUser(true).queue();
            return false;
        }
        if (!voicePermissions.contains(Permission.VOICE_CONNECT)) {
            event.getMessage().reply("**I am unable to connect to this voice channel**").mentionRepliedUser(true).queue();
            return false;
        }
        if (!voicePermissions.contains(Permission.VOICE_SPEAK)) {
            event.getMessage().reply("**I am unable to speak in this voice channel!**").mentionRepliedUser(true).queue();
            return false;
        }
        return true;
    }

    public static String calculateSongLength(AudioTrack track) {
        if (track.getInfo().isStream) return "Livestream";
        long minutes = TimeUnit.MILLISECONDS.toMinutes(track.getDuration());
        long seconds = TimeUnit.MILLISECONDS.toSeconds(track.getDuration()) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public static String getSongProgress(AudioTrack track) {
        long dms = track.getDuration();
        long pms = track.getPosition();
        long pmin = TimeUnit.MILLISECONDS.toMinutes(pms);
        long psec = TimeUnit.MILLISECONDS.toSeconds(pms) % 60;
        String duration = calculateSongLength(track);
        return String.format("%d:%02d/%s", pmin, psec, duration);
    }
}

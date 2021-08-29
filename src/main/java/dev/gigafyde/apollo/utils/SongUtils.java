package dev.gigafyde.apollo.utils;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
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

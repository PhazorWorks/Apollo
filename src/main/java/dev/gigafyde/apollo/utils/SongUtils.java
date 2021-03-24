package dev.gigafyde.apollo.utils;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.concurrent.TimeUnit;

public class SongUtils {
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

    static String generateProgressBar(AudioTrack track) {
        int size = 15;
        long position = track.getPosition();
        long duration = track.getDuration();
        double percent = (size + 0.0) * position / duration;
        percent = Math.floor(percent);
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < size - 1; i++) {
            if (percent > i * (size / 15)) s.append("[O]");
            else s.append("[_]");
        }
        return s.toString();
    }
}

package dev.gigafyde.apollo.core;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import dev.gigafyde.apollo.Main;
import dev.gigafyde.apollo.utils.SongUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.utils.FileUpload;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private BlockingQueue<AudioTrack> queue;

    private MessageChannelUnion boundChannel;
    private Message nowPlaying;
    private AudioTrack previousTrack;
    private boolean repeat = false;
    private boolean sendPlaying = true;
    private AudioPlayerManager audioPlayerManager;

    /**
     * @param player The audio player this scheduler uses
     */
    public TrackScheduler(AudioPlayer player, AudioPlayerManager manager) {
        this.player = player;
        this.player.setVolume(Main.DEFAULT_VOLUME);
        this.audioPlayerManager = manager;
        this.queue = new LinkedBlockingQueue<>();
    }

    public AudioPlayerManager getManager() {
        return audioPlayerManager;
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    /**
     * Add the next track to queue or play right away if nothing is in the
     * queue.
     *
     * @param track The track to play or add to queue.
     */
    public void queue(AudioTrack track) {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    public BlockingQueue<AudioTrack> getQueue() {
        return queue;
    }

    public void clear() {
        player.stopTrack();
        queue.clear();
    }

    public void skip() {
        skip(1);
    }

    public void skip(int amount) {
        if (queue.isEmpty()) {
            player.stopTrack();
        }
        if (amount == 1) {
            nextTrack();
            return;
        }
        if (queue.size() > amount) {
            for (int i = 0; i < amount; i++) {
                queue.remove();
            }
        } else {
            queue.clear();
            player.stopTrack();
        }
    }


    public void shuffleQueue() {
        final List<AudioTrack> tempList = new ArrayList<>(this.queue);
        Collections.shuffle(tempList);
        this.queue.clear();
        this.queue.addAll(tempList);

    }

    public boolean isRepeating() {
        return repeat;
    }

    public void setRepeat(boolean value) {
        repeat = value;
    }

    public boolean isSending() {
        return sendPlaying;
    }

    public void setSendPlaying(boolean active) {
        sendPlaying = active;
    }


    public boolean addTrack(AudioTrack track, String author) {
        track.setUserData(author);
        if (queue.contains(track))
            return false;
        queue.add(track);
        if (player.getPlayingTrack() == null)
            nextTrack();
        return true;
    }

    public boolean addTopSong(AudioTrack track) {
        List<AudioTrack> tracks = new ArrayList<>(queue);
        tracks.add(0, track);
        queue = new LinkedBlockingDeque<>(tracks);
        return true;
    }

    public int addTracks(String author, List<AudioTrack> tracks) {
        int added = 0;
        for (AudioTrack track : tracks) {
            if (addTrack(track, author)) added++;
        }
        return added;
    }

    public void removeTrack(int position) {
        List<AudioTrack> tracks = new ArrayList<>(queue);
        tracks.remove(position);
        queue = new LinkedBlockingDeque<>(tracks);
    }

    public String getTrackTitleByPosition(int position) {
        List<AudioTrack> tracks = new ArrayList<>(queue);
        return tracks.get(position).getInfo().title;
    }

    public void moveTrack(int oldPosition, int newPosition) {
        List<AudioTrack> tracks = new ArrayList<>(this.queue);
        AudioTrack oldPos = tracks.remove(oldPosition);
        tracks.add(newPosition, oldPos);
        queue = new LinkedBlockingDeque<>(tracks);
    }

    public void nextTrack() {
        AudioTrack track = queue.poll();
        if (track == null)
            return;
        player.playTrack(track);
        if (!sendPlaying) return;
        if (boundChannel != null) {
            try {
                // Try to delete the previous now-playing message
                nowPlaying.delete().complete();
            } catch (Exception ignored) {
                // If it fails. it'll most likely be because of something on discord's end. so it's not our problem.
            }
            if (player.getPlayingTrack() == track)
                try {
                    if (Main.USE_IMAGE_API) {
                        boundChannel.sendFiles(FileUpload.fromData(Objects.requireNonNull(SongUtils.generateNowPlaying(track, 1)), "playing.png"))
                                .queue(msg -> nowPlaying = msg);
                    } else {
                        boundChannel.sendMessage(track.getInfo().author + " - " + track.getInfo().title + " - " + SongUtils.getSongProgress(player)).queue(msg -> nowPlaying = msg);
                    }
                } catch (Exception e) {
                    boundChannel.sendMessage("**Something went wrong trying to generate the image. " + e + "**").queue();
                    boundChannel.sendMessage(track.getInfo().author + " - " + track.getInfo().title + " - " + SongUtils.getSongProgress(player)).queue(msg -> nowPlaying = msg);
                }
        }
    }

    public AudioTrack getPreviousTrack() {
        return previousTrack;
    }

    public MessageChannelUnion getBoundChannel() {
        return boundChannel;
    }

    public void setBoundChannel(MessageChannelUnion channel) {
        boundChannel = channel;
    }


    // player events
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        previousTrack = track;
        if (endReason.mayStartNext) {
            if (repeat) {
                player.startTrack(track.makeClone(), false);
            } else {
                nextTrack();
            }
        }
    }
}


package dev.gigafyde.apollo.core;

import dev.gigafyde.apollo.Main;
import java.net.URI;
import lavalink.client.io.jda.JdaLavalink;

public class LavalinkManager {
    private final JdaLavalink lavalink;

    public LavalinkManager() {
        lavalink = new JdaLavalink(
                Main.BOT_ID,
                Main.SHARDS_TOTAL,
                shardId -> Main.SHARD_MANAGER.getShardById(shardId)
        );
        lavalink.setAutoReconnect(true);

        lavalink.addNode(URI.create("ws://" + System.getenv("LAVALINK_URL")), System.getenv("LAVALINK_PASS"));
    }

    public JdaLavalink getLavalink() {
        return lavalink;
    }

}

package dev.gigafyde.apollo;

import dev.gigafyde.apollo.commands.CommandList;
import dev.gigafyde.apollo.core.Client;
import dev.gigafyde.apollo.core.LavalinkManager;
import io.sentry.Sentry;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

public class Main {
    // Load configuration values from system environment variables
    public static String BOT_ID = System.getenv("BOT_ID");
    public static String BOT_PREFIX = System.getenv("BOT_PREFIX");
    public static String OWNER_ID = System.getenv("BOT_OWNER_ID");
    public static String BOT_TOKEN = System.getenv("BOT_TOKEN");
    public static String LAVALINK_URL = System.getenv("LAVALINK_URL");
    public static String LAVALINK_PASS = System.getenv("LAVALINK_PASS");

    public static ShardManager SHARD_MANAGER;
    public static LavalinkManager LAVALINK;
    public static int SHARDS_TOTAL = Integer.parseInt(System.getenv("SHARDS_TOTAL"));

    public static void main(String[] args) throws LoginException {
        Sentry.init(System.getenv("SENTRY_DSN"));
        LAVALINK = new LavalinkManager();
        Client client = new Client(LAVALINK.getLavalink());
        new CommandList(client);
        SHARD_MANAGER = DefaultShardManagerBuilder.createLight(BOT_TOKEN)
                .enableIntents(GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES)
                .setVoiceDispatchInterceptor(LAVALINK.getLavalink().getVoiceInterceptor())
                .setShardsTotal(SHARDS_TOTAL)
                .setShards(SHARDS_TOTAL - 1)
                .addEventListeners(LAVALINK.getLavalink(), client)
                .build();
    }
}

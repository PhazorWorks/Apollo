package dev.gigafyde.apollo;

import dev.gigafyde.apollo.commands.CommandList;
import dev.gigafyde.apollo.core.Client;
import dev.gigafyde.apollo.core.LavalinkManager;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Main {
    public static ShardManager SHARD_MANAGER;
    public static LavalinkManager LAVALINK;
    public static int SHARDS_TOTAL = Integer.parseInt(System.getenv("SHARDS_TOTAL"));
    public static String BOT_ID = System.getenv("BOT_ID");

    public static void main(String[] args) throws LoginException {
        System.setProperty("java.awt.headless", "true");
        LAVALINK = new LavalinkManager();
        Client client = new Client(LAVALINK.getLavalink());
        new CommandList(client);
        SHARD_MANAGER = DefaultShardManagerBuilder.createDefault(System.getenv("DISCORD_BOT_TOKEN"))
                .disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.EMOTE, CacheFlag.VOICE_STATE, CacheFlag.ROLE_TAGS, CacheFlag.MEMBER_OVERRIDES)
                .enableIntents(GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES)
                .setVoiceDispatchInterceptor(LAVALINK.getLavalink().getVoiceInterceptor())
                .setShardsTotal(SHARDS_TOTAL)
                .setShards(SHARDS_TOTAL - 1)
                .addEventListeners(LAVALINK.getLavalink(), client)
                .build();
    }
}

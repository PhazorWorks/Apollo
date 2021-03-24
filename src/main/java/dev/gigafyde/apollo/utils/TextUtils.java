package dev.gigafyde.apollo.utils;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;

public class TextUtils {
    public static String getTag(Member member) {
        return member.getUser().getName() + "#" + member.getUser().getDiscriminator();
    }

    public static String getTag(User user) {
        return user.getName() + "#" + user.getDiscriminator();
    }

    public static String getTag(SelfUser selfUser) {
        return selfUser.getName() + "#" + selfUser.getDiscriminator();
    }

    public static String getStrippedSongUrl(String url) {
        return url.replace("<", "").replace(">", "");
    }
}

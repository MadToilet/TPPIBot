package tterrag.tppibot.commands;

import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.interfaces.ICommand;
import tterrag.tppibot.registry.CommandRegistry;
import tterrag.tppibot.registry.PermRegistry;
import tterrag.tppibot.util.IRCUtils;

public class Commands extends Command
{
    public Commands()
    {
        super("commands", PermLevel.DEFAULT);
    }
    
    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args)
    {
        String s = "";
        
        PermLevel perms = channel == null ? PermLevel.OP : PermRegistry.instance().getPermLevelForUser(channel, user);
        for (ICommand c : CommandRegistry.getCommands())
        {
            if (IRCUtils.isPermLevelAboveOrEqualTo(perms, c.getPermLevel()))
            {
                s += c.getIdent() + ", ";
            }
        }
        s = s.substring(0, s.length() - 2);
        lines.add("Commands: " + s);
    }
    
    @Override
    public String getDesc()
    {
        return "Shows all possible commands for you, perm level sensitive.";
    }
}

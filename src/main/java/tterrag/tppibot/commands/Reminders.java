package tterrag.tppibot.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.DisconnectEvent;

import tterrag.tppibot.Main;
import tterrag.tppibot.annotations.Subscribe;
import tterrag.tppibot.config.Config;
import tterrag.tppibot.util.IRCUtils;

import com.google.gson.reflect.TypeToken;

public class Reminders extends Command
{
    private Config delayConfig;
    public Map<String, Long> delayMap;
    
    public Reminders()
    {
        super("reminders", PermLevel.TRUSTED);
        
        delayConfig = new Config("delays.json");
        
        delayMap = Config.gson.fromJson(delayConfig.getText(), new TypeToken<Map<String, Long>>() {}.getType());
        
        if (delayMap == null)
        {
            delayMap = new HashMap<String, Long>();
        }
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args)
    {
        if (args.length < 1)
        {
            lines.add("This command requires one arg.");
            return;
        }
        
        if ("on".equals(args[0]))
        {
            if (!Main.reminders.isRemindEnabledFor(channel.getName()))
            {
                lines.add("Enabling reminders for channel \"" + channel.getName() + "\"");
                Main.reminders.enableRemindersFor(channel.getName());
            }
            else
            {
                lines.add("Reminders already enabled!");
            }
        }
        else if ("off".equals(args[0]))
        {
            if (Main.reminders.isRemindEnabledFor(channel.getName()))
            {
                lines.add("Disabling reminders for channel \"" + channel.getName() + "\"");
                Main.reminders.disableRemindersFor(channel.getName());
            }
            else
            {
                lines.add("Reminders already disabled!");
            }
        }
        else if ("delay".equals(args[0]))
        {
            if (args.length < 2)
            {
                lines.add("'delay' command requires a [time] arg");
            }
            else
            {
                int seconds = IRCUtils.getSecondsFromString(args[1]);
                delayMap.put(channel.getName(), (long) (seconds * 1000));
                Main.reminders.setDelay(channel.getName(), seconds * 1000);
            }
        }
    }

    @Override
    public String getDesc()
    {
        return "Turns reminders off for the current channel";
    }

    @Override
    public boolean executeWithoutChannel()
    {
        return false;
    }
    
    @Override
    public boolean shouldReceiveEvents()
    {
        return true;
    }
    
    @Subscribe
    public void onDisconnect(DisconnectEvent<PircBotX> event)
    {
        delayConfig.writeJsonToFile(delayMap);
    }
}
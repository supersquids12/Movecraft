package net.countercraft.movecraft.commands;

import net.countercraft.movecraft.api.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.localisation.I18nSupport;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CruiseCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!command.getName().equalsIgnoreCase("cruise")){
            return false;
        }
        if(!(commandSender instanceof Player)){
            commandSender.sendMessage("you need to be a player to cruise");
            return true;
        }
        Player player = (Player) commandSender;

        if(args.length<1){
            commandSender.sendMessage("you need to supply an argument"); //TODO: toggle cruising
            return true;
        }
        if (args[0].equalsIgnoreCase("off")) { //This goes before because players can sometimes freeze while cruising
            final Craft craft = CraftManager.getInstance().getCraftByPlayerName(player.getName());
            if (craft == null) {
                player.sendMessage(I18nSupport.getInternationalisedString("You must be piloting a craft"));
                return true;
            }
            craft.setCruising(false);
            return true;
        }
        if (!player.hasPermission("movecraft.commands") || !player.hasPermission("movecraft.commands.cruise")) {
            player.sendMessage(I18nSupport.getInternationalisedString("Insufficient Permissions"));
            return true;
        }

        final Craft craft = CraftManager.getInstance().getCraftByPlayerName(player.getName());
        if (craft == null) {
            player.sendMessage(I18nSupport.getInternationalisedString("You must be piloting a craft"));
            return true;
        }

        if (!player.hasPermission("movecraft." + craft.getType().getCraftName() + ".move")) {
            player.sendMessage(I18nSupport.getInternationalisedString("Insufficient Permissions"));
            return true;
        }
        if (craft.getType().getCanCruise()) {
            player.sendMessage("this craft cannot cruise");
            return true;
        }


        if (args[0].equalsIgnoreCase("on")) {
            float yaw = player.getLocation().getYaw();
            if (yaw >= 135 || yaw < -135) // north
                craft.setCruiseDirection((byte) 0x3);
            else if (yaw >= 45)  // west
                craft.setCruiseDirection((byte) 0x5);
            else if (yaw < -45) // south
                craft.setCruiseDirection((byte) 0x2);
            else // east
                craft.setCruiseDirection((byte) 0x4);
            craft.setCruising(true);
            return true;
        }
        if (args[0].equalsIgnoreCase("north") || args[0].equalsIgnoreCase("n")) {

            craft.setCruiseDirection((byte) 0x3);
            craft.setCruising(true);
            return true;
        }
        if (args[0].equalsIgnoreCase("south") || args[0].equalsIgnoreCase("s")) {
            craft.setCruiseDirection((byte) 0x2);
            craft.setCruising(true);
            return true;
        }
        if (args[0].equalsIgnoreCase("east") || args[0].equalsIgnoreCase("e")) {
            craft.setCruiseDirection((byte) 0x4);
            craft.setCruising(true);
            return true;
        }
        if (args[0].equalsIgnoreCase("west") || args[0].equalsIgnoreCase("w")) {
            craft.setCruiseDirection((byte) 0x5);
            craft.setCruising(true);
            return true;
        }
        return false;
    }

    private final String[] completions = {"North", "East", "South", "West", "On", "Off"};
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length !=1)
            return Collections.emptyList();
        List<String> returnValues = new ArrayList<>();
        for(String completion : completions)
            if(completion.toLowerCase().startsWith(strings[strings.length-1].toLowerCase()))
                returnValues.add(completion);
        return returnValues;
    }
}

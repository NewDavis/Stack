package me.newdavis.command;

import me.newdavis.stack.Stack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StackCmd implements CommandExecutor {

    private final String PERM;
    private final String PERM_ALL;
    private final String NO_PERM;
    private final String USAGE;
    private final String NOT_A_PLAYER;
    private final String ITEM_IS_AIR;
    private final String SUCCESS_HAND;
    private final String SUCCESS_ALL;

    public StackCmd() {
        PERM = "worldguard.stack";
        PERM_ALL = "worldguard.stack.all";
        NO_PERM = Stack.PREFIX + "§cDazu hast du keine Rechte!";
        USAGE = Stack.PREFIX + "§8/§bStack §8<§7hand§8/§7all§8>";
        NOT_A_PLAYER = Stack.PREFIX + "§cDieser Befehl kann nur von einem Spieler ausgeführt werden!";
        ITEM_IS_AIR = Stack.PREFIX + "§cDie Luft kann nicht gestacked werden!";
        SUCCESS_HAND = Stack.PREFIX + "§aDu hast erfolgreich das Item in deiner Hand gestacked!";
        SUCCESS_ALL = Stack.PREFIX + "§aDu hast erfolgreich alle Items in deinem Inventar gestacked!";
        Stack.getInstance().getCommand("nstack").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(NOT_A_PLAYER);
            return true;
        }

        Player p = (Player) sender;
        if(!p.hasPermission(PERM)) {
            p.sendMessage(NO_PERM);
            return true;
        }

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("hand")) {
                stack(p, true);
                return true;
            }else if(args[0].equalsIgnoreCase("all")) {
                if(!p.hasPermission(PERM_ALL)) {
                    p.sendMessage(NO_PERM);
                    return true;
                }
                stack(p, false);
                return true;
            }
        }
        p.sendMessage(USAGE);
        return true;
    }

    public void stack(Player p, boolean hand) {
        if(hand) {
            ItemStack itemInHand = p.getInventory().getItemInHand();
            if(itemInHand.getType() == Material.AIR) {
                p.sendMessage(ITEM_IS_AIR);
                return;
            }

            p.getInventory().setItem(p.getInventory().getHeldItemSlot(), new ItemStack(Material.AIR));
            giveItems(itemInHand, p);

            p.sendMessage(SUCCESS_HAND);
            return;
        }

        for(int i = 0; i < p.getInventory().getContents().length; i++) {
            ItemStack currentItem = p.getInventory().getItem(i);
            if (currentItem != null && currentItem.getType() != Material.AIR) {
                p.getInventory().setItem(i, new ItemStack(Material.AIR));
                giveItems(currentItem, p);
            }
        }
        p.sendMessage(SUCCESS_ALL);
    }

    public int getAmountOfItem(ItemStack currentItem, Player p) {
        int amount = currentItem.getAmount();
        for (ItemStack item : p.getInventory().getContents()) {
            if (item != null) {
                if (currentItem.isSimilar(item)) {
                    amount += item.getAmount();
                }
            }
        }

        return amount;
    }

    public void remove(ItemStack item, Player p) {
        for(int i = 0; i < p.getInventory().getContents().length; i++) {
            ItemStack item2 = p.getInventory().getItem(i);
            if(item2 != null) {
                if (item2.isSimilar(item)) {
                    p.getInventory().setItem(i, new ItemStack(Material.AIR));
                }
            }
        }
    }

    public void giveItems(ItemStack currentItem, Player p) {
        int amount = getAmountOfItem(currentItem, p);
        remove(currentItem, p);
        int items = amount / 64;

        for (int i = 0; i < items; i++) {
            currentItem.setAmount(64);
            p.getInventory().addItem(currentItem);
        }

        amount -= items * 64;
        if (amount > 0) {
            currentItem.setAmount(amount);
            p.getInventory().addItem(currentItem);
        }
    }

}

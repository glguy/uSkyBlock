package us.talabrek.ultimateskyblock;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerJoin implements Listener {
	private Player hungerman = null;

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerFoodChange(final FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player) {
			hungerman = (Player) event.getEntity();
			if (hungerman.getWorld().getName().equalsIgnoreCase(Settings.general_worldName)) {
				if (hungerman.getFoodLevel() > event.getFoodLevel()) {
					if (uSkyBlock.getInstance().playerIsOnIsland(hungerman)) {
						if (VaultHandler.checkPerk(hungerman.getName(), "usb.extra.hunger", hungerman.getWorld())) {
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(final PlayerInteractEvent event) {
		if (Settings.extras_obsidianToLava
				&& event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
				&& event.getPlayer().getItemInHand().getType().equals(Material.BUCKET)
				&& event.getClickedBlock().getType().equals(Material.OBSIDIAN)
				&& uSkyBlock.getInstance().playerIsOnIsland(event.getPlayer())
				&& !uSkyBlock.getInstance().testForObsidian(event.getClickedBlock())) {

			final Map<Integer, ItemStack> remaining = event.getPlayer().getInventory().removeItem(new ItemStack[] { new ItemStack(325, 1) });

			if (remaining.isEmpty()) {
				event.getPlayer().sendMessage(ChatColor.YELLOW + "Changing your obsidian back into lava. Be careful!");
				event.getClickedBlock().setType(Material.AIR);
				event.getPlayer().getInventory().addItem(new ItemStack[] { new ItemStack(327, 1) });
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		PlayerInfo pi = uSkyBlock.getInstance().readPlayerFile(event.getPlayer().getName());
		if (pi == null) {
			System.out.println("uSkyblock " + "Creating a new skyblock file for " + event.getPlayer().getName());
			pi = new PlayerInfo(event.getPlayer().getName());
			uSkyBlock.getInstance().writePlayerFile(event.getPlayer().getName(), pi);
		}
		if (pi.getHasParty() && pi.getPartyIslandLocation() == null) {
			final PlayerInfo pi2 = uSkyBlock.getInstance().readPlayerFile(pi.getPartyLeader());
			pi.setPartyIslandLocation(pi2.getIslandLocation());
			uSkyBlock.getInstance().writePlayerFile(event.getPlayer().getName(), pi);
		}

		pi.buildChallengeList();
		uSkyBlock.getInstance().addActivePlayer(event.getPlayer().getName(), pi);
		System.out.println("uSkyblock " + "Loaded player file for " + event.getPlayer().getName());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(final PlayerQuitEvent event) {
		uSkyBlock.getInstance().removeActivePlayer(event.getPlayer().getName());
	}
}
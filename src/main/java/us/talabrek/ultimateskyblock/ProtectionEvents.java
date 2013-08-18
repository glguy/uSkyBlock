package us.talabrek.ultimateskyblock;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;

public class ProtectionEvents implements Listener {

	/**
	 * Checks if actions by the given player on the given location are restricted
	 * @param player Player performing the action
	 * @param location Location of the action performed
	 * @return true when the action should be restricted
	 */
	private boolean isRestricted(final Player player, final Location location) {
		return player.getWorld().getName().equalsIgnoreCase(Settings.general_worldName)
				&& !uSkyBlock.getInstance().locationIsOnIsland(player, location)
				&& !VaultHandler.checkPerk(player.getName(), "usb.mod.bypassprotection", player.getWorld())
				&& !player.isOp();
	}

	/**
	 * Prevent players from interacting with entities on other players' islands
	 * but allow in spawn and on own island.
	 */
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	private void onEntityInteract(final PlayerInteractEntityEvent event) {
		final Player player = event.getPlayer();
		if (isRestricted(player, event.getRightClicked().getLocation())
			&& !uSkyBlock.getInstance().playerIsInSpawn(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerAttack(final EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			final Player attacker = (Player) event.getDamager();
			final Entity defender = event.getEntity();
			if (!(defender instanceof Player)
					&& isRestricted(attacker, defender.getLocation())) {

				event.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPlayerBedEnter(final PlayerBedEnterEvent event) {
		if (isRestricted(event.getPlayer(), event.getBed().getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerBlockBreak(final BlockBreakEvent event) {
		if (isRestricted(event.getPlayer(), event.getBlock().getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerBlockPlace(final BlockPlaceEvent event) {
		if (isRestricted(event.getPlayer(), event.getBlock().getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPlayerBreakHanging(final HangingBreakByEntityEvent event) {
		if (event.getRemover() instanceof Player) {
			final Player breaker = (Player) event.getRemover();
			if (isRestricted(breaker, event.getEntity().getLocation())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event) {
		if (isRestricted(event.getPlayer(), event.getBlockClicked().getLocation())) {
			event.getPlayer().sendMessage(ChatColor.RED + "You can only do that on your island!");
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPlayerBucketFill(final PlayerBucketFillEvent event) {
		if (isRestricted(event.getPlayer(), event.getBlockClicked().getLocation())) {
			event.getPlayer().sendMessage(ChatColor.RED + "You can only do that on your island!");
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPlayerInteract(final PlayerInteractEvent event) {
		if (isRestricted(event.getPlayer(), event.getClickedBlock().getLocation())
				&& !uSkyBlock.getInstance().playerIsInSpawn(event.getPlayer())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPlayerShearEntity(final PlayerShearEntityEvent event) {
		if (isRestricted(event.getPlayer(), event.getEntity().getLocation())) {
			event.getPlayer().sendMessage(ChatColor.RED + "You can only do that on your island!");
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPlayerVehicleDamage(final VehicleDamageEvent event) {
		if (event.getAttacker() instanceof Player) {
			final Player breaker = (Player) event.getAttacker();
			if (isRestricted(breaker, event.getVehicle().getLocation())) {
				event.setCancelled(true);
			}
		}
	}
}
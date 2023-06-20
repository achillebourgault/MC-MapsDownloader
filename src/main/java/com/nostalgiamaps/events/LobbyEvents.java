/*********************************************************\
*   @Author: AchilleBourgault                             *
*   @Github: https://github.com/achillebourgault          *
*   @Project: NostalgiaMaps                               *
\*********************************************************/

package com.nostalgiamaps.events;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class LobbyEvents implements Listener {

        @EventHandler
        public void onPlayerDamage(EntityDamageEvent e) {
            if (e.getEntity().getWorld().getName().equals("world")) {
                e.setCancelled(true);
                e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.BLOCK_AMETHYST_BLOCK_HIT, 1, 1);
                e.getEntity().getWorld().spawnParticle(Particle.CRIT_MAGIC, e.getEntity().getLocation(), 100, 0.5, 0.5, 0.5, 0.1);
            }
        }
}

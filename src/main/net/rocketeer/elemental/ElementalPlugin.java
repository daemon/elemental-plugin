package net.rocketeer.elemental;

import net.rocketeer.elemental.compute.ElementalLibrary;
import net.rocketeer.elemental.geometry.Scene;
import net.rocketeer.elemental.material.ElementalMaterial;
import net.rocketeer.elemental.material.ElementalMaterialStore;
import net.rocketeer.elemental.material.PhasedMaterial;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ElementalPlugin extends JavaPlugin {
  @Override
  public void onEnable() {
    int size = 100;
    Scene scene = new Scene(size);
    Lock lock = new ReentrantLock();
    for (int i = 0; i < size * size * size; ++i)
      scene.heatData().heatCoeffs()[i] = 0.1F;
    ElementalLibrary lib = ElementalLibrary.get();
    Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
      while (true) {
        lock.lock();
        lib.heat3d(scene.heatData().heatPoints(), scene.buffer(), scene.heatData().heatCoeffs(), 0.1F, size);
        lock.unlock();
        try {
          Thread.sleep(25);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });
    final World[] world = {null};
    Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
      if (world[0] == null) {
        for (Player player : Bukkit.getOnlinePlayers())
          world[0] = player.getWorld();
        if (world[0] == null)
          return;
      }
      for (int i = 0; i < size; ++i)
        for (int j = 0; j < size; ++j)
          for (int k = 0; k < size; ++k) {
            Block block = world[0].getBlockAt(i, j, k);
            Material type = block.getType();
            ElementalMaterial material = ElementalMaterialStore.instance().find(type);
            if (material == null || material.material == Material.AIR)
              continue;
            lock.lock();
            material.onSceneUpdate(scene, i, j, k);
            lock.unlock();
            float heat = scene.heatData().heatPoints()[size * size * i + size * j + k];
            if (material instanceof PhasedMaterial) {
              PhasedMaterial newMaterial = ((PhasedMaterial) material).convert(heat);
              if (newMaterial != material)
                block.setType(newMaterial.material);
            }
            if (heat < 85F)
              continue;
            heat = (float) Math.sqrt(heat - 85F);
            // http://www.tannerhelland.com/4435/convert-temperature-rgb-algorithm-code/
            double red, green, blue;
            if (heat <= 66) {
              red = 255;
              green = heat;
              green = 99.4708025861 * Math.log(green) - 161.1195681661;
              green = Math.min(Math.max(green, 0), 255);
              if (heat <= 19)
                blue = 0;
              else {
                blue = heat - 10;
                blue = 138.5177312231 * Math.log(blue) - 305.0446927307;
                blue = Math.min(Math.max(blue, 0), 255);
              }
            } else {
              red = heat - 60;
              red = 329.698727446 * Math.pow(red, -0.1332047592);
              red = Math.min(Math.max(red, 0), 255);
              green = heat - 60;
              green = 288.1221695283 * Math.pow(green, -0.0755148492);
              green = Math.min(Math.max(green, 0), 255);
              blue = 255;
            }
            ////////////////////////////
            ParticleEffect.REDSTONE.display((int) red, (int) green, (int) blue, 0.0035F, 0,
                new Location(world[0], i + 2 * (Math.random() - 0.3), j + 2 * (Math.random() - 0.3), k + 2 * (Math.random() - 0.3)), 64);
          }
    }, 0, 2);
    Bukkit.getPluginManager().registerEvents(new Listener() {
      @EventHandler
      public void onWaterFlow(BlockFromToEvent event) {
        if (event.getBlock().getType() != Material.WATER && event.getBlock().getType() != Material.STATIONARY_WATER)
          return;
        int x = event.getToBlock().getLocation().getBlockX();
        int y = event.getToBlock().getLocation().getBlockY();
        int z = event.getToBlock().getLocation().getBlockZ();
        if (x >= size || x < 0 || y >= size || y < 0 || z >= size || z < 0)
          return;
        float heat = scene.heatData().heatPoints()[size * size * x + size * y + z];
        if (heat > 100) {
          event.setCancelled(true);
        }
      }

      @EventHandler
      public void onBlockPlace(BlockPlaceEvent event) {
        int x = event.getBlock().getLocation().getBlockX();
        int y = event.getBlock().getLocation().getBlockY();
        int z = event.getBlock().getLocation().getBlockZ();
        if (x >= size || x < 0 || y >= size || y < 0 || z >= size || z < 0)
          return;
        ElementalMaterial material = ElementalMaterialStore.instance().find(event.getBlock().getType());
        if (material == null)
          return;
        lock.lock();
        material.onSceneInit(scene, x, y, z);
        lock.unlock();
      }
    }, this);
    Bukkit.getPluginCommand("heat").setExecutor(new CommandExecutor() {
      @Override
      public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player))
          return true;
        float heat = Float.parseFloat(strings[0]);
        Player p = (Player) commandSender;
        int x = p.getLocation().getBlockX();
        int y = p.getLocation().getBlockY();
        int z = p.getLocation().getBlockZ();
        if (x >= size || x < 0 || y >= size || y < 0 || z >= size || z < 0)
          return true;
        lock.lock();
        scene.heatData().setHeat(heat, x, y - 1, z);
        lock.unlock();
        return true;
      }
    });
    Bukkit.getPluginCommand("measure").setExecutor(new CommandExecutor() {
      @Override
      public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player))
          return true;
        Player p = (Player) commandSender;
        int x = p.getLocation().getBlockX();
        int y = p.getLocation().getBlockY();
        int z = p.getLocation().getBlockZ();
        if (x >= size || x < 0 || y >= size || y < 0 || z >= size || z < 0)
          return true;
        p.sendMessage(String.valueOf(Math.round(scene.heatData().heatPoints()[100 * 100 * x + 100 * y + z] * 100) / 100.0F) + "C");
        return true;
      }
    });
  }
}

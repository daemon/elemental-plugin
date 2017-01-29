package net.rocketeer.elemental.material;

import net.rocketeer.elemental.geometry.Scene;
import org.bukkit.Material;

public class FireMaterial extends ElementalMaterial {
  FireMaterial() {
    super(Material.FIRE, 180, true);
  }

  @Override
  public void onSceneUpdate(Scene scene, int x, int y, int z) {
    super.onSceneUpdate(scene, x, y, z);
  }
}

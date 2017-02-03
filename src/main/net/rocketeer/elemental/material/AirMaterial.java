package net.rocketeer.elemental.material;

import net.rocketeer.elemental.geometry.Scene;
import org.bukkit.Material;

public class AirMaterial extends PhasedMaterial {
  AirMaterial() {
    super(Material.AIR, 3, false, 0.00001F);
  }

  public void onSceneUpdate(Scene scene, int x, int y, int z) {
    int w = scene.width();
    float temp = scene.heatData().heatPoints()[w * w * x + w * y + z];
    scene.heatData().setHeat(x, y, z, Math.min(temp * 0.993F, this.temperature));
  }

  @Override
  public PhasedMaterial convert(float temperature) {
    return this;
  }
}

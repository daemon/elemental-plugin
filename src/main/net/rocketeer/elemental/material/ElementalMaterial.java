package net.rocketeer.elemental.material;

import net.rocketeer.elemental.geometry.Scene;
import org.bukkit.Material;

public class ElementalMaterial {
  public final int temperature;
  public final float alpha;
  public final Material material;
  public final boolean source;

  ElementalMaterial(Material material, int temperature, boolean source) {
    this(material, temperature, source, 0.2F);
  }

  ElementalMaterial(Material material, int temperature, boolean source, float alpha) {
    this.material = material;
    this.temperature = temperature;
    this.alpha = alpha;
    this.source = source;
  }

  public void onSceneInit(Scene scene, int x, int y, int z) {
    int w = scene.width();
    scene.heatData().setHeat(this.temperature, x, y, z);
    scene.heatData().heatCoeffs()[w * w * x + w * y + z] = this.alpha;
  }

  public void onSceneUpdate(Scene scene, int x, int y, int z) {
    if (this.source)
      scene.heatData().setHeat(this.temperature, x, y, z);
  }
}

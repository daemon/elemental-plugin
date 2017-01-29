package net.rocketeer.elemental.material;

import org.bukkit.Material;

public abstract class PhasedMaterial extends ElementalMaterial {
  PhasedMaterial(Material material, int temperature, boolean source) {
    super(material, temperature, source);
  }

  PhasedMaterial(Material material, int temperature, boolean source, float alpha) {
    super(material, temperature, source, alpha);
  }

  public abstract PhasedMaterial convert(float temperature);
}

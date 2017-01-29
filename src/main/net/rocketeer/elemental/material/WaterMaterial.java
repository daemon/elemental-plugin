package net.rocketeer.elemental.material;

import org.bukkit.Material;

public class WaterMaterial extends PhasedMaterial {
  WaterMaterial() {
    super(Material.WATER, 15, false);
  }

  @Override
  public PhasedMaterial convert(float temperature) {
    if (temperature > 100)
      return new AirMaterial();
    return this;
  }
}

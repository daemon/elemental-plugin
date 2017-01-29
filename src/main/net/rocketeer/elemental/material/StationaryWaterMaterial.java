package net.rocketeer.elemental.material;

import org.bukkit.Material;

public class StationaryWaterMaterial extends PhasedMaterial {
  StationaryWaterMaterial() {
    super(Material.STATIONARY_WATER, 15, false);
  }

  @Override
  public PhasedMaterial convert(float temperature) {
    if (temperature < -5)
      return new IceMaterial();
    else if (temperature > 100)
      return new AirMaterial();
    return this;
  }
}

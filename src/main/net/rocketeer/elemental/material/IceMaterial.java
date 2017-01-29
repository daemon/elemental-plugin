package net.rocketeer.elemental.material;

import org.bukkit.Material;

public class IceMaterial extends PhasedMaterial {
  IceMaterial() {
    super(Material.ICE, -5, false);
  }

  @Override
  public PhasedMaterial convert(float temperature) {
    if (temperature < -20)
      return new PackedIceMaterial();
    else if (temperature > 0)
      return new WaterMaterial();
    return this;
  }
}

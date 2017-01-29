package net.rocketeer.elemental.material;

import org.bukkit.Material;

public class PackedIceMaterial extends PhasedMaterial {
  PackedIceMaterial() {
    super(Material.PACKED_ICE, -273, false, 0.01F);
  }

  @Override
  public PhasedMaterial convert(float temperature) {
    if (temperature >= -20)
      return new IceMaterial();
    return this;
  }
}


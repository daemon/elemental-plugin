package net.rocketeer.elemental.material;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class ElementalMaterialStore {
  private static final ElementalMaterialStore instance = new ElementalMaterialStore();
  private Map<Material, ElementalMaterial> materialMap = new HashMap<>();
  private ElementalMaterialStore() {
    this.register(new ElementalMaterial(Material.GLOWSTONE, 10000, true));
    this.register(new ElementalMaterial(Material.SEA_LANTERN, 1000000, true));
    this.register(new ElementalMaterial(Material.IRON_BLOCK, 1, false));
    this.register(new AirMaterial());
    this.register(new FireMaterial());
    this.register(new IceMaterial());
    this.register(new WaterMaterial());
    this.register(new StationaryWaterMaterial());
    this.register(new PackedIceMaterial());
  }

  public void register(ElementalMaterial elemMaterial) {
    this.materialMap.put(elemMaterial.material, elemMaterial);
  }

  public ElementalMaterial find(Material material) {
    return this.materialMap.get(material);
  }

  public static ElementalMaterialStore instance() {
    return instance;
  }
}

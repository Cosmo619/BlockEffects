package net.betterverse.Lampstone;

public class LSLocation
{
  private int x;
  private int y;
  private int z;
  private String worldName;

  public LSLocation(int x, int y, int z, String worldName)
  {
    this.x = x;
    this.y = y;
    this.z = z;
    this.worldName = worldName;
  }

  public int getX() {
    return this.x;
  }

  public int getY() {
    return this.y;
  }

  public int getZ() {
    return this.z;
  }

  public String getWorldName() {
    return this.worldName;
  }

  public void setX(int x) {
    this.x = x;
  }

  public void setY(int y) {
    this.y = y;
  }

  public void setZ(int z) {
    this.z = z;
  }

  public void setWorldName(String worldName) {
    this.worldName = worldName;
  }
}
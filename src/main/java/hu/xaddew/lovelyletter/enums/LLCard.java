package hu.xaddew.lovelyletter.enums;

public enum LLCard {

  HERCEGNO("Hercegnő"),
  GROFNO("Grófnő"),
  KIRALY("Király"),
  KANCELLAR("Kancellár"),
  HERCEG("Herceg"),
  SZOBALANY("Szobalány"),
  BARO("Báró"),
  PAP("Pap"),
  OR("Őr"),
  KEM("Kém");

  private final String name;

  LLCard(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}

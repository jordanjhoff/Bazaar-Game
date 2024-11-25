package Common.converters;

public enum MName {
  SETUP("setup"),
  REQUESTPT("request-pebbles-or-trades"),
  REQUESTCARDS("request-cards"),
  WIN("win");

  private final String methodName;

  MName(String methodName) {
    this.methodName = methodName;
  }

  public static MName fromString(String name) throws BadJsonException {
    for (MName mName : MName.values()) {
      if (mName.methodName.equalsIgnoreCase(name)) {
        return mName;
      }
    }
    throw new BadJsonException("Invalid method name" + name);
  }

  public String toString() {
    return methodName;
  }
}

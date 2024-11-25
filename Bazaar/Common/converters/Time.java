package Common.converters;

public class Time {
  public static long NsToMs(long ns) {
    return ns / 1000000;
  }

  public static long MsToNs(long ms) {
    return ms * 1000000;
  }

  public static long MsToSeconds(long ms) {
    return ms / 1000;
  }

  public static long SecondsToMs(long s) {
    return s * 1000;
  }
}

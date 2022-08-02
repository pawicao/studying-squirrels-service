package pl.edu.agh.pawicao.studying_squirrels_api.util;

public class VarUtils {
  public static double round(double value) {
    int scale = (int) Math.pow(10, 1);
    return (double) Math.round(value * scale) / scale;
  }

  public static String removeSuffixIfExists(String key, String suffix) {
    return key.endsWith(suffix) ? key.substring(0, key.length() - suffix.length()) : key;
  }

  public static String getAlternativeNameFromLink(String link) {
    return link.substring(link.lastIndexOf('/') + 1).trim().replace('_', ' ');
  }
}

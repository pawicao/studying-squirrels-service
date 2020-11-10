package pl.edu.agh.pawicao.studying_squirrels_api.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateUtils {

  public static ZonedDateTime millsToLocalDateTime(long m){
    ZoneId zoneId = ZoneId.systemDefault();
    Instant instant = Instant.ofEpochSecond(m/1000);
    ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId);
    return zonedDateTime;
  }
}

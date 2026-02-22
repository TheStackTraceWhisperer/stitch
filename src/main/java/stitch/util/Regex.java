package stitch.util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Regex {
  private Regex() {
  }

  public static List<Map<String, String>> extractAllMatches(Pattern pattern, String content) {
    Set<String> groupNames = pattern.namedGroups().keySet();
    if (groupNames.isEmpty()) return List.of();

    List<Map<String, String>> results = new ArrayList<>();
    Matcher matcher = pattern.matcher(content);

    while (matcher.find()) {
      results.add(extractSingleMatch(matcher, groupNames));
    }

    return List.copyOf(results);
  }

  private static Map<String, String> extractSingleMatch(Matcher matcher, Set<String> groupNames) {
    Map<String, String> matchData = new HashMap<>();
    for (String name : groupNames) {
      String value = matcher.group(name);
      if (value != null) matchData.put(name, value);
    }
    return Map.copyOf(matchData);
  }
}


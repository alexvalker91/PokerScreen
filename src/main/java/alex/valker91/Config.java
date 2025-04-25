package alex.valker91;

import org.opencv.core.Rect;
import java.util.Map;

public class Config {
    private Map<String, String> paths;
    private Map<String, Rect> coordinates;
    private Map<String, Integer> heroSeparators;

    public Config(Map<String, String> paths, Map<String, Rect> coordinates, Map<String, Integer> heroSeparators) {
        this.paths = paths;
        this.coordinates = coordinates;
        this.heroSeparators = heroSeparators;
    }

    public String getPaths(String key) {
        return paths.get(key);
    }

    public Rect getCoordinates(String key) {
        return coordinates.get(key);
    }

    public Integer getHeroSeparator1() {
        return heroSeparators.get("separator_1");
    }

    public Integer getHeroSeparator2() {
        return heroSeparators.get("separator_2");
    }
}

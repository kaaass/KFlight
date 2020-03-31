package net.kaaass.kflight.data;

import lombok.Getter;
import lombok.Synchronized;
import net.kaaass.kflight.data.entry.EntryCity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * 城市数据管理
 */
public class CityManager {

    private static final CityManager INSTANCE = new CityManager();

    private List<EntryCity> data;

    @Getter
    private Index<EntryCity, String, Integer> indexName;

    private CityManager() {
        data = new ArrayList<>();
        indexName = new Index<>(EntryCity::getName, String::hashCode, Comparator.naturalOrder());
    }

    /**
     * 添加 entry
     */
    @Synchronized
    public static void addEntry(EntryCity entryCity) {
        INSTANCE.data.add(entryCity);
        INSTANCE.indexName.addIndexFor(entryCity);
    }

    /**
     * 删除 entry
     */
    @Synchronized
    public static void removeEntry(EntryCity entryCity) {
        INSTANCE.data.remove(entryCity);
        INSTANCE.indexName.removeIndexFor(entryCity);
    }

    /**
     * 通过城市名查找
     */
    public static Optional<EntryCity> findByName(String name) {
        return INSTANCE.indexName.findOne(name);
    }

    public static CityManager getInstance() {
        return INSTANCE;
    }
}

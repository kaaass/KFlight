package net.kaaass.kflight.data.entry;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 城市数据
 */
@Getter
@ToString
@EqualsAndHashCode(exclude = {"avgPrice"})
public class EntryCity implements IEntry {

    /**
     * 城市名
     */
    final String name = null;

    /**
     * 平均票价
     */
    @Setter
    float avgPrice = 0;

    @Setter
    int avgCnt = 0;
}

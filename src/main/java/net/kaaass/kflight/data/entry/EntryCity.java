package net.kaaass.kflight.data.entry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

/**
 * 城市数据
 */
@Getter
@ToString
@EqualsAndHashCode(of = {"name"})
@RequiredArgsConstructor
public class EntryCity implements IEntry {

    /**
     * 城市名
     */
    @NonNull
    final String name;

    /**
     * 平均票价
     */
    @Setter
    @JsonIgnore
    float avgPrice = 0;

    @Setter
    @JsonIgnore
    int avgCnt = 0;
}

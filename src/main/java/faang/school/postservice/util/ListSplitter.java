package faang.school.postservice.util;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

@Component
public class ListSplitter {
    public <T> List<List<T>> split(List<T> list, int sublistSize) {
        return IntStream.range(0, (list.size() + sublistSize - 1) / sublistSize)
                .mapToObj(i -> list.subList(i * sublistSize, Math.min(sublistSize * (i + 1), list.size())))
                .toList();
    }
}
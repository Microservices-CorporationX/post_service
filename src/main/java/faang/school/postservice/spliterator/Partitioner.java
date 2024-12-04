package faang.school.postservice.spliterator;

import java.util.List;

public interface Partitioner<T> {

    List<List<T>> splitList(List<T> list);
}

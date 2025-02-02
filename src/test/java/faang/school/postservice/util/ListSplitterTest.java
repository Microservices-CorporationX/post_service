package faang.school.postservice.util;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ListSplitterTest {

    private final ListSplitter listSplitter = new ListSplitter();

    @Test
    void split_ShouldSplitListIntoEqualSizedSublists() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6);
        int sublistSize = 2;

        List<List<Integer>> result = listSplitter.split(numbers, sublistSize);

        assertEquals(3, result.size());
        assertEquals(List.of(1, 2), result.get(0));
        assertEquals(List.of(3, 4), result.get(1));
        assertEquals(List.of(5, 6), result.get(2));
    }

    @Test
    void split_ShouldHandleUnevenSizedSublists() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);
        int sublistSize = 2;

        List<List<Integer>> result = listSplitter.split(numbers, sublistSize);

        assertEquals(3, result.size());
        assertEquals(List.of(1, 2), result.get(0));
        assertEquals(List.of(3, 4), result.get(1));
        assertEquals(List.of(5), result.get(2));
    }

    @Test
    void split_ShouldHandleListSmallerThanSublistSize() {
        List<Integer> numbers = List.of(1, 2, 3);
        int sublistSize = 5;

        List<List<Integer>> result = listSplitter.split(numbers, sublistSize);

        assertEquals(1, result.size());
        assertEquals(List.of(1, 2, 3), result.get(0));
    }

    @Test
    void split_ShouldHandleEmptyList() {
        List<Integer> numbers = List.of();
        int sublistSize = 3;

        List<List<Integer>> result = listSplitter.split(numbers, sublistSize);

        assertEquals(0, result.size());
    }

    @Test
    void split_ShouldReturnEachElementAsSeparateList_WhenSublistSizeIsOne() {
        List<Integer> numbers = List.of(1, 2, 3, 4);
        int sublistSize = 1;

        List<List<Integer>> result = listSplitter.split(numbers, sublistSize);

        assertEquals(4, result.size());
        assertEquals(List.of(1), result.get(0));
        assertEquals(List.of(2), result.get(1));
        assertEquals(List.of(3), result.get(2));
        assertEquals(List.of(4), result.get(3));
    }

    @Test
    void split_ShouldReturnWholeListAsOneSublist_WhenSublistSizeIsGreaterThanListSize() {
        List<Integer> numbers = List.of(1, 2, 3);
        int sublistSize = 10;

        List<List<Integer>> result = listSplitter.split(numbers, sublistSize);

        assertEquals(1, result.size());
        assertEquals(List.of(1, 2, 3), result.get(0));
    }
}

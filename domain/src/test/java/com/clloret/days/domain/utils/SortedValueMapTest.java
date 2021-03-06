package com.clloret.days.domain.utils;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

import java.util.Collection;
import java.util.Comparator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SortedValueMapTest {

  @Mock
  Comparator<Integer> comparator;
  private SortedValueMap<String, Integer, Integer> sut;

  private void addSampleValues() {

    sut.put("key1", 40);
    sut.put("key2", 20);
    sut.put("key3", 10);
    sut.put("key4", 30);
  }

  @Before
  public void setUp() {

    Comparator<Integer> naturalOrderComparator = Comparator.naturalOrder();
    sut = new SortedValueMap<>(naturalOrderComparator);
  }

  @Test
  public void put_Always_UpdateSortedValues() {

    addSampleValues();

    sut.put("key5", 25);

    Collection<Integer> result = sut.sortedValues();

    assertThat(result).containsExactly(10, 20, 25, 30, 40);
  }

  @Test
  public void remove_Always_UpdateSortedValues() {

    addSampleValues();

    sut.remove("key2");

    Collection<Integer> result = sut.sortedValues();

    assertThat(result).containsExactly(10, 30, 40);
  }

  @Test
  public void sortedValues_Always_ReturnSortedValues() {

    addSampleValues();

    Collection<Integer> result = sut.sortedValues();

    assertThat(result).containsExactly(10, 20, 30, 40);
  }

  @Test
  public void refreshValues_WhenSortedValuesInitialized_CallComparatorCompare() throws Exception {

    try (AutoCloseable ignored = MockitoAnnotations.openMocks(this)) {
      sut = new SortedValueMap<>(comparator);

      addSampleValues();

      sut.sortedValues();

      sut.refreshValues();

      //noinspection ResultOfMethodCallIgnored
      verify(comparator, atLeast(1)).compare(anyInt(), anyInt());
    }
  }
}
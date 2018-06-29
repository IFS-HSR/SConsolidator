package ch.hsr.ifs.sconsolidator.core.base.functional;

import static ch.hsr.ifs.sconsolidator.core.base.utils.CollectionUtil.list;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

public class FunctionalHelperTest {
  @Test
  public void testFilterUnary() {
    List<Integer> primesUpTo100 =
        list(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79,
            83, 89, 97);
    List<Integer> numbers = new ArrayList<Integer>();
    for (int i = 0; i <= 100; i++) {
      numbers.add(i);
    }

    Collection<Integer> filtered =
        FunctionalHelper.filter(numbers, new UnaryFunction<Integer, Boolean>() {
          @Override
          public Boolean apply(Integer number) {
            if (number <= 1)
              return false;

            for (int i = 2; i <= Math.sqrt(number); i++) {
              if ((number % i) == 0)
                return false;
            }

            return true;
          }
        });

    assertEquals(primesUpTo100, filtered);
  }

  @Test
  public void testMap() {
    List<Integer> numbers = list(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

    Collection<Integer> converted =
        FunctionalHelper.map(numbers, new UnaryFunction<Integer, Integer>() {
          @Override
          public Integer apply(Integer number) {
            return number * 2;
          }
        });

    List<Integer> expected = list(0, 2, 4, 6, 8, 10, 12, 14, 16, 18);
    assertEquals(expected, converted);
  }

  @Test
  public void testFold() {
    final List<Integer> numbersUpTo10 = list(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    double result = FunctionalHelper.fold(numbersUpTo10, new Injector<Integer, Double>() {
      double runningSum;

      @Override
      public Void apply(Integer param) {
        runningSum += param;
        return null;
      }

      @Override
      public Double yield() {
        double average = runningSum / numbersUpTo10.size();
        return average;
      }
    });

    assertEquals(5.5, result, 0.0);
  }
}

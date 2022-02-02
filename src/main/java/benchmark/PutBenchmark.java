package benchmark;

import io.vavr.collection.HashMap;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.organicdesign.fp.collections.PersistentHashMap;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.All)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-Xms2G", "-Xmx2G"})
@Warmup(iterations = 1)
@Measurement(iterations = 3)
public class PutBenchmark {

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(PutBenchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @State(Scope.Thread)
    public static class RandomNumbers {

        Random rand = new Random();
        public List<Integer> randNums = new ArrayList<Integer>(10000);

        @Setup(Level.Iteration)
        public void doSetup() {
            for (int i = 0; i < 10000; i++) {
                randNums.add(rand.nextInt());
            }
        }
    }

    @Benchmark
    public PersistentHashMap<Integer, Integer> put10kEntriesToPaguroHashMap(RandomNumbers nums) {
        PersistentHashMap<Integer, Integer> pmap = PersistentHashMap.empty();
        for (int i = 0; i < 10000; i++) {
            int num = nums.randNums.get(i);
            pmap = pmap.assoc(num, num);
        }
        return pmap;
    }

    @Benchmark
    public cyclops.data.HashMap<Integer, Integer> put10kEntriesToCyclopsHashMap(RandomNumbers nums) {
        cyclops.data.HashMap<Integer, Integer> cmap = cyclops.data.HashMap.empty();
        for (int i = 0; i < 10000; i++) {
            int num = nums.randNums.get(i);
            cmap = cmap.put(num, num);
        }
        return cmap;
    }

    @Benchmark
    public HashMap<Integer, Integer> put10kEntriesToVavrHashMap(RandomNumbers nums) {
        HashMap<Integer, Integer> vmap = HashMap.empty();
        for (int i = 0; i < 10000; i++) {
            int num = nums.randNums.get(i);
            vmap = vmap.put(num, num);
        }
        return vmap;
    }

    @Benchmark
    public PMap<Integer, Integer> put10kEntriesToPCollectionHashMap(RandomNumbers nums){
        PMap<Integer, Integer> pmap = HashTreePMap.empty();
        for (int i = 0; i < 10000; i++) {
            int num = nums.randNums.get(i);
            pmap = pmap.plus(num, num);
        }
        return pmap;
    }

    @Benchmark
    public Map<Integer, Integer> put10kEntriesToJavaHashMap(RandomNumbers nums) {
        java.util.HashMap<Integer, Integer> map = new java.util.HashMap<>();
        for (int i = 0; i < 10000; i++) {
            int num = nums.randNums.get(i);
            map.put(num, num);
        }
        return map;
    }


}



package benchmark;

import io.vavr.Tuple2;
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
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.organicdesign.fp.collections.PersistentHashMap;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@BenchmarkMode(Mode.All)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-Xms2G", "-Xmx2G"})
@Warmup(iterations = 1)
@Measurement(iterations = 3)
public class ScanBenchmark {

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(ScanBenchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @State(Scope.Thread)
    public static class SampleData {

        Random rand = new Random();
        public PersistentHashMap<Integer, Integer> pmap = PersistentHashMap.empty();
        public cyclops.data.HashMap<Integer, Integer> cmap = cyclops.data.HashMap.empty();
        public HashMap<Integer, Integer> vmap = HashMap.empty();
        public PMap<Integer, Integer> pCollectionmap = HashTreePMap.empty();
        public Map<Integer, Integer> map = new java.util.HashMap<>();

        /**
         * The maps are loaded with the same list of numbers from 0 to 10k
         * The queries use random numbers in the same range as keys to search in the maps
         */
        @Setup(Level.Iteration)
        public void doSetup() {
            for (int i = 0; i < 10000; i++) {
                int randNum = rand.nextInt();
                pmap = pmap.assoc(randNum, randNum);
                cmap = cmap.put(randNum, randNum);
                vmap = vmap.put(randNum, randNum);
                pCollectionmap = pCollectionmap.plus(randNum, randNum);
                map.put(randNum, randNum);
            }
        }
    }

    /**
     * Benchmark: Scan the map and return all keys whose value is greater than 10k
     *
     * @param data
     * @return
     */
    @Benchmark
    public List<Integer> scanPaguroHashMap(SampleData data) {
        return data.pmap.entrySet()
                .stream()
                .parallel()
                .filter(entry -> entry.getValue() > 10000)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Benchmark
    public List<Integer> scanCyclopsHashMap(SampleData data) {
        return data.cmap.stream()
                .filter(entry -> entry._2() > 10000)
                .map(cyclops.data.tuple.Tuple2::_1)
                .collect(Collectors.toList());
    }

    @Benchmark
    public List<Integer> scanVavrHashMap(SampleData data) {
        return data.vmap
                .filter(entry -> entry._2() > 10000)
                .map(Tuple2::_1)
                .collect(Collectors.toList());
    }

    @Benchmark
    public List<Integer> scanPCollectionHashMap(SampleData data) {
        return data.pCollectionmap.entrySet()
                .stream()
                .parallel()
                .filter(entry -> entry.getValue() > 10000)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Benchmark
    public List<Integer> scanJavaHashMap(SampleData data, Blackhole blackhole) {
        return data.map.entrySet()
                .stream()
                .parallel()
                .filter(entry -> entry.getValue() > 10000)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

}

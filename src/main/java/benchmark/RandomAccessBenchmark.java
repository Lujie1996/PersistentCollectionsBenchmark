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
import org.openjdk.jmh.infra.Blackhole;
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
public class RandomAccessBenchmark {

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(RandomAccessBenchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @State(Scope.Thread)
    public static class SampleData {

        Random rand = new Random();
        public List<Integer> randomIndices = new ArrayList<Integer>(10000);

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
                randomIndices.add(rand.nextInt(10000));

                pmap = pmap.assoc(i, i);
                cmap = cmap.put(i, i);
                vmap = vmap.put(i, i);
                pCollectionmap = pCollectionmap.plus(i, i);
                map.put(i, i);
            }
        }
    }

    @Benchmark
    public void randomAccess10KPaguroHashMap(SampleData data, Blackhole blackhole) {
        for (int i = 0; i < 10000; i++) {
            int index = data.randomIndices.get(i);
            blackhole.consume(data.pmap.get(index));
        }
    }

    @Benchmark
    public void randomAccess10KToCyclopsHashMap(SampleData data, Blackhole blackhole) {
        for (int i = 0; i < 10000; i++) {
            int index = data.randomIndices.get(i);
            blackhole.consume(data.cmap.get(index));
        }
    }

    @Benchmark
    public void randomAccess10KVavrHashMap(SampleData data, Blackhole blackhole) {
        for (int i = 0; i < 10000; i++) {
            int index = data.randomIndices.get(i);
            blackhole.consume(data.vmap.get(index));
        }
    }

    @Benchmark
    public void randomAccess10KPCollectionHashMap(SampleData data, Blackhole blackhole) {
        for (int i = 0; i < 10000; i++) {
            int index = data.randomIndices.get(i);
            blackhole.consume(data.pCollectionmap.get(index));
        }
    }

    @Benchmark
    public void randomAccess10KJavaHashMap(SampleData data, Blackhole blackhole) {
        for (int i = 0; i < 10000; i++) {
            int index = data.randomIndices.get(i);
            blackhole.consume(data.map.get(index));
        }
    }

}






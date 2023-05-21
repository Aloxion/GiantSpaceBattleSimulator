package gsbs.common.math;
/*
 ** KDTree.java by Julian Kent
 **
 ** Licenced under the  Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 **
 ** Licence summary:
 ** Under this licence you are free to:
 **      Share : copy and redistribute the material in any medium or format
 **      Adapt : remix, transform, and build upon the material
 **      The licensor cannot revoke these freedoms as long as you follow the license terms.
 **
 ** Under the following terms:
 **      Attribution:
 **            You must give appropriate credit, provide a link to the license, and indicate
 **            if changes were made. You may do so in any reasonable manner, but not in any
 **            way that suggests the licensor endorses you or your use.
 **      NonCommercial:
 **            You may not use the material for commercial purposes.
 **      ShareAlike:
 **            If you remix, transform, or build upon the material, you must distribute your
 **            contributions under the same license as the original.
 **      No additional restrictions:
 **            You may not apply legal terms or technological measures that legally restrict
 **            others from doing anything the license permits.
 **
 ** See full licencing details here: http://creativecommons.org/licenses/by-nc-sa/3.0/
 **
 ** For additional licencing rights (including commercial) please contact jkflying@gmail.com
 **
 */

import java.util.ArrayList;
import java.util.Arrays;

public abstract class KDTree<T> {

    // use a big bucketSize so that we have less node bounds (for more cache
    // hits) and better splits
    // if you have lots of dimensions this should be big, and if you have few small
    private static final int _bucketSize = 50;

    private final int _dimensions;
    private final Node root;
    private final ArrayList<Node> nodeList = new ArrayList<Node>();
    // the starting values for bounding boxes, for easy access
    private final double[] bounds_template;
    // one big self-expanding array to keep all the node bounding boxes so that
    // they stay in cache
    // node bounds available at:
    // low: 2 * _dimensions * node.index + 2 * dim
    // high: 2 * _dimensions * node.index + 2 * dim + 1
    private final ContiguousDoubleArrayList nodeMinMaxBounds;
    private int _nodes;
    // prevent GC from having to collect _bucketSize*dimensions*sizeof(double) bytes each
    // time a leaf splits
    private double[] mem_recycle;

    private KDTree(int dimensions) {
        _dimensions = dimensions;

        nodeMinMaxBounds = new ContiguousDoubleArrayList(1024 / 8 + 2 * _dimensions);
        mem_recycle = new double[_bucketSize * dimensions];

        bounds_template = new double[2 * _dimensions];
        Arrays.fill(bounds_template, Double.NEGATIVE_INFINITY);
        for (int i = 0, max = 2 * _dimensions; i < max; i += 2)
            bounds_template[i] = Double.POSITIVE_INFINITY;

        // and.... start!
        root = new Node();
    }

    static final double sqr(double d) {
        return d * d;
    }

    public int nodes() {
        return _nodes;
    }

    public int size() {
        return root.entries;
    }

    public int addPoint(double[] location, T payload) {

        Node addNode = root;
        // Do a Depth First Search to find the Node where 'location' should be
        // stored
        while (addNode.pointLocations == null) {
            addNode.expandBounds(location);
            if (location[addNode.splitDim] < addNode.splitVal)
                addNode = nodeList.get(addNode.lessIndex);
            else
                addNode = nodeList.get(addNode.moreIndex);
        }
        addNode.expandBounds(location);

        int nodeSize = addNode.add(location, payload);

        if (nodeSize % _bucketSize == 0)
            // try splitting again once every time the node passes a _bucketSize
            // multiple
            // in case it is full of points of the same location and won't split
            addNode.split();

        return root.entries;
    }

    public ArrayList<SearchResult<T>> nearestNeighbours(double[] searchLocation, int K) {

        K = Math.min(K, size());

        ArrayList<SearchResult<T>> returnResults = new ArrayList<SearchResult<T>>(K);

        if (K > 0) {
            IntStack stack = new IntStack();
            PrioQueue<T> results = new PrioQueue<T>(K, true);

            stack.push(root.index);

            int added = 0;

            while (stack.size() > 0) {
                int nodeIndex = stack.pop();
                if (added < K || results.peekPrio() > pointRectDist(nodeIndex, searchLocation)) {
                    Node node = nodeList.get(nodeIndex);
                    if (node.pointLocations == null)
                        node.search(searchLocation, stack);
                    else
                        added += node.search(searchLocation, results);
                }
            }

            double[] priorities = results.priorities;
            ArrayList<T> elements = results.elements;
            for (int i = 0; i < elements.size(); i++) { // forward (closest first)
                SearchResult<T> s = new SearchResult<T>(priorities[i], elements.get(i));
                returnResults.add(s);
            }
        }
        return returnResults;
    }

    public ArrayList<T> ballSearch(double[] searchLocation, double radius) {
        IntStack stack = new IntStack();
        ArrayList<T> results = new ArrayList<T>();

        stack.push(root.index);

        while (stack.size() > 0) {
            int nodeIndex = stack.pop();
            if (radius > pointRectDist(nodeIndex, searchLocation)) {
                Node node = nodeList.get(nodeIndex);
                if (node.pointLocations == null)
                    stack.push(node.moreIndex).push(node.lessIndex);
                else
                    node.searchBall(searchLocation, radius, results);
            }
        }
        return results;
    }

    public ArrayList<T> rectSearch(double[] mins, double[] maxs) {
        IntStack stack = new IntStack();
        ArrayList<T> results = new ArrayList<T>();

        stack.push(root.index);

        while (stack.size() > 0) {
            int nodeIndex = stack.pop();
            if (overlaps(mins, maxs, nodeIndex)) {
                Node node = nodeList.get(nodeIndex);
                if (node.pointLocations == null)
                    stack.push(node.moreIndex).push(node.lessIndex);
                else
                    node.searchRect(mins, maxs, results);
            }
        }
        return results;
    }

    abstract double pointRectDist(int offset, final double[] location);

    abstract double pointDist(double[] arr, double[] location, int index);

    boolean contains(double[] arr, double[] mins, double[] maxs, int index) {

        int offset = (index + 1) * mins.length;

        for (int i = mins.length; i-- > 0; ) {
            double d = arr[--offset];
            if (mins[i] > d | d > maxs[i])
                return false;
        }
        return true;
    }

    boolean overlaps(double[] mins, double[] maxs, int offset) {
        offset *= (2 * maxs.length);
        final double[] array = nodeMinMaxBounds.array;
        for (int i = 0; i < maxs.length; i++, offset += 2) {
            double bmin = array[offset], bmax = array[offset + 1];
            if (mins[i] > bmax | maxs[i] < bmin)
                return false;
        }

        return true;
    }

    public static class Euclidean<T> extends KDTree<T> {
        public Euclidean(int dims) {
            super(dims);
        }

        double pointRectDist(int offset, final double[] location) {
            offset *= (2 * super._dimensions);
            double distance = 0;
            final double[] array = super.nodeMinMaxBounds.array;
            for (int i = 0; i < location.length; i++, offset += 2) {

                double diff = 0;
                double bv = array[offset];
                double lv = location[i];
                if (bv > lv)
                    diff = bv - lv;
                else {
                    bv = array[offset + 1];
                    if (lv > bv)
                        diff = lv - bv;
                }
                distance += sqr(diff);
            }
            return distance;
        }

        double pointDist(double[] arr, double[] location, int index) {
            double distance = 0;
            int offset = (index + 1) * super._dimensions;

            for (int i = super._dimensions; i-- > 0; ) {
                distance += sqr(arr[--offset] - location[i]);
            }
            return distance;
        }
    }

    public static class Manhattan<T> extends KDTree<T> {
        public Manhattan(int dims) {
            super(dims);
        }

        double pointRectDist(int offset, final double[] location) {
            offset *= (2 * super._dimensions);
            double distance = 0;
            final double[] array = super.nodeMinMaxBounds.array;
            for (int i = 0; i < location.length; i++, offset += 2) {

                double diff = 0;
                double bv = array[offset];
                double lv = location[i];
                if (bv > lv)
                    diff = bv - lv;
                else {
                    bv = array[offset + 1];
                    if (lv > bv)
                        diff = lv - bv;
                }
                distance += (diff);
            }
            return distance;
        }

        double pointDist(double[] arr, double[] location, int index) {
            double distance = 0;
            int offset = (index + 1) * super._dimensions;

            for (int i = super._dimensions; i-- > 0; ) {
                distance += Math.abs(arr[--offset] - location[i]);
            }
            return distance;
        }
    }

    public static class WeightedManhattan<T> extends KDTree<T> {
        private double[] weights;

        public WeightedManhattan(int dims) {
            super(dims);
            weights = new double[dims];
            for (int i = 0; i < dims; i++)
                weights[i] = 1.0;
        }

        public void setWeights(double[] newWeights) {
            weights = newWeights;
        }

        double pointRectDist(int offset, final double[] location) {
            offset *= (2 * super._dimensions);
            double distance = 0;
            final double[] array = super.nodeMinMaxBounds.array;
            for (int i = 0; i < location.length; i++, offset += 2) {

                double diff = 0;
                double bv = array[offset];
                double lv = location[i];
                if (bv > lv)
                    diff = bv - lv;
                else {
                    bv = array[offset + 1];
                    if (lv > bv)
                        diff = lv - bv;
                }
                distance += (diff) * weights[i];
            }
            return distance;
        }

        double pointDist(double[] arr, double[] location, int index) {
            double distance = 0;
            int offset = (index + 1) * super._dimensions;

            for (int i = super._dimensions; i-- > 0; ) {
                distance += Math.abs(arr[--offset] - location[i]) * weights[i];
            }
            return distance;
        }
    }

    public static class SearchResult<S> {
        public double distance;
        public S payload;

        SearchResult(double dist, S load) {
            distance = dist;
            payload = load;
        }
    }

    // NB! This Priority Queue keeps things with the LOWEST priority.
    // If you want highest priority items kept, negate your values
    private static class PrioQueue<S> {

        ArrayList<S> elements;
        double[] priorities;
        private double minPrio;
        private int size;

        PrioQueue(int size, boolean prefill) {
            elements = new ArrayList<S>(size);
            priorities = new double[size];
            Arrays.fill(priorities, Double.POSITIVE_INFINITY);
            if (prefill) {
                minPrio = Double.POSITIVE_INFINITY;
                this.size = size;
            }
        }

        // uses O(log(n)) comparisons and one big shift of size O(N)
        // and is MUCH simpler than a heap --> faster on small sets, faster JIT

        void addNoGrow(S value, double priority) {
            int index = searchFor(priority);

            System.arraycopy(priorities, index, priorities, index + 1, size - index - 1);
            priorities[index] = priority;

            if (elements.size() == priorities.length)
                elements.remove(elements.size() - 1);
            elements.add(index, value);

            minPrio = priorities[size - 1];
        }

        int searchFor(double priority) {
            int i = size - 1;
            int j = 0;
            while (i >= j) {
                int index = (i + j) >>> 1;
                if (priorities[index] < priority)
                    j = index + 1;
                else
                    i = index - 1;
            }
            return j;
        }

        double peekPrio() {
            return minPrio;
        }
    }

    private static class ContiguousDoubleArrayList {
        double[] array;
        int size;

        ContiguousDoubleArrayList(int size) {
            this(new double[size]);
        }

        ContiguousDoubleArrayList(double[] data) {
            array = data;
        }

        ContiguousDoubleArrayList add(double[] da) {
            if (size + da.length > array.length)
                array = Arrays.copyOf(array, (array.length + da.length) * 2);

            System.arraycopy(da, 0, array, size, da.length);
            size += da.length;
            return this;
        }
    }

    private static class IntStack {
        int[] array;
        int size;

        IntStack() {
            this(64);
        }

        IntStack(int size) {
            this(new int[size]);
        }

        IntStack(int[] data) {
            array = data;
        }

        IntStack push(int i) {
            if (size >= array.length)
                array = Arrays.copyOf(array, (array.length + 1) * 2);

            array[size++] = i;
            return this;
        }

        int pop() {
            return array[--size];
        }

        int size() {
            return size;
        }
    }

    private class Node {

        // for accessing bounding box data
        // - if trees weren't so unbalanced might be better to use an implicit
        // heap?
        int index;

        // keep track of size of subtree
        int entries;

        // leaf
        ContiguousDoubleArrayList pointLocations;
        ArrayList<T> pointPayloads = new ArrayList<T>(_bucketSize);

        // stem
        // Node less, more;
        int lessIndex, moreIndex;
        int splitDim;
        double splitVal;

        Node() {
            this(new double[_bucketSize * _dimensions]);
        }

        Node(double[] pointMemory) {
            pointLocations = new ContiguousDoubleArrayList(pointMemory);
            index = _nodes++;
            nodeList.add(this);
            nodeMinMaxBounds.add(bounds_template);
        }

        void search(double[] searchLocation, IntStack stack) {
            if (searchLocation[splitDim] < splitVal)
                stack.push(moreIndex).push(lessIndex); // less will be popped
                // first
            else
                stack.push(lessIndex).push(moreIndex); // more will be popped
            // first
        }

        // returns number of points added to results
        int search(double[] searchLocation, PrioQueue<T> results) {
            int updated = 0;
            for (int j = entries; j-- > 0; ) {
                double distance = pointDist(pointLocations.array, searchLocation, j);
                if (results.peekPrio() > distance) {
                    updated++;
                    results.addNoGrow(pointPayloads.get(j), distance);
                }
            }
            return updated;
        }

        void searchBall(double[] searchLocation, double radius, ArrayList<T> results) {

            for (int j = entries; j-- > 0; ) {
                double distance = pointDist(pointLocations.array, searchLocation, j);
                if (radius >= distance) {
                    results.add(pointPayloads.get(j));
                }
            }
        }

        void searchRect(double[] mins, double[] maxs, ArrayList<T> results) {

            for (int j = entries; j-- > 0; )
                if (contains(pointLocations.array, mins, maxs, j))
                    results.add(pointPayloads.get(j));
        }

        void expandBounds(double[] location) {
            entries++;
            int mio = index * 2 * _dimensions;
            for (int i = 0; i < _dimensions; i++) {
                nodeMinMaxBounds.array[mio] = Math.min(nodeMinMaxBounds.array[mio], location[i]);
                mio++;
                nodeMinMaxBounds.array[mio] = Math.max(nodeMinMaxBounds.array[mio], location[i]);
                mio++;
            }
        }

        int add(double[] location, T load) {
            pointLocations.add(location);
            pointPayloads.add(load);
            return entries;
        }

        void split() {
            int offset = index * 2 * _dimensions;

            double diff = 0;
            for (int i = 0; i < _dimensions; i++) {
                double min = nodeMinMaxBounds.array[offset];
                double max = nodeMinMaxBounds.array[offset + 1];
                if (max - min > diff) {
                    double mean = 0;
                    for (int j = 0; j < entries; j++)
                        mean += pointLocations.array[i + _dimensions * j];

                    mean = mean / entries;
                    double varianceSum = 0;

                    for (int j = 0; j < entries; j++)
                        varianceSum += sqr(mean - pointLocations.array[i + _dimensions * j]);

                    if (varianceSum > diff * entries) {
                        diff = varianceSum / entries;
                        splitVal = mean;

                        splitDim = i;
                    }
                }
                offset += 2;
            }

            // kill all the nasties
            if (splitVal == Double.POSITIVE_INFINITY)
                splitVal = Double.MAX_VALUE;
            else if (splitVal == Double.NEGATIVE_INFINITY)
                splitVal = Double.MIN_VALUE;
            else if (splitVal == nodeMinMaxBounds.array[index * 2 * _dimensions + 2 * splitDim + 1])
                splitVal = nodeMinMaxBounds.array[index * 2 * _dimensions + 2 * splitDim];

            Node less = new Node(mem_recycle); // recycle that memory!
            Node more = new Node();
            lessIndex = less.index;
            moreIndex = more.index;

            // reduce garbage by factor of _bucketSize by recycling this array
            double[] pointLocation = new double[_dimensions];
            for (int i = 0; i < entries; i++) {
                System.arraycopy(pointLocations.array, i * _dimensions, pointLocation, 0, _dimensions);
                T load = pointPayloads.get(i);

                if (pointLocation[splitDim] < splitVal) {
                    less.expandBounds(pointLocation);
                    less.add(pointLocation, load);
                } else {
                    more.expandBounds(pointLocation);
                    more.add(pointLocation, load);
                }
            }
            if (less.entries * more.entries == 0) {
                // one of them was 0, so the split was worthless. throw it away.
                _nodes -= 2; // recall that bounds memory
                nodeList.remove(moreIndex);
                nodeList.remove(lessIndex);
            } else {

                // we won't be needing that now, so keep it for the next split
                // to reduce garbage
                mem_recycle = pointLocations.array;

                pointLocations = null;

                pointPayloads.clear();
                pointPayloads = null;
            }
        }
    }
}
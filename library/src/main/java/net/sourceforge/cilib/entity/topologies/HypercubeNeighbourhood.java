package net.sourceforge.cilib.entity.topologies;

import fj.Equal;
import fj.F;
import fj.data.List;

public class HypercubeNeighbourhood<E> extends Neighbourhood<E> {
    private int n = 5;

    @Override
    public List<E> f(final List<E> list, final E current) {
        final int index = list.elementIndex(Equal.<E>anyEqual(), current).orSome(-1);
        return List.range(0, n).map(new F<Integer, E>() {
            @Override
            public E f(Integer a) {
                return list.index((index ^ Double.valueOf(Math.pow(2, a)).intValue())%list.length());
            }
        });
    }

    public void setNeighbourhoodSize(int n) {
        this.n = n;
    }
}

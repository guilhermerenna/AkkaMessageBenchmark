package Cluster.message;

import java.io.Serializable;
import java.util.List;

public class CreationOrder implements Serializable{
    public final int nCacti;
    public final int nCreature;
    public final int nBackends;
    public final int periodo;
    public final List backends;

    public CreationOrder(int nCacti, int nCreature, int nBackends, int periodo, List backends){
        this.nCacti = nCacti;
        this.nCreature = nCreature;
        this.nBackends = nBackends;
        this.periodo = periodo;
        this.backends = backends;
    }
}

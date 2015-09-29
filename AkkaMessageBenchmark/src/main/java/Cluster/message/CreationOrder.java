package Cluster.message;

import java.io.Serializable;

public class CreationOrder implements Serializable{
    public final int nCacti;
    public final int nCreature;
    public final int nBackends;

    public CreationOrder(int nCacti, int nCreature, int nBackends){
        this.nCacti = nCacti;
        this.nCreature = nCreature;
        this.nBackends = nBackends;
    }
}

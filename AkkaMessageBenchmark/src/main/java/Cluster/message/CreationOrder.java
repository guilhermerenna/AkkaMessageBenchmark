package Cluster.message;

import java.io.Serializable;

public final class CreationOrder implements Serializable{
    public final int nCacti;
    public final int nCreature;

    public CreationOrder(int nCacti, int nCreature){
        this.nCacti = nCacti;
        this.nCreature = nCreature;
    }
}

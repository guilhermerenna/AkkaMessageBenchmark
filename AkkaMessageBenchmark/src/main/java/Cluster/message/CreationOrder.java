package Cluster.message;

import java.io.Serializable;

public class CreationOrder implements Serializable{
    public final int nCacti;
    public final int nCreature;

    public CreationOrder(int nCacti, int nCreature){
        this.nCacti = nCacti;
        this.nCreature = nCreature;
    }
}

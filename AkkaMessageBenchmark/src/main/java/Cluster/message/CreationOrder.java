package Cluster.message;

import java.io.Serializable;

public class CreationOrder implements Serializable{
    public final int nCacti;
    public final int nCreature;
    public final int nBackends;
    public final int periodo;

    public CreationOrder(int nCacti, int nCreature, int nBackends, int periodo){
        this.nCacti = nCacti;
        this.nCreature = nCreature;
        this.nBackends = nBackends;
        this.periodo = periodo;
    }
}

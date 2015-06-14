package AkkaMessageBenchmark;

import Stimuli.LuminousStimulusMessage;
import Stimuli.SpikeStimulusMessage;
import Stimuli.StimulusMessage;
import Stimuli.TouchStimulusMessage;
import akka.actor.UntypedActor;

/**
 *
 */
public class CactusActor extends UntypedActor {
    private String nome;

    /**
     * Construtor
     * @param nome Nome do ator
     */
    public CactusActor(String nome) {
        this.nome = nome;
    }

    /**
     * Evento disparado quando uma mensagem é recebida.
     * @param arg0 Mensagem do tipo OBJETO.
     * @throws Exception
     */
    @Override
    public void onReceive(Object arg0) throws Exception {
        if (arg0 instanceof LuminousStimulusMessage) {
            System.out.println(this.nome + "A can see something! " + ((LuminousStimulusMessage) arg0).getMessage() + "! Ref.: " + ((StimulusMessage) arg0).getSequenceNumber());
        } else if (arg0 instanceof TouchStimulusMessage) {
            System.out.println(this.nome + ": " + getSender().toString() + " toched me! Spiking it back! =P Ref.: " + ((StimulusMessage) arg0).getSequenceNumber());
            getSender().tell(new SpikeStimulusMessage("Spike sent "), this.getSelf());
        } else if (arg0 instanceof StimulusMessage) {
            System.out.println(this.nome+": unknown stimulus received.\n" + ((StimulusMessage) arg0).getMessage() + "\nDiscarding ref. " + ((StimulusMessage) arg0).getMessage());
        } else {
            throw new Exception("Message type not supported.");
        }
        Thread.sleep(3000);
        System.out.println("Woke up! Going for the next message.");
    }

}

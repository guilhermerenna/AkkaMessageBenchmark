package ArtificeMailbox.messageQueue;

import ArtificeMailbox.ReceiverMessage;
import ArtificeMailbox.SenderMessage;
import akka.actor.ActorRef;
import akka.dispatch.Envelope;
import akka.dispatch.MessageQueue;
import ArtificeMailbox.MyUnboundedMessageQueueSemantics;

import javax.sound.midi.Receiver;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GroupedMessageQueue implements MessageQueue,
        MyUnboundedMessageQueueSemantics {
    private SenderMessage senderMessage;

    private final Queue<Envelope> queue =
            new ConcurrentLinkedQueue<Envelope>();

    public void enqueue(ActorRef receiver, Envelope handle) {
        if (handle.message() instanceof SenderMessage) {
            this.senderMessage = (SenderMessage) handle.message();
            // TODO: descobrir forma de substituir this.senderMessage por referencia do respectivo ator
            queue.offer(new Envelope(new ReceiverMessage(this.senderMessage.getSender(), this.senderMessage.getReceiver(), this.senderMessage.getStimulusValues(), this.senderMessage.getSendingTime(), System.currentTimeMillis()),null));
        } else {
            System.out.println("Mensagem nao suportada: "+handle.message().getClass().toString()+"\nEsperado tipo ReceiverMessage.");
        }
    }

    public Envelope dequeue() {
        return queue.poll();
    }

    public int numberOfMessages() {
        return queue.size();
    }

    public boolean hasMessages() {
        return !queue.isEmpty();
    }

    public void cleanUp(ActorRef owner, MessageQueue deadLetters) {
        for (Envelope handle : queue) {
            deadLetters.enqueue(owner, handle);
        }
    }
}
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
            System.out.println("Timestamped message arrived at mailbox.");
            queue.offer(handle);
        } else if (handle.message() instanceof String) {
            queue.offer(handle);
        }
        else {
            System.out.println("Mensagem nao suportada: "+handle.message().getClass().toString()+" de "+handle.sender().path()+"\nEsperado tipo SenderMessage.");
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
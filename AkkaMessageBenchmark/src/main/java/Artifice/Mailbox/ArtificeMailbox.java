package Artifice.Mailbox;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.dispatch.MailboxType;
import akka.dispatch.MessageQueue;
import akka.dispatch.ProducesMessageQueue;
import com.typesafe.config.Config;
import Artifice.Mailbox.messageQueue.GroupedMessageQueue;
import scala.Option;

public class ArtificeMailbox implements MailboxType,
        ProducesMessageQueue<GroupedMessageQueue> {

    // This constructor signature must exist, it will be called by Akka
    public ArtificeMailbox(ActorSystem.Settings settings, Config config) {
        // put your initialization code here
    }

    // The create method is called to create the MessageQueue
    public MessageQueue create(Option<ActorRef> owner, Option<ActorSystem> system) {
        return new GroupedMessageQueue();
    }
}
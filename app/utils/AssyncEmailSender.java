package utils;

import java.util.concurrent.TimeUnit;

import play.libs.Akka;
import akka.util.Duration;

import com.typesafe.plugin.MailerAPI;
import com.typesafe.plugin.MailerPlugin;

public class AssyncEmailSender{
  private Runnable sender = null;
  
  public AssyncEmailSender(String subject, String body, String from, String replyTo, String to) {
    sender = new EmailSender(subject, body, from, replyTo, to);
  }
  
  public void send() {
    Akka.system().scheduler().scheduleOnce(
        Duration.create(0, TimeUnit.SECONDS),
        sender
      );
  }
  
  private class EmailSender implements Runnable {
    private String  subject,
            body,
            from,
            replyTo, 
            to;
    
    
    public EmailSender(String subject, String body, String from, String replyTo, String to) {
      this.subject = subject;
      this.body = body;
      this.from = from;
      this.replyTo = replyTo;
      this.to = to;
    }


    public void run() {
      MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
      mail.setSubject( subject );
      mail.addRecipient(to);
      mail.addFrom(from);
      mail.setReplyTo(replyTo);
      mail.send( body );
    }
  }
}


package com.andrey_sonido.russiancoins.helpers;

import com.andrey_sonido.russiancoins.JSSEProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by Sonido on 24.02.2016.
 */
public class GMailSender extends javax.mail.Authenticator {
    private static ExecutorService mExecService = Executors.newCachedThreadPool();
    private String mailhost = "smtp.gmail.com";
    private String user;
    private String password;
    private Session session;

    static {
        Security.addProvider(new JSSEProvider());
    }

    public GMailSender(String user, String password) {
        this.user = user;
        this.password = password;

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", mailhost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "true");

        session = Session.getDefaultInstance(props, this);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    public synchronized void sendMail(final SendListener listener, final String subject, final String body, final String sender, final String recipients) throws Exception {
        mExecService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    MimeMessage message = new MimeMessage(session);
                    DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
                    message.setSender(new InternetAddress(sender));
                    message.setSubject(subject);
                    message.setDataHandler(handler);
                    if (recipients.indexOf(',') > 0)
                        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
                    else
                        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
                    Transport.send(message);
                    listener.OnSendComplete("???????????? ????????????????????");
                } catch (AuthenticationFailedException e) {
                    listener.OnSendError("???????????? ??????????????????????");
                } catch (Exception e) {
                    //Log.e("SendMail", e.getMessage(), e);
                    listener.OnSendError("???????????? ???????????????? ????????????");
                }
            }
        });
    }

    public class ByteArrayDataSource implements DataSource {
        private byte[] data;
        private String type;

        public ByteArrayDataSource(byte[] data, String type) {
            super();
            this.data = data;
            this.type = type;
        }

        public ByteArrayDataSource(byte[] data) {
            super();
            this.data = data;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContentType() {
            if (type == null)
                return "application/octet-stream";
            else
                return type;
        }

        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(data);
        }

        public String getName() {
            return "ByteArrayDataSource";
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }
    }

    public interface SendListener {
        void OnSendComplete(Object result);

        void OnSendError(String error);
    }
}

package com.daniil.otp.service;

import com.daniil.otp.smpp.SmppProperties;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.smpp.TCPIPConnection;
import org.smpp.Session;
import org.smpp.pdu.*;
import org.smpp.util.ByteBuffer;

@Service
public class SmppService {

    private final SmppProperties properties;

    @Autowired
    public SmppService(SmppProperties properties) {
        this.properties = properties;
    }

    public void sendSms(String phoneNumber, String messageText) {
        Session session = null;

        try {
            TCPIPConnection connection = new TCPIPConnection(properties.getHost(), properties.getPort());
            connection.setReceiveTimeout(20000); // таймаут получения
            session = new Session(connection);

            BindRequest bindRequest = new BindTransmitter();
            bindRequest.setSystemId(properties.getSystemId());
            bindRequest.setPassword(properties.getPassword());
            bindRequest.setSystemType(properties.getSystemType());
            bindRequest.setInterfaceVersion((byte) 0x34);
            bindRequest.setAddressRange((byte) properties.getAddrTon(), (byte) properties.getAddrNpi(), "");
            bindRequest.setAddressRange("");

            BindResponse bindResponse = session.bind(bindRequest);
            System.out.println("Bind response: " + bindResponse.debugString());

            SubmitSM request = new SubmitSM();

            request.setSourceAddr(properties.getSourceAddr());
            request.setDestAddr(phoneNumber);
            request.setShortMessage(messageText);

            SubmitSMResp response = session.submit(request);
            System.out.println("Message sent. Message ID: " + response.getMessageId());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                try {
                    session.unbind();
                    session.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

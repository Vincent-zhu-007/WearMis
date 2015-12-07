package com.sg.util;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class SendMqttUtil {
	public static void send(String topic, String content, String broker){
        int qos = 2;
        String clientId = "WearMisMqtt";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient mqttClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            mqttClient.connect(connOpts);
            
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            message.setRetained(false);
            
            mqttClient.publish(topic, message);
            
            mqttClient.disconnect();
        } catch(MqttException me) {
            me.printStackTrace();
        }
	}
}

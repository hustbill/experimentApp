package actor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;
import java.net.URL;

import akka.actor.UntypedActor;

public class FileReadActor extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Exception {

		if (message instanceof String) {
			String fileName = (String) message;	
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(Thread.currentThread()
								.getContextClassLoader().getResource(fileName)
								.openStream()));
				String line = null;
		
				while ((line = reader.readLine()) != null) {
					//System.out.println("File contents->" + line);
					//getSender().tell(line, getSelf());
					getSender().tell(line, null);
				}
				System.out.println("All lines send !");
				// send the EOF message..
				//getSender().tell(String.valueOf("EOF"), getSelf());
				getSender().tell(String.valueOf("EOF"), null);
				
			} catch (IOException x) {
				System.err.format("IOException: %s%n", x);
			}
		} else
			unhandled(message);
	}
}
package m2u.el.camel.processor;

import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class DBWriteProcessor implements Processor{

	@Override
	public void process(Exchange exchange) throws Exception {
		Message msg = exchange.getIn();
		String line = (String)msg.getBody(String.class);
		
		String[] emp = line.split(Pattern.quote(","));
		
		String query;
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		query = "insert into Employee values (\'" + uuid + "\'" + ", \'" + emp[0].trim() + "\', " + emp[1].trim() + ", \'" + emp[2].trim() + "\')";  
		msg.setBody(query);
		System.out.println("query : " + query);
	}

}

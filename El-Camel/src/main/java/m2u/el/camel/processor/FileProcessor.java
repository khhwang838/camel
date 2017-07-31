package m2u.el.camel.processor;

import java.util.ArrayList;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class FileProcessor implements Processor{

	@Override
	public void process(Exchange exchange) throws Exception {
		Message msg = exchange.getIn();
		String line = (String)msg.getBody(String.class);
		
//		line = new StringBuffer(line).reverse().toString();
		line += System.lineSeparator();

		msg.setBody(line);
		System.out.println("line : " + line);
	}

}

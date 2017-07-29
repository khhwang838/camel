package m2u.el.camel.domain;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class LineProcessor implements Processor{

	@Override
	public void process(Exchange exchange) throws Exception {
		Message msg = exchange.getIn();
		String line = (String)msg.getBody(String.class);
		
		line = new StringBuffer(line).reverse().toString();
		line += System.lineSeparator();

		msg.setBody(line);
		System.out.println("line : " + line);
	}

}

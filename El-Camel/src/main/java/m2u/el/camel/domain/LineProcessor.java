package m2u.el.camel.domain;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class LineProcessor implements Processor{

	int lineNum;
	
	@Override
	public void process(Exchange exchange) throws Exception {
		Message msg = exchange.getIn();
		String line = (String)msg.getBody(String.class);
		System.out.println("line : " + line);
	}

}

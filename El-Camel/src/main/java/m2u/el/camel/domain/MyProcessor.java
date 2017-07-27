package m2u.el.camel.domain;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

public class MyProcessor implements Processor{

	@Override
	public void process(Exchange exchange) throws Exception {
		Message msg = exchange.getIn();
		String body = (String)msg.getBody();
		System.out.println("body : " + body);
	}

}

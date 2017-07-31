package m2u.el.camel.processor;

import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class DBReadProcessor implements Processor{

	@Override
	public void process(Exchange exchange) throws Exception {
		Message msg = exchange.getIn();
		List<Map<String, Object>> data = msg.getBody(List.class);

		// do filtering...or something else...
		
		StringBuffer sb = new StringBuffer();
		
		for ( Map<String, Object> rowData : data ) {
			for ( String key : rowData.keySet() ) {
				sb.append(rowData.get(key)).append(", ");
			}
			sb.append(System.lineSeparator());
		}
		msg.setBody(sb.toString());
	}

}

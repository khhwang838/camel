package m2u.el.camel;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sql.DataSource;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.JndiRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import m2u.el.camel.processor.DBReadProcessor;
import m2u.el.camel.processor.DBWriteProcessor;
import m2u.el.camel.processor.FileProcessor;

/**
 * Handles requests for the application home page.
 */
@Controller
public class EyelinkCamelController {

	private static final Logger logger = LoggerFactory.getLogger(EyelinkCamelController.class);

	final AtomicBoolean flag = new AtomicBoolean(true);

	@Autowired
	DataSource dataSource;
	
	/**
	 * Simply selects the home view to render by returning its name.
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public void home(Locale locale, Model model) throws Exception {
		logger.info("Welcome home! The client locale is {}.", locale);
	}

	public static void main(String[] args) throws Exception {
//		fileCopyTest();
//		fileModifyCopyTest();
//		fileToDB_JDBC_Test();
//		dbToFile_JDBC_Test();
//		httpToHttp_Test();
	}

	private static void manualRequestTest() {
		
		CamelContext context = new DefaultCamelContext();
		ProducerTemplate pt = context.createProducerTemplate();
		
		Exchange out = pt.request("http:localhost:9080/ProgramD/GetBotResponse?input=로또",
				new Processor() {
					@Override
					public void process(Exchange arg0) throws Exception {
						System.out.println("processing message......2222");
					}
				});

		String resp = out.getOut().getBody(String.class);
		System.out.println("resp : " + resp);
	}
	
	private static void httpToHttp_Test() throws InterruptedException {

		logger.info("Http(ELBOT) to Http(Program D) test started ...");

		CamelContext context = new DefaultCamelContext();

		try {
			context.addRoutes(new RouteBuilder() {
				
				@Override
				public void configure() throws Exception {
					from("jetty:http://localhost:10080/camel")
					.to("netty4-http:http://localhost:9080/ProgramD/GetBotResponse?bridgeEndpoint=true&throwExceptionOnFailure=false")
					;
				}
			});
			context.setHandleFault(false);
			context.start();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		Thread.currentThread().join();
		logger.info("thread joined");
	}
	
	private static void dbToFile_JDBC_Test() {
		logger.info("File to DB test started ...");

		String configLocation = "D:\\kihyun\\git\\camel\\El-Camel\\src\\main\\webapp\\WEB-INF\\spring\\appServlet\\servlet-context.xml";
		ApplicationContext ac = new FileSystemXmlApplicationContext(configLocation);
		DataSource ds = (DataSource)ac.getBean("dataSource");
		
		JndiRegistry reg = new JndiRegistry(true);
		reg.bind("dataSource", ds);

		final CamelContext context = new DefaultCamelContext(reg);
		final DBReadProcessor dbReadProcessor = new DBReadProcessor();
		
		try {
			context.addRoutes(new RouteBuilder() {
				public void configure() {
					from("timer:employee?period=20000")
					.setBody(constant("select * from employee order by name"))
					.to("jdbc:dataSource")
					.process(dbReadProcessor)
					.to("file:/d:/kihyun/z99. temp/camel/to?charset=utf-8&fileName=m2u-employee-${date:now:yyyyMMdd}.txt");
				}
			});
			context.start();
			Thread.currentThread().join();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private static void fileToDB_JDBC_Test() {

		logger.info("File to DB test started ...");

		String configLocation = "D:\\kihyun\\git\\camel\\El-Camel\\src\\main\\webapp\\WEB-INF\\spring\\appServlet\\servlet-context.xml";
		ApplicationContext ac = new FileSystemXmlApplicationContext(configLocation);
		DataSource ds = (DataSource)ac.getBean("dataSource");
		
		JndiRegistry reg = new JndiRegistry(true);
		reg.bind("dataSource", ds);

		final CamelContext context = new DefaultCamelContext(reg);
		final DBWriteProcessor dbProcessor = new DBWriteProcessor();
		
		try {
			context.addRoutes(new RouteBuilder() {
				public void configure() {
					from("file:/d:/kihyun/z99. temp/camel/from?antInclude=*.txt&initialDelay=3000&delay=5000&noop=true")
					.split()
					.tokenize(System.lineSeparator())
					.process(dbProcessor)
					.to("jdbc:dataSource");
				}
			});
			context.start();
			Thread.currentThread().join();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	private static void fileCopyTest() throws Exception {

		logger.info("Copying file test started !!!");

		CamelContext context = new DefaultCamelContext();

		context.addRoutes(new RouteBuilder() {
			public void configure() {
				from("file:/d:/kihyun/z99. temp/camel/from").to("file:/d:/kihyun/z99. temp/camel/to");
			}
		});
		context.start(); // start하면 계속 watch하고 있다가 파일이 추가될 경우 자동으로 move함.
//		Thread.sleep(10000);
//		context.stop();

		logger.info("Copying file test finished !!!");
	}

	private static void fileModifyCopyTest() throws Exception {

		logger.info("Modifying and copying file test started !!!");

		final CamelContext context = new DefaultCamelContext();
		final FileProcessor lineProcessor = new FileProcessor();
		
		context.addRoutes(new RouteBuilder() {
			public void configure() {
//				from("file:/d:/kihyun/z99. temp/camel/from/*.txt")
				from("file:/Users/KH/Documents/temp/from?antInclude=*.txt&initialDelay=3000&delay=5000&noop=true")
				.split()
				.tokenize(System.lineSeparator())
				.process(lineProcessor)
				.to("file:/Users/KH/Documents/temp/to?fileExist=Append");
//				.to("file:/d:/kihyun/z99. temp/camel/to");
			}
		});
		context.start(); // start하면 계속 watch하고 있다가 파일이 추가될 경우 자동으로 move함.
		Thread.currentThread().join();
		logger.info("Modifying and copying file test finished !!!");
	}
}

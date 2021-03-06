/*
 *
 *  * Copyright 2002-2011 the original author or authors, or Red-Black IT Ltd, as appropriate.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package rewards.messaging.server;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StartServer {
	public static void main(String[] args) throws IOException {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "rewards/messaging/server/infrastructure-config.xml",
			"rewards/messaging/server/jms-rewards-config.xml",
			"rewards/messaging/server/jmx-config.xml"
		);
		System.out.println("Started server, press Enter to stop");
		System.in.read();
		// ensure a proper shutdown of ActiveMQ and Derby
		context.close();
		try {
			DriverManager.getConnection("jdbc:derby:;shutdown=true");
		} catch (SQLException e) {
			// expected, indicates successful shutdown
			System.out.println(e.getMessage());
		}
		System.out.println("Server is shut down");
        System.exit(0);
	}
}

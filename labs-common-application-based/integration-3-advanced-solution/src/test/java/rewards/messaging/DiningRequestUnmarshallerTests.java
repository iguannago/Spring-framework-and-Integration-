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

package rewards.messaging;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.xml.transform.stream.StreamSource;

import rewards.Dining;

import java.io.File;
import java.io.IOException;

public class DiningRequestUnmarshallerTests extends UnmarshallingTestBase {

	DiningRequestUnmarshaller unmarshaller = new DiningRequestUnmarshaller();

    /**
     * Template method for doing the unmarshalling we want to test
     *
     * @return
     * @throws java.io.IOException
     */
    @Override
    protected Object doUnmarshallDining(File xmlFile) throws IOException {
        return (Dining) unmarshaller.unmarshal(new StreamSource(xmlFile));
    }
}

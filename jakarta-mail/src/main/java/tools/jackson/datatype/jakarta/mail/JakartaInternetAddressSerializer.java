/*
 * Copyright 2021 Vendorflow, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tools.jackson.datatype.jakarta.mail;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;

import tools.jackson.databind.SerializerProvider;
import tools.jackson.databind.ser.std.StdScalarSerializer;

import jakarta.mail.internet.InternetAddress;

/**
 * @author Christopher Smith
 */
public class JakartaInternetAddressSerializer extends StdScalarSerializer<InternetAddress>
{
    public JakartaInternetAddressSerializer() {
        super(InternetAddress.class);
    }

    @Override
    public void serialize(InternetAddress value, JsonGenerator gen, SerializerProvider provider)
        throws JacksonException
    {
        gen.writeString(value.toString());
    }
}
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

import static tools.jackson.core.JsonToken.VALUE_STRING;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdScalarDeserializer;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

/**
 * @author Christopher Smith
 */
public class JakartaInternetAddressDeserializer extends StdScalarDeserializer<InternetAddress>
{
    public JakartaInternetAddressDeserializer() {
        super(InternetAddress.class);
    }

    @Override
    public InternetAddress deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException
    {
        if (p.currentToken() != VALUE_STRING) {
            return (InternetAddress) ctxt.handleUnexpectedToken(InternetAddress.class, p);
        }

        final String address = p.getString();
        try {
            return new InternetAddress(address);
        } catch (AddressException e) {
            return (InternetAddress) ctxt.handleWeirdStringValue(InternetAddress.class, address,
                     e.getMessage());
        }
    }
}
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

package com.fasterxml.jackson.datatype.jakarta.mail;

import tools.jackson.databind.module.SimpleModule;

import jakarta.mail.internet.InternetAddress;

/**
 * @author Christopher Smith
 */
public class JakartaMailModule extends SimpleModule
{
    private static final long serialVersionUID = 1L;

    public JakartaMailModule() {
        super(PackageVersion.VERSION);

        addSerializer(new JakartaInternetAddressSerializer());
        addDeserializer(InternetAddress.class, new JakartaInternetAddressDeserializer());
    }
}

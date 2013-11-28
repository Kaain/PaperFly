/*
 * Copyright (C) 2013 Michael Koppen, Christoph Ott, Andy Klay
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.fhb.mi.paperfly.service;

/**
 * Exception which is thrown in RestConsumerService
 *
 * @author Andy Klay (klay@fh-brandenburg.de)
 *
 */
public class RestConsumerException extends Exception {

    public final static String INTERNAL_SERVER_MESSAGE="Internal server exception. The service call did not succeed.";
    public final static String INVALID_INPUT_MESSAGE="Invalid JSON/XML input";

    public RestConsumerException(String message){
        super(message);
    }

}

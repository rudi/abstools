/** 
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package org.abs_models.frontend.parser;

public class SyntaxError extends ParserError {

    public SyntaxError(final String message) {
        this(message, 0, 0);
    }

    public SyntaxError(final String message, int lineNumber, int columnNumber) {
        super(message, lineNumber, columnNumber);
    }
}

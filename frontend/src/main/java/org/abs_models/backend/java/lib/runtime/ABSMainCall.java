/** 
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package org.abs_models.backend.java.lib.runtime;

public class ABSMainCall extends AbstractAsyncCall<ABSObject> {

    public ABSMainCall(ABSObject target) {
        super(null, target);
    }

    @Override
    public Object execute() {
        return target.run(); 
    }

    @Override
    public String methodName() {
        return "main block";
    }

}

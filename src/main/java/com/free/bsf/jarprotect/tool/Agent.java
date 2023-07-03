package com.free.bsf.jarprotect.tool;

import com.free.bsf.jarprotect.core.AgentTransformer;
import com.free.bsf.jarprotect.core.base.Context;

import java.lang.instrument.Instrumentation;

public class Agent {
    public static void premain(String args, Instrumentation inst) {
        if(inst!=null) {
            Context.Default=new Context(null);
            AgentTransformer tran = new AgentTransformer();
            inst.addTransformer(tran);
        }
    }
}

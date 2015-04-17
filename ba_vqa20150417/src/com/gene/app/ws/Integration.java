package com.gene.app.ws;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

    public class Integration extends Application {
         public Set<Class<?>> getClasses() {
             Set<Class<?>> s = new HashSet<Class<?>>();
             s.add(UpdateStudyInBudget.class);
             return s;
         }
    }
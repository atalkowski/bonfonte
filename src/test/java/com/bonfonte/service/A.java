package com.bonfonte.service;

import java.util.List;

public abstract class A {
    public abstract A doIt() throws Exception;
   
    public abstract List<? extends A> doThese();
 
    public abstract List<? super A> doThose();
    
}

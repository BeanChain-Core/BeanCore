package com.beanpack.CENdev;

import org.junit.jupiter.api.Test;

import com.beanpack.CENdev.BaseContract;
import com.beanpack.TXs.CENCALL;

import static org.junit.jupiter.api.Assertions.*;

import org.iq80.leveldb.DB;

public class BaseContractTest {

    static class DummyContract implements BaseContract {
        String contractName; //hardcode this
        String devKey; //this is how contracts find their info 

        boolean called = false;
        String methodReceived = null;
        boolean initialized = false;

        @Override
        public void loadIndentity(DB db, String contractName, String devKey){

        }

        @Override
        public void init() {
            initialized = true;
        }

        @Override
        public void execute(CENCALL call) {
            called = true;
            methodReceived = call.getMethod();
        }
    }

    @Test
    public void testExecuteMethodIsCalled() {
        DummyContract contract = new DummyContract();

        // Simulate initialization
        contract.init();
        assertTrue(contract.initialized, "init() should have been called.");

        // Create a dummy CENCALL
        CENCALL call = new CENCALL();
        call.setMethod("testMethod");

        contract.execute(call);

        // Assertions
        assertTrue(contract.called, "execute(...) should have been called.");
        assertEquals("testMethod", contract.methodReceived, "Method name should match the test input.");
    }
}


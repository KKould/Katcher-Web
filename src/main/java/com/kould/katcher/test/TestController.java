package com.kould.katcher.test;

import com.kould.katcher.annotation.Controller;
import com.kould.katcher.annotation.Mapping;
import com.kould.katcher.status.HttpMethod;

import java.util.ArrayList;
import java.util.List;

@Controller(uri = "/test")
public class TestController {
    class Data {
        int data1 ;
        int data2 ;
        int data3 ;
        int data4 ;

        public Data(int data1, int data2, int data3, int data4) {
            this.data1 = data1;
            this.data2 = data2;
            this.data3 = data3;
            this.data4 = data4;
        }

        public int getData1() {
            return data1;
        }

        public void setData1(int data1) {
            this.data1 = data1;
        }

        public int getData2() {
            return data2;
        }

        public void setData2(int data2) {
            this.data2 = data2;
        }

        public int getData3() {
            return data3;
        }

        public void setData3(int data3) {
            this.data3 = data3;
        }

        public int getData4() {
            return data4;
        }

        public void setData4(int data4) {
            this.data4 = data4;
        }
    }

    @Mapping(uri = "/test", method = HttpMethod.GET)
    public Object test(int args1,int args2, int args3, int args4) {
        return new Data(args1, args2, args3, args4);
    }
}

/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2015 All Rights Reserved.
 */
package class_overload;
/**
 * 
 * @author kezhu.wukz<kezhu.wukz@alipay.com>
 * @version $Id: AA.java, v 0.1 2015年12月11日 下午11:25:30 kezhu.wukz<kezhu.wukz@alipay.com> Exp $
 */

public class CC extends BB {

    /** 
     * @see CC#print(java.lang.Object)
     */
    public void print(String o) {
        System.out.println("CC");
    }

    public static void main(String[] args) {
        AA aa = new CC();
        aa.print("string");

        BB bb = new CC();
        bb.print("string");

        CC cc = new CC();
        cc.print("string");
    }

}

interface AA {
    void print(Object o);

}

class BB implements AA {

    /** 
     * @see CC#print(java.lang.Object)
     */
    @Override
    public void print(Object o) {
        System.out.println("BB");
    }

}

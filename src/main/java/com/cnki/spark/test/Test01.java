package com.cnki.spark.test;

public class Test01 {

    public static void main(String[] args) {


        String strError =
                "    </action>\n" +
                "    <end name=\"End\"/>\n" +
                "</workflow-app>\n";
        
        String strYes =
                "    </action>\n" +
                "    <end name=\"End\"/>\n" +
                "</workflow-app>\n";

        System.out.println(strYes.equals(strError));

        for (int i = 0; i < strYes.length(); i++){
            System.out.print(strError.charAt(i));
            if(!(strYes.charAt(i)+"").equals(strError.charAt(i)+"")){
                System.out.println(i);
                System.out.println(strYes.charAt(i));
                System.out.println(strError.charAt(i));
            }

        }

    }

}

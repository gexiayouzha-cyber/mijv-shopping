package com.gexiayouzha.mijvshopping;

import java.io.FileWriter;

public class GenerateCSV {
    public static void main(String[] args) throws Exception {
        FileWriter writer = new FileWriter("C:\\ProgramData\\MySQL\\MySQL Server 8.0\\Uploads\\orders.csv"
                );
        for(long i = 50; i <= 1000000; i++){
            long userId = (long)(Math.random()*100000);
            String status;
            int random = (int)(Math.random()*3);
            if(random==0){
                status="W";
            }else if(random==1){
                status="P";
            }else{
                status="F";
            }
            writer.write(
                    i+","+
                            userId+","+
                            status+","+
                            "2026-01-01 10:00:00,"+
                            "99.9\n"
            );
        }
        writer.close();
    }
}

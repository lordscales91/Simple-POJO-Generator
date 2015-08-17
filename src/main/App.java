package main;

import util.database.PojoDBGenerator;
import util.xls.PojoXlsGenerator;

public class App{
	
	
    public static void main(String[] args) throws Exception {
        String generator = null;
        if (args.length >= 2 && args.length % 2 == 0) {
            if (args[0].startsWith("-")) {
                String cmd = args[0];
                String param = args[1];

                if (cmd.equals("-gen")) {
                    generator = param;
                }
            }

            generator = generator.toLowerCase();
            String[] newArgs = new String[args.length - 2];
            for(int i = 2;i < args.length;i++){
                newArgs[i - 2] = args[i];
            }
            if(generator.equals("db")){
                System.out.println("Go to PojoDBGenerator");
                PojoDBGenerator.main(newArgs);
            }else if(generator.equals("xlsx")){
                System.out.println("Go to PokoXlsGenerator");
                PojoXlsGenerator.main(newArgs);
            }else{
            	System.out.println("Parameter should be 'db' or 'xlsx'");
            }
        }


    }

}
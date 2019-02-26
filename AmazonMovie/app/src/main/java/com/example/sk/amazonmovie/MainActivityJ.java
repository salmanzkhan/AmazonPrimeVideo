package com.example.purva.amazonmovie;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by purva on 4/8/2018.
 */

public class MainActivityJ extends Activity{

    EditText stringName;
    Button subButton;
    TextView textView;
    TextView recomOut;
    TextView results;
    String out = "";

    Button subButton2;
    EditText recom;
    EditText recom_output;
    TextView text_v;
    String[] testDoc =new String[401];
    String Mname = null;
    String rating = null;
    String review = "";
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stringName = (EditText) findViewById(R.id.commentId);
        recomOut = (TextView)findViewById(R.id.recommendID);
        subButton = (Button) findViewById(R.id.buttonR);
       // textView = (TextView) findViewById(R.id.commentId);

        subButton2 = (Button) findViewById(R.id.button2);

        recom = (EditText) findViewById(R.id.editText4);
        recom_output = (EditText) findViewById(R.id.editPass);
        text_v=(TextView) findViewById(R.id.propertyID);
        results=(TextView) findViewById(R.id.resultID);

        subButton2.setOnClickListener(new  View.OnClickListener(){

            @Override
            public  void  onClick(View view){
                TF_IDF2 tfIdf;
                 out="";
                String output="Recommending Similar types of movies \n";
                try{
                    String k= recom.getText().toString();
                    tfIdf = readRecom();
                    for(int i=0; i< tfIdf.docs.size(); i++) {
                        if(testDoc[i].equals(k)) {
                            int count=0;
                            ArrayList<Double> m= new ArrayList<>();
                            for (int j =1; j < tfIdf.docs.size(); j++) {
                                double g = tfIdf.getSimilarity(i, j);
                                if(g>0.1 && g<1 && count<3) {
                                    count++;
                                    output += testDoc[j];
                                    output += "\n";

                                }
                            }
                        }

                    }
                    if(output.isEmpty()){
                        recomOut.setText("No related movies found");
                    }
                    else {
                        recomOut.setText(output);
                    }

                }
               catch (IOException e) {
                    e.printStackTrace();
                }



                NBClassifierB obj1;

                try{

                    obj1 = readData();
                    if(review.isEmpty()){
                        text_v.setText("no review");
                    }
                    else {
                        int answer = obj1.classfiy(review);
                        //System.out.print("Result - " + answer);
                        if (answer == 1) {
                            out += "\n Review of movie :negative review";
                        } else {

                            out += "\n Review of movie:positive review";
                        }
                        text_v.setText(out);
                    }
                }
                catch(Exception e){

                }






            }
        });




        subButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                NBClassifierB obj;
                try {

                    obj = readData();
                    int answer = obj.classfiy(stringName.getText().toString());
                    //System.out.print("Result - " + answer);
                    if(answer ==1) {
                        results.setText(" You have entered a  negative review");
                    }
                    else{

                        results.setText("You have entered a positive review");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });


    }


    public TF_IDF2 readRecom() throws IOException{



        File csvFile = new File ("/storage/sdcard1/AmazonR.csv");


        BufferedReader br2 = null;
        String line = "";
        String cvsSplitBy = ",";
        String[] country = null;
        int l=0;

        br2 = new BufferedReader(new FileReader(csvFile));

        while ((line = br2.readLine()) != null) {

            country = line.split(cvsSplitBy);
            testDoc[l]= country[1].trim().toString().replaceAll("^\"|\"$", "");

            l++;
        }

       TF_IDF2 tfIdf = new TF_IDF2(testDoc);

       return  tfIdf;
    }




    public NBClassifierB readData() throws IOException {
        String[] trainDocs = new String[10000];
        int[] trainLabels = new int[10000];
        int numClass = 2;

        //File Pfile = new File("app/sampledata/postive.txt");
        //File Nfile = new File("app/sampledata/negative.txt");

        File sdcard = Environment.getExternalStorageDirectory();
        File Pfile = new File("/storage/sdcard1/postive.txt");

        File Nfile = new File("/storage/sdcard1/negative.txt");


        File csvFile = new File("/storage/sdcard1/AmazonR.csv");



        BufferedReader br = new BufferedReader(new FileReader(Pfile));
        BufferedReader br1 = new BufferedReader(new FileReader(Nfile));
        String st;
        int i=0;
        while ((st = br.readLine()) != null) {
            if(st != null) {
                trainDocs[i]= st;
                trainLabels[i]=0;
                i++;
            }
        }

        while ((st = br1.readLine()) != null) {
            if(st != null) {
                trainDocs[i]= st;
                trainLabels[i]=1;
                i++;
            }
        }

       // String csvFile = "app/sampledata/AmazonR.csv";
        BufferedReader br3 = null;
        String line = "";
        String cvsSplitBy = ",";
        String[] country = null;
        int l=0;
        String[] testDoc =new String[402];


        String use_inputs = recom.getText().toString();
        br3 = new BufferedReader(new FileReader(csvFile));
        out ="";
        out ="Description Of Movie:";
        while ((line = br3.readLine()) != null) {

            // use comma as separator
            country = line.split(cvsSplitBy);
            String namm2 = country[7].toString();
            namm2.trim();
            namm2.toLowerCase();
            use_inputs.toLowerCase();

            if(namm2.equals(use_inputs)) {
                Mname=country[7].toString();
                rating =country[4].toString();
                review= country[3].toString();
                out+= "Name :";
                out +=Mname;
                out +="\nRating:";
                out +=rating+"/5";




            }

            testDoc[l]= country[3].toString();


            l++;
        }



        NBClassifierB nb = new NBClassifierB(trainDocs, trainLabels, numClass);

        return nb;

    }


}

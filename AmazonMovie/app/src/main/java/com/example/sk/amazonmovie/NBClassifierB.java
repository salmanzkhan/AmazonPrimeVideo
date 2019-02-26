package com.example.purva.amazonmovie;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
/**
 * Naive Bayes Classification
 * @author Team2
 */
public class NBClassifierB {
    private String[] trainingDocs;         //training data
    private int[] trainingClasses;         //training class values
    private int numClasses;
    private int[] classDocCounts;          //number of docs per class
    private String[] classStrings;         //concatenated string for a given class
    private int[] classTokenCounts;        //total number of tokens per class
    private HashMap<String,Double>[] condProb; //term conditional prob
    private HashSet<String> vocabulary;    //entire vocabulary

    @SuppressWarnings("unchecked")

    /**
     * NBClassifier constructor for probabilty based classification algorithm
     */
    public NBClassifierB(String[] docs, int[] classes, int numC) {
        trainingDocs = docs;   //documents that needs to be trained
        trainingClasses = classes;
        numClasses = numC;

        classDocCounts = new int[numClasses];
        classStrings = new String[numClasses];
        classTokenCounts = new int[numClasses];

        condProb = new HashMap[numClasses];
        vocabulary = new HashSet<String>();

        for(int i=0;i<numClasses;i++){
            classStrings[i] = "";
            condProb[i] = new HashMap<String,Double>();
        }

        for(int i=0;i<trainingClasses.length;i++){
            classDocCounts[trainingClasses[i]]++;
            classStrings[trainingClasses[i]] += (trainingDocs[i] + " ");
        }

        for(int i=0;i<numClasses;i++){
            String[] tokens = classStrings[i].split(" "); //tokenization
            classTokenCounts[i] = tokens.length;
            for(String token:tokens){
                vocabulary.add(token);   //add token to create vocabulary
                if(condProb[i].containsKey(token)){
                    double count = condProb[i].get(token);
                    condProb[i].put(token, count+1);
                }
                else
                    condProb[i].put(token, 1.0);
            }
        }

        for(int i=0;i<numClasses;i++){
            Iterator<Map.Entry<String, Double>> iterator = condProb[i].entrySet().iterator();
            int vSize = vocabulary.size();
            while(iterator.hasNext()) {
                Map.Entry<String, Double> entry = iterator.next();
                String token = entry.getKey();
                Double count = entry.getValue();
                Double prob = (count + 1) / (classTokenCounts[i] + vSize); //performing laplace transformation
                condProb[i].put(token, prob);   //calculating probability
            }
        }
    }

    public int classfiy(String doc){
        int label = 0;
        int vSize = vocabulary.size();
        double[] score = new double[numClasses];

        for(int i=0;i<score.length;i++) {
            score[i] = classDocCounts[i] * 1.0/trainingDocs.length; //calculating prior prob
        }
        String[] tokens = doc.split(" ");   //tokenization

        for(int i=0;i<numClasses;i++) {
            for(String token:tokens) {
                if(condProb[i].containsKey(token))
                    score[i] *= condProb[i].get(token);    //calculating scores
                else
                    score[i] *= (1.0/(classTokenCounts[i] * vSize));
            }
        }

        //Calculating maxinum score
        double maxScore = score[0];
        System.out.println("score[0]:" + score[0]);
        for(int i=1;i<score.length;i++) {
            System.out.println("score[" + i + "]:" + score[i]);
            if(score[i] > maxScore) {
                maxScore = score[i];
                label = i;
            }
        }

        return label;
    }
    @SuppressWarnings("resource")
    /*
    * Testing naive bayes implementation
    * */
    public static void main(String[] args) throws IOException{

        String[] trainDocs = new String[10000];
        int[] trainLabels = new int[10000];
        int numClass = 2;

        //File Pfile = new File("C:\\Users\\purva\\AndroidStudioProjects\\AmazonMovie\\app\\sampledata\\postive.txt");
        //File Nfile = new File("C:\\Users\\purva\\AndroidStudioProjects\\AmazonMovie\\app\\sampledata\\negative.txt");

        File sdcard = Environment.getExternalStorageDirectory();
        File Pfile = new File(sdcard,"postive.txt");  //reading positive word from sdcard

        File Nfile = new File(sdcard,"negative.txt");  //reading negative word from sdcard


        File csvFile = new File(sdcard, "AmazonR.csv"); //reading training data csv from sdcard


        BufferedReader br = new BufferedReader(new FileReader(Pfile));
        BufferedReader br1 = new BufferedReader(new FileReader(Nfile));
        String st;
        int i=0;
        while ((st = br.readLine()) != null) {
            if(st != null) {
                trainDocs[i]= st;
                trainLabels[i]=0;     //storing positive word as 0
                i++;
            }
        }

        while ((st = br1.readLine()) != null) {
            if(st != null) {
                trainDocs[i]= st;
                trainLabels[i]=1;    //storing negative word as 1
                i++;
            }
        }

        //String csvFile = "C:\\Users\\purva\\AndroidStudioProjects\\AmazonMovie\\app\\sampledata\\AmazonR.csv";
        BufferedReader br3 = null;
        String line = "";
        String cvsSplitBy = ",";
        String[] country = null;
        int l=0;
        String[] testDoc =new String[402];
        br3 = new BufferedReader(new FileReader(csvFile));
        while ((line = br3.readLine()) != null) {

            // use comma as separator
            country = line.split(cvsSplitBy);

            testDoc[l]= country[3].toString();


            l++;
        }


        NBClassifierB nb = new NBClassifierB(trainDocs, trainLabels, numClass);
        System.out.println("\nClass Prediction: " + nb.classfiy("good"));
    }
}

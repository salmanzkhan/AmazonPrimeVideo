package com.example.purva.amazonmovie;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.*;


public class TF_IDF2 {

    public Set<String> stopwords;      // List of common stopwords

    public List<List<String>> docs;    // List of documents holds the movies name

    public int docLen;                //total number of documents

    public ArrayList<String> terms;    // Holds the terms that are unique
    public int numTerms;               //Total number of terms

    public int[][] termsFreq;           // Matrix to represent terms frequency
    public double[][] WeightTfIdf;      // tf-idf matrix
    public int[] docFreq;              // terms' frequency in all documents

    /**
     * Constructor function to represent TF-IDF for cosine similarity and build matrix
     * @param documents represented in the form of strings array.
     */
    public TF_IDF2(String[] documents) {


        try {

            docs = this.parseDoc(documents);   //call parse document to tokenize words
            docLen = docs.size();

            terms = this.generateTerms(docs);
            numTerms = terms.size();

            docFreq = new int[numTerms];
            termsFreq = new int[numTerms][docLen];
            WeightTfIdf = new double[numTerms][docLen];

            this.totalTermOccurrence();
            this.totalTermWeight();
        }
        catch (Exception e){

            e.printStackTrace();

        }
    }

    /**
     * Generates bag of words that holds parse documents
     * @param docs documents in strings
     * @return a list of documents represented by bags of words
     */
    public List<List<String>> parseDoc(String[] docs) {
        List<List<String>> DocLists = new ArrayList<List<String>>();

        for(String doc: docs) {
            String[] words = doc.trim().split("\\s");  //tokenize the word using white space
            List<String> docList = new ArrayList<String>();
            for(String word: words) {
                word = word.trim();       //removes whitespace from front and back of a word
                if (word.length() > 0 ) {
                    docList.add(word);   //add the word to the list if it exists
                }
            }
            DocLists.add(docList);    //add doclist to docLists
        }

        return DocLists;
    }

    /**
     * Generate unique terms from a list of documents
     * @param docs
     * @return
     */
    public ArrayList<String> generateTerms(List<List<String>> docs) {
        ArrayList<String> specificTerms = new ArrayList();
        for(List<String> doc: docs) {
            for(String word: doc) {
                if (!specificTerms.contains(word)) {
                    specificTerms.add(word); //add the word to a unique list
                }
            }
        }
        return specificTerms;
    }

    /**
     * Count total term occurrence and index of each term in the entire documents
     */
    public void totalTermOccurrence() {
        for (int i = 0; i < docs.size(); i++) {
            List<String> doc = docs.get(i);
            HashMap<String, Integer> tf_map = this.countTermOccurInSingleDoc(doc);
            for(Entry<String, Integer> entry: tf_map.entrySet()) {
                String word = entry.getKey();
                int wordFreq = entry.getValue(); //get word frequency
                int termIndex = terms.indexOf(word);

                termsFreq[termIndex][i] = wordFreq; //store the word frequency at specific index
                docFreq[termIndex]++;
            }
        }
    }

    /**
     * Count the term frequency in single document
     * @param doc a document as a bag of words
     * @return a map of term occurrence; key - term; value - occurrence.
     */
    public HashMap<String, Integer> countTermOccurInSingleDoc(List<String> doc) {

        HashMap<String, Integer> tf_map = new HashMap<String, Integer>();

        for(String word: doc) {
            int count = 0;
            for(String str: doc) {
                if (str.equals(word)) {
                    count++;
                }
            }
            tf_map.put(word, count);   //stores the word and its frequency of a single document
        }

        return tf_map;
    }

    /**
     * Calculate term weight using tf*idf algorithm
     * weight of each term is calculated by the multiplication of frequency of term in documents and number of documents
     * in which the particular term is present
     */
    public void totalTermWeight()
    {
        for (int i = 0; i < numTerms; i++) {
            for (int j = 0; j < docLen; j++) {
                double tf = this.getTF(i, j); //get the term frequency
                double idf = this.getIDF(i);  //get the document frequency
                WeightTfIdf[i][j] = tf * idf;  // calculates the weights
            }
        }
    }

    /**
     * Considering the calculation term frequency by calculating square root of term frequency
     * @param term
     * @param doc
     * @return
     */
    public double getTF(int term, int doc) {
        int freq = termsFreq[term][doc];
        return Math.sqrt((double) freq);
    }

    /**
     * Considering the calculation of idf by using calculating 1+ log(N/1+df)
     * @param term
     * @return
     */
    public double getIDF(int term) {
        int df = docFreq[term];
        return 1.0d + Math.log( (double) (docLen) / (1.0d + df) );
    }

    /**
     * Calculating similarity score between two documents
     * we consider each document as a vector for calculating cosine similarity
     * @param doc_i index of one document
     * @param doc_j index of another document
     * @return similarity score
     */
    public double getSimilarity(int doc_i, int doc_j) {
        double[] vector1 = this.getDocVector(doc_i);
        double[] vector2 = this.getDocVector(doc_j);
        return TF_IDF2.calculateCosineSimilarity(vector1, vector2);
    }

    /**
     * Creates a vector for a document
     * @param docIndex index of a document
     * @return the vector representation of the document
     */
    public double[] getDocVector(int docIndex) {
        double[] v = new double[numTerms];
        for (int i = 0; i < numTerms; i++) {
            v[i] = WeightTfIdf[i][docIndex];   //represent doc in a vector format.
        }
        return v;
    }

    /**
     * Calculate cosine similarity between two vectors
     * @param vector1 a vector
     * @param vector2 another vector
     * @return cosine similarity score
     */
    public static double calculateCosineSimilarity(double[] vector1, double[] vector2)
    {
        if (vector1.length != vector2.length) {
            System.out.println("Difference in the length of vector");
        }

        double denominator = (vectorLength(vector1) * vectorLength(vector2));
        if (denominator == 0.0d) {
            return 0.0d;
        } else {
            return (vecProduct(vector1, vector2) / denominator); //calculate vector products and normalized it
        }
    }

    /**
     * Calculate product of two vectors
     * @param vector1 first vector
     * @param vector2 second vector
     * @return production of two vectors
     */
    public static double vecProduct(double[] vector1, double[] vector2)
    {
        double result = 0.0d;
        for (int i = 0; i < vector1.length; i++) {
            result += vector1[i] * vector2[i]; //product of vectors
        }
        return result;
    }

    /**
     * Calculate vector length by calculating magnitude.
     * @param vector a vector
     * @return vector length
     */
    public static double vectorLength(double[] vector)
    {
        double sum = 0.0d;
        for(double d: vector) {
            sum += d * d;
        }
        return Math.sqrt(sum);
    }


    /**
     * Main function for functionality Testing
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String csvFile = "C:\\Users\\salma\\eclipse-workspace\\Amazon\\src\\com\\movieTV.csv";
        BufferedReader br2 = null;
        String line = "";
        String cvsSplitBy = ",";
        String[] country = null;
        int l=0;
        String[] testDoc =new String[33];
        br2 = new BufferedReader(new FileReader(csvFile));
        while ((line = br2.readLine()) != null) {

            country = line.split(cvsSplitBy);
            testDoc[l]= country[1].trim().toString().replaceAll("^\"|\"$", "");

            l++;
        }

        TF_IDF2 tfIdf = new TF_IDF2(testDoc);
        String k ="fifty shades of grey";
        for(int i=0; i< tfIdf.docs.size(); i++) {
            if(testDoc[i].equals(k)) {
                int count=0;
                ArrayList<Double> m= new ArrayList<>();
                for (int j =1; j < tfIdf.docs.size(); j++) {
                    double g = tfIdf.getSimilarity(i, j);

                    if(g>0.1 && g<1 && count<3) {

                        System.out.println(testDoc[j] + "\t");
                        count++;

                    }
                }
            }
        }
    }
}

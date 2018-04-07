package com.example.francesco.calcolatrix;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashMap;

public class CalcolatrixActivity extends AppCompatActivity implements View.OnClickListener{

    protected static int CLR_SCRITTE = Color.WHITE;
    protected static int CLR_CIFRE = Color.rgb(100,100,100);
    protected static int CLR_OPERATORI = Color.rgb(50,150,200);
    protected static int CLR_UGUALE = Color.rgb(0,200,50);
    protected static int CLR_OFF = Color.rgb(200,0,50);
    protected static int CLR_CLEAR = Color.LTGRAY;
    protected static int CLR_OTHER = Color.GRAY;

    protected static int T_CIFRE=0, T_OP_BINARI=1, T_OP_UNARI=2, T_OFF=3, T_UGUALE=4, T_AC=5;

    protected static int rows = 5, cols = 4;
    protected static int numeroTasti = rows*cols;

    protected int screenH;
    protected int screenW;

    protected LinearLayout appContainer;
    protected TextView display;
    protected TableLayout controlPanel;
    protected TableRow[] tRows;
    protected Bottonix<String>[] tasti;

    // la lunghezza dell'array deve corrispondere a numeroTasti
    protected String[] valori = {"7", "8", "9", "+",
                                 "4", "5", "6", "-",
                                 "1", "2", "3", "*",
                                 ".", "0","+/-","/",
                                 "OFF","C","AC","="};

    protected HashMap<String,Integer> value_to_type;

    ///Variabili della calcolatrice
    protected String operatore, operandoA, operandoB;
    protected double numeroA, numeroB;
    protected int step; //0=OFF,1=A,2=B
    ///


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        operatore = "";
        operandoA = "0";
        operandoB = "0";
        step = 1;

        creaInterfacciaGrafica();

        setContentView(appContainer);
    }

    private void creaInterfacciaGrafica() {

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenH = metrics.heightPixels;
        screenW = metrics.widthPixels;

        appContainer = new LinearLayout(this);
        appContainer.setOrientation(LinearLayout.VERTICAL);

        display = new TextView(this);
        display.setText(operandoA);
        display.setTextSize(50);
        display.setSingleLine();
        display.setBackgroundColor(Color.LTGRAY);
        display.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        appContainer.addView(display);

        controlPanel = new TableLayout(this);

        creaRighe();
        creaMappa();
        creaTasti();
        creaTabella();
    }

    private void creaMappa() {
        value_to_type = new HashMap<>();
        //assegna ad ogni valore un tipo
        for (int i=0; i<valori.length; i++){

            int temp = T_CIFRE;

            if(i==19){ //uguale
                temp = T_UGUALE;
            }
            else if((i+1)%4==0){ //colonna operatori bin
                temp = T_OP_BINARI;
            }
            else if (i==16){ //OFF
                temp = T_OFF;
            }
            else if(i==18){ //all clear
                temp = T_AC;
            }
            else if(i==12 || i==14 || i==17){ // "." or "+/-" or "C"
                temp = T_OP_UNARI;
            }

            value_to_type.put(valori[i], temp);
        }

    }

    private void creaRighe() {

        tRows = new TableRow[rows];
        for (int i = 0; i < rows; i++) {
            tRows[i] = new TableRow(this);
        }
    }

    private void creaTabella() {
        for (int i=0; i<rows; i++){
            controlPanel.addView(tRows[i]);
        }
        controlPanel.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        appContainer.addView(controlPanel);
    }

    private void creaTasti() {

        this.tasti = new Bottonix[numeroTasti];
        // popola this.tasti
        for (int i=0; i<numeroTasti; i++){

            tasti[i] = new Bottonix<>(this, valori[i]);

            tasti[i].setWidth(screenW / cols);
            tasti[i].setHeight((screenH*3/4) / rows);

            tasti[i].setText(tasti[i].getValue());

            tasti[i].setTextColor(CLR_SCRITTE);
            coloraTasto(i);

            tasti[i].setOnClickListener(this);

            //assegna i tasti alle righe della tabella
            tRows[i/cols].addView(tasti[i]);
        }
    }

    private void coloraTasto(int i) {
        int temp;
        if(i==19){ //uguale
            temp = CLR_UGUALE;
        }
        else if((i+1)%4==0){ //colonna operatori
            temp = CLR_OPERATORI;
        }
        else if (i==16){ //OFF
            temp = CLR_OFF;
        }
        else if(i==17 || i==18){
            temp = CLR_CLEAR;
        }
        else if(i==12 || i==14){ // "." or "+/-"
            temp = CLR_OTHER;
        }
        else{
            temp = CLR_CIFRE;
        }
        tasti[i].setBackgroundColor(temp);
    }

    protected void quit(){
        this.finish();
    }

    @Override
    public void onClick(View view) {
        Bottonix<String> btn = (Bottonix) view;
        String val = btn.getValue();
        int typ = value_to_type.get(val);


        if (typ == T_OFF){
            quit();
        }
        else if (typ == T_AC) {
            operatore = "";
            operandoA = "0";
            operandoB = "0";
            numeroA = 0;
            numeroB = 0;

            goToStep(1);
            updateDisplay();
        }
        else if(step==1) { //Inserimento operandoA
            if (typ == T_CIFRE) {
                if (operandoA.equals("0")){
                    operandoA = val;
                }
                else {
                    operandoA += val;
                }
            }
            else if (val.equals("+/-")){
                    if(!operandoA.contains("-"))
                        operandoA = "-" + operandoA;
                    else
                        operandoA = (String) operandoA.subSequence(1,operandoA.length());
            }
            else if (val.equals(".")){
                if (!operandoA.contains(".")) {
                    operandoA += ".";
                }
            }
            else if (val.equals("C")){
                if (operandoA.length()<=1)
                    operandoA = "0";
                else
                    operandoA = operandoA.substring(0,operandoA.length()-1);
            }

            else if (typ == T_OP_BINARI){

                numeroA = Double.parseDouble(operandoA);
                operandoA = "0";
                operandoB = "0";
                operatore = val;

                goToStep(2);

            }

            updateDisplay();

        }
        else if (step == 2){ // Inserimento operandoB

            if (typ == T_CIFRE) {
                if (operandoB.equals("0")){
                    operandoB = val;
                }
                else {
                    operandoB += val;
                }
            }
            else if (val.equals("+/-")){
                if(!operandoB.contains("-"))
                    operandoB = "-" + operandoB;
                else
                    operandoB = (String) operandoB.subSequence(1,operandoB.length());
            }
            else if (val.equals(".")){
                if (!operandoB.contains(".")) {
                    operandoB += ".";
                }
            }
            else if (val.equals("C")){ //TODO concedere di annullare operatore
                if (operandoB.length()<=1)
                    operandoB = "0";
                else
                    operandoB = operandoB.substring(0,operandoB.length()-1);
            }

            else if (typ == T_OP_BINARI){
                operatore = val;
                numeroB = Double.parseDouble(operandoB);
                numeroA = faIlConto();
                operandoA = "0";
                operandoB = "0";
                numeroB = 0;

                goToStep(2);

            }
            else if (typ == T_UGUALE){
                //operatore = val;

                numeroB = Double.parseDouble(operandoB);
                numeroA = faIlConto();
                operandoB = "0";
                operandoA = ""+ numeroA;

                goToStep(1);
            }

            updateDisplay();
        }

    }
    private double faIlConto(){
        double temp = 0;

        if (operatore.equals("+"))
            temp = numeroA + numeroB;
        else if (operatore.equals("-"))
            temp = numeroA - numeroB;
        else if (operatore.equals("*"))
            temp = numeroA * numeroB;
        else if (operatore.equals("/"))
            temp = numeroA / numeroB;

        return temp;
    }

    private void goToStep(int n){
        step = n;
        if (operandoA.endsWith(".0"))
            operandoA = operandoA.substring(0,operandoA.length()-2);
        if (operandoB.endsWith(".0"))
            operandoB = operandoB.substring(0,operandoB.length()-2);
    }
    private void updateDisplay(){
        if(step==1)
            display.setText(operandoA);
        else if(step==2)
            display.setText(operandoB);
    }
}
